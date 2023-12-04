package com.tfs.datapack;

import java.util.List;

/**
 * 音乐进度
 */
public class MusicProgress {
    private String musicId;
    private double musicTime;
    private String musicStatus;
    private List<String> musicList;
    
    public MusicProgress(String musicId, double musicTime, String musicStatus, List<String> musicList) {
        this.musicId = musicId;
        this.musicTime = musicTime;
        this.musicStatus = musicStatus;
        this.musicList = musicList;
    }
    public MusicProgress(){}
    
    public String getMusicId(){
        return musicId;
    }
    public double getMusicTime(){
        return musicTime;
    }
    public String getMusicStatus(){
        return musicStatus;
    }
    public boolean isEmpty(){
        return this.equals(EMPTY_PACK);
    }
    public List<String> getMusicList() {
        return musicList;
    }

    public static final MusicProgress EMPTY_PACK = new MusicProgress();
}
