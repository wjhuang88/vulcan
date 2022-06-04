package io.vulcan.async;

import io.vulcan.api.base.functional.Callable;
import io.vulcan.async.impl.AsyncManagerImpl;
import io.vulcan.worker.WorkerPool;
import java.util.Collection;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Future;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AsyncManager {

    class Holder {
        private final static AsyncManagerImpl INSTANCE = new AsyncManagerImpl(WorkerPool.getDefault());
    }

    static AsyncManager getDefault() {
        return Holder.INSTANCE;
    }

    static AsyncManager create() {
        return new AsyncManagerImpl(WorkerPool.getDefault());
    }

    static AsyncManager create(WorkerPool pool) {
        return new AsyncManagerImpl(pool);
    }

    <T> Mono<T> run(Callable<T> callable);

    Mono<Void> run(Runnable runnable);

    <T> Flux<T> runMultiple(Collection<Callable<T>> callables);

    <T> CompletionStage<T> stage(Callable<T> callable);

    CompletionStage<Void> stage(Runnable runnable);

    <T> Future<T> future(Callable<T> callable);

    Future<Void> future(Runnable runnable);
}
