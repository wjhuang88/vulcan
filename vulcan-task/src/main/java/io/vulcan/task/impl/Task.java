package io.vulcan.task.impl;

import io.vulcan.api.base.functional.Callback;
import io.vulcan.task.TaskStatus;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Task<R> implements Runnable, Future<R> {

    private final Logger log = LoggerFactory.getLogger(Task.class);

    private final String id;
    private volatile Thread runningThread;

    private R result;
    private Throwable cause;
    private volatile long startTime;
    private volatile TaskStatus status;
    private short waiters;

    private Callback<Task<R>, Throwable> onStart;
    private Callback<R, Throwable> onSuccess;
    private Callback<Throwable, Throwable> onFail;
    private Callback<Task<R>, Throwable> onCancel;

    @SuppressWarnings("rawtypes")
    private static final AtomicReferenceFieldUpdater<Task, TaskStatus> STATUS_UPDATER = AtomicReferenceFieldUpdater.newUpdater(Task.class, TaskStatus.class, "status");

    protected Task(String id) {
        this.id = id;
        setStatus(TaskStatus.CREATED);
    }

    public void onStart(Callback<Task<R>, Throwable> onStart) {
        this.onStart = onStart;
    }

    void doStart() {
        if (this.onStart == null) {
            return;
        }
        this.onStart.onSuccess(this);
    }

    public void onSuccess(Callback<R, Throwable> onSuccess) {
        this.onSuccess = onSuccess;
        if (TaskStatus.SUCCEEDED.equals(status)) {
            this.onSuccess.onSuccess(result);
        }
    }

    void doSuccess() {
        if (this.onSuccess == null) {
            return;
        }
        this.onSuccess.onSuccess(result);
    }

    public void onFail(Callback<Throwable, Throwable> onFail) {
        this.onFail = onFail;
        if (TaskStatus.FAILED.equals(status)) {
            this.onFail.onSuccess(cause);
        }
    }

    void doFail() {
        if (this.onFail == null) {
            return;
        }
        this.onFail.onSuccess(cause);
    }

    public void onCancel(Callback<Task<R>, Throwable> onCancel) {
        this.onCancel = onCancel;
    }

    void doCancel() {
        if (this.onCancel == null) {
            return;
        }
        this.onCancel.onSuccess(this);
    }

    private void incWaiters() {
        if (waiters == Short.MAX_VALUE) {
            throw new IllegalStateException("too many waiters: " + this);
        }
        ++waiters;
    }

    private void checkThread() {
        if (TaskStatus.SUCCEEDED.equals(status) && null == runningThread) {
            throw new IllegalStateException("Running thread is null.");
        }

        if (runningThread == Thread.currentThread()) {
            throw new IllegalStateException("Blocking current thread[" + Thread.currentThread() + "] may cause dead lock");
        }
    }

    private void decWaiters() {
        --waiters;
    }

    public String getId() {
        return id;
    }

    public R getResult() {
        return result;
    }

    void setResult(R result) {
        this.result = result;
    }

    public long getStartTime() {
        return startTime;
    }

    void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public TaskStatus getStatus() {
        return status;
    }

    void setStatus(TaskStatus status) {
        STATUS_UPDATER.set(this, status);
    }

    @SuppressWarnings("all")
    boolean setStatus(TaskStatus oldStatus, TaskStatus status) {
        return STATUS_UPDATER.compareAndSet(this, oldStatus, status);
    }

    void setRunningThread(Thread thread) {
        this.runningThread = thread;
    }

    public void cancel() {
        setStatus(TaskStatus.CANCELED);
        this.doCancel();
    }

    abstract protected R runNow() throws Throwable;

    @Override
    public void run() {
        setRunningThread(Thread.currentThread());
        setStatus(TaskStatus.RUNNING);
        this.doStart();
        try {
            setResult(runNow());
            setStatus(TaskStatus.RUNNING, TaskStatus.SUCCEEDED);
            this.doSuccess();
        } catch (Throwable e) {
            setCause(e);
            setStatus(TaskStatus.FAILED);
            this.doFail();
        }
        log.info("Task: " + id + " run finish");
        synchronized (this) {
            try {
                notifyAll();
            } catch (IllegalMonitorStateException e) {
                log.error("IllegalMonitorStateException", e);
            }
        }
    }

    public boolean isDone() {
        return TaskStatus.SUCCEEDED.equals(status)
                || TaskStatus.FAILED.equals(status)
                || TaskStatus.CANCELED.equals(status)
                || TaskStatus.TIMEOUT.equals(status);
    }

    private void await() throws InterruptedException {

        if (isDone()) {
            return;
        }

        if (Thread.interrupted()) {
            throw new InterruptedException(toString());
        }

        checkThread();

        synchronized (this) {
            while (!isDone()) {
                incWaiters();
                try {
                    if (log.isTraceEnabled()) {
                        log.trace("start wait for task: " + id);
                    }
                    wait();
                    if (log.isTraceEnabled()) {
                        log.trace("finished wait for task: " + id);
                    }
                } finally {
                    decWaiters();
                }
            }
        }
    }

    private boolean await(long timeoutNanos) throws InterruptedException {
        if (isDone()) {
            return true;
        }

        if (timeoutNanos <= 0) {
            return isDone();
        }

        if (Thread.interrupted()) {
            throw new InterruptedException(toString());
        }

        checkThread();

        final long startTime = System.nanoTime();
        synchronized (this) {
            boolean interrupted = false;
            try {
                long waitTime = timeoutNanos;
                while (!isDone() && waitTime > 0) {
                    incWaiters();
                    try {
                        wait(waitTime / 1000000, (int) (waitTime % 1000000));
                    } catch (InterruptedException e) {
                        interrupted = true;
                    } finally {
                        decWaiters();
                    }
                    // Check isDone() in advance, try to avoid calculating the elapsed time later.
                    if (isDone()) {
                        return true;
                    }
                    // Calculate the elapsed time here instead of in the while condition,
                    // try to avoid performance cost of System.nanoTime() in the first loop of while.
                    waitTime = timeoutNanos - (System.nanoTime() - startTime);
                }
                return isDone();
            } finally {
                if (interrupted) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public R waitAndGetResult() throws InterruptedException {
        if (TaskStatus.SUCCEEDED.equals(status)) {
            return getResult();
        }

        await();

        if (TaskStatus.SUCCEEDED.equals(status)) {
            return getResult();
        }

        return null;
    }

    public R waitAndGetResult(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
        if (TaskStatus.SUCCEEDED.equals(status)) {
            return getResult();
        }

        if (!await(unit.toNanos(timeout))) {
            throw new TimeoutException();
        }

        if (TaskStatus.SUCCEEDED.equals(status)) {
            return getResult();
        }

        return null;
    }

    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    public void forceFail(Throwable cause) {
        this.cause = cause;
        setStatus(TaskStatus.FAILED);
        doFail();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (isDone()) {
            return false;
        }
        if (mayInterruptIfRunning) {
            cancel();
            return TaskStatus.CANCELED.equals(status);
        }
        if (TaskStatus.RUNNING.equals(status)) {
            return false;
        }
        cancel();
        return TaskStatus.CANCELED.equals(status);
    }

    @Override
    public boolean isCancelled() {
        return TaskStatus.CANCELED.equals(status)
                || TaskStatus.TIMEOUT.equals(status);
    }

    @Override
    public R get() throws InterruptedException, ExecutionException {
        if (TaskStatus.FAILED.equals(status)) {
            throw new ExecutionException(cause);
        }
        return waitAndGetResult();
    }

    @Override
    public R get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (TaskStatus.FAILED.equals(status)) {
            throw new ExecutionException(cause);
        }
        return waitAndGetResult(timeout, unit);
    }
}
