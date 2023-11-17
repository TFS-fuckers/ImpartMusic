package com.tfs.musicplayer;

import javax.sound.sampled.*;
import java.io.*;

import javafx.scene.media.*;
import javafx.util.Duration;

public class PlayMusic {
    private MediaPlayer player;
    private String filePath;
    public PlayMusic(String filePath) {
        this.filePath = filePath;
        Media media = new Media("file:///" + filePath.replace("\\", "/"));
        this.player = new MediaPlayer(media);
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