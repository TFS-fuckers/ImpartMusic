import javafx.embed.swing.JFXPanel;

import java.io.File;
import java.util.Scanner;

import com.tfs.musicplayer.DownloadMusic;
import com.tfs.musicplayer.PlayMusic;

public class ClientTest {
    public static void main(String[] args) {
        new JFXPanel();
        DownloadMusic downloadMusic = new DownloadMusic("http://music.163.com/song/media/outer/url?id=436346833", "./data");
        File file = downloadMusic.downloadMusicFile();
        PlayMusic playMusic = new PlayMusic(file.getAbsolutePath());
        Scanner in = new Scanner(System.in);
        while (true) {
            String str = in.nextLine();
            String[] s = str.split("\\s+");
            if (s[0].equals("pause")) {
                playMusic.pauseMusic();
            } else if (s[0].equals("play")) {
                playMusic.playMusic();
            } else if (s[0].equals("resume")) {
                playMusic.resumeMusic();
            } else if (s[0].equals("jump")) {
                if (s.length == 2) {
                    playMusic.setPositionMusic(Integer.parseInt(s[1]));
                } else if (s.length == 3) {
                    playMusic.setPositionMusic(Integer.parseInt(s[1]), Integer.parseInt(s[2]));
                }
            }
        }
    }
}