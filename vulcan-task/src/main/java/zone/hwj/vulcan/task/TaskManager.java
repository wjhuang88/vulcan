package zone.hwj.vulcan.task;

import zone.hwj.vulcan.api.base.functional.Callable;
import zone.hwj.vulcan.api.base.functional.ParameterizedCallable;
import zone.hwj.vulcan.task.impl.LocalTaskStrategy;
import zone.hwj.vulcan.task.impl.Task;
import zone.hwj.vulcan.task.impl.TaskManagerImpl;
import zone.hwj.vulcan.worker.WorkerPool;

public interface TaskManager {

    class Holder {
        private final static TaskManagerImpl INSTANCE = new TaskManagerImpl(new LocalTaskStrategy(WorkerPool.getDefault()));
    }

    static TaskManager getDefault() {
        return Holder.INSTANCE;
    }

    static TaskManager create() {
        return new TaskManagerImpl(new LocalTaskStrategy(WorkerPool.getDefault()));
    }

    static TaskManager create(WorkerPool pool) {
        return new TaskManagerImpl(new LocalTaskStrategy(pool));
    }

    <R> Task<R> runTask(final Callable<R> task);

    <R> Task<R> runTask(final ParameterizedCallable<String, R> task);

    <R> Task<R> runTaskInOrder(final Callable<R> task);

    <R> Task<R> runTaskInOrder(final ParameterizedCallable<String, R> task);

    Task<?> loopTask(final Runnable task);

    TaskStatus taskStatus(final String id);

    <R> R getResult(final String id);

    <R> R waitAndGetResult(final String id);

    void cancel(final String id);

    void cancel(final Task<?> task);

    void close();

}
