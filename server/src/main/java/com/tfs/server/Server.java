package com.tfs.server;

import java.util.Timer;
import java.util.TimerTask;

import com.tfs.datapack.Datapack;
import com.tfs.datapack.GetMusicProcess;
import com.tfs.datapack.MusicProgress;
import com.tfs.datapack.PlayMusicInstruction;
import com.tfs.datapack.UserInfo;
import com.tfs.logger.Logger;

public class Server {
    private static Server INSTANCE = null;
    private int syncMusicPlayerNoResponseCount = 0;
    private boolean syncReceiveTrigger = false;
    private boolean autoSyncInSleepTrigger = false;
    public static final int AUTO_SYNC_SLEEP_TICK = 5;

    public static final int MAX_SYNC_NO_RESPONSE = 5;
    private int userIndex = 0;

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
            private int sleepCount = 0;
            private boolean inSleep = false;
            @Override
            public void run(){
                if (autoSyncInSleepTrigger) {
                    sleepCount = 0;
                    inSleep = true;
                    autoSyncInSleepTrigger = false;
                }
                if (inSleep) {
                    sleepCount++;
                    Logger.logTest();
                    if (sleepCount > AUTO_SYNC_SLEEP_TICK) {
                        inSleep = false;
                        sleepCount = 0;
                    }
                    return;
                }
                ServerHandler serverHandler = ServerHandler.instance();
                User standardUser;
                //确保getUser()不越界
                if(serverHandler.getUserNum() == 0){
                    return;
                }
                if(userIndex > serverHandler.getUserNum() - 1){
                    userIndex = 0;
                }
                standardUser = serverHandler.getUser(userIndex);
                
                if(syncReceiveTrigger) {
                    syncReceiveTrigger = false;
                    syncMusicPlayerNoResponseCount = 0;
                } else {
                    syncMusicPlayerNoResponseCount++;
                    if(syncMusicPlayerNoResponseCount > MAX_SYNC_NO_RESPONSE) {
                        userIndex++;
                        syncReceiveTrigger = true;
                        return;
                    }
                }
                if(standardUser != null){
                    serverHandler.sendToUserImmediately(standardUser.getName(),new Datapack("GetMusicProcess",new GetMusicProcess()));
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

    protected void setMusicProgress(MusicProgress musicProgress){
        this.autoSyncInSleepTrigger = true;
        if(musicProgress.isEmpty() == false)
            ServerHandler.instance().sendToAllImmediately(new Datapack("SynchronizeMusic",musicProgress));
    }

    protected void playMusicInstruction(PlayMusicInstruction playMusicInstruction){
        this.autoSyncInSleepTrigger = true;
        ServerHandler.instance().sendToAllImmediately(new Datapack("PlayMusicInstruction", playMusicInstruction));
    }

    protected void broadcastUserConnection(UserInfo info) {

    }

    public void onUserLogin(UserInfo info) {
        User user = ServerHandler.instance().getUser(info.getName());
        // TODO: to this user
        ServerHandler.instance().sendToAll(null);
    }

    public void onUserDisconnect(User info) {

    }
}
