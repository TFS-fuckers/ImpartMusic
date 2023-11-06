package com.tfs.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.tfs.server.logger.Logger;

public class Server {
    private static Server INSTANCE = null;

    private boolean running = true;
    private List<ClientHandler> connectedClients = new ArrayList<>();

    public Server(int port){
        if(INSTANCE != null){
            Logger.logError("You can't run two or more servers in one process");
            return;
        }
        INSTANCE = this;
        long prepareStart = System.currentTimeMillis();
        Thread.currentThread().setName("ServerThread");
        ThreadPoolExecutor pool = new ThreadPoolExecutor(20, 50, 3L, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
        Logger.logInfo("Server Starting...");
        try {
            ServerSocket server = new ServerSocket(port);
            Logger.logInfo("Server is starting on port " + port);
            Logger.logInfo("Done! [%.2f seconds]", (System.currentTimeMillis() - prepareStart) * 1.0f / 1000);
            
            while(this.running){
                try{
                    Socket connection = server.accept();
                    Logger.logInfo("User %s is connected", connection.getInetAddress().toString());
                    pool.execute(new ClientHandler(connection));
                } catch(SocketException socketException){
                    Logger.logError(socketException.toString());
                } catch(Exception e){
                    Logger.logError(e.toString());
                    this.kill();
                }
            }
            server.close();
        } catch (Exception e) {
            Logger.logError("Error occured message: %s", e.toString());
            e.printStackTrace();
        }
        pool.shutdown();
        Logger.logInfo("Server is shutting down");
        INSTANCE = null;
    }

    public boolean isRunning() {
        return this.running;
    }

    public void kill(){
        this.running = false;
    }

    public List<ClientHandler> getConnectedClients() {
        return connectedClients;
    }

    public static Server instance(){
        return INSTANCE;
    }
}