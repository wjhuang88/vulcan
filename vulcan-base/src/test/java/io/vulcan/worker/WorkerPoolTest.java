package io.vulcan.worker;

import static org.junit.jupiter.api.Assertions.*;

import io.vertx.junit5.VertxTestContext;
import io.vulcan.api.base.functional.Runnable;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class WorkerPoolTest {

    WorkerPool pool = WorkerPool.getInstance();

    @Test
    void executeRunnable() throws Throwable {
        VertxTestContext testContext = new VertxTestContext();
        pool.execute(() -> System.out.println("runnable thread: " + Thread.currentThread()), r -> {
            assertNull(r);
            testContext.completeNow();
        });

        Assertions.assertTrue(testContext.awaitCompletion(5, TimeUnit.SECONDS));
        if (testContext.failed()) {
            throw testContext.causeOfFailure();
        }
    }

    @Test
    void executeCallable() throws Throwable {
        VertxTestContext testContext = new VertxTestContext();
        pool.execute(() -> {
            System.out.println("callable thread: " + Thread.currentThread());
            return "test callable";
        }, r -> {
            assertEquals("test callable", r);
            testContext.completeNow();
        });

        Assertions.assertTrue(testContext.awaitCompletion(5, TimeUnit.SECONDS));
        if (testContext.failed()) {
            throw testContext.causeOfFailure();
        }
    }
}