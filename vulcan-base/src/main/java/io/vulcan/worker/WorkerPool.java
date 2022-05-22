package io.vulcan.worker;

import io.vulcan.worker.impl.WorkerPoolImpl;

public interface WorkerPool {

    class Holder {
        private final static WorkerPool INSTANCE = new WorkerPoolImpl();
    }

    static WorkerPool getInstance() {
        return Holder.INSTANCE;
    }

}
