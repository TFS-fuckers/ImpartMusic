package com.tfs.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.tfs.datapack.Datapack;
import com.tfs.logger.Logger;

/**Impart Music 服务器 */
public class Server {
    /**服务器的唯一实例 */
    private static Server INSTANCE = null;
    /**服务器每两个tick（逻辑运行）的间隔时间 */
    public static int TICK_DELAY_MILLISECONDS = 50;
    /**服务器是否在运行 */
    private boolean running = true;
    /**服务器已经连接的所有客户端 */
    public final List<ClientHandler> connectedClients = new ArrayList<>();

    /**
     * 服务器实例构造，也是启动服务器的入口
     * @param port 服务器监听的端口
     */
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
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run(){
                for(ClientHandler handler : connectedClients){
                    handler.onTick();
                }
            }
        }, 0, 50);
        
        Logger.logInfo("Server tick started");
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

    /**
     * 获取服务器是否正在运行
     * @return 获取服务器是否正在运行，如果是，返回${true}$
     */
    public boolean isRunning() {
        return this.running;
    }

    /**
     * 中断服务器的运行
     */
    public void kill(){
        this.running = false;
    }

    /**
     * 获取服务器的实例
     * @return 服务器实例
     */
    public static Server instance(){
        return INSTANCE;
    }

    /**
     * 向与服务器连接的所有客户端发送信息，内容应遵守Datapack的Json规范
     * @param message 待发送的信息
     */
    public void sentToAll(String message){
        for(ClientHandler handler : connectedClients){
            handler.sendMessage(message);
        }
        return;
    }

    /**
     * 向与服务器连接的所有客户端发送数据包
     * @param datapack 待发送的数据包
     */
    public void sendToAll(Datapack datapack){
        for(ClientHandler handler : connectedClients){
            handler.sendMessage(datapack);
        }
        return;
    }
    // TODO:对单独用户的发送
}