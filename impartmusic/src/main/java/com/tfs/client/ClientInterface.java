package com.tfs.client;

import com.tfs.datapack.MusicProgress;

public interface ClientInterface {
    public void onSetProgress(MusicProgress progress);
    public void onSetVolume(float volume);
}