package com.tfs.datapack;
/**
 * 控制音乐播放暂停、切换歌曲，由某一客户端发出，传至所有用户
 */
public class PlayMusicInstruction {
    /**
     * pause / continue / change
     */
    public String opType;
    public String musicId;
    public PlayMusicInstruction(String opType){
        this.opType = opType;
        musicId = null;
    }
    /**
     * 
     * @param musicId UI接受操作后直接给出歌曲ID
     */
    public PlayMusicInstruction(String opType, String musicId){
        this.opType = opType;
        this.musicId = musicId;
    }

}
