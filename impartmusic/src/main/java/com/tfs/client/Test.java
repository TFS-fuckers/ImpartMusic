package com.tfs.client;

import com.tfs.datapack.PlayMusicInstruction;

public class Test {
    public static void main(String[] args) {
        Client client = new Client();
        PlayMusicInstruction playMusicInstruction = new PlayMusicInstruction("continue", "436346833");
        client.playMusic(playMusicInstruction);
    }
}
