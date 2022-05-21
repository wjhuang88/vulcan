package io.vulcan.eventbus;

import io.vulcan.api.base.functional.Callback;
import io.vulcan.api.eventbus.Actions;
import io.vulcan.api.eventbus.ConsumerHandler;
import io.vulcan.api.eventbus.EventStrategy;
import io.vulcan.eventbus.strategy.LocalEventStrategy;
import io.vulcan.utils.StringUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import javax.annotation.Nullable;

/**
 * 异步事件总线，统一处理各种通道的异步事件和异步消息
 * Add on 2021/01/20
 *
 * @author Huang.Wj
 * @since 1.0.12
 */
public final class EventBus {

    private static final String LOCAL_SCHEMA = "local";

    public EventBus() {
        registerEventStrategy(LOCAL_SCHEMA, new LocalEventStrategy());
    }

    private final Map<String, EventStrategy> strategyRouter = new HashMap<>();

    /**
     * 注册事件处理策略类
     * @param schema 事件协议字段
     * @param strategy 策略实现类
     */
    public void registerEventStrategy(final String schema, final EventStrategy strategy) {
        strategyRouter.put(schema, strategy);
    }

    /**
     * 注册事件消费订阅处理器
     * @param router 事件路由
     * @param clazz 事件数据类型对象
     * @param handler 消费处理器
     * @param <T> 事件数据类型
     */
    public <T> void listen(final String router, final Class<T> clazz, final ConsumerHandler<T> handler) {
        final StrategyMeta meta = checkAndGetStrategy(router);
        if (!meta.strategy.supportedActions().contains(Actions.CONSUME)) {
            throw new RuntimeException("Cannot register message handler through channel <" + meta.schema + ">, operation not supported");
        }
        meta.strategy.consumer(meta.topic, clazz, handler);
    }

    /**
     * 发送异步事件到单个订阅者
     * @param router 事件路由地址
     * @param clazz 事件数据类型对象
     * @param payload 事件数据对象
     * @param <T> 事件数据类型
     * @return 发送结果的Future对象
     */
    public <T> Future<T> send(final String router, final Class<T> clazz, final T payload) {
        CompletableFuture<T> future = new CompletableFuture<>();
        Callback<T, Throwable> callback = new Callback<T, Throwable>() {
            @Override
            public void onSuccess(T sendResult) {
                future.complete(sendResult);
            }

            @Override
            public void onException(Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        };
        send(router, clazz, payload, callback);
        return future;
    }

    /**
     * 发送异步事件到单个订阅者
     * @param router 事件路由地址
     * @param clazz 事件数据类型对象
     * @param payload 事件数据对象
     * @param callback 回调方法
     * @param <T> 事件数据类型
     */
    public <T> void send(final String router, final Class<T> clazz, final T payload, Callback<T, Throwable> callback) {
        final StrategyMeta meta = checkAndGetStrategy(router);
        if (!meta.strategy.supportedActions().contains(Actions.SEND)) {
            throw new RuntimeException("Cannot send message through channel <" + meta.schema + ">, operation not supported");
        }
        meta.strategy.send(meta.topic, clazz, payload, callback);
    }

    /**
     * 发送异步事件到所有订阅者
     * @param router 事件路由地址
     * @param clazz 事件数据类型对象
     * @param payload 事件数据对象
     * @param <T> 事件数据类型
     */
    public <T> Future<T> publish(final String router, final Class<T> clazz, final T payload) {
        CompletableFuture<T> future = new CompletableFuture<>();
        Callback<T, Throwable> callback = new Callback<T, Throwable>() {
            @Override
            public void onSuccess(T sendResult) {
                future.complete(sendResult);
            }

            @Override
            public void onException(Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        };
        publish(router, clazz, payload, callback);
        return future;
    }

    /**
     * 发送异步事件到所有订阅者
     * @param router 事件路由地址
     * @param clazz 事件数据类型对象
     * @param payload 事件数据对象
     * @param callback 回调方法
     * @param <T> 事件数据类型
     */
    public <T> void publish(final String router, final Class<T> clazz, final T payload, Callback<T, Throwable> callback) {
        final StrategyMeta meta = checkAndGetStrategy(router);
        if (!meta.strategy.supportedActions().contains(Actions.PUBLISH)) {
            throw new RuntimeException("Cannot publish message through channel <" + meta.schema + ">, operation not supported");
        }
        meta.strategy.publish(meta.topic, clazz, payload, callback);
    }

    /**
     * 判断是否存在路由模式
     * @param schema 路由模式名
     * @return 判断结果
     */
    public boolean hasSchema(String schema) {
        return strategyRouter.containsKey(schema);
    }

    /**
     * 关闭服务
     */
    void close() throws Exception {
        for (EventStrategy strategy : strategyRouter.values()) {
            strategy.close();
        }
    }

    static class StrategyMeta {
        final EventStrategy strategy;
        final String schema;
        final String topic;

        public StrategyMeta(EventStrategy strategy, String schema, String topic) {
            this.strategy = strategy;
            this.schema = schema;
            this.topic = topic;
        }
    }

    private StrategyMeta checkAndGetStrategy(@Nullable final String router) {
        if (StringUtils.isNullOrEmpty(router)) {
            throw new RuntimeException("Event router is null. Please provide a topic router");
        }
        final String[] splitRouter = router.split("://", 2);
        final String schema, topic;
        if (splitRouter.length < 2) {
            schema = LOCAL_SCHEMA;
            topic = router;
        } else {
            schema = splitRouter[0];
            topic = splitRouter[1];
        }
        final EventStrategy strategy = strategyRouter.get(schema);
        if (strategy == null) {
            throw new RuntimeException("Event schema: " + schema + " is not registered.");
        }
        return new StrategyMeta(strategy, schema, topic);
    }
}
