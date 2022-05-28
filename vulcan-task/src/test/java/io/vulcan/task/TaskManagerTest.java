package io.vulcan.task;

import io.vulcan.task.impl.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TaskManagerTest {
    final TaskManager manager;

    TaskManagerTest() {
        manager = TaskManager.getInstance();
    }

    @Test
    public void testRunTask() {
        System.out.println("Start");
        Task<String> task = manager.runTask(() -> {
            System.out.println("thread: " + Thread.currentThread().getName());
            Thread.sleep(2000);
            return "test run task";
        });
        task.onSuccess(result -> System.out.println("from onSuccess: " + result));
        String id = task.getId();

        String result = manager.waitAndGetResult(id);
        Assertions.assertEquals("test run task", result, "运行结果不一致");
        Assertions.assertEquals(task.getStatus(), TaskStatus.SUCCEEDED);

        Task<String> taskWithId = manager.runTask(taskId -> {
            System.out.println("task: " + taskId);
            return "test run task with id";
        });
        String id2 = taskWithId.getId();
        String result2 = manager.waitAndGetResult(id2);
        System.out.println(id2);
        Assertions.assertEquals("test run task with id", result2, "运行结果不一致");
        Assertions.assertEquals(taskWithId.getStatus(), TaskStatus.SUCCEEDED);
    }

    @Test
    public void testSyncRun() {
        // 任务中不需要使用taskId时
        Task<String> task = manager.runTaskInOrder(() -> {
            System.out.println("thread: " + Thread.currentThread().getName());
            Thread.sleep(2000);
            return "test run task";
        });
        task.onSuccess(result -> System.out.println("from onSuccess: " + result));
        String id = task.getId();

        String result = manager.waitAndGetResult(id);
        Assertions.assertEquals("test run task", result, "运行结果不一致");
        Assertions.assertEquals(task.getStatus(), TaskStatus.SUCCEEDED);

        // 任务中需要使用taskId时
        Task<String> taskWithId = manager.runTaskInOrder(taskId -> {
            System.out.println("task: " + taskId);
            return "test run task with id";
        });
        String id2 = taskWithId.getId();
        String result2 = manager.waitAndGetResult(id2);
        System.out.println(id2);
        Assertions.assertEquals("test run task with id", result2, "运行结果不一致");
        Assertions.assertEquals(taskWithId.getStatus(), TaskStatus.SUCCEEDED);

        for (int i = 0; i < 10; i++) {
            final int idx = i;
            Task<String> taskWithIdSync = manager.runTaskInOrder(taskId -> {
                System.out.println("sync task-" + idx + " : " + taskId);
                return "test sync run task with id " + idx;
            });
            System.out.println(taskWithIdSync.getId());
        }
    }
}