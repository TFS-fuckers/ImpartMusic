package com.tfs.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.tfs.datapack.Datapack;
import com.tfs.datapack.ControlConnect;
import com.tfs.exceptions.AccessToOfflineUserException;
import com.tfs.logger.Logger;

/**Impart Music 服务器 */
public class ServerHandler {
    /**服务器的唯一实例 */
    private static ServerHandler INSTANCE = null;
    /**服务器每两个tick（逻辑运行）的间隔时间 */
    public static int tickIntervalMilliseconds = 50;
    private ServerSocket server;
    /**服务器是否在运行 */
    private boolean running = true;
    /**服务器已经连接的所有客户端 */
    public final List<User> connectedUsers = new ArrayList<>();
    public final Map<String, User> nameToUser = new HashMap<String, User>();

    /**服务器从所有客户端收到的所有数据包 */
    public final Queue<Datapack> receivedDatapacks = new LinkedList<>();
    /**
     * 服务器实例构造，也是启动服务器的入口。注意，这是一个阻塞方法，所以应该考虑是否放入一个独立的线程。
     * @param port 服务器监听的端口
     * @param onServerTick 自定义服务器每个tick内的额外逻辑，不得为阻塞方法
     */
    public ServerHandler(int port, Runnable onServerTick){
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
                for(User handler : connectedUsers){
                    try {
                        handler.getHandler().onTick();
                    } catch (AccessToOfflineUserException e) {
                        e.printStackTrace();
                    }
                }
                if(onServerTick != null){
                    onServerTick.run();
                }
            }
        }, 0, 50);
        
        Logger.logInfo("Server tick started");
        try {
            this.server = new ServerSocket(port);
            Logger.logInfo("Server is starting on port " + port);
            Logger.logInfo("Done! [%.2f seconds]", (System.currentTimeMillis() - prepareStart) * 1.0f / 1000);
            
            while(this.running){
                try{
                    Socket connection = server.accept();
                    Logger.logInfo("User %s is connected", connection.getInetAddress().toString());
                    pool.execute(new ClientHandler(connection));
                } catch(IOException e){
                    Logger.logInfo("Server is being closed");
                } catch(Exception e){
                    Logger.logError(e.toString());
                    this.kill();
                }
            }
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
     * @return 获取服务器是否正在运行，如果是，返回true
     */
    public boolean isRunning() {
        return this.running;
    }

    /**
     * 中断服务器的运行
     */
    public void kill(){
        try {
            this.server.close();
            this.running = false;
        } catch (Exception e) {
            Logger.logError("Exception while shutting down:%s", e.getMessage());
        }
    }

    /**
     * 获取服务器的实例
     * @return 服务器实例
     */
    public static ServerHandler instance(){
        return INSTANCE;
    }

    /**
     * 向与服务器连接的所有客户端发送信息，内容应遵守Datapack的Json规范
     * @param message 待发送的信息
     */
    public void sentToAll(String message){
        for(User user : connectedUsers){
            try {
                user.getHandler().sendMessage(message);
            } catch (AccessToOfflineUserException e) {
                e.printStackTrace();
            }
        }
        return;
    }

    /**
     * 向与服务器连接的所有客户端发送数据包
     * @param datapack 待发送的数据包
     */
    public void sendToAll(Datapack datapack){
        for(User user : connectedUsers){
            try {
                user.getHandler().sendMessage(datapack);
            } catch (AccessToOfflineUserException e) {
                e.printStackTrace();
            }
        }
        return;
    }
    
    /**
     * 向与服务器连接的所有客户端立即发送数据包
     * @param datapack 立即发送的数据包
     */
    public void sendToAllImmediately(Datapack datapack){
        for(User user : connectedUsers){
            try {
                user.getHandler().sendImmediateMessage(datapack);
            } catch (AccessToOfflineUserException e) {
                e.printStackTrace();
            }
        }
        return;
    }

    /**
     * 向某用户发送数据包（放入队列）
     * @param userName 用户的昵称
     * @param datapack 发送的数据包
     */
    public void sendToUser(String userName, Datapack datapack) {
        User user = this.nameToUser.get(userName);
        if(user == null) {
            Logger.logError("User %s not found", userName);
            return;
        }

        try {
            user.getHandler().sendMessage(datapack);
        } catch (AccessToOfflineUserException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向某用户立即发送数据包（立刻发送）
     * @param userName 用户的名称
     * @param datapack 立即发送的数据包
     */
    public void sendToUserImmediately(String userName, Datapack datapack) {
        User user = this.nameToUser.get(userName);
        if(user == null) {
            Logger.logError("User %s not found", userName);
            return;
        }
        try {
            user.getHandler().sendImmediateMessage(datapack);
        } catch (AccessToOfflineUserException e) {
            e.printStackTrace();
        }
    }

    /**
     * 踢出某用户
     * @param userName 踢出的用户名称
     */
    public void kickUser(String userName) {
        User user = getUser(userName);
        if(user == null) {
            Logger.logError("User %s not found", userName);
            return;
        }
        try {
            user.getHandler().askForKillConnection(new ControlConnect("kicked"));
        } catch (AccessToOfflineUserException e) {
            e.printStackTrace();
        }
    }

    /**
     * 踢出某用户
     * @param userName 踢出的用户名称
     * @param cause 踢出的原因
     */
    public void kickUser(String userName, String cause) {
        User user = getUser(userName);
        if(user == null) {
            Logger.logError("User %s not found", userName);
            return;
        }
        try {
            user.getHandler().askForKillConnection(new ControlConnect(cause));
        } catch (AccessToOfflineUserException e) {
            e.printStackTrace();
        }
    }

    /**
     * 寻找某个用户实例
     * @param userName 用户的名称
     * @return 用户实例，如果没有找到，返回null
     */
    public User getUser(String userName) {
        return this.nameToUser.get(userName);
    }

    /**
     * 
     * @param index
     * @return
     */
    public User getUser(int index) {
        return this.connectedUsers.get(index);
    }

    public int getUserNum() {
        return this.connectedUsers.size();
    }
}