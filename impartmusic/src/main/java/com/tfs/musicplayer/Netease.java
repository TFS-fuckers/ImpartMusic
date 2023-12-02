package com.tfs.musicplayer;

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
}
