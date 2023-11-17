package com.tfs.datapack;

public class UserInfo {
    private String name;
    /**
     * login 或 logout
     */
    private String action;
    public String toString(){
        return "User " + name + action;
    }

}
