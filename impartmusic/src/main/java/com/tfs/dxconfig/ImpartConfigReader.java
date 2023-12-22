package com.tfs.dxconfig;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tfs.logger.Logger;

/**
 * 专用于ImpartMusic的配置文件阅读器
 */
public class ImpartConfigReader {
    private DXConfig<IMConfig> config = new DXConfig<>(IMConfig.class, "./data/config.json", (mes) -> Logger.logInfo(mes));
    private static ImpartConfigReader INSTANCE;

    private ImpartConfigReader() {}

    /**
     * 获取单例
     * @return 单例
     */
    public static ImpartConfigReader instance() {
        if(INSTANCE == null) {
            INSTANCE = new ImpartConfigReader();
        }
        return INSTANCE;
    }

    /**
     * 获取某个键的对应值
     * @param key 键
     * @return 对应值
     */
    public JsonElement get(String key) {
        return config.getValue(key);
    }

    /**
     * 获取该阅读器是否可用
     * @return 是否可用
     */
    public boolean isValid() {
        return config.isValid();
    }

    /**
     * 初始化阅读器
     */
    public static void initialize() {
        INSTANCE = new ImpartConfigReader();
    }

    /**
     * 保存某配置类
     * @param config 配置类实例
     */
    public void save(IMConfig config) {
        Gson gson = new Gson();
        JsonObject object = JsonParser.parseString(gson.toJson(config)).getAsJsonObject();
        this.config.save(object);
    }

    /**
     * 获取默认配置类信息
     * @return 默认配置类
     */
    public IMConfig getDeafult() {
        return this.config.getDefault();
    }
}
