package com.tfs.modloader;

import com.tfs.datapack.Datapack;
import com.tfs.server.Server;
import com.tfs.server.ServerHandler;

public interface DatapackListener {
    public void handle(Datapack message, Server server, ServerHandler serverHandler);
}
