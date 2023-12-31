package com.tfs.musicplayer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.tfs.Json.JsonAnalyser;
import com.tfs.logger.Logger;
import com.tfs.ui.MusicDetails;

/**
 * 有关网易云音乐的类
 */
public class Netease {
    private Netease() {}
    /**
     * 根据音乐id生成下载url
     * @param id 音乐id
     * @return 下载url
     */
    public static String buildNeteaseURL(String id) {
        return "http://music.163.com/song/media/outer/url?id=" + id;
    }

    /**
     * 根据下载url获取音乐id
     * @param url 下载url
     * @return 音乐id
     */
    public static String downloadURLtoID(String url) {
        int index = url.indexOf('=');
        if(index == -1) {
            return null;
        }

        return url.substring(index + 1);
    }

    /**
     * 获取某音乐的信息
     * @param id 音乐id
     * @return 音乐信息
     */
    public static MusicDetails getMusicDetails(String id) {
        String apiURL = String.format(
            "http://music.163.com/api/song/detail/?id=%s&ids=[%s]",
            id,
            id          
        );
        MusicDetails result = null;
        try {
            URL url = new URL(apiURL);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Charset", "UTF-8");
            int code = connection.getResponseCode();
            if(code != HttpURLConnection.HTTP_OK) {
                Logger.logError("Getting music details failed, code: %d", code);
                return null;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null) {
                builder.append(line);
            }
            connection.disconnect();
            reader.close();
            JsonAnalyser jsonAnalyser = new JsonAnalyser(builder.toString());
            result = new MusicDetails(
                id, 
                jsonAnalyser.getArrayValue("songs").get(0).getAsJsonObject().get("name").getAsString() + " -- " +
                jsonAnalyser.getArrayValue("songs").get(0).getAsJsonObject().getAsJsonArray("artists").get(0).getAsJsonObject()
                .get("name").getAsString()
            );
        } catch (Exception e) {
            Logger.logError("Get music details failed, maybe it's a network failure or wrong music id");
        }
        return result;
    }
}
