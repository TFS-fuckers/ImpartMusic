package com.tfs.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

import com.tfs.server.logger.Logger;

public class ClientHandler implements Runnable{
    private final Socket clientSocket;

    public ClientHandler(Socket clientSocket){
        this.clientSocket = clientSocket;
        Thread.currentThread().setName(String.format("ClientHandler IP %s", clientSocket.getInetAddress().toString()));
    }

    public final Queue<String> messageQueue = new LinkedList<>();

    @Override
    public void run(){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            while(clientSocket.isConnected()){
                String receive = reader.readLine();
                if(receive != null){
                    Logger.logInfo("Message from %s:%s", clientSocket.getInetAddress(), receive);
                }
                if(this.messageQueue.size() != 0){
                    writer.println(messageQueue);
                }
            }
            clientSocket.close();
        } catch (Exception e) {
            Logger.logError("Error occurred: %s", e.toString());
            e.printStackTrace();
        }
        Logger.logInfo("User %s disconnected from the server", clientSocket.getInetAddress().toString());
    }
}
