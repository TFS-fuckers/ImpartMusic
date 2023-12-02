package com.tfs.client;

import com.tfs.datapack.ControlConnect;
import com.tfs.datapack.Datapack;
import com.tfs.datapack.MusicProgress;
import com.tfs.datapack.UserInfo;
import com.tfs.logger.Logger;
import com.tfs.musicplayer.MusicDownloader;
import com.tfs.musicplayer.MusicPlayer;
import com.tfs.ui.ImpartUI;
import com.tfs.ui.MusicTvController;

import javafx.application.Platform;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Client implements ClientInterface{
    private static Client INSTANCE = null;
    private Connection connection = null;
    private HashMap<String, File> musicFileHashMap = new HashMap<>();
    private MusicPlayer music = null;
    final public String MUSIC_LIST_PATH = "./data/MusicList.dat"; 
    final public double MAX_SYNC_INTERVAL = 0.5;
    private ClientConnectionStatus status = ClientConnectionStatus.UNCONNECTED;
    private ArrayList<String> musicList;
    public Client() {
        INSTANCE = this;
        musicList = new ArrayList<>();
        ImpartUI.showUI();
        this.readMusicList();
        while(MusicTvController.instance() == null);
        try {
            Thread.sleep(50);
        } catch (Exception e) {
            e.printStackTrace();
        }
        while(true) {
            clientMainLoop();
        }
    }
                                    
    private void clientMainLoop() {
        if(this.connection == null) {
            return;
        }
        synchronized(this.connection) {
            if(this.connection.isConnected()) {
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
            this.connection.notify();
        }
    }

    public static Client INSTANCE() {
        return INSTANCE;
    }

    protected void controlConnect(ControlConnect controlconnect) {
        Logger.logInfo("You are kicked out the sever! Cause: %s", controlconnect.getCause());
        connection.killConnection();
    }

    protected void synchronizeMusicProgress(MusicProgress musicProgress) {
        if(music == null){
            music = getPlayMusic(musicProgress.getMusicId());
            music.setPositionMusic(musicProgress.getMusicTime());

            return;
        }

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
            MusicPlayer tmp = new MusicPlayer(musicFileHashMap.get(musicId).getAbsolutePath());
            tmp.playMusic();
            return tmp;
        } else {
            File musicFile = new MusicDownloader("http://music.163.com/song/media/outer/url?id=" + musicId,
                    downloadPath)
                    .downloadMusicFile();
            musicFileHashMap.put(musicId, musicFile);
            MusicPlayer tmp = new MusicPlayer(musicFile.getAbsolutePath());
            tmp.playMusic();
            return tmp;
        }
    }

    public void connect(String host, int port, String loginAs) {
        if(this.connection != null) {
            synchronized(this) {
                this.connection = new Connection(host, port, new UserInfo(loginAs, "login"));
                this.notify();
            }
        } else {
            this.connection = new Connection(host, port, new UserInfo(loginAs, "login"));
        }
    }

    public void setStatus(ClientConnectionStatus status) {
        this.status = status;
        switch(this.status) {
        case UNCONNECTED:
            Platform.runLater(() -> MusicTvController.instance().getConnection_state_info_label().setText("未连接"));
            break;    
        case CONNECTED:
            Platform.runLater(() -> MusicTvController.instance().getConnection_state_info_label().setText("已连接"));
            break;
        case CONNECTING:
            Platform.runLater(() -> MusicTvController.instance().getConnection_state_info_label().setText("连接中"));
            break;
        case CONNECTFAIL:
            Platform.runLater(() -> MusicTvController.instance().getConnection_state_info_label().setText("连接中断"));
            break;
        }
    }

    public ClientConnectionStatus getStatus() {
        return status;
    }

    @Override
    public void onSetProgress(MusicProgress progress){
        connection.sendMessage(new Datapack("StrandardRequest", null));
        synchronizeMusicProgress(progress);
    }

    @Override
    public void onSetVolume(float volume){
        music.setVolume(volume);
    }
    public ArrayList<String> getMusicList() {
        return musicList;
    }

    public void addMusic(String id) {
        if (!findMusic(id)) {
            musicList.add(id);
        }
    }

    public boolean findMusic(String id) {
        if (musicList.contains(id)) {
            return true;
        }
        return false;
    }

    public void deleteMusic(String id) {
        if (findMusic(id)) {
            musicList.remove(id);
        }
    }
    public String getMusic(int i) {
        if (!musicList.isEmpty()) {
            i = i % musicList.size();
            return musicList.get(i);
        }
        return null;
    }
}
