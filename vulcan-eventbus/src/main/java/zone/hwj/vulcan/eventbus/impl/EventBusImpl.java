package zone.hwj.vulcan.eventbus.impl;

import zone.hwj.vulcan.api.base.functional.Callback;
import zone.hwj.vulcan.api.eventbus.Actions;
import zone.hwj.vulcan.api.eventbus.ConsumerHandler;
import zone.hwj.vulcan.api.eventbus.EventStrategy;
import zone.hwj.vulcan.eventbus.EventBus;
import zone.hwj.vulcan.utils.StringUtils;
import zone.hwj.vulcan.worker.WorkerPool;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nullable;

/**
 * 异步事件总线，统一处理各种通道的异步事件和异步消息
 * Add on 2021/01/20
 *
 * @author Huang.Wj
 * @since 1.0.12
 */
public final class EventBusImpl implements EventBus {

    private static final String LOCAL_SCHEMA = "local";

    private final AtomicBoolean closed = new AtomicBoolean();

    public EventBusImpl() {
        this(WorkerPool.getDefault());
    }

    public EventBusImpl(WorkerPool pool) {
        registerEventStrategy(LOCAL_SCHEMA, new LocalEventStrategy(pool));
        closed.getAndSet(false);
    }

    private final Map<String, EventStrategy> strategyRouter = new HashMap<>();

    @Override
    public void registerEventStrategy(final String schema, final EventStrategy strategy) {
        strategyRouter.put(schema, strategy);
    }

    @Override
    public <T> void listen(final String router, final Class<T> clazz, final ConsumerHandler<T> handler) {
        checkClosed();
        final StrategyMeta meta = checkAndGetStrategy(router);
        if (!meta.strategy.supportedActions().contains(Actions.CONSUME)) {
            throw new RuntimeException("Cannot register message handler through channel <" + meta.schema + ">, operation not supported");
        }
        meta.strategy.consumer(meta.topic, clazz, handler);
    }

    @Override
    public <T> Future<T> send(final String router, final Class<T> clazz, final T payload) {
        checkClosed();
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

    @Override
    public <T> void send(final String router, final Class<T> clazz, final T payload, Callback<T, Throwable> callback) {
        checkClosed();
        final StrategyMeta meta = checkAndGetStrategy(router);
        if (!meta.strategy.supportedActions().contains(Actions.SEND)) {
            throw new RuntimeException("Cannot send message through channel <" + meta.schema + ">, operation not supported");
        }
        meta.strategy.send(meta.topic, clazz, payload, callback);
    }

    @Override
    public <T> Future<T> publish(final String router, final Class<T> clazz, final T payload) {
        checkClosed();
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

    @Override
    public <T> void publish(final String router, final Class<T> clazz, final T payload, Callback<T, Throwable> callback) {
        checkClosed();
        final StrategyMeta meta = checkAndGetStrategy(router);
        if (!meta.strategy.supportedActions().contains(Actions.PUBLISH)) {
            throw new RuntimeException("Cannot publish message through channel <" + meta.schema + ">, operation not supported");
        }
        meta.strategy.publish(meta.topic, clazz, payload, callback);
    }

    @Override
    public boolean hasSchema(String schema) {
        return strategyRouter.containsKey(schema);
    }

    /**
     * 关闭服务
     */
    @Override
    public void close() throws Exception {
        for (EventStrategy strategy : strategyRouter.values()) {
            strategy.close();
        }
        closed.getAndSet(true);
    }

    void checkClosed() {
        if (closed.get()) {
            throw new IllegalStateException("EventBus instance is closed");
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
