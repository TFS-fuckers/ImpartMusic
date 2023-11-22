package com.tfs.server;

import java.util.Timer;
import java.util.TimerTask;

import com.tfs.datapack.Datapack;
import com.tfs.datapack.GetProgress;

public class Server {
    private int syncMusicPlayerNoResponseCount = 0;
    private boolean syncReceiveTrigger = false;
    public static final int MAX_SYNC_NO_RESPONSE = 5;

    public Server(int port){
        new Thread(() -> new ServerHandler(port, null)).start();
        Timer synchronizeMusicTimer = new Timer();
        synchronizeMusicTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run(){
                if(syncReceiveTrigger) {
                    syncReceiveTrigger = false;
                    syncMusicPlayerNoResponseCount = 0;
                } else {
                    syncMusicPlayerNoResponseCount++;
                    if(syncMusicPlayerNoResponseCount > MAX_SYNC_NO_RESPONSE) {

                    }
                }
                ServerHandler serverHandler = ServerHandler.instance();
                User standardUser =serverHandler.getFirstUser();
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
                // TODO: handle exception
            }
        }        
    }
}
