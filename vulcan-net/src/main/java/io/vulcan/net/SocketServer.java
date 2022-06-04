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
import io.vulcan.net.echo.EchoHandler;
import io.vulcan.worker.WorkerPool;
import org.jetbrains.annotations.NotNull;

public class SocketServer {
    private final int port;

    public SocketServer(int port) {
        this.port = port;
    }

    public void start() throws InterruptedException {
        final WorkerPool workerPool = WorkerPool.getDefault();
        final EventLoopGroup bossGroup = new NioEventLoopGroup();
        final EventLoopGroup workerGroup = new NioEventLoopGroup(workerPool.maxSize(), workerPool.executor());
        try {
            final ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(makeInitializer(new EchoHandler()))
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            final ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
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

//    public static void main(String[] args) throws Exception {
//        int port = 8080;
//        if (args.length > 0) {
//            port = Integer.parseInt(args[0]);
//        }
//
//        new SocketServer(port).start();
//    }
}
