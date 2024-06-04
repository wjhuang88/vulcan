package zone.hwj.vulcan.net;

import io.netty.channel.ChannelFuture;

public interface CloseHandler extends AutoCloseable {
    ChannelFuture closeAsync();

    default void close() {
        closeAsync().syncUninterruptibly();
    }
}
