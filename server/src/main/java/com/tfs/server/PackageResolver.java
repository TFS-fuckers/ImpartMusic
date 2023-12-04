package com.tfs.server;

import com.tfs.datapack.Datapack;
import com.tfs.datapack.MusicProgress;
import com.tfs.logger.Logger;

public class PackageResolver {
    protected static void packageResolver(Datapack datapack){
        Server server = Server.INSTANCE();
        switch (datapack.identifier) {
            case "SynchronizeMusic":
                server.synchronizeMusicProgress(datapack.deserializeContent(MusicProgress.class));
                break;

            case "StrandardRequest":
                server.setStandardUserIndex(ServerHandler.instance().getUserIndex(datapack.senderTag));
                break;

            case "PlayMusic":
                break;
        
            case "ControlConnect":
                User user = ServerHandler.instance().getUser(datapack.senderTag);
                try {
                    user.getHandler().killConnection();
                } catch (Exception e) {
                    Logger.logWarning("Failed to kill connection with user's datapack instruction");
                    e.printStackTrace();
                }
                break;

            default:
                Logger.logError("Wrong Identifier in packageResolver: " + datapack.identifier);
                break;
        }
    }
}
