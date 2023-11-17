package com.tfs.client;

import com.tfs.datapack.Datapack;
import com.tfs.datapack.UserInfo;
import com.tfs.logger.Logger;

public class PackageResolver {
    protected static void resolveDatapack(Datapack datapack){
        Client client = Client.INSTANCE();
            switch (datapack.identifier) {
                case "PlayMusic":
                    client.playMusic();
                    break;
    
                case "ControlConnect":
                    client.controlConnect();
                    break;
    
                case "SynchronizeMusic":
                    client.synchronizeMusic();
                    break;
    
                case "CheckLoginInfo":
                    client.checkLoginInfo(datapack.deserializeContent(UserInfo.class));
                    break;
    
                case "Adjust":
                    client.AdjustPlayingSchedule();
                    break;
            
                default:
                    Logger.logError("wrong datapack identifer");
                    break;
            }
    }
}
