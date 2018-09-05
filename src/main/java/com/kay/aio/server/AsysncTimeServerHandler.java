package com.kay.aio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

public class AsysncTimeServerHandler implements Runnable {

     CountDownLatch countDownLatch;

     AsynchronousServerSocketChannel asynchronousServerSocketChannel;

    public static void main(String[] args) {
        new Thread(new AsysncTimeServerHandler(8001)).start();
    }

    public AsysncTimeServerHandler(int port) {
        try {
            asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
            asynchronousServerSocketChannel.bind(new InetSocketAddress("127.0.0.1", 8001));
            System.out.println("aio 服务器启动了...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        countDownLatch = new CountDownLatch(1);
        doAccept();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void doAccept() {
        /**
         *
         */
        asynchronousServerSocketChannel.accept(this, new AcceptCompletionHandler());
    }


}
