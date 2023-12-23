package com.tfs.modloader;

import com.tfs.datapack.Datapack;
import com.tfs.server.Server;
import com.tfs.server.ServerHandler;

/**数据包监听器 */
public interface DatapackListener {
    /**
     * 监听器
     * @param message 数据包
     * @param server 服务器实例
     * @param serverHandler 服务器网络处理器实例
     */
    public void handle(Datapack message, Server server, ServerHandler serverHandler);
}
