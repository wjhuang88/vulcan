package io.vulcan.worker;

import io.vulcan.api.base.functional.Callable;
import io.vulcan.api.base.functional.Callback;
import io.vulcan.api.base.functional.Runnable;
import io.vulcan.worker.impl.WorkerPoolImpl;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public interface WorkerPool extends AutoCloseable {

    class Holder {
        private final static WorkerPoolImpl INSTANCE = new WorkerPoolImpl();
    }

    static WorkerPool getDefault() {
        return Holder.INSTANCE;
    }

    static WorkerPool getDefault(RunnableHandler runnableHandler) {
        final WorkerPoolImpl pool = Holder.INSTANCE;
        pool.onRejected(runnableHandler);
        return pool;
    }

    static WorkerPool getDefault(CallableHandler callableHandler) {
        final WorkerPoolImpl pool = Holder.INSTANCE;
        pool.onRejected(callableHandler);
        return pool;
    }

    static WorkerPool getDefault(RunnableHandler runnableHandler, CallableHandler callableHandler) {
        final WorkerPoolImpl pool = Holder.INSTANCE;
        pool.onRejected(runnableHandler);
        pool.onRejected(callableHandler);
        return pool;
    }

    static WorkerPool create() {
        return new WorkerPoolImpl();
    }

    static WorkerPool create(RunnableHandler runnableHandler) {
        final WorkerPoolImpl pool = new WorkerPoolImpl();
        pool.onRejected(runnableHandler);
        return pool;
    }

    static WorkerPool create(CallableHandler callableHandler) {
        final WorkerPoolImpl pool = new WorkerPoolImpl();
        pool.onRejected(callableHandler);
        return pool;
    }

    static WorkerPool create(RunnableHandler runnableHandler, CallableHandler callableHandler) {
        final WorkerPoolImpl pool = new WorkerPoolImpl();
        pool.onRejected(runnableHandler);
        pool.onRejected(callableHandler);
        return pool;
    }

    Executor executor();

    int coreSize();

    int maxSize();

    int queueSize();

    void execute(Runnable runnable, Callback<Void, Throwable> callback);

    <R> void execute(Callable<R> callable, Callback<R, Throwable> callback);

    interface RunnableHandler extends Consumer<io.vulcan.api.base.functional.Runnable> {}

    interface CallableHandler extends Consumer<io.vulcan.api.base.functional.Callable<?>> {}
}
