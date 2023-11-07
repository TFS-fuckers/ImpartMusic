package com.tfs.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;

import com.tfs.datapack.Datapack;
import com.tfs.logger.Logger;


import java.lang.Thread;

public class ClientHandler implements Runnable{
    private final Socket clientSocket;
    private String mainThreadName;
    private final Queue<String> toSend = new LinkedList<>();
    private final Queue<String> receive = new LinkedList<>();

    private PrintWriter writer;
    private BufferedReader reader;

    public ClientHandler(Socket clientSocket){
        this.clientSocket = clientSocket;
    }


    @Override
    public void run(){
        this.mainThreadName = String.format("ClientHandler IP %s", clientSocket.getInetAddress().toString());
        Thread.currentThread().setName(this.mainThreadName);
        Server.instance().connectedClients.add(this);
        try {
            this.writer = new PrintWriter(clientSocket.getOutputStream(), true);
            this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (Exception e) {
            Logger.logError(e.getMessage());
            this.killConnection();
        }
    }

    public void onTick(){
        try {
            this.sendMessage();
            this.receiveMessage();
        } catch (IOException ioException) {
            Logger.logError(ioException.getMessage());
            this.killConnection();
        }
    }

    private void sendMessage(){
        if(this.toSend.size() != 0){
            this.writer.println(toSend.remove());
        }
    }

    private void receiveMessage() throws IOException{
        if(this.reader.ready()){
            this.receive.add(this.reader.readLine());
        }
    }

    public boolean isConnected(){
        return this.clientSocket.isConnected();
    }

    public void killConnection(){
        try {
            this.clientSocket.close();
            Server.instance().connectedClients.remove(this);
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

    public void sendMessage(Datapack datapack){
        this.sendMessage(datapack.toJson());
    }

    public String popReceive(){
        if(this.receive.size() == 0){
            return null;
        }
        return this.receive.remove();
    }
}
