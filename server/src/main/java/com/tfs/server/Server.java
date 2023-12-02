package com.tfs.server;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.tfs.datapack.Datapack;
import com.tfs.datapack.GetMusicProcess;
import com.tfs.datapack.MusicProgress;
import com.tfs.datapack.UserInfo;
import com.tfs.datapack.UserSimpleInfo;
import com.tfs.logger.Logger;

public class Server {
    private static Server INSTANCE = null;
    private int syncMusicPlayerNoResponseCount = 0;

    private boolean syncReceiveTrigger = false;
    public static final int AUTO_SYNC_SLEEP_TICK = 5;
    public static final int MAX_SYNC_NO_RESPONSE = 5;
    private int standardUserIndex = 0;

    public Server(int port){
        INSTANCE = this;
        new Thread(() -> new ServerHandler(port, new CustomServerTick())).start();
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

    public static Server INSTANCE(){
        return INSTANCE;
    }

    protected void synchronizeMusicProgress(MusicProgress musicProgress){
        this.syncReceiveTrigger = true;
        if(musicProgress.isEmpty() == false)
            ServerHandler.instance().sendToAllImmediately(new Datapack("SynchronizeMusic",musicProgress));
    }

    protected void broadcastUserConnection(UserInfo info) {

    }

    public void onUserLogin(UserInfo info) {
        User user = ServerHandler.instance().getUser(info.getName());
        // TODO: to this user
        ArrayList<UserSimpleInfo> userInfoList = new ArrayList<>();
        for(User tmp:ServerHandler.instance().nameToUser.values()){
            userInfoList.add(new UserSimpleInfo(tmp.getName(), tmp.getAddress().getHostAddress()));
        }
        ServerHandler.instance().sendToAll(new Datapack("UserList", userInfoList));
        ServerHandler.instance().sendToAll(new Datapack("LoginUser",info));
        // TODO: 发送内容等待修改后补充
        
    }

    public void onUserDisconnect(UserInfo info) {
        ArrayList<UserSimpleInfo> userInfoList = new ArrayList<>();
        for(User tmp:ServerHandler.instance().nameToUser.values()){
            userInfoList.add(new UserSimpleInfo(tmp.getName(), tmp.getAddress().getHostAddress()));
        }
        ServerHandler.instance().sendToAll(new Datapack("UserList", userInfoList));
        ServerHandler.instance().sendToAll(new Datapack("LogoutUser",info));
        // ServerHandler.instance().sendToAll(new Datapack);
    }

    public int getStandardUserIndex() {
        return this.standardUserIndex;
    }

    public void setStandardUserIndex(int index) {
        if(index == -1) {
            Logger.logError("Cannot set standard user index to -1");
            return;
        }
        this.standardUserIndex = index;
    }

}
