package com.kay.other.threadLocal;

import java.util.concurrent.ThreadLocalRandom;

public class ThreadLocalRandomTest {


    public static void main(String[] args) {
        ThreadLocalRandom threadLocalRandom=ThreadLocalRandom.current();
        for(int i=0;i<50;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ThreadLocalRandom threadLocalRandom=ThreadLocalRandom.current();
                    for (int i=0;i<10;i++){
                        System.out.println(threadLocalRandom.nextInt(100));
                    }
                }
            }).start();
        }
    }


}
