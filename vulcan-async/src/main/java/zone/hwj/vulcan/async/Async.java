package zone.hwj.vulcan.async;

import zone.hwj.vulcan.async.impl.AsyncImpl;
import zone.hwj.vulcan.worker.WorkerPool;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Future;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface Async {

    class Holder {
        private final static AsyncImpl INSTANCE = new AsyncImpl(WorkerPool.getDefault());
    }

    static Async getDefault() {
        return Holder.INSTANCE;
    }

    static Async create() {
        return new AsyncImpl(WorkerPool.getDefault());
    }

    static Async create(WorkerPool pool) {
        return new AsyncImpl(pool);
    }

    <T> Mono<T> run(Callable<T> callable);

    Mono<Void> run(Runnable runnable);

    <T> Flux<T> runMultiple(Collection<Callable<T>> callables);

    <T> CompletionStage<T> stage(Callable<T> callable);

    CompletionStage<Void> stage(Runnable runnable);

    <T> Future<T> future(Callable<T> callable);

    Future<Void> future(Runnable runnable);
}
