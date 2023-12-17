package com.tfs.dxconfig;

public class IMConfig implements Config{
    public int REQUEST_STD_IGNORE_COUNT = 2;
    
    public double MAX_SYNC_INTERVAL = 0.5;
    
    public String MUSIC_LIST_PATH = "./data/MusicList.dat"; 

    public double SYNC_RANGE_SECOND = 1.0;

    public int HEART_BEAT_INTERVAL_MILLISECONDS = 1000;

    public int NO_RESPONSE_TIMEOUT_TRIES = 5;
    
    public int VERTIFICATION_MAX_TRIES = 10;
}