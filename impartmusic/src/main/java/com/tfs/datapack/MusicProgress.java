package com.tfs.datapack;
/**
 * 音乐进度
 */
public class MusicProgress {
    private String musicId;
    private double musicTime;
    
    public MusicProgress(String musicId, double musicTime){
        this.musicId = musicId;
        this.musicTime = musicTime;
    }
    
    public String getMusicId(){
        return musicId;
    }
    public double getMusicTime(){
        return musicTime;
    }
}
