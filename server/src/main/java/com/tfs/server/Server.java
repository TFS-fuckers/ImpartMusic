package com.tfs.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.tfs.server.logger.Logger;

public class Server {
    private boolean running = true;

    public Server(int port){
        long prepareStart = System.currentTimeMillis();
        Thread.currentThread().setName("ServerThread");
        ThreadPoolExecutor pool = new ThreadPoolExecutor(20, 50, 2L, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
        Logger.logInfo("Server Starting...");
        try {
            ServerSocket server = new ServerSocket(port);
            Logger.logInfo("Server is starting on port " + port);
            Logger.logInfo("Done! [%.2f seconds]", (System.currentTimeMillis() - prepareStart) * 1.0f / 1000);
            
            while(this.running){
                Socket connection = server.accept();
                Logger.logInfo("User %s is connected", connection.getInetAddress().toString());
                pool.execute(new ClientHandler(connection));
            }

            server.close();
        } catch (Exception e) {
            Logger.logError("Error occured message: %s", e.toString());
            e.printStackTrace();
        }
        pool.shutdown();
        Logger.logInfo("Server is shutting down");
    }

    public boolean isRunning() {
        return this.running;
    }

    public void kill(){
        this.running = false;
    }
}