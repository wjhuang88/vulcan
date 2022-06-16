package io.vulcan.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.vulcan.worker.WorkerPool;
import org.jetbrains.annotations.NotNull;

public class SocketServer {

    private final int port;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;

    public SocketServer(int port, WorkerPool pool) {
        this.port = port;
        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup(pool.maxSize(), pool.executor());
    }

    public void start(ChannelHandler... chs) throws InterruptedException {
        try {
            final ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(makeInitializer(chs))
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            final ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public CloseHandler startAsync(ChannelHandler... chs) {
        final ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(makeInitializer(chs))
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        final ChannelFuture f = b.bind(port);
        return new CloseHandler(f);
    }

    private ChannelInitializer<SocketChannel> makeInitializer(ChannelHandler... chs) {
        if (null == chs || 0 == chs.length) {
            throw new IllegalArgumentException("Initializer channel handler is null");
        }
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(@NotNull SocketChannel ch) throws Exception {
                ch.pipeline().addLast(chs);
            }
        };
    }

    public class CloseHandler implements AutoCloseable {
        private final ChannelFuture f;
        private
        CloseHandler(ChannelFuture f) {
            this.f = f;
        }

        @Override
        public void close() throws InterruptedException {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            f.channel().closeFuture().sync();
        }
    }
}
