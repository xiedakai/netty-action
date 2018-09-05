package com.kay.packUnpacking.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class TimerServer {

    public static void main(String[] args) {
        int port=8006;
        new TimerServer().bind(port);
    }



    public  void bind(int port) {

        EventLoopGroup bossGroup=new NioEventLoopGroup();
        EventLoopGroup workerGroup=new NioEventLoopGroup();

        //nio服务端的启动类 目的降低nio开发难度
        ServerBootstrap serverBootstrap=new ServerBootstrap();

        serverBootstrap.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG,1024)
                //io时间处理类
                .childHandler(new ChildChannelHandler());
        ChannelFuture channelFuture= null;
        try {
            channelFuture = serverBootstrap.bind("127.0.0.1",8006).sync();
            //此方法进行阻塞 等服务端链路关闭后才退出main 函数
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    private  class ChildChannelHandler extends ChannelInitializer<SocketChannel>{

        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            socketChannel.pipeline().addLast(new TimerServerHandler());
        }
    }




}
