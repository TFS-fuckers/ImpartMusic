package com.tfs.ui;

public class MusicDetails {
    private String name;
    private String id;

    public MusicDetails(String id, String name) {
        this.name = name;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
