package com.tfs.client;

import java.nio.charset.Charset;

import com.tfs.datapack.ControlConnect;
import com.tfs.datapack.Datapack;
import com.tfs.datapack.MusicProgress;
import com.tfs.datapack.SimpleString;
import com.tfs.datapack.UserInfo;
import com.tfs.datapack.UserSimpleInfo;
import com.tfs.logger.Logger;
import com.tfs.ui.ImpartUI;

public class PackageResolver {
    /**忽略的数据包个数计数器 */
    public static int ignoreSyncCounter = 0;

    /**
     * 解析数据包并行动
     * @param datapack 数据包对象
     */
    protected static void resolveDatapack(Datapack datapack){
        Client client = Client.INSTANCE();
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
                ImpartUI.infoToUI(datapack.deserializeContent(SimpleString.class).getString(Charset.forName("UTF-8")));
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
