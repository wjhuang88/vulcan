package io.vulcan.worker;

public final class WorkerPool {

    private WorkerPool() {}

    private static class Holder {
        private final static WorkerPool INSTANCE = new WorkerPool();
    }

    public static WorkerPool getInstance() {
        return Holder.INSTANCE;
    }

}
