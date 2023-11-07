package com.tfs.datapack;
import com.google.gson.Gson;

/**服务器数据包 */
public class Datapack {
    /**用于进行JSON序列化和反序列化的GSON实例 */
    private static final Gson GSON = new Gson();
    /**数据包的标识信息 */
    public String identifier;
    /**数据包的主体内容 */
    public String content;
    
    /**
     * 从json文本生成数据包实例
     * @param rawJson json文本
     * @return 构造的数据包实例
     */
    public static Datapack toDatapack(String rawJson){
        return GSON.fromJson(rawJson, Datapack.class);
    }

    /**
     * 将数据包序列化为json
     * @return 序列化后的json文本
     */
    public String toJson(){
        return GSON.toJson(this);
    }

    /**
     * 根据json文本构造一个数据包实例
     * @param rawJson 数据包实例
     */
    public Datapack(String rawJson){
        Datapack source = GSON.fromJson(rawJson, Datapack.class);
        this.identifier = source.identifier;
        this.content = source.content;
    }

    /**
     * 根据标识信息和内容主体构造数据包实例
     * @param identifier 标识信息
     * @param content 内容主体
     */
    public Datapack(String identifier, String content){
        this.identifier = identifier;
        this.content = content;
    }

    /**
     * 用于验证服务器和客户端之间通信的特殊数据包HEARTBEAT
     */
    public static final Datapack HEARTBEAT = new Datapack("HEARTBEAT", null);
}
