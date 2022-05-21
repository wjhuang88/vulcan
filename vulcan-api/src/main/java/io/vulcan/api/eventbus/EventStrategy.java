package io.vulcan.api.eventbus;

import io.vulcan.api.base.functional.Callback;
import java.util.EnumSet;
import java.util.Set;

/**
 * 异步事件处理策略接口
 * Add on 2021/01/20
 *
 * @author Huang.Wj
 * @since 1.0.12
 */
public interface EventStrategy extends AutoCloseable {

    /**
     * 注册事件消费订阅处理器
     * @param router 事件路由地址
     * @param clazz 事件数据类型对象
     * @param handler 事件消费处理器
     * @param <T> 事件数据类型
     */
    <T> void consumer(String router, Class<T> clazz, ConsumerHandler<T> handler);

    /**
     * 注册事件消费订阅处理器
     * @param router 事件路由地址
     * @param clazz 事件数据类型对象
     * @param handler 事件消费处理器
     * @param <T> 事件数据类型
     */
    <T> void consumer(String router, Class<T> clazz, ConsumerHandler<T> handler, ErrorHandler errorHandler);

    /**
     * 发送异步事件到单个订阅者
     * @param router 事件路由地址
     * @param clazz 事件数据类型对象
     * @param payload 事件数据对象
     * @param <T> 事件数据类型
     */
    <T> void send(String router, Class<T> clazz, T payload);

    /**
     * 发送异步事件到单个订阅者
     * @param router 事件路由地址
     * @param clazz 事件数据类型对象
     * @param payload 事件数据对象
     * @param <T> 事件数据类型
     */
    <T> void send(String router, Class<T> clazz, T payload, Callback<T, Throwable> result);

    /**
     * 发送异步事件到所有订阅者
     * @param router 事件路由地址
     * @param clazz 事件数据类型对象
     * @param payload 事件数据对象
     * @param <T> 事件数据类型
     */
    <T> void publish(String router, Class<T> clazz, T payload);

    /**
     * 发送异步事件到所有订阅者
     * @param router 事件路由地址
     * @param clazz 事件数据类型对象
     * @param payload 事件数据对象
     * @param <T> 事件数据类型
     */
    <T> void publish(String router, Class<T> clazz, T payload, Callback<T, Throwable> result);

    default <T, R> void request(String router, Class<T> clazz, T payload, Callback<R, Exception> callback) {

    }

    // 操作全集
    Set<Actions> allActions = EnumSet.allOf(Actions.class);

    /**
     * 返回支持的动作
     * @return 支持的动作
     */
    default Set<Actions> supportedActions() {
        return allActions;
    }
}
