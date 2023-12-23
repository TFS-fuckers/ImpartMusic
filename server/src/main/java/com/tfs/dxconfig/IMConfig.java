package com.tfs.dxconfig;

/**
 * 适用于IM服务端的配置文件结构
 */
public class IMConfig implements Config{
    /**
     * 向登录用户发送的欢迎信息
     */
    public String MES_TO_LOGIN_USER = "Welcome to my music house~";

    /**
     * 对标准用户没有回应的最大容许次数
     */
    public int MAX_SYNC_NO_RESPONSE = 5;

    /**
     * 心跳数据包的发送间隔
     */
    public int HEART_BEAT_INTERVAL_MILLISECONDS = 1000;

    /**
     * 对客户端没有响应的最大容许次数
     */
    public int NO_RESPONSE_TIMEOUT_TRIES = 5;

    /**
     * 对客户端没有验证信息的最大容许次数
     */
    public int NO_VERTIFICATION_MAX_COUNT = 10;

    /**
     * 服务端开放的端口号
     */
    public int PORT = 25585;
}