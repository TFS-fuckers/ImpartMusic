package com.tfs.dxconfig;

import com.google.gson.JsonElement;
import com.tfs.logger.Logger;

public class ImpartConfigReader {
    private DXConfig<IMConfig> config = new DXConfig<>(IMConfig.class, "./config/config.json", (mes) -> Logger.logInfo(mes));
    private static ImpartConfigReader INSTANCE;

    public static ImpartConfigReader instance() {
        if(INSTANCE == null) {
            INSTANCE = new ImpartConfigReader();
        }
        return INSTANCE;
    }

    public JsonElement get(String key) {
        return config.getValue(key);
    }

    public boolean isValid() {
        return config.isValid();
    }

    public static void initialize() {
        INSTANCE = new ImpartConfigReader();
    }
}
