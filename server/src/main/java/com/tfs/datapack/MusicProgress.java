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
    
    /**
     * 获取音乐id
     * @return 音乐id
     */
    public String getMusicId(){
        return musicId;
    }

    /**
     * 获取音乐播放进度
     * @return 进度时间（s）
     */
    public double getMusicTime(){
        return musicTime;
    }

    /**
     * 音乐播放的状态
     * @return 播放（play）或暂停（pause）
     */
    public String getMusicStatus(){
        return musicStatus;
    }

    /**
     * 获取是否为空包
     * @return 是否为空包
     */
    public boolean isEmpty(){
        return this.musicId == null && this.musicTime == 0.0 && this.musicStatus == null && this.musicList == null;
    }

    /**
     * 获取音乐列表
     * @return 音乐id列表
     */
    public List<String> getMusicList() {
        return musicList;
    }
}
