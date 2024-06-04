package zone.hwj.vulcan.worker.impl;

import zone.hwj.vulcan.api.base.functional.Callback;
import javax.annotation.Nonnull;
import zone.hwj.vulcan.api.base.functional.Runnable;

public class RunnableWrapper implements java.lang.Runnable {

    @Nonnull
    private final Callback<Void, Throwable> callback;

    @Nonnull
    private final Runnable task;

    RunnableWrapper(@Nonnull Runnable task, @Nonnull Callback<Void, Throwable> callback) {
        this.callback = callback;
        this.task = task;
    }

    @Nonnull
    public Runnable getTask() {
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
