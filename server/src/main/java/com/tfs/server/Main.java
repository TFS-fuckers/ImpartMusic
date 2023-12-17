package com.tfs.server;

public class Main {
    public static void main(String[] args){
        new Thread(() -> new Server()).start();
    }
}
