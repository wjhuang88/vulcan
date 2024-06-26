package zone.hwj.vulcan.eventbus.impl;

import zone.hwj.vulcan.api.eventbus.ConsumerHandler;
import zone.hwj.vulcan.api.eventbus.ErrorHandler;
import javax.annotation.Nonnull;

public final class LocalConsumerMeta<T> {

    @Nonnull
    final String router;

    @Nonnull
    final ConsumerHandler<T> handler;

    @Nonnull
    final ErrorHandler errorHandler;

    @Nonnull
    final Class<T> clazz;

    public LocalConsumerMeta(
            @Nonnull String router,
            @Nonnull ConsumerHandler<T> handler,
            @Nonnull ErrorHandler errorHandler,
            @Nonnull Class<T> clazz) {
        this.router = router;
        this.handler = handler;
        this.errorHandler = errorHandler;
        this.clazz = clazz;
    }
}
