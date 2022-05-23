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

    private final CallbackPolicy callbackPolicy;

    public WorkerPoolImpl() {
        this(CORES, CORES * 5, 60L, TimeUnit.SECONDS, 200);
    }

    public WorkerPoolImpl(int coreSize, int maxSize, long keepAlive, TimeUnit unit, int queueSize) {
        this.callbackPolicy = new CallbackPolicy();
        this.executor = new ThreadPoolExecutor(
                coreSize,
                maxSize,
                keepAlive,
                unit,
                new LinkedBlockingQueue<>(queueSize),
                new WorkerThreadFactory(),
                this.callbackPolicy
        );
    }

    public void onRejected(RunnableHandler handler) {
        callbackPolicy.setRunnableHandler(handler);
    }

    public void onRejected(CallableHandler handler) {
        callbackPolicy.setCallableHandler(handler);
    }

    @Override
    public Executor executor() {
        return executor;
    }

    @Override
    public void execute(Runnable runnable, Callback<Void, Throwable> callback) {
        try {
            executor.execute(new RunnableWrapper(runnable, callback));
        } catch (Throwable t) {
            callback.onException(t);
        }
    }

    @Override
    public <R> void execute(Callable<R> callable, Callback<R, Throwable> callback) {
        try {
            executor.execute(new CallableWrapper<>(callable, callback));
        } catch (Throwable t) {
            callback.onException(t);
        }
    }

    @Override
    public void close() {
        executor.shutdown();
    }
}
