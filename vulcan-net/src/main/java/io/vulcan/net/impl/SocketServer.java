package io.vulcan.net.impl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketServer implements CloseHandler {

    private static final Logger log = LoggerFactory.getLogger(SocketServer.class);

    private static final Integer BACKLOG = 128;

    private final int port;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private final ChannelFuture channelFuture;

    protected SocketServer(int port, ChannelFuture channelFuture, EventLoopGroup bossGroup, EventLoopGroup workerGroup) {
        this.port = port;
        this.channelFuture = channelFuture;
        this.bossGroup = bossGroup;
        this.workerGroup = workerGroup;
    }

    public static SocketServer serve(int port, WorkerPool pool, ChannelHandler... chs) {

        log.info("Starting socket at port: {}", port);

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup(pool.maxSize(), pool.executor());

        final ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(makeInitializer(chs))
                .option(ChannelOption.SO_BACKLOG, BACKLOG)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        final ChannelFuture bind = b.bind(port);

        bind.addListener((ChannelFutureListener)f -> log.info("Socket created: {}", f.channel()));

        return new SocketServer(port, bind, bossGroup, workerGroup);
    }

    public static SocketServer serve(int port, ChannelHandler... chs) {
        return serve(port, WorkerPool.getDefault(), chs);
    }

    private static ChannelInitializer<SocketChannel> makeInitializer(ChannelHandler... chs) {
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

    public int getPort() {
        return port;
    }

    @Override
    public ChannelFuture closeAsync() {
        return channelFuture.channel().close().addListener((ChannelFutureListener) f -> {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            log.info("Socket closed: {}", f.channel());
        });
    }
}
