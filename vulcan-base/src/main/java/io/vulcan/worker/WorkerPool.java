package io.vulcan.worker;

import io.vulcan.api.base.functional.Callable;
import io.vulcan.api.base.functional.Callback;
import io.vulcan.api.base.functional.Runnable;
import io.vulcan.worker.impl.WorkerPoolImpl;
import java.util.concurrent.Executor;

public interface WorkerPool extends AutoCloseable {

    class Holder {
        private final static WorkerPool INSTANCE = new WorkerPoolImpl();
    }

    static WorkerPool getInstance() {
        return Holder.INSTANCE;
    }

    Executor executor();

    void execute(Runnable runnable, Callback<Void, Throwable> callback);

    <R> void execute(Callable<R> callable, Callback<R, Throwable> callback);
}
