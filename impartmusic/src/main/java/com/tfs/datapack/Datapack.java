package com.tfs.datapack;
import com.google.gson.Gson;

public class Datapack {
    private static final Gson GSON = new Gson();
    public String identifier;
    public String content;
    
    public static Datapack toDatapack(String rawJson){
        return GSON.fromJson(rawJson, Datapack.class);
    }

    public String toJson(){
        return GSON.toJson(this);
    }

    public Datapack(String rawJson){
        Datapack source = GSON.fromJson(rawJson, Datapack.class);
        this.identifier = source.identifier;
        this.content = source.content;
    }

    public Datapack(String identifier, String content){
        this.identifier = identifier;
        this.content = content;
    }

    public static final Datapack HEARTBEAT = new Datapack("HEARTBEAT", null);
}
