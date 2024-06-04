package zone.hwj.vulcan.task.impl;

public abstract class LoopTask<R> extends Task<R> {

    protected LoopTask(String id) {
        super(id);
    }

    @Override
    public R waitAndGetResult() {
        throw new UnsupportedOperationException("This task is a loop task");
    }
}
