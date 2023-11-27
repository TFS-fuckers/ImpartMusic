package com.tfs.musicplayer;

import javafx.embed.swing.JFXPanel;

import java.io.File;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        new JFXPanel();
        MusicDownloader musicDownloader = new MusicDownloader("http://music.163.com/song/media/outer/url?id=436346833", "./data");
        File file = musicDownloader.downloadMusicFile();
        MusicPlayer musicPlayer = new MusicPlayer(file.getAbsolutePath());
        musicPlayer = new MusicPlayer((new File("./data/" + "436346833" + ".mp3")).getAbsolutePath());
        System.out.println("--------");
        System.out.println(file.getAbsolutePath());
        System.out.println(file.getPath());
        musicPlayer.playMusic();
        System.out.println(musicPlayer.getAbsoluteFilePath());
        // System.out.println(playMusic.getAbsoluteFilePath().indexOf("\\"));
        // System.out.println(playMusic.getAbsoluteFilePath().indexOf("."));
        // Scanner in = new Scanner(System.in);
        while (true) {
            // String s = in.nextLine();
            // if (s.equals("get")) {
                // System.out.println(playMusic.getAbsoluteFilePath());
                // System.out.println(playMusic.getCurrentTime());
            // }
            // System.out.println(playMusic.getCurrentTime());
        }

    }
}
