package zone.hwj.vulcan.task;

public enum TaskStatus {
    /**
     * 任务已创建
     */
    CREATED,

    /**
     * 任务运行中
     */
    RUNNING,

    /**
     * 任务执行失败
     */
    FAILED,

    /**
     * 任务执行超时
     */
    TIMEOUT,

    /**
     * 任务被取消
     */
    CANCELED,

    /**
     * 任务执行成功
     */
    SUCCEEDED
}
