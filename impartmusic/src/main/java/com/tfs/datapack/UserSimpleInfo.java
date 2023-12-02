package com.tfs.datapack;

public class UserSimpleInfo {
    private String userName;
    private String userIP;
    public UserSimpleInfo(String userName, String userIp){
        this.userName = userName;
        this.userIP = userIp;
    }
    public String getUserName(){
        return userName;
    }
    public String getUserIP(){
        return userIP;
    }
}
