package com.tfs.client;
import com.tfs.datapack.ControlConnect;
import com.tfs.datapack.Datapack;
import com.tfs.datapack.PlayMusic;
import com.tfs.datapack.SynchronizeMusic;
import com.tfs.datapack.UserInfo;
import com.tfs.logger.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client {
    private static Client INSTANCE = null;
    private Connection connection = null;
    private HashMap<String, File> musicFileHashMap = new HashMap<>();
    public Client(){
        INSTANCE = this;

        this.connection = new Connection("localhost", 25585, new UserInfo("yufan_nb", "login"));

        while(true){
            try {
                Thread.sleep(20);
            } catch (Exception e) {
                Logger.logError("Thread sleep error: " + e.getMessage());
            }
            Datapack datapack = connection.popReceive();
            if(datapack != null){
                PackageResolver.resolveDatapack(datapack);
            }
            //break条件待定
        }
    }

    public static Client INSTANCE(){
        return INSTANCE;
    }


    protected void playMusic(PlayMusic playMusic){
        switch(playMusic.opType){
            case "continue":

                break;
            
            case "pause":

                break;

            case "change":

                break;

            default:
                Logger.logError("wrong opType of PlayMusic: " + playMusic.opType);
        }
    }

    protected void controlConnect(ControlConnect controlconnect){
        Logger.logInfo("You are kicked out the sever! Cause: %s", controlconnect.getCause());
        connection.killConnection();
    }

    protected void synchronizeMusic(SynchronizeMusic synchronizeMusic){
        // if(getMusicId() != synchronizeMusic.musicId){
        //     changeMusic(musicId);
        // }

    }

    protected void checkLoginInfo(UserInfo loginInfo){
        Logger.logInfo(loginInfo.toString());
    }

    protected void saveMusicList() {
        String path = ".data/MusicList";
        File file = new File(path);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
            oos.writeObject(this.musicFileHashMap);
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void readMusicList() {
        String path = ".data/MusicList";
        try {
            FileInputStream in = new FileInputStream(path);
            ObjectInputStream input=new ObjectInputStream(in);
            this.musicFileHashMap = (HashMap<String, File>) (input.readObject());
            input.close();
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
