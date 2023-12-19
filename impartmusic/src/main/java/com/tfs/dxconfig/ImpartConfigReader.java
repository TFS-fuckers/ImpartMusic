package com.tfs.dxconfig;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tfs.logger.Logger;

public class ImpartConfigReader {
    private DXConfig<IMConfig> config = new DXConfig<>(IMConfig.class, "./data/config.json", (mes) -> Logger.logInfo(mes));
    private static ImpartConfigReader INSTANCE;

    private ImpartConfigReader() {}

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

    public void save(IMConfig config) {
        Gson gson = new Gson();
        JsonObject object = JsonParser.parseString(gson.toJson(config)).getAsJsonObject();
        this.config.save(object);
    }

    public IMConfig getDeafult() {
        return this.config.getDefault();
    }
}
