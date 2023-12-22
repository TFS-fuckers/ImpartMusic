package com.tfs.server;

import com.tfs.datapack.Datapack;
import com.tfs.datapack.MusicProgress;
import com.tfs.datapack.SimpleString;
import com.tfs.logger.Logger;
import com.tfs.modloader.ModLoader;

public class PackageResolver {
    /**
     * 解析数据包并行动
     * @param datapack 数据包对象
     */
    protected static void packageResolver(Datapack datapack){
        Server server = Server.INSTANCE();
        ModLoader.onResolveDatapack(datapack);
        switch (datapack.identifier) {
            case "SynchronizeMusic":
                server.synchronizeMusicProgress(datapack.deserializeContent(MusicProgress.class));
                break;

            case "StandardRequest":
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

            case "SimpleString":
                SimpleString message = datapack.deserializeContent(SimpleString.class);
                String msg = String.format(
                    "%s 说： %s", 
                    datapack.senderTag,
                    message.getString()
                );
                ServerHandler.instance().sendToAll(
                    new Datapack("SimpleString", new SimpleString(msg, "UTF-8"))
                );
                break;

            default:
                Logger.logError("Wrong Identifier in packageResolver: " + datapack.identifier);
                break;
        }
    }
}
