package com.tfs.server;

import com.tfs.datapack.Datapack;
import com.tfs.datapack.MusicProgress;
import com.tfs.datapack.PlayMusicInstruction;
import com.tfs.logger.Logger;

public class PackageResolver {
    protected static void packageResolver(Datapack datapack){
        Server server = Server.INSTANCE();
        switch (datapack.identifier) {
            case "SynchronizeMusic":
                server.synchronizeMusicProgress(datapack.deserializeContent(MusicProgress.class));
                break;

            case "PlayMusic":
                server.playMusicInstruction(datapack.deserializeContent(PlayMusicInstruction.class));
                break;
        
            default:
                Logger.logError("Wrong Identifier in packageResolver: " + datapack.identifier);
                break;
        }
    }
}
