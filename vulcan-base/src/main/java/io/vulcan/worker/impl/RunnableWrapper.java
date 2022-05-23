package io.vulcan.worker.impl;

import io.vulcan.api.base.functional.Callback;
import javax.annotation.Nonnull;

public class RunnableWrapper implements Runnable {

    @Nonnull
    private final Callback<Void, Throwable> callback;

    @Nonnull
    private final io.vulcan.api.base.functional.Runnable task;

    RunnableWrapper(@Nonnull io.vulcan.api.base.functional.Runnable task, @Nonnull Callback<Void, Throwable> callback) {
        this.callback = callback;
        this.task = task;
    }

    @Nonnull
    public io.vulcan.api.base.functional.Runnable getTask() {
        return task;
    }

    @Override
    public void run() {
        try {
            task.run();
            callback.onSuccess(null);
        } catch (Throwable t) {
            callback.onException(t);
        }
    }
}
