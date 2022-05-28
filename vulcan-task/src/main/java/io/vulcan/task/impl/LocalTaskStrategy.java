package io.vulcan.task.impl;

import com.lmax.disruptor.dsl.Disruptor;
import io.vulcan.api.base.functional.Callback;
import io.vulcan.task.TaskStatus;
import io.vulcan.task.TaskStrategy;
import io.vulcan.utils.StringUtils;
import io.vulcan.worker.WorkerPool;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalTaskStrategy implements TaskStrategy {

    private final WorkerPool workerPool;
    private final Map<String, Task<?>> taskMap = new ConcurrentHashMap<>();

    private static final int BUFFER_SIZE = 1024;

    private final Disruptor<StringEvent> disruptor;

    @SuppressWarnings("deprecation")
    public LocalTaskStrategy(WorkerPool workerPool) {
        this.workerPool = workerPool;
        disruptor = new Disruptor<>(StringEvent::new, BUFFER_SIZE,
                workerPool.executor());

        disruptor.handleEventsWith((event, sequence, endOfBatch) -> {
            if (null == event || StringUtils.isNullOrEmpty(event.getValue())) {
                return;
            }
            final String taskId = event.getValue();
            final Task<?> task = taskMap.get(taskId);
            if (task != null) {
                try {
                    task.run();
                } catch (Throwable e) {
                    task.forceFail(e);
                }
            }
        });
        disruptor.start();
    }

    @Override
    public void start(final Task<?> task) {
        if (task == null) {
            throw new RuntimeException("待执行的task为null");
        }
        taskMap.put(task.getId(), task);
        workerPool.execute(task::run, new TaskCallback(task));
    }

    @Override
    public void startInOrder(final Task<?> task) {
        if (task == null) {
            throw new RuntimeException("待执行的task为null");
        }
        taskMap.put(task.getId(), task);
        disruptor.publishEvent((event, sequence) -> {
            event.setValue(task.getId());
        });
    }

    @Override
    public void loop(final Task<?> task) {
        if (task == null) {
            throw new RuntimeException("待执行的task为null");
        }
        taskMap.put(task.getId(), task);
        new Thread(() -> {
            while (TaskStatus.RUNNING.equals(task.getStatus())) {
                task.run();
            }
        }).start();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> Task<R> getTask(String id) {
        return (Task<R>) taskMap.get(id);
    }

    @Override
    public synchronized void remove(String id) {
        final Task<?> task = taskMap.remove(id);
        if (task != null) {
            task.cancel();
        }
    }

    @Override
    public synchronized void clear() {
        taskMap.values().forEach(Task::cancel);
        taskMap.clear();
    }

    static class StringEvent {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    static class TaskCallback implements Callback<Void, Throwable> {

        private final Task<?> task;

        TaskCallback(Task<?> task) {
            this.task = task;
        }

        @Override
        public void onSuccess(Void sendResult) {
            // do nothing.
        }

        @Override
        public void onException(Throwable throwable) {
            task.forceFail(throwable);
        }
    }
}
