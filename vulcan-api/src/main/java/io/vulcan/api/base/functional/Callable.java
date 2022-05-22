package io.vulcan.api.base.functional;

@FunctionalInterface
public interface Callable<R> {
    R call() throws Throwable;
}
