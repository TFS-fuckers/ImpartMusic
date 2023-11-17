package com.tfs.client;
import com.tfs.datapack.Datapack;
import com.tfs.datapack.UserInfo;
import com.tfs.logger.Logger;

public class Client {
    private static Client INSTANCE = null;
    private Connection connection = null;

    public Client(){
        INSTANCE = this;

        this.connection = new Connection("localhost", 25585);

        while(true){
            try {
                Thread.sleep(20);
            } catch (Exception e) {
                Logger.logError("Thread sleep error: " + e.getMessage());
            }
            // Datapack datapack = connection.popReceive();
            // if(datapack != null){
            //     PackageResolver.resolveDatapack(datapack);
            // }
            //break条件待定
            if(true){
                break;
            }
        }
        INSTANCE = null;
    }

    public static Client INSTANCE(){
        return INSTANCE;
    }


    protected void playMusic(){

    }

    protected void controlConnect(){

    }

    protected void synchronizeMusic(){
        
    }

    protected void checkLoginInfo(UserInfo loginInfo){
        Logger.logInfo("");
    }

    protected void AdjustPlayingSchedule(){

    }
}
