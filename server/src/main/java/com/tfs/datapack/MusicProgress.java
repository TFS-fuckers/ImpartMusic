package com.tfs.datapack;
/**
 * 维护音乐播放进度 同步/修改
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
