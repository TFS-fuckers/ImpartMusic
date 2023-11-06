package com.tfs.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Queue;

import com.tfs.server.logger.Logger;

public class ClientHandler implements Runnable{
    private final Socket clientSocket;
    private String mainThreadName;
    private boolean connected = true;

    private final Queue<String> toSend = new LinkedList<>();
    private final Queue<String> receive = new LinkedList<>();

    public ClientHandler(Socket clientSocket){
        this.clientSocket = clientSocket;
    }

    public final Queue<String> messageQueue = new LinkedList<>();

    @Override
    public void run(){
        Server.instance().getConnectedClients().add(this);
        this.mainThreadName = String.format("ClientHandler IP %s", clientSocket.getInetAddress().toString());
        Thread.currentThread().setName(this.mainThreadName);
    
        Thread sendMessageThread = new Thread(() -> sendMessageThread());
        Thread receiveMessageThread = new Thread(() -> receiveMessageThread());
        sendMessageThread.setDaemon(true);
        receiveMessageThread.setDaemon(true);
        sendMessageThread.start();
        receiveMessageThread.start();
    
        try {
            //ClientHandler本身run就是线程池分配的一个子线程，所以不需要单独新建一个mainThread
            while(!clientSocket.isClosed() && !Thread.currentThread().isInterrupted()){
                try {
                    // 主要处理逻辑
                } catch (Exception e) {
                    Logger.logError(e.toString());
                }
            }
            Thread.currentThread().interrupt();
            sendMessageThread.interrupt();
            receiveMessageThread.interrupt();
            clientSocket.close();
        } catch (SocketException e) {
            Logger.logInfo("User disconnected, cause: %s", e.getMessage());
        } catch (Exception e){
            Logger.logError(e.toString());
            e.printStackTrace();
        }
        
        this.connected = false;
        Logger.logInfo("User %s disconnected from the server", clientSocket.getInetAddress().toString());
        Server.instance().getConnectedClients().remove(this);
    }

    private void sendMessageThread(){
        Thread.currentThread().setName(this.mainThreadName + " SEND");
        try {
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            while(!this.clientSocket.isClosed() && !Thread.currentThread().isInterrupted()){
                if(this.toSend.size() > 0){
                    writer.println(this.toSend.remove());
                }
            }
            writer.close();
        } catch (Exception e) {
            Logger.logError(e.toString());
            this.killConnection();
        }
    }

    private void receiveMessageThread(){
        Thread.currentThread().setName(this.mainThreadName + " RECEIVE");
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            while(!this.clientSocket.isClosed() && !Thread.currentThread().isInterrupted()){
                String receive = reader.readLine();
                if(receive == null){
                    break;
                }
                this.receive.add(receive);
            }
            reader.close();
        } catch (Exception e){
            Logger.logError(e.toString());
            this.killConnection();
        }
    }

    public boolean isConnected(){
        return this.connected;
    }

    public void killConnection(){
        try {
            this.clientSocket.close();
            this.connected = false;
        } catch (IOException err) {
            Logger.logError("Error while closing socket");
            Logger.logError(err.getMessage());
        }
    }

    public void sendMessage(String message){
        if(message == null){
            Logger.logError("Can't send null message");
            return;
        }
        this.toSend.add(message);
    }

    public String popReceive(){
        if(this.receive.size() == 0){
            return null;
        }
        return this.receive.remove();
    }
}
