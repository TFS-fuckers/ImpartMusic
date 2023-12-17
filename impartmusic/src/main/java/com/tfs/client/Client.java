package com.tfs.client;

import com.tfs.datapack.ControlConnect;
import com.tfs.datapack.Datapack;
import com.tfs.datapack.MusicProgress;
import com.tfs.datapack.UserInfo;
import com.tfs.datapack.UserSimpleInfo;
import com.tfs.dxconfig.ImpartConfigReader;
import com.tfs.logger.Logger;
import com.tfs.musicplayer.MusicDownloader;
import com.tfs.musicplayer.MusicPlayer;
import com.tfs.musicplayer.Netease;
import com.tfs.ui.ImpartUI;
import com.tfs.ui.MusicTvController;

import javafx.application.Platform;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Client implements ClientInterface{
    private static Client INSTANCE = null;
    private Connection connection = null;
    private HashMap<String, File> musicFileHashMap = new HashMap<>();
    private MusicPlayer music = null;
    final public String MUSIC_LIST_PATH = ImpartConfigReader.instance().get("MUSIC_LIST_PATH").getAsString(); 
    final public double MAX_SYNC_INTERVAL = ImpartConfigReader.instance().get("MAX_SYNC_INTERVAL").getAsDouble();;
    final public int REQUEST_STD_IGNORE_COUNT = ImpartConfigReader.instance().get("REQUEST_STD_IGNORE_COUNT").getAsInt();
    final public double SYNC_RANGE_SECOND = ImpartConfigReader.instance().get("SYNC_RANGE_SECOND").getAsDouble();
    private ClientConnectionStatus status = ClientConnectionStatus.UNCONNECTED;
    private List<String> musicList = new ArrayList<>();
    private boolean killed = false;
    private int playingMusicIndex = -1;

    public Client() {
        System.setProperty("file.encoding", "UTF-8");
        INSTANCE = this;
        ImpartUI.showUI();
        this.initializeNewMusicHashMap();
        Logger.logInfo("UI started, main loop running...");
        while(!killed) {
            clientMainLoop();
        }
        if(this.connection != null) {
            this.connection.killConnection();
            this.connection = null;
        }
        try {
            Thread.sleep(800);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.logError("Error while exiting program");
        }
        System.exit(0);
    }
    
    public void kill() {
        Logger.logInfo("Killed client main loop");
        this.killed = true;
    }
    
    private void clientMainLoop() {
        try {
            Thread.sleep(50);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(this.connection != null && this.connection.isConnected()) {
            try {
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

    public void disconnect() {
        if(this.connection == null) {
            return;
        }

        synchronized(this.connection) {
            if(this.connection != null && this.connection.isConnected()) {
                this.connection.sendMessageImmediately(new Datapack(
                    "ControlConnect",
                    new ControlConnect("Disconnected")
                ));
            }
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
                Logger.logError("Error occurred while disconnecting");
            }
            this.clearUserList();
            this.connection.killConnection();
            this.connection = null;
        }
    }

    protected void controlConnect(ControlConnect controlconnect) {
        Logger.logInfo("You are kicked out the sever! Cause: %s", controlconnect.getCause());
        connection.killConnection();
    }

    protected void synchronizeMusicProgress(MusicProgress musicProgress) {
        if(musicProgress.isEmpty()) {
            if(music != null) {
                music.pauseMusic();
                music = null;
            }
            setMusicList(null);
            return;
        }

        //sync music list
        if(this.musicList == null || !this.musicList.equals(musicProgress.getMusicList())) {
            this.musicList = musicProgress.getMusicList();
            ImpartUI.displaySongList(musicList);
            for(String id : musicProgress.getMusicList()) {
                if(!musicFileHashMap.containsKey(id)) {
                    MusicDownloader.downloadMusicFileAsync(Netease.buildNeteaseURL(id), "./data");
                }
            }
        }

        //sync music player
        if(music == null || music.getPlayingID() == null || (!music.getPlayingID().equals(musicProgress.getMusicId()))) {
            this.useTargetMusic(musicProgress.getMusicId(), false);
        }

        //sync musicplayer status
        if(musicProgress.getMusicStatus() != null) {
            switch(musicProgress.getMusicStatus()) {
                case "pause":
                    music.pauseMusic();
                    break;
                case "play":
                    music.playMusic();
                    break;
                default:
                    Logger.logError("Unknown music player status tag %s", musicProgress.getMusicStatus());
                    break;
            }
        }

        //sync musicplayer process
        if(this.music != null && (Math.abs(this.music.getCurrentTime() - musicProgress.getMusicTime()) > SYNC_RANGE_SECOND)) {
            double time = musicProgress.getMusicTime();
            if(!this.music.isMediaReady()) {
                this.music.addOnReady(() -> {
                    this.music.setPositionMusic(time);
                });
            } else {
                this.music.setPositionMusic(time);
            }
        }
    }

    private void setMusicList(List<String> idList) {
        this.musicList = idList;
        ImpartUI.displaySongList(musicList);
    }

    protected void getMusicProcess() {
        if(music == null && this.musicList == null){
            connection.sendMessage(new Datapack("SynchronizeMusic", new MusicProgress()));
            return;
        }

        MusicProgress toSend = music == null ? 
        new MusicProgress(null, 0.0, null, this.musicList) : 
        new MusicProgress(
            music.getMusicId(),
            music.getCurrentTime(),
            music.getStatus(),
            this.musicList
        );
        connection.sendMessage(
            new Datapack(
                "SynchronizeMusic",
                toSend
            )
        );
    }

    public Connection getConnection() {
        return connection;
    }

    protected void checkLoginInfo(UserInfo loginInfo) {
        Logger.logInfo(loginInfo.toString());
    }

    @Deprecated
    public void saveMusicHashMap() {
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

    @Deprecated
    @SuppressWarnings("unchecked")
    protected void readMusicHashMap() {
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
                this.initializeNewMusicHashMap();
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
                this.initializeNewMusicHashMap();
            }
            input.close();
            in.close();
        } catch (EOFException eof) {
            Logger.logError("Empty music list file, creating new hashmap");
            this.musicFileHashMap = new HashMap<>();
            musicFileHashMap.put("nothing", null);
            this.initializeNewMusicHashMap();
            this.saveMusicHashMap();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(this.musicFileHashMap != null) {
            Logger.logInfo("Music file cache list read, showing as follows:");
            for(File cached : musicFileHashMap.values()) {
                Logger.logInfo("name: %s\t size(KB): %d", cached.getName(), cached.length() / 1024L);
            }
        }
    }

    private void initializeNewMusicHashMap() {
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
                Logger.logInfo("Found music file %s.mp3, size(KB): %d", id, child.length() / 1024L);
            }
        }
    }

    private MusicPlayer getMusicPlayer(String musicId) {
        if(musicId == null) {
            Logger.logWarning("Passing null musicID to getMusicPlayer(), returning null");
            return null;
        }
        File musicFile = null;
        if(musicFileHashMap.containsKey(musicId)) {
            musicFile = musicFileHashMap.get(musicId);
            if(!musicFile.exists()) {
                musicFile = null;
                musicFileHashMap.remove(musicId);
            }
        }
        if(musicFile == null) {
            String downloadPath = "./data";
            MusicDownloader asyncDownloading = MusicDownloader.getAsyncDownloading();
            boolean asyncDownloaded = false;

            if(MusicDownloader.isWaitingAsyncDownload(musicId)) {
                MusicDownloader.removeAsyncWaiter(musicId);
            }

            if(asyncDownloading != null && Netease.downloadURLtoID(asyncDownloading.getUrlPath()).equals(musicId)) {
                try {
                    MusicDownloader.downloadCondition.await();
                    musicFile = musicFileHashMap.get(musicId);                   
                    asyncDownloaded = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.logError(e.getMessage());
                    asyncDownloaded = false;
                }
            }
            if(!asyncDownloaded) {
                musicFile = new MusicDownloader(Netease.buildNeteaseURL(musicId), downloadPath).downloadMusicFile();
                if(!musicFile.exists()) {
                    Logger.logError("No music file was downloaded due to downloader error");
                    return null;
                }
                this.cacheFile(musicId, musicFile);
            }
        }
        return new MusicPlayer(musicFile.getAbsolutePath(), musicId);
    }

    public void connect(String host, int port, String loginAs) {
        this.connection = new Connection(host, port, new UserInfo(loginAs, "login"));
        this.connection.setOnDisconnected(() -> {
            this.pauseMusic(false);
            this.musicList.clear();
            ImpartUI.infoToUI("你已经从服务器断开连接");
            ImpartUI.clearMusicList();
            ImpartUI.resetPlayerUIDisplay();
        });
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
        this.requestStandardUser();
        synchronizeMusicProgress(progress);
    }

    @Override
    public void onSetVolume(float volume){
        music.setVolume(volume);
    }
    
    public List<String> getMusicList() {
        return musicList;
    }

    public void addMusic(String id) {
        if (findMusic(id)) {
            ImpartUI.infoToUI("歌曲已经存在于歌单中");
            return;
        }
        this.requestStandardUser();
        musicList.add(id);
        ImpartUI.displaySongList(musicList);
    }
    
    public boolean findMusic(String id) {
        return musicList.contains(id);
    }
    
    public void deleteMusic(String id) {
        if (!findMusic(id)) {
            Logger.logWarning("Ignoring missing music delete request of %s", id);
            Logger.logWarning("Cause: music unfound in list");
            return;
        }
        this.requestStandardUser();
        int elemIndex = this.musicList.indexOf(id);
        musicList.remove(id);
        ImpartUI.displaySongList(musicList);
        if(this.musicList.size() == 0 && this.music != null) {
            this.music.pauseMusic();
            this.music = null;
            return;
        }

        if(elemIndex > this.playingMusicIndex) {
            return;
        }
        this.playingMusicIndex = Math.min(
            this.playingMusicIndex,
            this.musicList.size() - 1
        );
        boolean isPlaying = this.music == null ? false : this.music.isPlaying();
        this.pauseMusic(false);
        this.useTargetMusic(this.musicList.get(this.playingMusicIndex), killed);
        if(isPlaying) {
            this.playMusic(false);
        }
    }
    
    public String getMusic(int i) {
        if (!musicList.isEmpty()) {
            i = i % musicList.size();
            return musicList.get(i);
        }
        return null;
    }
    
    public void swapMusic(int a, int b) {
        this.requestStandardUser();
        String idA = this.musicList.get(a);
        String idB = this.musicList.get(b);
        this.musicList.set(a, idB);
        this.musicList.set(b, idA);
        ImpartUI.displaySongList(musicList);
    }
    
    public void insertMusic(String id, int place) {
        this.requestStandardUser();
        this.musicList.add(place, id);
        ImpartUI.displaySongList(musicList);
    }
    
    public void useTargetMusic(String id, boolean doRequest) {
        MusicPlayer old = this.music;
        MusicPlayer newPlayer = getMusicPlayer(id);
        if(newPlayer == null) {
            Logger.logWarning("Play music %s failed", id);
        }
        if(doRequest) {
            this.requestStandardUser();
        }
        this.playingMusicIndex = this.musicList.indexOf(id);
        this.music = newPlayer;
        onChangeMusic(old, this.music);
    }

    public void displayUserList(List<UserSimpleInfo> list) {
        ImpartUI.displayUserList(list);
    }

    public void clearUserList() {
        Logger.logInfo("Cleared user list");
        ImpartUI.clearUserList();
    }

    public boolean isConnected() {
        return this.connection != null && this.connection.isConnected();
    }

    public boolean fileExists(String id) {
        return this.musicFileHashMap.containsKey(id);
    }

    public File getCachedFile(String id) {
        return this.musicFileHashMap.get(id);
    }

    public void cacheFile(String id, File file) {
        this.musicFileHashMap.put(id, file);
    }

    public MusicPlayer getCurrentMusic() {
        return this.music;
    }

    private final Runnable nextMusicStrategy = new Runnable() {
        @Override
        public void run() {
            if(Client.this.getMusicList().size() == 0) {
                return;
            }
            Client.this.playingMusicIndex++;
            Client.this.playingMusicIndex %= Client.this.getMusicList().size();
            Client.this.pauseMusic(false);
            Client.this.useTargetMusic(Client.this.getMusicList().get(Client.this.playingMusicIndex), false);
            Client.this.playMusic(false);
        }
    };

    private final Runnable previousMusicStrategy = new Runnable() {
        @Override
        public void run() {
            if(Client.this.getMusicList().size() == 0) {
                return;
            }
            Client.this.playingMusicIndex--;
            if(Client.this.playingMusicIndex == -1) {
                Client.this.playingMusicIndex = Client.this.getMusicList().size() - 1;
            }
            Client.this.pauseMusic(false);
            Client.this.useTargetMusic(Client.this.getMusicList().get(Client.this.playingMusicIndex), false);
            Client.this.playMusic(false);
        }
    };

    public void onChangeMusic(MusicPlayer oldVal, MusicPlayer newVal) {
        if(oldVal != null) {
            oldVal.setOnEnd(null);
            oldVal.clearOnReady();
            oldVal.pauseMusic();
        }

        if(newVal != null) {
            newVal.addOnReady(() -> {
                Logger.logInfo("Music player of %s is ready!", newVal.getMusicId());
                ImpartUI.bindLabel(newVal.getTotalTimeDuration());
                ImpartUI.bindProgressDisplay(newVal.getPlayerProcessProperty());
                ImpartUI.bindProgressSetter(newVal);
                ImpartUI.bindShower(newVal.getMusicId());
            });
            newVal.setOnEnd(nextMusicStrategy);
        }
    }

    public void requestStandardUser() {
        Logger.logInfo("Requesting standard user");
        this.connection.sendMessageImmediately(new Datapack("StandardRequest", null));
        PackageResolver.ignoreSyncCounter = REQUEST_STD_IGNORE_COUNT;
    }

    public int getPlayingMusicIndex() {
        return playingMusicIndex;
    }

    public void playMusic(boolean doRequest) {
        if(!this.isConnected()) {
            return;
        }

        if(this.music == null && this.musicList != null && !this.musicList.isEmpty()) {
            if(doRequest) {
                this.requestStandardUser();
            }
            this.playingMusicIndex = 0;
            this.music = getMusicPlayer(this.musicList.get(playingMusicIndex));
            this.onChangeMusic(null, music);
        }
        if(this.music != null) {
            this.music.resumeMusic();
        }
        return;
    }

    public void pauseMusic(boolean doRequest) {
        if(this.music == null) {
            return;
        }
        if(doRequest) {
            this.requestStandardUser();
        }
        this.music.pauseMusic();
    }

    public boolean isPlaying() {
        return this.music == null ? false : this.music.isPlaying();
    }

    public void goNextMusic(boolean doRequest) {
        if(doRequest) {
            this.requestStandardUser();
        }
        this.nextMusicStrategy.run();
    }

    public void goPreviousMusic(boolean doRequest) {
        if(doRequest) {
            this.requestStandardUser();
        }
        this.previousMusicStrategy.run();
    }
}