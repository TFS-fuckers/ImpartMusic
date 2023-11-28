package com.tfs.client;

import com.tfs.datapack.ControlConnect;
import com.tfs.datapack.Datapack;
import com.tfs.datapack.PlayMusicInstruction;
import com.tfs.datapack.MusicProgress;
import com.tfs.datapack.UserInfo;
import com.tfs.logger.Logger;
import com.tfs.musicplayer.MusicDownloader;
import com.tfs.musicplayer.MusicPlayer;

import java.io.*;
import java.util.HashMap;

public class Client {
    private static Client INSTANCE = null;
    private Connection connection = null;
    private HashMap<String, File> musicFileHashMap = new HashMap<>();
    private MusicPlayer music = null;
    final private double MAX_SYNC_INTERVAL = 0.5;

    public Client() {
        INSTANCE = this;
        this.readMusicList();
        this.connection = new Connection("localhost", 25585, new UserInfo("test0", "login"));

        while (true) {
            try {
                Thread.sleep(20);
            } catch (Exception e) {
                Logger.logError("Thread sleep error: " + e.getMessage());
            }
            Datapack datapack = connection.popReceive();
            if (datapack != null) {
                PackageResolver.resolveDatapack(datapack);
            }
            // break条件待定
        }
    }

    public static Client INSTANCE() {
        return INSTANCE;
    }

    protected void playMusic(PlayMusicInstruction playMusicInstruction) {
        switch (playMusicInstruction.opType) {
            case "continue":
                music.resumeMusic();
                break;

            case "pause":
                music.pauseMusic();
                break;

            case "change":
                music = getPlayMusic(playMusicInstruction.musicId);
                music.playMusic();
                break;

            default:
                Logger.logError("Wrong opType of PlayMusic: " + playMusicInstruction.opType);
                break;
        }
    }

    protected void controlConnect(ControlConnect controlconnect) {
        Logger.logInfo("You are kicked out the sever! Cause: %s", controlconnect.getCause());
        connection.killConnection();
    }

    protected void synchronizeMusicProgress(MusicProgress musicProgress) {
        if (musicProgress.getMusicId().equals(music.getMusicId()) == false) {
            music = getPlayMusic(musicProgress.getMusicId());
        } else {
            if (Math.abs(music.getCurrentTime() - musicProgress.getMusicTime()) >= MAX_SYNC_INTERVAL) {
                music.setPositionMusic(musicProgress.getMusicTime());
            }
        }
        switch (musicProgress.getMusicStatus()) {
            case "pause":
                music.pauseMusic();
                break;

            case "play":
                music.resumeMusic();
        
            default:
                break;
        }
        
    }

    protected void getMusicProcess() {
        connection.sendMessage(
            new Datapack("GetMusicProcess",
                new MusicProgress(
                    music.getMusicId(), music.getCurrentTime(), music.getStatus()
                )
            )
        );
    }

    public Connection getConnection() {
        return connection;
    }

    protected void checkLoginInfo(UserInfo loginInfo) {
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

    @SuppressWarnings("unchecked")
    protected void readMusicList() {
        String path = "./data/MusicList";
        File file = new File(path);
        if (!file.exists()) {
            Logger.logWarning("Music list file not found, using default...");
            try {
                File parent = file.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                file.createNewFile();
            } catch (Exception e) {
                Logger.logError("Error while creating new music list file");
                e.printStackTrace();
            } finally {
                this.musicFileHashMap = new HashMap<>();
                this.initializeNewMusicList();
            }
            return;
        }

        try {
            FileInputStream in = new FileInputStream(path);
            ObjectInputStream input = new ObjectInputStream(in);
            Object object = input.readObject();
            if (object instanceof HashMap<?, ?>) {
                this.musicFileHashMap = HashMap.class.cast(object);
            } else {
                Logger.logError("Error while reading music hash map set, creating new hashmap");
                this.musicFileHashMap = new HashMap<>();
                this.initializeNewMusicList();
            }
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

    private void initializeNewMusicList() {
        File path = new File("./data");
        for (File child : path.listFiles()) {
            String fileName = child.getName();
            int dotIndex = fileName.indexOf('.');
            if (dotIndex == -1) {
                continue;
            }
            String type = fileName.substring(dotIndex + 1);
            if (type.equals("mp3")) {
                String id = fileName.substring(0, dotIndex);
                this.musicFileHashMap.put(id, child);
                Logger.logInfo("Found music file %s.mp3", id);
            }
        }
    }

    private MusicPlayer getPlayMusic(String musicId) {
        String downloadPath = "./data";
        if (musicFileHashMap.containsKey(musicId)) {
            return new MusicPlayer(musicFileHashMap.get(musicId).getAbsolutePath());
        } else {
            File musicFile = new MusicDownloader("http://music.163.com/song/media/outer/url?id=" + musicId,
                    downloadPath)
                    .downloadMusicFile();
            musicFileHashMap.put(musicId, musicFile);
            return new MusicPlayer(musicFile.getAbsolutePath());
        }
    }
}
