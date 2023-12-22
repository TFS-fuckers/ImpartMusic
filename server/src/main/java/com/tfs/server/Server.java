package com.tfs.server;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.tfs.datapack.Datapack;
import com.tfs.datapack.GetMusicProcess;
import com.tfs.datapack.MusicProgress;
import com.tfs.datapack.SimpleString;
import com.tfs.datapack.UserInfo;
import com.tfs.datapack.UserSimpleInfo;
import com.tfs.dxconfig.ImpartConfigReader;
import com.tfs.logger.Logger;
import com.tfs.modloader.ModLoader;

public class Server {
    private static Server INSTANCE = null;
    private int syncMusicPlayerNoResponseCount = 0;

    private boolean syncReceiveTrigger = false;
    public static final int AUTO_SYNC_SLEEP_TICK = ImpartConfigReader.instance().get("AUTO_SYNC_SLEEP_TICK").getAsInt();
    public static final int MAX_SYNC_NO_RESPONSE = ImpartConfigReader.instance().get("MAX_SYNC_NO_RESPONSE").getAsInt();
    public static final String MES_TO_LOGIN_USER = ImpartConfigReader.instance().get("MES_TO_LOGIN_USER").getAsString();
    public static final int PORT = ImpartConfigReader.instance().get("PORT").getAsInt();
    private int standardUserIndex = 0;
    private boolean musicListSyncTrigger = true;
    private MusicProgress musicProgress;

    /**
     * 启动服务器
     */
    public Server(){
        ModLoader.load();
        INSTANCE = this;
        new Thread(() -> new ServerHandler(PORT, new CustomServerTick())).start();
        Timer synchronizeMusicTimer = new Timer();

        try {
            Thread.sleep(20);
        } catch (Exception e) {
            e.printStackTrace();
        }
        synchronizeMusicTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run(){
                ServerHandler serverHandler = ServerHandler.instance();
                User standardUser;

                //确保getUser()不越界
                if(serverHandler.getUserNum() == 0){
                    return;
                }
                if(standardUserIndex > serverHandler.getUserNum() - 1){
                    standardUserIndex = 0;
                }
                // protector

                standardUser = serverHandler.getUser(standardUserIndex);
                if(standardUser != null){
                    serverHandler.sendToUserImmediately(
                        standardUser.getName(),
                        new Datapack("GetMusicProcess",new GetMusicProcess())
                    );
                }

                if(syncReceiveTrigger) {
                    syncReceiveTrigger = false;
                    syncMusicPlayerNoResponseCount = 0;
                } else {
                    syncMusicPlayerNoResponseCount++;
                    if(syncMusicPlayerNoResponseCount > MAX_SYNC_NO_RESPONSE) {
                        standardUserIndex++;
                        syncReceiveTrigger = true;
                        return;
                    }
                }
            }
        },0,2000);      
    }

    /**
     * 依附于服务器Tick的自定义逻辑
     */
    private class CustomServerTick implements Runnable {
        @Override
        public void run() {
            synchronized(ServerHandler.instance().receivedDatapacks) {
                Datapack pack = null;
                if(ServerHandler.instance().receivedDatapacks.size() > 0) {
                    pack = ServerHandler.instance().receivedDatapacks.remove();
                }
                if(pack != null){
                    PackageResolver.packageResolver(pack);
                }
                ServerHandler.instance().receivedDatapacks.notify();
            }
        }
    }

    /**
     * 获取服务器实例
     * @return 服务器实例
     */
    public static Server INSTANCE(){
        return INSTANCE;
    }

    /**
     * 分发同步音乐播放器信息
     * @param musicProgress 同步目标
     */
    protected void synchronizeMusicProgress(MusicProgress musicProgress){
        this.syncReceiveTrigger = true;
        this.musicProgress = musicProgress;
        Logger.logInfo("musicProgress list count: %d", musicProgress.getMusicList().size());
        ServerHandler.instance().sendToAll(new Datapack("SynchronizeMusic", musicProgress));
    }

    /**
     * 用户登录时的事件监听逻辑
     * @param info 用户登录的信息
     */
    public void onUserLogin(UserInfo info) {
        User user = ServerHandler.instance().getUser(info.getName());
        ArrayList<UserSimpleInfo> userInfoList = new ArrayList<>();
        for(User tmp:ServerHandler.instance().nameToUser.values()){
            userInfoList.add(new UserSimpleInfo(tmp.getName(), tmp.getAddress().getHostAddress()));
        }
        ServerHandler.instance().sendToAll(new Datapack("UserList", userInfoList));
        ServerHandler.instance().sendToAll(new Datapack("LoginUser",info));
        ServerHandler.instance().sendToUser(info.getName(), new Datapack("SimpleString", new SimpleString(MES_TO_LOGIN_USER, "UTF-8")));
        if(musicListSyncTrigger){
            Logger.logInfo("Sending cached music progress to user");
            if(musicProgress == null) {
                ServerHandler.instance().sendToUser(user.getName(), new Datapack("SynchronizeMusic", new MusicProgress()));
            } else {
                ServerHandler.instance().sendToUser(user.getName(), new Datapack("SynchronizeMusic", this.musicProgress));
            }
            musicListSyncTrigger = false;
        }
    }
    
    /**
     * 在用户退出时的事件监听逻辑
     * @param info 用户退出信息
     */
    public void onUserDisconnect(UserInfo info) {
        ArrayList<UserSimpleInfo> userInfoList = new ArrayList<>();
        for(User tmp:ServerHandler.instance().nameToUser.values()){
            userInfoList.add(new UserSimpleInfo(tmp.getName(), tmp.getAddress().getHostAddress()));
        }
        ServerHandler.instance().sendToAll(new Datapack("UserList", userInfoList));
        ServerHandler.instance().sendToAll(new Datapack("LogoutUser", info));
        if(ServerHandler.instance().getUserNum() == 0){
            musicListSyncTrigger = true;
        }
    }

    /**
     * 获取当前使用的标准用户的序号
     * @return 标准用户的序号
     */
    public int getStandardUserIndex() {
        return this.standardUserIndex;
    }

    /**
     * 设置标准用户
     * @param index 标准用户的序号
     */
    public void setStandardUserIndex(int index) {
        if(index == -1) {
            Logger.logError("Cannot set standard user index to -1");
            return;
        }
        Logger.logInfo("set stadard user to %d", index);
        this.standardUserIndex = index;
    }

}
