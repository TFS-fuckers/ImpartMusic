package com.tfs.musicplayer;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.tfs.Json.JsonAnalyser;
import com.tfs.logger.Logger;
import com.tfs.ui.MusicDetails;

public class Netease {
    private Netease() {}
    public static String buildNeteaseURL(String id) {
        return "http://music.163.com/song/media/outer/url?id=" + id;
    }

    public static String downloadURLtoID(String url) {
        int index = url.indexOf('=');
        if(index == -1) {
            return null;
        }

        return url.substring(index + 1);
    }

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
            File relocate = new File("./data/out.txt");
            PrintWriter writer = new PrintWriter(relocate);
            writer.write(jsonAnalyser.getRawJson());
            writer.close();
        } catch (Exception e) {
            Logger.logError("Get music details failed, maybe it's a network failure or wrong music id");
        }
        return result;
    }
}
