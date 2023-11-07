package com.tfs.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

import com.tfs.datapack.Datapack;
import com.tfs.logger.Logger;


import java.lang.Thread;

/**服务器用于处理与客户端的连接的类 */
public class ClientHandler implements Runnable{
    /**与客户端通信的socket实例 */
    private final Socket clientSocket;
    private String mainThreadName;
    /**发送字符串队列 */
    private final Queue<String> toSend = new LinkedList<>();
    /**接受字符串队列 */
    private final Queue<String> receive = new LinkedList<>();

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
     * 初始化方法
     * 
     * 将本实例放入Server实例的连接实例表中，加入服务器tick统一监听
     */
    @Override
    public void run(){
        this.mainThreadName = String.format("ClientHandler IP %s", clientSocket.getInetAddress().toString());
        Thread.currentThread().setName(this.mainThreadName);
        //将自己加入服务器单例的clients实例名单，方便通过Server单例进行统一管理
        Server.instance().connectedClients.add(this);
        try {
            this.writer = new PrintWriter(clientSocket.getOutputStream(), true);
            this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (Exception e) {
            Logger.logError(e.getMessage());
            this.killConnection();
        }
        int noResponseCount = 0;
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
            this.sendMessage();
            this.receiveMessage();
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
    private void sendMessage(){
        //如果队列中有待发送的消息，就发送一个
        if(this.toSend.size() != 0){
            this.writer.println(toSend.remove());
        }
    }

    /**
     * 内部方法
     * 代表服务器在一个tick内的接受信息逻辑
     * @throws IOException 可能出现的连接错误
     */
    private void receiveMessage() throws IOException{
        //如果不用ready()，readLine()将会阻塞线程，用ready()来确保缓冲区有数据可读
        if(this.reader.ready()){
            String received = this.reader.readLine();
            Logger.logInfo("received message from client: %s", received);
            this.receiveTrigger = true;
            //如果是HeartBeat验证消息，不用加入数据包集合，因为这只是个辅助消息，对其他功能没有用处
            if(Datapack.toDatapack(received).identifier.equals(Datapack.HEARTBEAT.identifier)){
                return;
            }
            this.receive.add(received);
        }
    }

    /**
     * 获取连接实例是否仍然已连接
     * @return 连接状态
     */
    public boolean isConnected(){
        return this.clientSocket.isConnected();
    }

    /**
     * 强行中断连接
     */
    public void killConnection(){
        try {
            //断开连接
            this.clientSocket.close();
            Server.instance().connectedClients.remove(this);
            Logger.logInfo("%s disconnected from the server", this.clientSocket.getInetAddress().toString());
        } catch (IOException err) {
            Logger.logError("Error while closing socket");
            Logger.logError(err.getMessage());
        }
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
        this.toSend.add(message);
    }

    /**
     * 对外发送一个数据包的Json
     * 
     * @param datapack 待发送的数据包
     */
    public void sendMessage(Datapack datapack){
        this.sendMessage(datapack.toJson());
    }

    /**
     * 从输入流中获取一个从客户端收取的信息，处理应该按照数据包规范
     * @return json信息（开发中允许直接发送普通字符串）
     */
    public String popReceive(){
        if(this.receive.size() == 0){
            return null;
        }
        return this.receive.remove();
    }
}
