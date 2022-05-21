package io.vulcan.api.eventbus;

/**
 * 异步事件异常处理器接口
 * Add on 2021/01/20
 *
 * @author Huang.Wj
 * @since 1.0.12
 */
@FunctionalInterface
public interface ErrorHandler {

    /**
     * 异步事件异常处理方法
     * @param cause 事件异常对象
     */
    void handle(Throwable cause);
}
