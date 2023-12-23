package com.tfs.datapack;

/**
 * 用于交换简单用户列表并显示于UI的类
 */
public class UserSimpleInfo {
    private String userName;
    private String userIP;

    /**
     * 构建一个简单用户信息
     * @param userName 用户名称
     * @param userIp 用户IP
     */
    public UserSimpleInfo(String userName, String userIp){
        this.userName = userName;
        this.userIP = userIp;
    }
    /**
     * 获取用户的名称
     * @return 名称
     */
    public String getUserName(){
        return userName;
    }

    /**
     * 获取用户的IP
     * @return IP
     */
    public String getUserIP(){
        return userIP;
    }
}
