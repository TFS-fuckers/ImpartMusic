package com.tfs.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import com.tfs.datapack.Datapack;
import com.tfs.logger.Logger;

/**与服务器之间的连接实例 */
public class Connection {
    /**发送内容队列 */
    private final Queue<Datapack> toSend = new LinkedList<>();
    /**接受内容队列 */
    private final Queue<Datapack> received = new LinkedList<>();
    /**与服务器的socket通信实例 */
    private Socket socket;
    /**指定的服务器ip */
    private InetSocketAddress address = null;
    /**处理与服务器连接的监听主线程 */
    private Thread mainThread = null;
    /**请勿更改 服务器是否响应的触发器 */
    private boolean receiveTrigger = false;

    private PrintWriter writer;
    private BufferedReader reader;

    /**与服务器通信的验证间隔时间 */
    public static final int HEART_BEAT_INTERVAL_MILLISECONDS = 1000;
    /**服务器无响应的最大容忍次数 */
    public static final int NO_RESPONSE_TIMEOUT_TRIES = 100000;

    /**
     * 创建一个与服务器的连接实例
     * @param host 服务器的IP
     * @param port 服务器的端口号
     */
    public Connection(String host, int port){
        Logger.logInfo("Trying to connect to " + host + ":" + port);
        try {
            this.address = new InetSocketAddress(host, port);
            mainThread = new Thread(() -> this.mainThread());
            mainThread.start();
        } catch (Exception e) {
            Logger.logError("Connection failed: " + e.getMessage());
        }
    }

    /**
     * 尝试连接服务器
     * @param maxTries 最大尝试次数
     * @param timeout 每次尝试的最大响应时间，超过无响应即尝试失败
     */
    public void connect(int maxTries, int timeout){
        for (int i = 0; i < maxTries; i++) {
            try {
                this.socket = new Socket();
                this.socket.connect(address, timeout);
                Logger.logInfo("Connected to %s:%d", address.getHostName(), address.getPort());
                break;
            } catch (Exception e) {
                Logger.logError("Connection failed: %s", e.getMessage());
            }
        }

        if(!this.isConnected()){
            Logger.logError("Connection failed after %d tries", maxTries);
        }
    }

    /**
     * 向服务器发送信息，内容应遵守Datapack Json规范
     * @param message 字符串
     */
    public void sendMessage(String message){
        if(message == null){
            Logger.logError("Can't send null message");
        }
        this.toSend.add(Datapack.toDatapack(message));
        //发送内容就是将待发送内容排队
    }

    /**
     * 从接受内容流中获取队列首部内容
     * @return 获取的内容，应为Datapack的Json信息
     */
    public Datapack popReceive(){
        if(this.received.size() == 0){
            return null;
        }
        return this.received.remove();
        //从收取的队列中弹出一个信息
    }

    /**
     * 断开与服务器的连接
     */
    public void killConnection(){
        try {
            Logger.logInfo("Disconnected from the server");
            this.socket.close();
        } catch (IOException e) {
            Logger.logError("Error while closing connection");
            Logger.logError(e.toString());
        }
    }

    /**
     * 主线程，初始化refresh（类似于服务器端的tick，避免线程高速运行循环）并
     * 监视与服务器间的连接。客户端的监视是被动的，也就是客户端不会主动发送
     * HEARTBEAT验证数据包
     */
    private void mainThread(){
        this.connect(5, 3000);
        if(!this.isConnected()){
            return;
        }
        Timer timer = new Timer();
        try {
            this.writer = new PrintWriter(socket.getOutputStream(), true);
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e) {
            Logger.logError(e.getMessage());
            this.killConnection();
        }
        timer.scheduleAtFixedRate(new RefreshTask(), 0, 50);
        int noResponseCount = 0;
        while(this.isConnected()){
            try {
                Thread.sleep(HEART_BEAT_INTERVAL_MILLISECONDS);
                if(this.receiveTrigger){
                    this.receiveTrigger = false;
                    noResponseCount = 0;
                }
                noResponseCount++;
                if(noResponseCount >= NO_RESPONSE_TIMEOUT_TRIES){
                    Logger.logInfo("Disconnected after server not responding after %d tries", NO_RESPONSE_TIMEOUT_TRIES);
                    this.killConnection();
                    break;
                }
            } catch (Exception e) {
                Logger.logError(e.getMessage());
                this.killConnection();
                break;
            }
        }
    }

    /**
     * 内部方法，代表一个refresh内客户端接受信息的逻辑
     * @throws IOException 可能出现的reader错误
     */
    private void receiveMessage() throws IOException{
        if(this.reader.ready()){
            Datapack receive = Datapack.toDatapack(this.reader.readLine());
            Logger.logInfo("message from server: %s", receive);
            this.receiveTrigger = true;
            if(receive.identifier.equals(Datapack.HEARTBEAT.identifier)){
                this.sendMessage(Datapack.HEARTBEAT);
                Logger.logInfo("responding server's heartbeat");
                return;
            }
            this.received.add(receive);
        }
    }

    /**
     * 内部方法，代表一次refresh内客户端的发送信息的逻辑
     */
    private void sendMessage(){
        if(this.toSend.size() > 0){
            this.writer.println(this.toSend.remove().toJson());
            Logger.logInfo("sent message to server");
        }
    }

    /**
     * 向服务器发送一个数据包，内容将会进入待发送队列
     * @param datapack 待发送的数据包
     */
    public void sendMessage(Datapack datapack){
        this.toSend.add(datapack);
    }

    /**
     * 与服务器的连接是否仍然已连接
     * @return 是否已连接
     */
    public boolean isConnected(){
        return this.socket.isConnected();
    }

    /**
     * 内部方法，代表客户端在一次refresh内的所有逻辑
     */
    private void onRefresh(){
        try {
            this.receiveMessage();
            this.sendMessage();
        } catch (Exception e) {
            Logger.logError("Connection error :%s", e.getMessage());
            this.killConnection();
        }
    }

    /**
     * 内部辅助类，代表refresh的任务。
     */
    private class RefreshTask extends TimerTask{
        @Override
        public void run(){
            Connection.this.onRefresh();
        }
    }
}
