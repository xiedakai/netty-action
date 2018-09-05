package com.kay.protocol.privateprotocol.marshalling;

import com.kay.protocol.privateprotocol.coder.MarshallingFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by 3307 on 2016/3/5.
 */
public class MarshallingClient {
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    EventLoopGroup group = new NioEventLoopGroup();

    public void connect( String host,int port) throws Exception {
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(MarshallingFactory.buildMarshallingDecoder());
                            ch.pipeline().addLast(MarshallingFactory.buildMarshallingEncoder());
                            ch.pipeline().addLast(new SubReqServerHandler());

                        }
                    });
            //发起异步操作
            ChannelFuture future = b.connect(host,port).sync();
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        try {
            new MarshallingClient().connect("localhost",7393);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
