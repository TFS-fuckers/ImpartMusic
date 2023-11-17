package com.tfs.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

import com.tfs.datapack.AccessInstruction;
import com.tfs.datapack.Datapack;
import com.tfs.datapack.KillConnectionCommand;
import com.tfs.datapack.UserInfo;
import com.tfs.logger.Logger;

import java.lang.Thread;

/**服务器用于处理与客户端的连接的类 */
public class ClientHandler implements Runnable{
    /**与客户端通信的socket实例 */
    private final Socket clientSocket;
    private String mainThreadName;
    /**发送字符串队列 */
    private final Queue<Datapack> toSend = new LinkedList<>();
    /**接受字符串队列 */
    private final Queue<Datapack> receive = new LinkedList<>();
    private User user;

    /** 请勿更改 收取信息的触发器，用于检测是否有信息流入*/
    private boolean receiveTrigger = false;

    private PrintWriter writer;
    private BufferedReader reader;

    /**
     * 创建一个socket管理实例
     * @param clientSocket 与客户端的连接的socket实例
     */
    public ClientHandler(Socket clientSocket){
        this.clientSocket = clientSocket;
    }

    /**
     * 与客户端之间发送HeartBeat验证信息的时间间隔
     */
    public static final int HEART_BEAT_INTERVAL_MILLISECONDS = 1000;

    /**
     * 客户端没有回应的最大次数容忍限度
     */
    public static final int NO_RESPONSE_TIMEOUT_TRIES = 5;

    /**
     * 客户端没有发送验证信息的最大次数容忍限度
     */
    public static final int NO_VERTIFICATION_MAX_COUNT = 10;

