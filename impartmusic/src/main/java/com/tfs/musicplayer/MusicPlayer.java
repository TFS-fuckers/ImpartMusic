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
    public void changeMusicPath(String absoluteFilePath) {
        this.absoluteFilePath = absoluteFilePath;
        Media media = new Media("file:///" + absoluteFilePath.replace("\\", "/"));
        this.player = new MediaPlayer(media);
        this.isPlaying = false;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void playMusic() {
        if(player != null) {
            player.play();
            isPlaying = true;
        }
    }
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
    public void resumeMusic() {
        player.play();
        isPlaying = true;
    }
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
    public void setPositionMusic(double minutes, double seconds) {
        this.setPositionMusic(minutes * 60 + seconds);
    }
    public void setVolume(float volume){
        player.setVolume(volume);
    }
    public String getAbsoluteFilePath() {
        return this.absoluteFilePath;
    }
    public String getMusicId(){
        return this.absoluteFilePath.substring(this.absoluteFilePath.lastIndexOf("\\") + 1, this.absoluteFilePath.lastIndexOf("."));
    }
    public double getCurrentTime() {
        return player.getCurrentTime().toSeconds();
    }

    public Duration getTotalTimeDuration() {
        return player.getTotalDuration();
    }

    public String getStatus() {
        if (isPlaying) {
            return "play";
        }
        return "pause";
    }

    public float getVolume(){
        return Double.valueOf(player.getVolume()).floatValue();
    }

    public ReadOnlyObjectProperty<Duration> getPlayerProcessProperty() {
        return this.player.currentTimeProperty();
    }

    public void setMusicProcessListener(ChangeListener<Duration> listener) {
        this.clearMusicProcessListener();
        this.player.currentTimeProperty().addListener(listener);
        this.processChangeListener = listener;
    }

    public void clearMusicProcessListener() {
        if(this.processChangeListener != null) {
            this.player.currentTimeProperty().removeListener(this.processChangeListener);
            this.processChangeListener = null;
        }
    }

    public String getPlayingID() {
        return playingID;
    }

    public void setOnEnd(Runnable delegate) {
        this.player.setOnEndOfMedia(delegate);
    }

    public void addOnReady(Runnable delegate) {
        this.onReadyListeners.add(delegate);
    }

    public void clearOnReady() {
        this.onReadyListeners.clear();
    }

    public boolean isMediaReady() {
        return this.isReady;
    }
}