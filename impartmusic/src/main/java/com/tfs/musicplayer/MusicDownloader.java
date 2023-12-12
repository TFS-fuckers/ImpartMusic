package com.tfs.musicplayer;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.tfs.client.Client;

public class MusicDownloader {
    private String downloadPath;
    private String urlPath;
    private double downloadProgress;
    
    private static final Queue<MusicDownloader> ASYNC_QUEUE = new LinkedList<>();
    private static MusicDownloader asyncDownloadTask = null;
    private static final Lock conditionLock = new ReentrantLock();
    public static final Condition downloadCondition = conditionLock.newCondition();
    
    public static MusicDownloader getAsyncDownloading() {
        return asyncDownloadTask;
    }
    
    public static void downloadMusicFileAsync(String urlPath, String downloadPath) {
        ASYNC_QUEUE.add(new MusicDownloader(urlPath, downloadPath));
    }
    
    

    public MusicDownloader(String urlPath, String downloadPath) {
        this.urlPath = urlPath;
        this.downloadPath = downloadPath;
    }
    
    public File downloadMusicFile() {
        File file = null;
        String path = null;
        try {
            String[] strings = urlPath.split("=");
            if(Client.INSTANCE().fileExists(strings[1])) {
                return Client.INSTANCE().getCachedFile(strings[1]);
            }

            String fileName = "/" + strings[1] + ".mp3";
            URL url = new URL(this.urlPath);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setConnectTimeout(10);
            httpURLConnection.connect();
            int fileLength = httpURLConnection.getContentLength();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(httpURLConnection.getInputStream());
            path = this.downloadPath + fileName;
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
            }
            bufferedInputStream.close();
            outputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    
    public String getUrlPath() {
        return urlPath;
    }

    public double getDownloadProgress() {
        return downloadProgress;
    }
}
