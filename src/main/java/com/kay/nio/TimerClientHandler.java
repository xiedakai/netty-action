package com.kay.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class TimerClientHandler implements Runnable {

    private Selector selector;
    private SocketChannel socketChannel;
    private volatile boolean stop;


    public TimerClientHandler() {
        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Thread(new TimerClientHandler(),"client-thread").start();
    }

    @Override
    public void run() {
        try {
            doConnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (!stop) {
            try {
                selector.select(1000);
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> it = keys.iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();
                    handleInput(key);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInput(SelectionKey selectionKey) throws IOException {
        if (!selectionKey.isValid()) {
            return;
        }
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        if (selectionKey.isConnectable()) {
            if (socketChannel.finishConnect()) {
                socketChannel.register(selector,SelectionKey.OP_READ);
                doWrite(socketChannel);
            }else {
                System.exit(1);//连接失败 退出
            }
        }
        if(selectionKey.isReadable()){
            ByteBuffer byteBuffer= ByteBuffer.allocate(1024);
            int i=socketChannel.read(byteBuffer);
            if(i>0){
                byteBuffer.flip();
                byte[] bytes=new byte[byteBuffer.remaining()];
                byteBuffer.get(bytes);
                String str=new String(bytes,"utf-8");
                System.out.println("===> 读到服务端传过来的数据： "+str);
                this.stop=true;
            }else if(i<0){
                //对链路关闭
                selectionKey.cancel();
                socketChannel.close();
            }
        }
    }

    private void doConnect() throws IOException {
        if (socketChannel.connect(new InetSocketAddress("127.0.0.1", 8000))) {
            socketChannel.register(selector, SelectionKey.OP_READ);

        } else {
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
        }
    }

    private void doWrite(SocketChannel socketChannel) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put("hello，this is client".getBytes("utf-8"));
        byteBuffer.flip();
        socketChannel.write(byteBuffer);
        if (!byteBuffer.hasRemaining()) {
            System.out.println("数据发送完毕....");
        }

    }


}
