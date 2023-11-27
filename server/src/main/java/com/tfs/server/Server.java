package com.tfs.server;

import java.util.Timer;
import java.util.TimerTask;

import com.tfs.datapack.Datapack;
import com.tfs.datapack.GetProgress;
import com.tfs.datapack.MusicProgress;
import com.tfs.datapack.PlayMusicInstruction;

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
        new Thread(() -> new ServerHandler(port, null)).start();
        Timer synchronizeMusicTimer = new Timer();
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
                        return;
                    }
                }
                if(standardUser != null){
                    serverHandler.sendToUserImmediately(standardUser.getName(),new Datapack("GetProgress",new GetProgress()));
                }
            }
        },0,2000);
        while(true) {
            try {
                Thread.sleep(20);
                Datapack pack = ServerHandler.instance().receivedDatapacks.remove();
                if(pack.identifier.equals("MusicProgess")) {
                    this.syncReceiveTrigger = true;                    
                }

            } catch (Exception e) {
                
            }
        }        
    }

    public static Server INSTANCE(){
        return INSTANCE;
    }

    protected void synchronizeMusicProgress(MusicProgress musicProgress){
        this.autoSyncInSleepTrigger = true;
        ServerHandler.instance().sendToAllImmediately(new Datapack("SynchronizeMusic",musicProgress));
    }

    protected void playMusicInstruction(PlayMusicInstruction playMusicInstruction){
        this.autoSyncInSleepTrigger = true;
        ServerHandler.instance().sendToAllImmediately(new Datapack("PlayMusicInstruction", playMusicInstruction));
    }
}
