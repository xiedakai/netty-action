package com.kay.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class TimeServer implements Runnable {

    private Selector selector;

    private ServerSocketChannel serverSocketChannel;

    private volatile boolean stop;


    public static void main(String[] args) {
        TimeServer timeServer = new TimeServer(8000);
        new Thread(timeServer, "timeServer-Thread").start();
    }

    public TimeServer(int port) {
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress("127.0.0.1", port), 1024);
            //监听客户端连接事件
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("服务端已启动...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        this.stop = true;
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                selector.select(1000);//selector 1s被唤醒一次
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> it = keys.iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();
                    try {
                        handleInput(key);
                    } catch (IOException ex) {
                        if (key != null) {
                            key.cancel();
                            if (key.channel() != null) {
                                key.channel().close();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleInput(SelectionKey selectionKey) throws IOException {
        if (selectionKey.isAcceptable()) {
            try {
                ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
                //监听到有新的连接 完成TCP3次握手  建立连接
                SocketChannel socketChannel = serverSocketChannel.accept();
                socketChannel.configureBlocking(false);
                System.out.println("===> LocalAddress: " + socketChannel.getLocalAddress().toString());
                //将接入的客户端注册到selector上监听读操作
                socketChannel.register(selector, SelectionKey.OP_READ);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (selectionKey.isReadable()) {
            SocketChannel socketChannel = null;
            socketChannel = (SocketChannel) selectionKey.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            //因为设置了为非阻塞  所以socketChannel.read()是非阻塞的 使用返回值进行判断
            /**
             * 返回值>0 读到了字节
             * 返回值=0 没有读到字节
             * 返回值<0 链路已经关闭
             */
            int size = socketChannel.read(byteBuffer);
            if (size > 0) {
                byteBuffer.flip(); //limit设置为position position再设置为0
                byte[] bytes = new byte[byteBuffer.remaining()];
                byteBuffer.get(bytes);
                String str = new String(bytes, "utf-8");
                write(socketChannel, str);
            } else if (size < 0) {
                //对链路关闭
                selectionKey.cancel();
                socketChannel.close();
            } else {
                //读到0字节 忽略
            }

        }
    }

    public void write(SocketChannel socketChannel, String str) throws IOException {
        if (str != null && str.length() > 0) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            buffer.put(str.getBytes("utf-8"));
            buffer.flip();
            /**
             * socketChannel是异步非阻塞的 并不能保证一次能把数据都发送出去 会出现写半包的问题
             * 需要注册写操作 不断的轮询有没有未发完的数据
             * ByteBuffer.hasRemaining()判断是否有数据没发送完成
             */
            socketChannel.write(buffer);
            System.out.println("===> 服务端已写出数据: " + str);
        }
    }

}