    /**
     * 初始化方法
     * 
     * 将本实例放入Server实例的连接实例表中，加入服务器tick统一监听
     * 注意，此方法虽然是public，但是不要在外部进行直接调用
     */
    @Override
    public void run(){
        this.mainThreadName = String.format("ClientHandler IP %s", clientSocket.getInetAddress().toString());
        Thread.currentThread().setName(this.mainThreadName);
        try {
            this.writer = new PrintWriter(clientSocket.getOutputStream(), true);
            this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (Exception e) {
            Logger.logError(e.getMessage());
            this.killConnection();
        }
        int noResponseCount = 0;
        boolean vertified = false;
        Logger.logInfo("User connected, vertifying...");
        for(int i = 0; i < NO_VERTIFICATION_MAX_COUNT; i++) {
            try {
                if(this.reader.ready()) {
                    Datapack vertificationPack = new Datapack(reader.readLine());
                    UserInfo userInfo = vertificationPack.deserializeContent(UserInfo.class);
                    this.user = new User(userInfo.getName(), clientSocket.getInetAddress(), this);
                    User confiltUser = Server.instance().nameToUser.get(user.getName());
                    if(confiltUser != null){
                        confiltUser.getHandler().askForKillConnection(
                            new KillConnectionCommand("A user with the same name logged in")
                        );
                    }
                    // 如果已经有相同名称的用户，将其踢出（异地登录）
                    Server.instance().receivedDatapacks.add(vertificationPack);
                    this.writer.println(new Datapack("AccessInstruction", new AccessInstruction("Granted", "")).toJson());
                    Server.instance().nameToUser.put(this.user.getName(), this.user);
                    Server.instance().connectedUsers.add(this.user);
                    vertified = true;
                    break;
                }
                Thread.sleep(200);
            } catch (Exception e) {
                Logger.logError("Error while receiving vertification info");
                this.killConnection();
                return;
            }
        }

        if(!vertified) {
            Logger.logInfo("User is not sending vertification info, kicked.");
            this.killConnection();
            return;
        }
        Logger.logInfo("User %s logged in", this.user.getName());
        
        //将自己加入服务器单例的clients实例名单，方便通过Server单例进行统一管理
        while(this.isConnected()){
            try {
                //主线程进行初始化后就进入监视模式，时刻监视是否还与客户端保持连接
                Thread.sleep(HEART_BEAT_INTERVAL_MILLISECONDS);
                if(this.receiveTrigger){
                    this.receiveTrigger = false;
                    noResponseCount = 0;
                    continue;
                }
                if(noResponseCount > NO_RESPONSE_TIMEOUT_TRIES){
                    this.killConnection();
                    Logger.logInfo("Connection timed out");
                    break;
                }
                //如果还没有到达最大容忍限度，可能是因为网络速度低，尝试发送HeartBeat数据包
                this.sendMessage(Datapack.HEARTBEAT);
                noResponseCount++;
            } catch (Exception e) {
                //此处发生错误大概率为严重错误，直接崩溃此线程
                Logger.logError(e.getMessage());
                this.killConnection();
                break;
            }
        }
    }

    /**
     * 内部方法
     * 代表服务器在一个tick内的全部逻辑
     */
    public void onTick(){
        try {
            //服务器tick一次的逻辑
            this.sendMessageTick();
            this.receiveMessageTick();
            this.popReceiveTick();
        } catch (IOException ioException) {
            //如果出现IOException，应为连接已经被断开，所以直接断开本地的连接
            Logger.logError(ioException.getMessage());
            this.killConnection();
        }
    }

    /**
     * 内部方法
     * 代表服务器在一个tick内的发送信息逻辑
     */
    private void sendMessageTick(){
        //如果队列中有待发送的消息，就发送一个
        if(this.toSend.size() != 0){
            this.writer.println(toSend.remove().toJson());
        }
    }

    /**
     * 内部方法
     * 代表服务器在一个tick内的接受信息逻辑
     * @throws IOException 可能出现的连接错误
     */
    private void receiveMessageTick() throws IOException{
        //如果不用ready()，readLine()将会阻塞线程，用ready()来确保缓冲区有数据可读
        if(this.reader.ready()){
            Datapack received = Datapack.toDatapack(this.reader.readLine());
            this.receiveTrigger = true;
            //如果是HeartBeat验证消息，不用加入数据包集合，因为这只是个辅助消息，对其他功能没有用处
            if(received.identifier.equals(Datapack.HEARTBEAT.identifier)){
                return;
            }
            Logger.logInfo("received message from client: %s", received);
            this.receive.add(received);
        }
    }

    /**
     * 获取连接实例是否仍然已连接
     * @return 连接状态
     */
    public boolean isConnected(){
        return this.clientSocket.isConnected() && !this.clientSocket.isClosed();
    }

    /**
     * 强行中断连接
     */
    public void killConnection(){
        try {
            //断开连接
            this.clientSocket.close();
            Server.instance().connectedUsers.remove(this.user);
            Server.instance().nameToUser.remove(this.user.getName());
            this.user.setConnected(false);
            Logger.logInfo("%s disconnected from the server", this.user.getName());
        } catch (IOException err) {
            Logger.logError("Error while closing socket");
            Logger.logError(err.getMessage());
        }
    }

    /**
     * 命令用户断开连接(相对于强行中断的killConnection，向客户端说明了原因)
     * @param killConnectionDatapack 断开连接的最后原因
     */
    public void askForKillConnection(KillConnectionCommand killConnectionCommand){
        this.sendImmediateMessage(new Datapack("kick", killConnectionCommand));
        Logger.logInfo("kicked user %s from server, cause: %s", user.getName(), killConnectionCommand.getCause());
        this.killConnection();
    }

    /**
     * 对外发送一个字符串，字符串应当遵守Datapack的Json规范
     * 字符串将会被放入发送队列等待发送
     * 
     * @param message 待发送的字符串
     */
    public void sendMessage(String message){
        if(message == null){
            //不能发送null，因为这可能导致链接中断
            Logger.logError("Can't send null message");
            return;
        }
        this.toSend.add(Datapack.toDatapack(message));
    }

    /**
     * 对外发送一个数据包的Json
     * 
     * @param datapack 待发送的数据包
     */
    public void sendMessage(Datapack datapack){
        this.toSend.add(datapack);
    }

    /**
     * 将从客户端收取的所有信息在一次tick中交予Server实例
     */
    private void popReceiveTick(){
        if(this.receive.size() > 0){
            Server.instance().receivedDatapacks.add(receive.remove());
        }
    }

    /**
     * 立即向客户端发送信息而无需等待
     * @param datapack 发送的数据包
     */
    public void sendImmediateMessage(Datapack datapack) {
        this.writer.println(datapack.toJson());
        this.writer.flush();
    }
}
