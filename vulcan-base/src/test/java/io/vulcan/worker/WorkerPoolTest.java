package io.vulcan.worker;

import static org.junit.jupiter.api.Assertions.*;

import io.vertx.junit5.VertxTestContext;
import io.vulcan.api.base.functional.Callable;
import io.vulcan.api.base.functional.Callback;
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

    @Test
    void executeFail() throws Throwable {

        VertxTestContext testContext = new VertxTestContext();

        Callback<String, Throwable> callback = new Callback<String, Throwable>() {
            @Override
            public void onSuccess(String sendResult) {
                System.out.println("onSuccess thread: " + Thread.currentThread());
                testContext.failNow("Should not run here");
            }

            @Override
            public void onException(Throwable throwable) {
                System.out.println("onException thread: " + Thread.currentThread());
                assertEquals(RuntimeException.class, throwable.getClass());
                assertEquals("test fail", throwable.getMessage());
                testContext.completeNow();
            }
        };

        pool.execute(() -> {
            System.out.println("fail thread: " + Thread.currentThread());
            throw new RuntimeException("test fail");
        }, callback);

        Assertions.assertTrue(testContext.awaitCompletion(5, TimeUnit.SECONDS));
        if (testContext.failed()) {
            throw testContext.causeOfFailure();
        }
    }
}