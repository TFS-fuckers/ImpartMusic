package com.tfs.musicplayer;

import javafx.scene.media.*;
import javafx.util.Duration;

public class MusicPlayer {
    private boolean isPlaying = false;
    private MediaPlayer player;
    private String absoluteFilePath;
    
    public MusicPlayer(String absoluteFilePath) {
        this.absoluteFilePath = absoluteFilePath;
        Media media = new Media("file:///" + absoluteFilePath.replace("\\", "/"));
        this.player = new MediaPlayer(media);
        isPlaying = false;
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
        player.play();
        isPlaying = true;
    }
    public void pauseMusic() {
        player.pause();
        isPlaying = false;
    }
    public void resumeMusic() {
        player.play();
        isPlaying = true;
    }
    public void setPositionMusic(double seconds) {
        player.pause();
        player.setStartTime(Duration.seconds(seconds));
        if (isPlaying) {
            player.play();
        }
    }
    public void setPositionMusic(double minutes, double seconds) {
        seconds += 60 * minutes;
        player.pause();
        player.setStartTime(Duration.seconds(seconds));
        if (isPlaying) {
            player.play();
        }
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

    public double getTotalTime() {
        player.setVolume(0);
        player.play();
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        player.pause();
        player.setStartTime(Duration.seconds(0));
        player.setVolume(20);
        return player.getCycleDuration().toSeconds();
    }
    public String getStatus() {
        System.out.println(isPlaying);
        if (isPlaying) {
            return "play";
        }
        return "pause";
    }

    public float getVolume(){
        return Double.valueOf(player.getVolume()).floatValue();
    }
}