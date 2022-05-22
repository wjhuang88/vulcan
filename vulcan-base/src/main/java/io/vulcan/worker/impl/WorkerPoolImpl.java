package io.vulcan.worker.impl;

import io.vulcan.api.base.functional.Callable;
import io.vulcan.api.base.functional.Callback;
import io.vulcan.api.base.functional.Runnable;
import io.vulcan.worker.WorkerPool;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WorkerPoolImpl implements WorkerPool {

    private static final int CORES = Runtime.getRuntime().availableProcessors();

    private final ThreadPoolExecutor executor;

    public WorkerPoolImpl() {
        executor = new ThreadPoolExecutor(
                CORES,
                CORES * 5,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(200),
                new WorkerThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    @Override
    public Executor executor() {
        return executor;
    }

    @Override
    public void execute(Runnable runnable, Callback<Void, Throwable> callback) {
        executor.execute(() -> {
            try {
                runnable.run();
                callback.onSuccess(null);
            } catch (Throwable t) {
                callback.onException(t);
            }
        });
    }

    @Override
    public <R> void execute(Callable<R> callable, Callback<R, Throwable> callback) {
        executor.execute(() -> {
            try {
                R result = callable.call();
                callback.onSuccess(result);
            } catch (Throwable t) {
                callback.onException(t);
            }
        });
    }
}
