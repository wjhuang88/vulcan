package zone.hwj.vulcan.worker.impl;

import zone.hwj.vulcan.api.base.functional.Callable;
import zone.hwj.vulcan.api.base.functional.Callback;
import javax.annotation.Nonnull;

public class CallableWrapper<R> implements Runnable {

    @Nonnull
    private final Callback<R, Throwable> callback;

    @Nonnull
    private final Callable<R> task;

    CallableWrapper(@Nonnull Callable<R> task, @Nonnull Callback<R, Throwable> callback) {
        this.callback = callback;
        this.task = task;
    }

    @Nonnull
    public Callable<R> getTask() {
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
