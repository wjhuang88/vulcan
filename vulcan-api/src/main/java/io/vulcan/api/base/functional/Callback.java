/*
 * Copyright (c) 2016. Runyi Co., Ltd. All rights reserved.
 */

package io.vulcan.api.base.functional;

/**
 * Created by GHuang on 2016/10/25.
 */
@FunctionalInterface
public interface Callback<R, E extends Throwable> {

    void onSuccess(final R sendResult);

    default void onException(E throwable) {
        throw new RuntimeException("Callback fail.", throwable);
    }
}
