package com.tfs.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

import com.tfs.logger.Logger;

public class Connection {
    private final Queue<String> toSend = new LinkedList<>();
    private final Queue<String> received = new LinkedList<>();
    private Socket socket = null;
    private InetSocketAddress address = null;
    private boolean connected = false;
    private Thread mainThread = null;

    public Connection(String host, int port){
        Logger.logInfo("Trying to connect to " + host + ":" + port);
        try {
            this.address = new InetSocketAddress(host, port);
            this.connect(5, 3000);
        } catch (Exception e) {
            Logger.logError("Connection failed: " + e.getMessage());
        }
    }

    public void connect(int maxTries, int timeout){
        if(this.connected){
            Logger.logError("Client is already connected but is still trying to connect");
            return;
        }
        for(int i = 1; i <= maxTries; i++){
            try{
                this.socket = new Socket();
                this.socket.connect(this.address, timeout);
                this.connected = true;
                Logger.logInfo("Connected to %s:%d", this.address.getHostString(), this.address.getPort());
                this.mainThread = new Thread(() -> this.mainThread());
                this.mainThread.start();
                break;
            }catch(Exception e){
                Logger.logError("Connection failed: " + e.getMessage());
                Logger.logInfo("Connection attempt %d failed", i);
            }
        }
        if(!this.connected){
            Logger.logError("Connection failed after %d tries", maxTries);
        }
    }

    public void sendMessage(String message){
        if(message == null){
            Logger.logError("Can't send null message");
        }
        this.toSend.add(message);
        //发送内容就是将待发送内容排队
    }

    public String popReceive(){
        if(this.received.size() == 0){
            return null;
        }
        return this.received.remove();
        //从收取的队列中弹出一个信息
    }

    public void killConnection(){
        try {
            this.socket.close();
        } catch (IOException e) {
            Logger.logError("Error while closing connection");
            Logger.logError(e.toString());
        }
        this.mainThread.interrupt();
        this.connected = false;
        //关闭主线程来中断连接
    }

    private void mainThread(){
        this.connected = true;
        Thread receiveMessageThread = new Thread(() -> receiveMessageThread());
        Thread sendMessageThread = new Thread(() -> sendMessageThread());
        receiveMessageThread.setDaemon(true);
        sendMessageThread.setDaemon(true);
        receiveMessageThread.start();
        sendMessageThread.start();
        while(!this.socket.isClosed() && !(Thread.currentThread().isInterrupted())){
            try {
                //主要处理逻辑
            } catch (Exception e) {
                Logger.logError("Error occured in connection: " + e.getMessage());
            }
        }
        try {
            this.socket.close();
        } catch (Exception e) {
            Logger.logError("Error while closing connection: " + e.getMessage());
        }
        this.connected = false;
        receiveMessageThread.interrupt();
        sendMessageThread.interrupt();
        Logger.logInfo("Connection closed");
        //关闭子线程
    }

    private void receiveMessageThread(){
        Thread.currentThread().setName("ReceiveMessageThread");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while(!socket.isClosed() && !Thread.currentThread().isInterrupted()){
                String receive = reader.readLine();
                if(receive == null){
                    break;
                }
                Logger.logInfo("Received message: %s", receive);
                this.received.add(receive);
                //将收取到的内容（json格式）加入到收取队列。
            }
            reader.close();
        } catch (Exception e){
            this.killConnection();
            Logger.logError(e.toString());
        }
        connected = false;
    }

    private void sendMessageThread(){
        Thread.currentThread().setName("SendMessageThread");
        try{
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            while(!socket.isClosed() && !Thread.currentThread().isInterrupted()){
                if(this.toSend.size() > 0){
                    //将排队的待发送内容发送
                    writer.println(toSend.remove());
                }
            }
            writer.close();
        } catch (Exception e){
            this.killConnection();
            Logger.logError(e.toString());
        }
        connected = false;
    }

    public boolean isConnected(){
        return this.connected;
    }
}
