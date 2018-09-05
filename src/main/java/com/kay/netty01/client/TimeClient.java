package com.kay.netty01.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TimeClient {

    public static void main(String[] args) {

        new TimeClient().connetct("127.0.0.1",8006);
    }

    public  void connetct(String host,int port) {

        EventLoopGroup eventLoopGroup=new NioEventLoopGroup();
        Bootstrap bootstrap=new Bootstrap();

        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
        .option(ChannelOption.TCP_NODELAY,true).handler(
                new ChannelInitializer<SocketChannel>() {
                    /**
                     * 初始化NioSocketChannel时 将channelHandler设置到channelPipeline，
                     * 用于处理网络事件
                     * @param socketChannel
                     * @throws Exception
                     */
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new TimerClientHandler());
                    }
                }
        );
        try {
            // bootstrap.connect()是异步连接  加上sync()是同步连接
            ChannelFuture channelFuture=bootstrap.connect(host,port).sync();
            //当等待客户端连接关闭
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            //当客户端连接关闭后 退出main函数,释放nio线程组
            eventLoopGroup.shutdownGracefully();
        }
    }
}
