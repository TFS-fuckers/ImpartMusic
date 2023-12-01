package com.tfs.client;

import com.tfs.datapack.ControlConnect;
import com.tfs.datapack.Datapack;
import com.tfs.datapack.PlayMusicInstruction;
import com.tfs.datapack.MusicProgress;
import com.tfs.datapack.UserInfo;
import com.tfs.logger.Logger;

public class PackageResolver {
    protected static void resolveDatapack(Datapack datapack){
        Client client = Client.INSTANCE();
        switch (datapack.identifier) {
            case "PlayMusic":
                client.playMusic(datapack.deserializeContent(PlayMusicInstruction.class));
                break;

            case "ControlConnect":
                client.controlConnect(datapack.deserializeContent(ControlConnect.class));
                break;

            case "SynchronizeMusic":
                client.synchronizeMusicProgress(datapack.deserializeContent(MusicProgress.class));
                break;

            case "CheckLoginInfo":
                client.checkLoginInfo(datapack.deserializeContent(UserInfo.class));
                break;
            
            case "GetMusicProcess":
                client.getMusicProcess();
                break;
            
            case "SimpleString":
                Logger.logInfo(datapack.content);
                break;

            case "UserInfo":
                Logger.logInfo(datapack.deserializeContent(UserInfo.class).toString());
                break;
            
            case "UserList":
                Logger.logInfo("get user list");
                break;
                
            default:
                Logger.logError("wrong datapack identifer: " + datapack.identifier);
                break;
        }
    }
}
