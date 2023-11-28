package com.tfs.datapack;
/**
 * 音乐进度
 */
public class MusicProgress {
    private String musicId;
    private double musicTime;
    private String musicStatus;
    
    public MusicProgress(String musicId, double musicTime, String playingType){
        this.musicId = musicId;
        this.musicTime = musicTime;
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
}
