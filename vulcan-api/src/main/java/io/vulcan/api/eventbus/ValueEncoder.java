package io.vulcan.api.eventbus;

/**
 * 消息体值处理器接口 - 编码
 *
 * @author Huang.Wj
 */
@FunctionalInterface
public interface ValueEncoder<T> {

    /**
     * 消息体值编码逻辑
     * @param value 原始值
     * @return 编码后的值
     */
    byte[] encode(T value) throws Throwable;
}
