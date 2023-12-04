package com.tfs.datapack;

import java.util.List;

/**
 * 音乐进度
 */
public class MusicProgress {
    private String musicId;
    private double musicTime;
    private String musicStatus;
    private boolean _isEmpty;
    private List<String> musicList;
    
    public MusicProgress(String musicId, double musicTime, String musicStatus, List<String> musicList) {
        this.musicId = musicId;
        this.musicTime = musicTime;
        this.musicStatus = musicStatus;
        this._isEmpty = false;
        this.musicList = musicList;
    }
    public MusicProgress(){
        this._isEmpty = true;
    }
    
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
        return _isEmpty;
    }
    public List<String> getMusicList() {
        return musicList;
    }
}
