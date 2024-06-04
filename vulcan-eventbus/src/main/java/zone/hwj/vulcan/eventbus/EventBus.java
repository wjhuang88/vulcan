package zone.hwj.vulcan.eventbus;

import zone.hwj.vulcan.api.base.functional.Callback;
import zone.hwj.vulcan.api.eventbus.ConsumerHandler;
import zone.hwj.vulcan.api.eventbus.EventStrategy;
import zone.hwj.vulcan.eventbus.impl.EventBusImpl;
import zone.hwj.vulcan.worker.WorkerPool;
import java.util.concurrent.Future;

/**
 * 异步事件总线，统一处理各种通道的异步事件和异步消息
 *
 * @author Huang.Wj
 */
public interface EventBus extends AutoCloseable {

    class Holder {
        private static final EventBusImpl INSTANCE = new EventBusImpl();
    }

    static EventBus getDefault() {
        return Holder.INSTANCE;
    }

    static EventBus create() {
        return new EventBusImpl();
    }

    static EventBus create(WorkerPool pool) {
        return new EventBusImpl(pool);
    }

    /**
     * 注册事件处理策略类
     * @param schema 事件协议字段
     * @param strategy 策略实现类
     */
    void registerEventStrategy(final String schema, final EventStrategy strategy);

    /**
     * 注册事件消费订阅处理器
     * @param router 事件路由
     * @param clazz 事件数据类型对象
     * @param handler 消费处理器
     * @param <T> 事件数据类型
     */
    <T> void listen(final String router, final Class<T> clazz, final ConsumerHandler<T> handler);

    /**
     * 发送异步事件到单个订阅者
     * @param router 事件路由地址
     * @param clazz 事件数据类型对象
     * @param payload 事件数据对象
     * @param <T> 事件数据类型
     * @return 发送结果的Future对象
     */
    <T> Future<T> send(final String router, final Class<T> clazz, final T payload);

    /**
     * 发送异步事件到单个订阅者
     * @param router 事件路由地址
     * @param clazz 事件数据类型对象
     * @param payload 事件数据对象
     * @param callback 回调方法
     * @param <T> 事件数据类型
     */
    <T> void send(final String router, final Class<T> clazz, final T payload, Callback<T, Throwable> callback);

    /**
     * 发送异步事件到所有订阅者
     * @param router 事件路由地址
     * @param clazz 事件数据类型对象
     * @param payload 事件数据对象
     * @param <T> 事件数据类型
     */
    <T> Future<T> publish(final String router, final Class<T> clazz, final T payload);

    /**
     * 发送异步事件到所有订阅者
     * @param router 事件路由地址
     * @param clazz 事件数据类型对象
     * @param payload 事件数据对象
     * @param callback 回调方法
     * @param <T> 事件数据类型
     */
    <T> void publish(final String router, final Class<T> clazz, final T payload, Callback<T, Throwable> callback);

    /**
     * 判断是否存在路由模式
     * @param schema 路由模式名
     * @return 判断结果
     */
    boolean hasSchema(String schema);
}
