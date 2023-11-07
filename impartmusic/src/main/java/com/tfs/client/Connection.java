package com.tfs.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import com.tfs.logger.Logger;

public class Connection {
    private final Queue<String> toSend = new LinkedList<>();
    private final Queue<String> received = new LinkedList<>();
    private Socket socket;
    private InetSocketAddress address = null;
    private Thread mainThread = null;

    private PrintWriter writer;
    private BufferedReader reader;

    public Connection(String host, int port){
        Logger.logInfo("Trying to connect to " + host + ":" + port);
        try {
            this.address = new InetSocketAddress(host, port);
            mainThread = new Thread(() -> this.mainThread());
            mainThread.start();
        } catch (Exception e) {
            Logger.logError("Connection failed: " + e.getMessage());
        }
    }

    public void connect(int maxTries, int timeout){
        for (int i = 0; i < maxTries; i++) {
            try {
                this.socket = new Socket();
                this.socket.connect(address, timeout);
                Logger.logInfo("Connected to %s:%d", address.getHostName(), address.getPort());
                break;
            } catch (Exception e) {
                Logger.logError("Connection failed: %s", e.getMessage());
            }
        }

        if(!this.isConnected()){
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
        //关闭主线程
    }

    private void mainThread(){
        this.connect(5, 3000);
        if(!this.isConnected()){
            return;
        }
        Timer timer = new Timer();
        try {
            this.writer = new PrintWriter(socket.getOutputStream(), true);
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e) {
            Logger.logError(e.getMessage());
            this.killConnection();
        }
        timer.scheduleAtFixedRate(new RefreshTask(), 0, 50);
    }

    private void receiveMessage() throws IOException{
        if(this.reader.ready()){
            String receive = this.reader.readLine();
            Logger.logInfo("message from server: %s", receive);
            this.received.add(receive);
        }
    }

    private void sendMessage(){
        if(this.toSend.size() > 0){
            this.writer.write(this.toSend.remove());
        }
    }

    public boolean isConnected(){
        return this.socket.isConnected();
    }

    public void onRefresh(){
        try {
            this.receiveMessage();
            this.sendMessage();
        } catch (Exception e) {
            Logger.logError("Connection error :%s", e.getMessage());
            this.killConnection();
        }
    }

    private class RefreshTask extends TimerTask{
        @Override
        public void run(){
            Connection.this.onRefresh();
        }
    }
}
