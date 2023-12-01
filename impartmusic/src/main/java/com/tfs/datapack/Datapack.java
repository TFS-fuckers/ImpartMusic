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
    /**数据包的发出者 */
    public String senderTag;
    
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
     * 根据标识信息和对象实例构造数据包实例
     * @param identifier 标识信息
     * @param object 对象实例
     */
    public Datapack(String identifier, Object object){
        this.content = GSON.toJson(object);
        this.identifier = identifier;
    }

    /**
     * 把该数据包持有的内容反序列化为某对象
     * @param <T> 对象类型
     * @param targetClass 对象的类
     * @return 反序列化后的对象实例
     */
    public <T> T deserializeContent(Class<T> targetClass){
        return GSON.fromJson(this.content, targetClass);
    }

    /**
     * 用于验证服务器和客户端之间通信的特殊数据包HEARTBEAT
     */
    public static final Datapack HEARTBEAT = new Datapack("HEARTBEAT", null);
}

/*
 * 数据包的标识信息应该表明数据包的类型，比如HEARTBEAT就是服务器与客户端之间进行验证的“心跳”
 * 如果要直接传输字符串，可以构造
 * ```java
 * Datapack pack = new Datapack("String", "<要传输的字符串>");
 * ``` 
 * 并且在接收端获取，根据identifier进行解析。
 * 如果要进行对象的传输，可以把identifier设置为对象的类名，在接收端对pack的content进行从json
 * 的反序列化，对象格式由identifier决定。
 * 
 * 重点是约定发送端和接收端的统一协议，这样才能进行数据的有效传输。
 */