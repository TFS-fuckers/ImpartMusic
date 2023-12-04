package com.tfs.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonAnalyser {
    private String rawJson;
    private JsonElement jsonElement;
    private JsonObject jsonObject;

    public JsonAnalyser(String rawJson) {
        this.rawJson = rawJson;
        this.jsonElement = JsonParser.parseString(rawJson);
        this.jsonObject = jsonElement.getAsJsonObject();
    }

    public String getStringValue(String key) {
        return jsonObject.get(key).getAsString();
    }

    public JsonArray getArrayValue(String key) {
        return jsonObject.get(key).getAsJsonArray();
    }

    public String getRawJson() {
        return rawJson;
    }
}
