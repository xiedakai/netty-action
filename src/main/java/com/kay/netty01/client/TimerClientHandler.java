package com.kay.netty01.client;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.io.UnsupportedEncodingException;


public class TimerClientHandler extends ChannelHandlerAdapter {

    private final ByteBuf byteBuf;

    public TimerClientHandler() {
        byte[] bytes = "this is nio client TimerClientHandler".getBytes();
        byteBuf = Unpooled.buffer(bytes.length);
        byteBuf.writeBytes(bytes);
    }

    /**
     * 客户端与服务端的tcp链路创建成功后 回调此方法，将byteBuf发送给服务端
     *
     * @param channelHandlerContext
     */
    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) {
        System.out.println("客户端与服务端的tcp链路创建成功...");
        //雷区 之前用的是channelHandlerContext.write()  消息没写出去 服务端一直没收到消息
        channelHandlerContext.writeAndFlush(byteBuf);
    }

    /**
     * 服务端返回应答后 回调此方法
     *
     * @param channelHandlerContext
     * @param object
     */
    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object object) {
        ByteBuf response = (ByteBuf) object;
        byte[] bytes = new byte[response.readableBytes()];
        response.readBytes(bytes);
        try {
            String str = new String(bytes, "utf-8");
            System.out.println("客户端收到消息：" + str);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.fireExceptionCaught(cause);
    }

}
