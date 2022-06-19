package io.vulcan.net.impl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.vulcan.net.CloseHandler;
import io.vulcan.worker.WorkerPool;
import org.jetbrains.annotations.NotNull;

public class SocketServer {

    private static final Integer BACKLOG = 128;

    private final int port;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;

    public SocketServer(int port) {
        this(port, WorkerPool.getDefault());
    }

    public SocketServer(int port, WorkerPool pool) {
        this.port = port;
        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup(pool.maxSize(), pool.executor());
    }

    public CloseHandler start(ChannelHandler... chs) {
        final ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(makeInitializer(chs))
                .option(ChannelOption.SO_BACKLOG, BACKLOG)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        final ChannelFuture f = b.bind(port);
        return new InternalCloseHandler(f);
    }

    private ChannelInitializer<SocketChannel> makeInitializer(ChannelHandler... chs) {
        if (null == chs || 0 == chs.length) {
            throw new IllegalArgumentException("Initializer channel handler is null");
        }
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(@NotNull SocketChannel ch) {
                ch.pipeline().addLast(chs);
            }
        };
    }

    private class InternalCloseHandler implements CloseHandler {
        private final ChannelFuture f;
        private InternalCloseHandler(ChannelFuture f) {
            this.f = f;
        }

        @Override
        public ChannelFuture closeAsync() {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            return f.channel().closeFuture();
        }
    }
}
