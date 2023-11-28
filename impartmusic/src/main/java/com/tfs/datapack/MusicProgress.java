package com.tfs.datapack;
/**
 * 音乐进度
 */
public class MusicProgress {
    private String musicId;
    private double musicTime;
    private String musicStatus;
    private boolean _isEmpty;
    
    public MusicProgress(String musicId, double musicTime, String musicStatus){
        this.musicId = musicId;
        this.musicTime = musicTime;
        this.musicStatus = musicStatus;
        this._isEmpty = false;
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
}
