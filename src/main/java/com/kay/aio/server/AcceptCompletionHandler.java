package com.kay.aio.server;


import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AsysncTimeServerHandler> {


    /**
     * completed()方法表示接收到客户端了
     *
     * @param result
     * @param attachment
     */
    @Override
    public void completed(AsynchronousSocketChannel result, AsysncTimeServerHandler attachment) {
        System.out.println("服务端completed() 连接客户端成功...");
        attachment.asynchronousServerSocketChannel.accept(attachment,this);
        ByteBuffer byteBuffer= ByteBuffer.allocate(1024);
        /**
         * completed()既然已经连接客户端成功了，为什么需要传入一个新的CompletionHandler？
         * 没接入一个客户端之后，需要再次异步接收新的客户端连接
         */
        result.read(byteBuffer,byteBuffer,new ReadCompletionHandler(result));
    }

    @Override
    public void failed(Throwable exc, AsysncTimeServerHandler attachment) {

    }

}
