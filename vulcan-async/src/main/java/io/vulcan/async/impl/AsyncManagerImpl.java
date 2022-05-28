package io.vulcan.async.impl;

import io.vulcan.api.base.functional.Callable;
import io.vulcan.api.base.functional.Callback;
import io.vulcan.async.AsyncManager;
import io.vulcan.worker.WorkerPool;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Future;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class AsyncManagerImpl implements AsyncManager {

    private final WorkerPool workerPool;

    public AsyncManagerImpl(WorkerPool workerPool) {
        this.workerPool = workerPool;
    }

    @Override
    public <T> Mono<T> run(Callable<T> callable) {
        return Mono.create(sink -> stage(callable).whenComplete((r, e) -> {
            if (e != null) {
                sink.error(e);
            } else {
                sink.success(r);
            }
        }));
    }

    @Override
    public Mono<Void> run(Runnable runnable) {
        return Mono.create(sink -> stage(runnable).whenComplete((r, e) -> {
            if (e != null) {
                sink.error(e);
            } else {
                sink.success(r);
            }
        }));
    }

    @Override
    public <T> Flux<T> runMultiple(Collection<Callable<T>> callables) {
        if (callables == null || callables.isEmpty()) {
            return Flux.empty();
        }
        return Flux.create(sink -> {
            final int size = callables.size();

            @SuppressWarnings("rawtypes")
            final CompletableFuture[] futureArr = new CompletableFuture[size];

            int i = 0;
            for (Callable<T> callable : callables) {
                futureArr[i++] = stage(callable).thenAccept(sink::next).toCompletableFuture();
            }
            CompletableFuture.allOf(futureArr).whenComplete((r, e) -> {
                if (e != null) {
                    sink.error(e);
                } else {
                    sink.complete();
                }
            });
        });
    }

    @Override
    public <T> CompletionStage<T> stage(Callable<T> callable) {
        final CompletableFuture<T> completableFuture = new CompletableFuture<>();
        workerPool.execute(callable, new Callback<T, Throwable>() {
            @Override
            public void onSuccess(T sendResult) {
                completableFuture.complete(sendResult);
            }

            @Override
            public void onException(Throwable throwable) {
                completableFuture.completeExceptionally(throwable);
            }
        });
        return completableFuture;
    }

    @Override
    public CompletionStage<Void> stage(Runnable runnable) {
        final CompletableFuture<Void> completableFuture = new CompletableFuture<>();
        workerPool.execute(runnable::run, new Callback<Void, Throwable>() {
            @Override
            public void onSuccess(Void sendResult) {
                completableFuture.complete(null);
            }

            @Override
            public void onException(Throwable throwable) {
                completableFuture.completeExceptionally(throwable);
            }
        });
        return completableFuture;
    }

    @Override
    public <T> Future<T> future(Callable<T> callable) {
        return stage(callable).toCompletableFuture();
    }

    @Override
    public Future<Void> future(Runnable runnable) {
        return stage(runnable).toCompletableFuture();
    }
}
