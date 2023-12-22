package com.tfs.ui;

public class MusicDetails {
    private String name;
    private String id;

    public MusicDetails(String id, String name) {
        this.name = name;
        this.id = id;
    }

    /**
     * 获取音乐id
     * @return 音乐id
     */
    public String getId() {
        return id;
    }

    /**
     * 获取音乐名称
     * @return 音乐名称
     */
    public String getName() {
        return name;
    }
}
