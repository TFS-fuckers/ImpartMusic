package com.tfs.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 分析某Json文件的实体
 */
public class JsonAnalyser {
    private String rawJson;
    private JsonElement jsonElement;
    private JsonObject jsonObject;

    /**
     * 生成一个Json分析器
     * @param rawJson 分析的对象
     */
    public JsonAnalyser(String rawJson) {
        this.rawJson = rawJson;
        this.jsonElement = JsonParser.parseString(rawJson);
        this.jsonObject = jsonElement.getAsJsonObject();
    }

    /**
     * 获取某键的对应String值
     * @param key 键
     * @return 对应的String值
     */
    public String getStringValue(String key) {
        return jsonObject.get(key).getAsString();
    }

    /**
     * 对应某键对应的表型值
     * @param key 键
     * @return 对应的表型值
     */
    public JsonArray getArrayValue(String key) {
        return jsonObject.get(key).getAsJsonArray();
    }

    /**
     * 获取Json原文本
     * @return Json原文本
     */
    public String getRawJson() {
        return rawJson;
    }
}
