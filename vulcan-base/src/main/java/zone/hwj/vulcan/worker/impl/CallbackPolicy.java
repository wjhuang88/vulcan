package zone.hwj.vulcan.worker.impl;

import zone.hwj.vulcan.worker.WorkerPool.CallableHandler;
import zone.hwj.vulcan.worker.WorkerPool.RunnableHandler;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.Nullable;

public class CallbackPolicy implements RejectedExecutionHandler {

    @Nullable
    private RunnableHandler runnableHandler;

    @Nullable
    private CallableHandler callableHandler;

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {

        if (null != runnableHandler && r instanceof RunnableWrapper) {
            runnableHandler.accept(((RunnableWrapper) r).getTask());
            return;
        }

        if (null != callableHandler && r instanceof CallableWrapper) {
            callableHandler.accept(((CallableWrapper<?>) r).getTask());
            return;
        }

        throw new RejectedExecutionException("Task " + r.toString() +
                " rejected from " +
                e.toString());
    }

    void setRunnableHandler(@Nullable RunnableHandler runnableHandler) {
        this.runnableHandler = runnableHandler;
    }

    void setCallableHandler(@Nullable CallableHandler callableHandler) {
        this.callableHandler = callableHandler;
    }
}
