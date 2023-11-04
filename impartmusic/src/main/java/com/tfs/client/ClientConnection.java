package com.tfs.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

import com.tfs.logger.Logger;

public class ClientConnection {
    public final Queue<String> toSend = new LinkedList<>();
    public final Queue<String> received = new LinkedList<>();

    private boolean connected = false;

    public ClientConnection(String host, int port){
        Logger.logInfo("Trying to connect to " + host + ":" + port);
        try {
            Socket socket = new Socket(host, port);
            Thread receiveMessageThread = new Thread(() -> receiveMessageThread(socket));
            Thread sendMessageThread = new Thread(() -> sendMessageThread(socket));
            receiveMessageThread.start();
            sendMessageThread.start();
            connected = true;
            Logger.logInfo("Connected");
        } catch (Exception e) {
            Logger.logError("Connection failed: " + e.getMessage());
        }
    }

    public void sendMessage(String message){
        this.toSend.add(message);
    }

    private void receiveMessageThread(Socket socket){
        Thread.currentThread().setName("ReceiveMessageThread");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while(!socket.isClosed()){
                String receive = reader.readLine();
                if(receive == null){
                    break;
                }
                this.received.add(receive);
            }
        } catch (Exception e){
            Logger.logError(e.toString());
        }
        connected = false;
    }

    private void sendMessageThread(Socket socket){
        Thread.currentThread().setName("SendMessageThread");
        try{
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            while(!socket.isClosed()){
                if(this.toSend.size() > 0){
                    writer.println(toSend.remove());
                }
            }
        } catch (Exception e){
            Logger.logError(e.toString());
        }
        connected = false;
    }

    public boolean isConnected(){
        return this.connected;
    }
}
