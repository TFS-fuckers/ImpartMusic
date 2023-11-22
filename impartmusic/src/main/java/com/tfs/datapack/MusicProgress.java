package com.tfs.datapack;
/**
 * 音乐进度
 */
public class MusicProgress {
    private String musicId;
    private String musicTime;
    
    public MusicProgress(String musicId, String musicTime){
        this.musicId = musicId;
        this.musicTime = musicTime;
    }
    
    public String getMusicId(){
        return musicId;
    }
    public String getMusicTime(){
        return musicTime;
    }
}
