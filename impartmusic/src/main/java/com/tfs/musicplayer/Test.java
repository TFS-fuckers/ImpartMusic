package com.tfs.musicplayer;

import java.io.File;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        DownloadMusic downloadMusic = new DownloadMusic("http://music.163.com/song/media/outer/url?id=1919555631", "./data");
        File file = downloadMusic.downloadMusicFile();
        PlayMusic playMusic = new PlayMusic(file.getPath());
        Scanner in = new Scanner(System.in);
        while (true) {
            String s = in.nextLine();
            if (s.equals("pause")) {
                playMusic.pauseMusic();
            } else if (s.equals("play")) {
                playMusic.playMusic();
            } else if (s.equals("resume")) {
                playMusic.resumeMusic();
            } else if (s.equals("jump")) {
                playMusic.setPositionMusic(60);
            }
        }
    }
}
