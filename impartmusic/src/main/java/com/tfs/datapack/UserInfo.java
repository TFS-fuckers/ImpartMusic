package com.tfs.datapack;
/**
 * 传递用户登录登出信息，传至所有用户
 */
public class UserInfo {
    private String name;
    /**
     * login 或 logout
     */
    private String action;
    public String toString(){
        return "User " + name + action;
    }

    public UserInfo(String name, String action) {
        this.name = name;
        this.action = action;
    }

    public String getName() {
        return name;
    }

    public String getAction() {
        return action;
    }
}
