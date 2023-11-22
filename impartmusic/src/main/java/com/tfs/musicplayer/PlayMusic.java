package com.tfs.musicplayer;

import javafx.scene.media.*;
import javafx.util.Duration;

public class PlayMusic {
    private boolean isPlaying = false;
    private MediaPlayer player;
    private String absoluteFilePath;
    
    public PlayMusic(String absoluteFilePath) {
        this.absoluteFilePath = absoluteFilePath;
        Media media = new Media("file:///" + absoluteFilePath.replace("\\", "/"));
        this.player = new MediaPlayer(media);
        player.setOnPlaying(() -> isPlaying = true);
        player.setOnPaused(() -> isPlaying = false);
        player.setOnStopped(() -> isPlaying = false);
        player.setOnEndOfMedia(() -> isPlaying = false);
    }

    public void changeMusicPath(String absoluteFilePath) {
        this.absoluteFilePath = absoluteFilePath;
        Media media = new Media("file:///" + absoluteFilePath.replace("\\", "/"));
        this.player = new MediaPlayer(media);
        player.setOnPlaying(() -> isPlaying = true);
        player.setOnPaused(() -> isPlaying = false);
        player.setOnStopped(() -> isPlaying = false);
        player.setOnEndOfMedia(() -> isPlaying = false);
    }

    public void playMusic() {
        player.play();
    }
    public void pauseMusic() {
        player.pause();
    }
    public void resumeMusic() {
        player.play();
    }
    public void setPositionMusic(int seconds) {
        player.seek(Duration.seconds(seconds));
    }
    public void setPositionMusic(int minutes, int seconds) {
        seconds += 60 * minutes;
        player.seek(Duration.seconds(seconds));
    }
}