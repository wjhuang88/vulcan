package io.vulcan.worker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.vertx.junit5.VertxTestContext;
import io.vulcan.api.base.functional.Callback;
import io.vulcan.worker.WorkerPool.CallableHandler;
import io.vulcan.worker.WorkerPool.RunnableHandler;
import io.vulcan.worker.impl.WorkerPoolImpl;
import java.util.concurrent.RejectedExecutionException;
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

    @Test
    void failHandlerTest1() throws Throwable {

        WorkerPool pool = WorkerPool.getInstance((CallableHandler) callable -> System.out.println("Fail handler(callable) thread: " + Thread.currentThread()));

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
    void failHandlerTest2() throws Throwable {

        WorkerPool pool = WorkerPool.getInstance((RunnableHandler) runnable -> System.out.println("Fail handler(runnable) thread: " + Thread.currentThread()));

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
    void failHandlerTest3() throws Throwable {

        WorkerPool pool = WorkerPool.getInstance(
                runnable -> System.out.println("Fail handler(runnable) thread: " + Thread.currentThread()),
                callable -> System.out.println("Fail handler(callable) thread: " + Thread.currentThread())
        );

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

    WorkerPoolImpl mockPool = new WorkerPoolImpl(1, 1, 0L, TimeUnit.MILLISECONDS, 1);


    @Test
    void failHandlerTest4() throws Throwable {

        VertxTestContext testContext = new VertxTestContext();

        Callback<Void, Throwable> callback = new Callback<Void, Throwable>() {
            @Override
            public void onSuccess(Void sendResult) {
                assertNull(sendResult);
            }

            @Override
            public void onException(Throwable throwable) {
                System.out.println("onException thread: " + Thread.currentThread());
                assertEquals(RejectedExecutionException.class, throwable.getClass());
                testContext.completeNow();
            }
        };

        mockPool.execute(() -> {
            System.out.println("runnable thread1: " + Thread.currentThread());
            Thread.sleep(200);
        }, callback);

        mockPool.execute(() -> {
            System.out.println("runnable thread2: " + Thread.currentThread());
            Thread.sleep(200);
        }, callback);

        mockPool.execute(() -> {
            System.out.println("runnable thread3: " + Thread.currentThread());
            Thread.sleep(200);
        }, callback);

        mockPool.execute(() -> {
            System.out.println("runnable thread4: " + Thread.currentThread());
            Thread.sleep(200);
        }, callback);

        Assertions.assertTrue(testContext.awaitCompletion(5, TimeUnit.SECONDS));
        if (testContext.failed()) {
            throw testContext.causeOfFailure();
        }
    }

    @Test
    void failHandlerTest5() throws Throwable {

        VertxTestContext testContext = new VertxTestContext();

        mockPool.onRejected((RunnableHandler) runnable -> {
            System.out.println("Fail handler(runnable) thread: " + Thread.currentThread());
            testContext.completeNow();
        });

        mockPool.execute(() -> {
            System.out.println("runnable thread1: " + Thread.currentThread());
            Thread.sleep(200);
        }, Assertions::assertNull);

        mockPool.execute(() -> {
            System.out.println("runnable thread2: " + Thread.currentThread());
            Thread.sleep(200);
        }, Assertions::assertNull);

        mockPool.execute(() -> {
            System.out.println("runnable thread3: " + Thread.currentThread());
            Thread.sleep(200);
        }, Assertions::assertNull);

        mockPool.execute(() -> {
            System.out.println("runnable thread4: " + Thread.currentThread());
            Thread.sleep(200);
        }, Assertions::assertNull);

        Assertions.assertTrue(testContext.awaitCompletion(5, TimeUnit.SECONDS));
        if (testContext.failed()) {
            throw testContext.causeOfFailure();
        }
    }
}