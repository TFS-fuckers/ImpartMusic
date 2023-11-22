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
        this.setPlayer(media);
    }

    private void setPlayer(Media media) {
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnPlaying(() -> isPlaying = true);
        mediaPlayer.setOnPaused(() -> isPlaying = false);
        mediaPlayer.setOnStopped(() -> isPlaying = false);
        mediaPlayer.setOnEndOfMedia(() -> isPlaying = false);
    }
    public void changeMusicPath(String absoluteFilePath) {
        this.absoluteFilePath = absoluteFilePath;
        Media media = new Media("file:///" + absoluteFilePath.replace("\\", "/"));
        this.player = new MediaPlayer(media);
        this.setPlayer(media);
    }

    public boolean isPlaying() {
        return isPlaying;
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
    public void setPositionMusic(double seconds) {
        player.pause();
        player.setStartTime(Duration.seconds(seconds));
        player.play();
    }
    public void setPositionMusic(double minutes, double seconds) {
        seconds += 60 * minutes;
        player.pause();
        player.setStartTime(Duration.seconds(seconds));
        player.play();
    }
    public String getAbsoluteFilePath() {
        return this.absoluteFilePath;
    }
    public double getCurrentTime() {
        return player.getCurrentTime().toSeconds();
    }
}