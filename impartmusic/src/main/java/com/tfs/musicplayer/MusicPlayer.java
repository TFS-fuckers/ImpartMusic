package com.tfs.musicplayer;

import java.util.LinkedList;
import java.util.List;

import com.tfs.logger.Logger;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.media.*;
import javafx.util.Duration;

public class MusicPlayer {
    private boolean isPlaying = false;
    private MediaPlayer player;
    private String absoluteFilePath;
    private ChangeListener<Duration> processChangeListener = null;
    private String playingID;
    private List<Runnable> onReadyListeners = new LinkedList<>();
    private boolean isReady = false;

    public MusicPlayer(String absoluteFilePath, String playingID) {
        this.absoluteFilePath = absoluteFilePath;
        Media media = new Media("file:///" + absoluteFilePath.replace("\\", "/"));
        this.player = new MediaPlayer(media);
        isPlaying = false;
        this.playingID = playingID;
        this.player.setOnReady(() -> {
            for(Runnable action : onReadyListeners) {
                action.run();
            }
        });
        this.onReadyListeners.add(() -> this.isReady = true);
    }

    /**
     * 更改音乐播放器的文件路径
     * @param absoluteFilePath 绝对路径
     */
    public void changeMusicPath(String absoluteFilePath) {
        this.absoluteFilePath = absoluteFilePath;
        Media media = new Media("file:///" + absoluteFilePath.replace("\\", "/"));
        this.player = new MediaPlayer(media);
        this.isPlaying = false;
    }

    /**
     * 获取音乐的播放状态
     * @return 是否正在播放
     */
    public boolean isPlaying() {
        return isPlaying;
    }

    /**
     * 播放音乐
     */
    public void playMusic() {
        if(player != null) {
            player.play();
            isPlaying = true;
        }
    }

    /**
     * 暂停音乐
     */
    public void pauseMusic() {
        try {
            synchronized(player) {
                if(player != null) {
                    player.pause();
                }
            }    
        } catch (Exception e) {
            Logger.logWarning("Exception in player pause %s", e.getMessage());
        }
        isPlaying = false;
    }

    /**
     * 继续音乐
     */
    public void resumeMusic() {
        player.play();
        isPlaying = true;
    }

    /**
     * 设置音乐的播放进度
     * @param seconds 进度（s）
     */
    public void setPositionMusic(double seconds) {
        if(player == null) {
            Logger.logError("Music process set failed, because player is null");
            return;
        }
        player.pause();
        player.seek(Duration.seconds(seconds));
        if (isPlaying) {
            player.play();
        }
    }

    /**
     * 设置音乐的播放进度
     * @param minutes 分钟
     * @param seconds 秒
     */
    public void setPositionMusic(double minutes, double seconds) {
        this.setPositionMusic(minutes * 60 + seconds);
    }

    /**
     * 设置音量
     * @param volume 音量大小（0~1.0f）
     */
    public void setVolume(float volume){
        player.setVolume(volume);
    }

    /**
     * 获取正在播放的绝对路径
     * @return 绝对路径
     */
    public String getAbsoluteFilePath() {
        return this.absoluteFilePath;
    }

    /**
     * 获取正在播放的音乐id
     * @return 音乐id
     */
    public String getMusicId(){
        return this.absoluteFilePath.substring(this.absoluteFilePath.lastIndexOf("\\") + 1, this.absoluteFilePath.lastIndexOf("."));
    }

    /**
     * 获取播放的进度
     * @return 播放进度（s）
     */
    public double getCurrentTime() {
        return player.getCurrentTime().toSeconds();
    }

    /**
     * 获取播放的进度
     * @return 播放进度（Duration格式）
     */
    public Duration getTotalTimeDuration() {
        return player.getTotalDuration();
    }

    /**
     * 获取音乐播放的状态（字符串格式）
     * @return 播放（play）暂停（pause）
     */
    public String getStatus() {
        if (isPlaying) {
            return "play";
        }
        return "pause";
    }

    /**
     * 获取当前的播放音量
     * @return 播放音量（0~1.0f）
     */
    public float getVolume(){
        return Double.valueOf(player.getVolume()).floatValue();
    }

    /**
     * 获取当前音乐播放的进度属性实体
     * @return 进度属性实体
     */
    public ReadOnlyObjectProperty<Duration> getPlayerProcessProperty() {
        return this.player.currentTimeProperty();
    }

    /**
     * 设置音乐进度更改监听器
     * @param listener 监听器
     */
    public void setMusicProcessListener(ChangeListener<Duration> listener) {
        this.clearMusicProcessListener();
        this.player.currentTimeProperty().addListener(listener);
        this.processChangeListener = listener;
    }

    /**
     * 清空音乐进度更改监听器
     */
    public void clearMusicProcessListener() {
        if(this.processChangeListener != null) {
            this.player.currentTimeProperty().removeListener(this.processChangeListener);
            this.processChangeListener = null;
        }
    }

    /**
     * 获取正在播放的音乐ID
     * @return 音乐ID
     */
    public String getPlayingID() {
        return playingID;
    }

    /**
     * 设置音乐结束时的监听器
     * @param delegate 监听器
     */
    public void setOnEnd(Runnable delegate) {
        this.player.setOnEndOfMedia(delegate);
    }

    /**
     * 设置音乐准备就绪时的监听器
     * @param delegate 监听器
     */
    public void addOnReady(Runnable delegate) {
        this.onReadyListeners.add(delegate);
    }

    /**
     * 清空音乐准备就绪时的监听器
     */
    public void clearOnReady() {
        this.onReadyListeners.clear();
    }

    /**
     * 获取音乐是否准备就绪
     * @return 是否准备就绪
     */
    public boolean isMediaReady() {
        return this.isReady;
    }
}