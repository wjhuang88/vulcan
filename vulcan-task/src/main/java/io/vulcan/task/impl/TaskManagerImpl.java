package io.vulcan.task.impl;

import io.vulcan.api.base.functional.Callable;
import io.vulcan.api.base.functional.ParameterizedCallable;
import io.vulcan.task.TaskManager;
import io.vulcan.task.TaskStatus;
import io.vulcan.task.TaskStrategy;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TaskManagerImpl implements TaskManager {

    private final Logger log = LoggerFactory.getLogger(TaskManagerImpl.class);

    private final TaskStrategy strategy;

    public TaskManagerImpl(TaskStrategy strategy) {
        this.strategy = strategy;
    }

    private String generateId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private <R> Task<R> makeTask(final Callable<R> task) {
        return new Task<R>(generateId()) {
            @Override
            public R runNow() throws Throwable {
                return task.call();
            }
        };
    }

    private <R> Task<R> makeTask(final ParameterizedCallable<String, R> task) {
        final String taskId = generateId();
        return new Task<R>(taskId) {
            @Override
            public R runNow() throws Throwable {
                return task.call(taskId);
            }
        };
    }

    @Override
    public <R> Task<R> runTask(final Callable<R> task) {
        final Task<R> realTask = makeTask(task);
        strategy.start(realTask);
        return realTask;
    }

    @Override
    public <R> Task<R> runTask(final ParameterizedCallable<String, R> task) {
        final Task<R> realTask = makeTask(task);
        strategy.start(realTask);
        return realTask;
    }

    @Override
    public <R> Task<R> runTaskInOrder(final Callable<R> task) {
        final Task<R> realTask = makeTask(task);
        strategy.startInOrder(realTask);
        return realTask;
    }

    @Override
    public <R> Task<R> runTaskInOrder(final ParameterizedCallable<String, R> task) {
        final Task<R> realTask = makeTask(task);
        strategy.startInOrder(realTask);
        return realTask;
    }

    @Override
    public Task<?> loopTask(final Runnable task) {
        final Task<Object> realTask = new LoopTask<Object>(generateId()) {
            @Override
            public Object runNow() {
                task.run();
                return null;
            }
        };
        strategy.loop(realTask);
        return realTask;
    }

    @Override
    public TaskStatus taskStatus(final String id) {
        Task<Object> task = strategy.getTask(id);
        if (task == null) {
            log.error("任务id为 " + id + " 的任务不存在");
            return null;
        }
        return task.getStatus();
    }

    @Override
    public <R> R getResult(final String id) {
        final Task<R> task = strategy.getTask(id);
        if (task == null) {
            log.error("任务id为 " + id + " 的任务不存在");
            return null;
        }
        return task.getResult();
    }

    @Override
    public <R> R waitAndGetResult(final String id) {
        try {
            Task<R> task = strategy.getTask(id);
            if (task == null) {
                log.error("任务id为 " + id + " 的任务不存在");
                return null;
            }
            return task.waitAndGetResult();
        } catch (InterruptedException e) {
            log.error("Went wrong while waiting result for task: " + id, e);
            return null;
        }
    }

    @Override
    public synchronized void cancel(final String id) {
        final Task<?> task = strategy.getTask(id);
        if (task == null) {
            log.error("任务id为 " + id + " 的任务不存在");
            return;
        }
        task.cancel();
        strategy.remove(id);
    }

    @Override
    public synchronized void cancel(final Task<?> task) {
        task.cancel();
        strategy.remove(task.getId());
    }

    @Override
    public void close() {
        strategy.clear();
    }

}
