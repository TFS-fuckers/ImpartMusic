package com.tfs.client;

import com.tfs.datapack.ControlConnect;
import com.tfs.datapack.Datapack;
import com.tfs.datapack.MusicProgress;
import com.tfs.datapack.UserInfo;
import com.tfs.datapack.UserSimpleInfo;
import com.tfs.logger.Logger;
import com.tfs.ui.ImpartUI;

public class PackageResolver {
    public static int ignoreSyncCounter = 0;

    protected static void resolveDatapack(Datapack datapack){
        Client client = Client.INSTANCE();
        Logger.logInfo("Resolving datapack");
        switch (datapack.identifier) {
            case "ControlConnect":
                client.controlConnect(datapack.deserializeContent(ControlConnect.class));
                break;

            case "SynchronizeMusic":
                if(ignoreSyncCounter != 0) {
                    ignoreSyncCounter --;
                    break;
                }
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

            case "LoginUser":
                UserInfo userInfo = datapack.deserializeContent(UserInfo.class);
                if(Client.INSTANCE().getConnection().getUserInfo().getName().equals(userInfo.getName()) == false)
                    ImpartUI.infoToUI("User " + userInfo.getName() + " joins in.");
                break;

            case "LogoutUser":
                userInfo = datapack.deserializeContent(UserInfo.class);
                if(Client.INSTANCE().getConnection().getUserInfo().getName().equals(userInfo.getName()) == false)
                    ImpartUI.infoToUI("User " + userInfo.getName() + " is out.");
                break;
            
            case "UserList":
                Client.INSTANCE().displayUserList(datapack.deserializeToList(UserSimpleInfo.class));
                break;
                
            default:
                Logger.logError("wrong datapack identifer: " + datapack.identifier);
                break;
        }
    }
}
