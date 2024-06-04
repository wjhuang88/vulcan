package zone.hwj.vulcan.api.eventbus;

/**
 * 异步事件消费处理器接口
 * Add on 2021/01/20
 *
 * @param <T> 消费信息类型
 *
 * @author Huang.Wj
 * @since 1.0.12
 */
@FunctionalInterface
public interface ConsumerHandler<T> {

    /**
     * 异步事件处理方法
     * @param payload 事件数据对象
     */
    void handle(T payload) throws Throwable;
}
