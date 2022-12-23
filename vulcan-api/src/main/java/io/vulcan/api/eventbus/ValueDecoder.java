package io.vulcan.api.eventbus;

/**
 * 消息体值处理器接口 - 解码
 *
 * @author Huang.Wj
 */
@FunctionalInterface
public interface ValueDecoder<T> {

    /**
     * 消息体值解码逻辑
     * @param bytes 字节数组
     * @param clazz 值类型
     * @return 实际值
     */
    T decode(byte[] bytes, Class<T> clazz) throws Throwable;
}
