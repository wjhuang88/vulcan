package io.vulcan.net.echo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nonnull;

public class EchoHandler extends ChannelInboundHandlerAdapter {

    public static final String APEX = "Replay: ";

    @Override
    public void channelRead(@Nonnull ChannelHandlerContext ctx, @Nonnull Object msg) {
        ByteBuf msgBuf = (ByteBuf) msg;
        try {
            ByteBuf stringBuffer = ctx.alloc().buffer();
            stringBuffer.writeCharSequence(APEX, StandardCharsets.UTF_8);
            stringBuffer.writeBytes(msgBuf);
            System.out.println("服务端接收到：" + stringBuffer);
            ctx.writeAndFlush(stringBuffer);
        } finally {
            msgBuf.release();
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
