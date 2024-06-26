package zone.hwj.vulcan.async;

import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class AsyncTest {

    final Async async;

    AsyncTest() {
        async = Async.getDefault();
    }

    @Test
    void run() throws Throwable {
        Thread currentThread = Thread.currentThread();
        System.out.println("main thread: " + currentThread.getName());
        VertxTestContext testContext = new VertxTestContext();

        async.run(() -> {
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

        String block = async.run(() -> {
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

        List<String> block = async.runMultiple(taskList).collectList().block();
        Assertions.assertNotNull(block);
        Assertions.assertArrayEquals(new String[]{"async_result1", "async_result2", "async_result3"}, block.toArray(new String[0]));
    }

    @Test
    void stage() throws Throwable {
        Thread currentThread = Thread.currentThread();
        System.out.println("main thread: " + currentThread.getName());
        VertxTestContext testContext = new VertxTestContext();

        async.stage(() -> {
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

        CompletionStage<String> stage = async.stage(() -> {
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
        Future<Void> future = async.future(() -> {
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

        Future<String> future = async.future(() -> {
            Thread innerThread = Thread.currentThread();
            System.out.println("inner thread: " + innerThread.getName());
            Assertions.assertNotEquals(currentThread.getId(), innerThread.getId());
            return "async_result";
        });
        String blockResult = future.get();
        Assertions.assertEquals("async_result", blockResult);
    }
}