package com.tfs.musicplayer;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;

import com.tfs.logger.Logger;

public class MusicDownloader {
    private String downloadPath;
    private String urlPath;
    private double downloadProgress;
    private static final Queue<MusicDownloader> ASYNC_QUEUE = new LinkedList<>();

    static {
        new Thread(() -> {
            Thread.currentThread().setName("async-downloader");
            while(true) {
                try {
                    Thread.sleep(1000);
                    while(ASYNC_QUEUE.size() > 0) {
                        ASYNC_QUEUE.remove().downloadMusicFile();
                    }
                } catch (Exception e) {
                    Logger.logError("Error downloading music file asynchronously");
                    e.printStackTrace();
                }
            }
        }).start();     
    }
    
    public MusicDownloader(String urlPath, String downloadPath) {
        this.urlPath = urlPath;
        this.downloadPath = downloadPath;
    }

    public File downloadMusicFile() {
        File file = null;
        String path = null;
        try {
            URL url = new URL(this.urlPath);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.connect();
            int fileLength = httpURLConnection.getContentLength();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(httpURLConnection.getInputStream());
            String[] strings = urlPath.split("=");
            String fileName = "/" + strings[1] + ".mp3";
            path = this.downloadPath + fileName;
            System.out.println(fileName);
            file = new File(path);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
            }
            OutputStream outputStream = new FileOutputStream(file);
            int size = 0;
            int length = 0;
            byte[] buf = new byte[1024];
            while ((size = bufferedInputStream.read(buf)) != -1) {
                length += size;
                outputStream.write(buf, 0, size);
                downloadProgress = length * 100 / fileLength;
                // System.out.println(downloadProgress + "%");
            }
            bufferedInputStream.close();
            outputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println(path);
        }
        return file;
    }

    public static void downloadMusicFileAsync(String urlPath, String downloadPath) {
        ASYNC_QUEUE.add(new MusicDownloader(urlPath, downloadPath));
    }
}
