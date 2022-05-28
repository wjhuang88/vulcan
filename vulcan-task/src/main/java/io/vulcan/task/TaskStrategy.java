package io.vulcan.task;

import io.vulcan.task.impl.Task;

public interface TaskStrategy {

    void start(Task<?> task);
    void startInOrder(Task<?> task);
    void loop(Task<?> task);

    <R> Task<R> getTask(String id);

    void remove(String id);

    void clear();
}
