package com.kay.protocol.privateprotocol.marshalling;

import com.kay.protocol.privateprotocol.coder.MarshallingFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Created by 3307 on 2016/3/5.
 */
public class MarshallingServer {
    public void bind(String ip,int port) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(MarshallingFactory.buildMarshallingDecoder());
                            ch.pipeline().addLast(MarshallingFactory.buildMarshallingEncoder());
                            ch.pipeline().addLast(new SubRespServerHandler());
                        }
                    });
            ChannelFuture f = b.bind(ip, port).sync();
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
        System.out.println("Netty server start ok : " + (ip + " : " + port));
    }

    public static void main(String[] args) {
        try {
            new MarshallingServer().bind("localhost",7393);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
