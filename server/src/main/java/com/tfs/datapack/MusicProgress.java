package com.tfs.datapack;
/**
 * 音乐同步信息 有两个标识符：客户端接收统一为sync，意为调整。服务端接收sync代表自动同步，接收set代表主动同步。
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
