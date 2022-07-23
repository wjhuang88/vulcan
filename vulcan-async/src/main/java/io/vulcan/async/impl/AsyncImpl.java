package io.vulcan.async.impl;

import io.vulcan.api.base.functional.Callback;
import io.vulcan.async.Async;
import io.vulcan.worker.WorkerPool;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

public class AsyncImpl implements Async {

    private final WorkerPool workerPool;

    public AsyncImpl(WorkerPool workerPool) {
        this.workerPool = workerPool;
    }

    @Override
    public <T> Mono<T> run(Callable<T> callable) {
        return Mono.create(sink -> workerPool.execute(callable::call, makeCallback(sink)));
    }

    @Override
    public Mono<Void> run(Runnable runnable) {
        return Mono.create(sink -> workerPool.execute(runnable::run, makeCallback(sink)));
    }

    @Override
    public <T> Flux<T> runMultiple(Collection<Callable<T>> callables) {
        if (callables == null || callables.isEmpty()) {
            return Flux.empty();
        }

        return Flux.mergeSequential(callables.stream().map(this::run).collect(Collectors.toList()));
    }

    @Override
    public <T> CompletionStage<T> stage(Callable<T> callable) {
        return workerPool.execute(callable::call);
    }

    @Override
    public CompletionStage<Void> stage(Runnable runnable) {
        return workerPool.execute(runnable::run);
    }

    @Override
    public <T> Future<T> future(Callable<T> callable) {
        return stage(callable).toCompletableFuture();
    }

    @Override
    public Future<Void> future(Runnable runnable) {
        return stage(runnable).toCompletableFuture();
    }

    private <R> Callback<R, Throwable> makeCallback(MonoSink<R> sink) {
        return new Callback<R, Throwable>() {
            @Override
            public void onSuccess(R sendResult) {
                sink.success(sendResult);
            }

            @Override
            public void onException(Throwable throwable) {
                sink.error(throwable);
            }
        };
    }
}
