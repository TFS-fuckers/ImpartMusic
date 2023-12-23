package com.tfs.server;

import java.net.InetAddress;

import com.tfs.exceptions.AccessToOfflineUserException;

/**
 * 用户类
 */
public class User {
    private String name;
    private InetAddress address;
    private ClientHandler handler;
    private boolean isConnected;

    /**
     * 构建一个用户
     * @param name 用户名称
     * @param address 用户IP地址
     * @param handler 用户对应处理器
     */
    public User(String name, InetAddress address, ClientHandler handler) {
        this.name = name;
        this.address = address;
        this.handler = handler;
        this.isConnected = true;
    }

    /**
     * 获取用户的地址
     * @return 用户的网络地址
     */
    public InetAddress getAddress() {
        return address;
    }

    /**
     * 获取用户的昵称
     * @return 用户的昵称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取用户的处理器
     * @return 处理器实例
     * @throws AccessToOfflineUserException 如果用户已经下线，仍然试图获取，将会抛出此异常
     */
    public ClientHandler getHandler() throws AccessToOfflineUserException{
        if(!this.isConnected){
            throw new AccessToOfflineUserException();
        }
        return handler;
    }

    /**
     * 获取用户是否仍然连接
     * @return 用户是否仍然连接
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * 更改用户的连接状态（内部调用）
     * @param isConnected 目标状态
     */
    public void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }
}
