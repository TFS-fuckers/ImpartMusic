package com.tfs.musicplayer;

import javafx.embed.swing.JFXPanel;

import java.io.File;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        new JFXPanel();
        DownloadMusic downloadMusic = new  DownloadMusic("http://music.163.com/song/media/outer/url?id=436346833", "./data");
        File file = downloadMusic.downloadMusicFile();
        PlayMusic playMusic = new PlayMusic(file.getAbsolutePath());
        playMusic = new PlayMusic((new File("./data/" + "436346833" + ".mp3")).getAbsolutePath());
        System.out.println("--------");
        System.out.println(file.getAbsolutePath());
        System.out.println(file.getPath());
        playMusic.playMusic();
        System.out.println(playMusic.getAbsoluteFilePath());
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
