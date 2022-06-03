package io.vulcan.net.echo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import javax.annotation.Nonnull;

public class EchoHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(@Nonnull ChannelHandlerContext ctx, @Nonnull Object msg) {
        ctx.write(msg);
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
