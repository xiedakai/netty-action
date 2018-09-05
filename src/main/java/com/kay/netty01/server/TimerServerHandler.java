package com.kay.netty01.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class TimerServerHandler extends ChannelHandlerAdapter{

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf=(ByteBuf)msg;
        byte[] bytes=new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        String str=new String(bytes,"utf-8");
        System.out.println("netty server 读取到的内容： "+str);

        //String response="this is netty server response, 客户端发送的消息是："+str;
        String response="this is netty server response, 客户端发送的消息是："+str;
        ByteBuf responseBuf= Unpooled.copiedBuffer(response.getBytes()) ;
        System.out.println("netty server 正在写出内容： "+response);
        ctx.write(responseBuf);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        /**
         * ctx.flush()将消息队列中消息写入到 socketChannel发送给客户端，
         * 从性能的角度考虑 为了频繁的唤醒selector netty的write方法并不直接将消息write到socketchannel
         * 调用write方法只是将消息发送到缓冲数组, 在通过flush()，将缓冲数组的消息写到socketchannel
         */
        ctx.flush();
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端连接成功 ");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
