package com.tfs.client;

import com.tfs.datapack.ControlConnect;
import com.tfs.datapack.Datapack;
import com.tfs.datapack.PlayMusic;
import com.tfs.datapack.SynchronizeMusic;
import com.tfs.datapack.UserInfo;
import com.tfs.logger.Logger;

public class PackageResolver {
    protected static void resolveDatapack(Datapack datapack){
        Client client = Client.INSTANCE();
            switch (datapack.identifier) {
                case "PlayMusic":
                    client.playMusic(datapack.deserializeContent(PlayMusic.class));
                    break;
    
                case "ControlConnect":
                    client.controlConnect(datapack.deserializeContent(ControlConnect.class));
                    break;
    
                case "SynchronizeMusic":
                    client.synchronizeMusic(datapack.deserializeContent(SynchronizeMusic.class));
                    break;
    
                case "CheckLoginInfo":
                    client.checkLoginInfo(datapack.deserializeContent(UserInfo.class));
                    break;
    
                default:
                    Logger.logError("wrong datapack identifer");
                    break;
            }
    }
}
