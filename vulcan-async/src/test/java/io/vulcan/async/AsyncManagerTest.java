package io.vulcan.async;

import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vulcan.api.base.functional.Callable;
import io.vulcan.worker.WorkerPool;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class AsyncManagerTest {

    final Executor executor;
    final AsyncManager asyncManager;

    AsyncManagerTest() {
        executor = WorkerPool.getInstance().executor();
        asyncManager = new AsyncManager(executor);
    }

    @Test
    void run() throws Throwable {
        Thread currentThread = Thread.currentThread();
        System.out.println("main thread: " + currentThread.getName());
        VertxTestContext testContext = new VertxTestContext();

        asyncManager.run(() -> {
            Thread innerThread = Thread.currentThread();
            System.out.println("inner thread: " + innerThread.getName());
            Assertions.assertNotEquals(currentThread.getId(), innerThread.getId());
            testContext.completeNow();
        }).block();
        Assertions.assertTrue(testContext.awaitCompletion(5, TimeUnit.SECONDS));
        if (testContext.failed()) {
            throw testContext.causeOfFailure();
        }
    }

    @Test
    void runCallable() throws Throwable {
        Thread currentThread = Thread.currentThread();
        System.out.println("main thread: " + currentThread.getName());
        VertxTestContext testContext = new VertxTestContext();

        String block = asyncManager.run(() -> {
            Thread innerThread = Thread.currentThread();
            System.out.println("inner thread: " + innerThread.getName());
            Assertions.assertNotEquals(currentThread.getId(), innerThread.getId());
            testContext.completeNow();
            return "async_result";
        }).block();
        Assertions.assertEquals("async_result", block);
        Assertions.assertTrue(testContext.awaitCompletion(5, TimeUnit.SECONDS));
        if (testContext.failed()) {
            throw testContext.causeOfFailure();
        }
    }

    @Test
    void runMultiple() {
        Thread currentThread = Thread.currentThread();
        System.out.println("main thread: " + currentThread.getName());

        List<Callable<String>> taskList = new ArrayList<>();
        taskList.add(() -> {
            Thread innerThread = Thread.currentThread();
            System.out.println("inner thread1: " + innerThread.getName());
            Assertions.assertNotEquals(currentThread.getId(), innerThread.getId());
            return "async_result1";
        });
        taskList.add(() -> {
            Thread innerThread = Thread.currentThread();
            System.out.println("inner thread2: " + innerThread.getName());
            Assertions.assertNotEquals(currentThread.getId(), innerThread.getId());
            return "async_result2";
        });
        taskList.add(() -> {
            Thread innerThread = Thread.currentThread();
            System.out.println("inner thread3: " + innerThread.getName());
            Assertions.assertNotEquals(currentThread.getId(), innerThread.getId());
            return "async_result3";
        });

        List<String> block = asyncManager.runMultiple(taskList).collectList().block();
        Assertions.assertNotNull(block);
        Assertions.assertArrayEquals(new String[]{"async_result1", "async_result2", "async_result3"}, block.toArray(new String[0]));

        List<String> block2 = asyncManager.runMultiple(() -> {
            Thread innerThread = Thread.currentThread();
            System.out.println("inner thread1: " + innerThread.getName());
            Assertions.assertNotEquals(currentThread.getId(), innerThread.getId());
            return "async_result4";
        }, () -> {
            Thread innerThread = Thread.currentThread();
            System.out.println("inner thread1: " + innerThread.getName());
            Assertions.assertNotEquals(currentThread.getId(), innerThread.getId());
            return "async_result5";
        }).collectList().block();

        Assertions.assertNotNull(block2);
        Assertions.assertArrayEquals(new String[]{"async_result4", "async_result5"}, block2.toArray(new String[0]));
    }

    @Test
    void stage() throws Throwable {
        Thread currentThread = Thread.currentThread();
        System.out.println("main thread: " + currentThread.getName());
        VertxTestContext testContext = new VertxTestContext();

        asyncManager.stage(() -> {
            Thread innerThread = Thread.currentThread();
            System.out.println("inner thread: " + innerThread.getName());
            Assertions.assertNotEquals(currentThread.getId(), innerThread.getId());
            testContext.completeNow();
        });

        Assertions.assertTrue(testContext.awaitCompletion(5, TimeUnit.SECONDS));
        if (testContext.failed()) {
            throw testContext.causeOfFailure();
        }
    }

    @Test
    void stageCallable() throws Throwable {
        Thread currentThread = Thread.currentThread();
        System.out.println("main thread: " + currentThread.getName());
        VertxTestContext testContext = new VertxTestContext();

        CompletionStage<String> stage = asyncManager.stage(() -> {
            Thread innerThread = Thread.currentThread();
            System.out.println("inner thread: " + innerThread.getName());
            Assertions.assertNotEquals(currentThread.getId(), innerThread.getId());
            return "async_result";
        });
        stage.whenComplete((r, e) -> {
            if (e != null) {
                testContext.failNow(e);
            } else {
                Assertions.assertEquals("async_result", r);
                testContext.completeNow();
            }
        });

        Assertions.assertTrue(testContext.awaitCompletion(5, TimeUnit.SECONDS));
        if (testContext.failed()) {
            throw testContext.causeOfFailure();
        }
    }

    @Test
    void future() throws Throwable {
        Thread currentThread = Thread.currentThread();
        System.out.println("main thread: " + currentThread.getName());
        VertxTestContext testContext = new VertxTestContext();

        @SuppressWarnings("unused")
        Future<Void> future = asyncManager.future(() -> {
            Thread innerThread = Thread.currentThread();
            System.out.println("inner thread: " + innerThread.getName());
            Assertions.assertNotEquals(currentThread.getId(), innerThread.getId());
            testContext.completeNow();
        });

        Assertions.assertTrue(testContext.awaitCompletion(5, TimeUnit.SECONDS));
        if (testContext.failed()) {
            throw testContext.causeOfFailure();
        }
    }

    @Test
    void futureCallable() throws Throwable {
        Thread currentThread = Thread.currentThread();
        System.out.println("main thread: " + currentThread.getName());

        Future<String> future = asyncManager.future(() -> {
            Thread innerThread = Thread.currentThread();
            System.out.println("inner thread: " + innerThread.getName());
            Assertions.assertNotEquals(currentThread.getId(), innerThread.getId());
            return "async_result";
        });
        String blockResult = future.get();
        Assertions.assertEquals("async_result", blockResult);
    }
}