package com.tfs.server;

/**
 * 主类
 */
public class Main {
    /**
     * 程序入口
     * @param args 启动参数
     */
    public static void main(String[] args){
        new Thread(() -> new Server()).start();
    }
}
