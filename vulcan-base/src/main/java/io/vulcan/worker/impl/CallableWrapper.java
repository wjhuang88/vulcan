package io.vulcan.worker.impl;

import io.vulcan.api.base.functional.Callback;
import javax.annotation.Nonnull;

public class CallableWrapper<R> implements Runnable {

    @Nonnull
    private final Callback<R, Throwable> callback;

    @Nonnull
    private final io.vulcan.api.base.functional.Callable<R> task;

    CallableWrapper(@Nonnull io.vulcan.api.base.functional.Callable<R> task, @Nonnull Callback<R, Throwable> callback) {
        this.callback = callback;
        this.task = task;
    }

    @Nonnull
    public io.vulcan.api.base.functional.Callable<R> getTask() {
        return task;
    }

    @Override
    public void run() {
        try {
            R result = task.call();
            callback.onSuccess(result);
        } catch (Throwable t) {
            callback.onException(t);
        }
    }
}
