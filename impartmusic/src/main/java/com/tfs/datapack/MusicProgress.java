package com.tfs.datapack;
/**
 * 间隔同步所有用户的播放进度，由服务端(?存疑)自动发出，传至所有用户
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
