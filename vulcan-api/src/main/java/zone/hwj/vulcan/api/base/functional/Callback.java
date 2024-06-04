/*
 * Copyright (c) 2016. Runyi Co., Ltd. All rights reserved.
 */

package zone.hwj.vulcan.api.base.functional;

import java.util.concurrent.CompletableFuture;

/**
 * Created by GHuang on 2016/10/25.
 */
@FunctionalInterface
public interface Callback<R, E extends Throwable> {

    void onSuccess(final R sendResult);

    default void onException(E throwable) {
        throw new RuntimeException("Callback fail.", throwable);
    }

    static <R, E extends Throwable> Callback<R, E> make(CompletableFuture<R> future) {
        return new Callback<R, E>() {
            @Override
            public void onSuccess(R sendResult) {
                future.complete(sendResult);
            }

            @Override
            public void onException(E throwable) {
                future.completeExceptionally(throwable);
            }
        };
    }
}
