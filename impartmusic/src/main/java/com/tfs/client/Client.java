package com.tfs.client;

import com.tfs.datapack.ControlConnect;
import com.tfs.datapack.Datapack;
import com.tfs.datapack.PlayMusicInstruction;
import com.tfs.datapack.MusicProgress;
import com.tfs.datapack.UserInfo;
import com.tfs.logger.Logger;
import com.tfs.musicplayer.MusicDownloader;
import com.tfs.musicplayer.MusicPlayer;
import com.tfs.ui.ImpartUI;

import java.io.*;
import java.util.HashMap;

public class Client {
    private static Client INSTANCE = null;
    private Connection connection = null;
    private HashMap<String, File> musicFileHashMap = new HashMap<>();
    private MusicPlayer music = null;
    final public String MUSIC_LIST_PATH = "./data/MusicList.dat"; 
    final public double MAX_SYNC_INTERVAL = 0.5;

    public Client() {
        INSTANCE = this;
        ImpartUI.showUI();
        this.readMusicList();
        // TODO: delete this 测试用，暂时先注释掉，防止测试时忘记加入
        // this.connect("localhost", 25585, "zyl");
        try {
            Thread.sleep(20);
        } catch (Exception e) {
            e.printStackTrace();
        }
        while(true) {
            clientMainLoop();
        }
    }

    private void clientMainLoop() {
        if(this.connection != null && this.connection.isConnected()) {
            try {
                Thread.sleep(50);
                Datapack pack = this.connection.popReceive();
                if(pack != null) {
                    PackageResolver.resolveDatapack(pack);
                }
            } catch (Exception e) {
                e.printStackTrace();
                this.connection.killConnection();
            }
        }
    }

    public static Client INSTANCE() {
        return INSTANCE;
    }

    protected void playMusic(PlayMusicInstruction playMusicInstruction) {
        if(music == null)
            return;
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
        if(music == null)
            return;
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
                break;
        
            default:
                Logger.logError("Wrong status: "+musicProgress.getMusicStatus());
                break;
        }
        
    }

    protected void getMusicProcess() {
        if(music == null){
            connection.sendMessage(new Datapack("SynchronizeMusic",new MusicProgress()));
        }
        else{
            connection.sendMessage(
            new Datapack("SynchronizeMusic",
                new MusicProgress(
                    music.getMusicId(), music.getCurrentTime(), music.getStatus()
                )
            )
        );
        }
    }

    public Connection getConnection() {
        return connection;
    }

    protected void checkLoginInfo(UserInfo loginInfo) {
        Logger.logInfo(loginInfo.toString());
    }

    public void saveMusicList() {
        String path = MUSIC_LIST_PATH;
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
        String path = MUSIC_LIST_PATH;
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
            FileInputStream in = new FileInputStream(file);
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
        } catch (EOFException eof) {
            Logger.logError("Empty music list file, creating new hashmap");
            this.musicFileHashMap = new HashMap<>();
            musicFileHashMap.put("nothing", null);
            this.initializeNewMusicList();
            this.saveMusicList();
        } catch (Exception e) {
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

    public void connect(String host, int port, String loginAs) {
        this.connection = new Connection(host, port, new UserInfo(loginAs, "login"));
    }
}
