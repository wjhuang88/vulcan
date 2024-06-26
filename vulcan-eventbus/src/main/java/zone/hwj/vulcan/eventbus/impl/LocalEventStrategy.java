package zone.hwj.vulcan.eventbus.impl;

import com.lmax.disruptor.dsl.Disruptor;
import zone.hwj.vulcan.api.base.functional.Callback;
import zone.hwj.vulcan.api.eventbus.Actions;
import zone.hwj.vulcan.api.eventbus.ConsumerHandler;
import zone.hwj.vulcan.api.eventbus.ErrorHandler;
import zone.hwj.vulcan.api.eventbus.EventStrategy;
import zone.hwj.vulcan.utils.JsonUtils;
import zone.hwj.vulcan.utils.StringUtils;
import zone.hwj.vulcan.worker.WorkerPool;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 本地eventbus实现事件处理
 * Add on 2021/1/20.
 * @author Huang.Wj
 */
public class LocalEventStrategy implements EventStrategy {

    private static final int BUFFER_SIZE = 1024;

    private final Logger log = LoggerFactory.getLogger(LocalEventStrategy.class);

    private final ConcurrentMap<String, LocalConsumerMeta<?>> consumerMap = new ConcurrentHashMap<>();
    private final Set<Actions> actions = EnumSet.of(Actions.SEND, Actions.CONSUME);

    private final Disruptor<BytesEvent> disruptor;

    @SuppressWarnings("deprecation")
    LocalEventStrategy(WorkerPool pool) {
        disruptor = new Disruptor<>(BytesEvent::new, BUFFER_SIZE, pool.executor());
        disruptor.handleEventsWith((event, sequence, endOfBatch) -> {
            if (null == event || StringUtils.isNullOrEmpty(event.getRouter())) {
                return;
            }
            handleConsume(event.getRouter(), event.getData());
        });
        disruptor.start();
    }

    private <T> void handleConsume(final String router, final byte[] data) {
        @SuppressWarnings("unchecked")
        final LocalConsumerMeta<T> meta = (LocalConsumerMeta<T>) consumerMap.get(router);
        if (null == meta) {
            return;
        }
        try {
            T decoded = decodeValue(data, meta.clazz, JsonUtils::decode);
            meta.handler.handle(decoded);
        } catch (Throwable e) {
            meta.errorHandler.handle(e);
        }
    }

    @Override
    public <T> void consumer(final String router, final Class<T> clazz, final ConsumerHandler<T> handler) {
        consumer(router, clazz, handler, e -> log.error("Received message from local topic: " + router + ", but fail on handling", e));
    }

    @Override
    public <T> void consumer(final String router, final Class<T> clazz, final ConsumerHandler<T> handler, final ErrorHandler errorHandler) {
        consumerMap.put(router, new LocalConsumerMeta<>(router, handler, errorHandler, clazz));
    }

    @Override
    public <T> void send(final String router, final Class<T> clazz, final T payload) {
        disruptor.publishEvent((event, sequence) -> {
            event.setRouter(router);
            try {
                event.setData(encodeValue(payload, clazz, JsonUtils::encodeToBytes));
            } catch (Throwable e) {
                throw new IllegalArgumentException("Payload cannot be encoded to json bytes.", e);
            }
        });
    }

    @Override
    public <T> void send(final String router, final Class<T> clazz, final T payload, final Callback<T, Throwable> result) {
        disruptor.publishEvent((event, sequence) -> {
            event.setRouter(router);
            try {
                event.setData(encodeValue(payload, clazz, JsonUtils::encodeToBytes));
                result.onSuccess(payload);
            } catch (Throwable e) {
                result.onException(e);
            }
        });
    }

    @Override
    public <T> void publish(final String router, final Class<T> clazz, final T payload) {
        throw new RuntimeException("Local channel publish() logic not implemented, please use send() method");
    }

    @Override
    public <T> void publish(final String router, final Class<T> clazz, final T payload, final Callback<T, Throwable> result) {
        throw new RuntimeException("Local channel publish() logic not implemented, please use send() method");
    }

    @Override
    public Set<Actions> supportedActions() {
        return actions;
    }

    @Override
    public void close() {
        consumerMap.clear();
        disruptor.shutdown();
    }
}
