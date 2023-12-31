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

    /**
     * 构建一个用户登入登出信息
     * @param name 用户名称
     * @param action 行为
     */
    public UserInfo(String name, String action) {
        this.name = name;
        this.action = action;
    }

    /**
     * 获取名称
     * @return 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称
     * @param name 名称
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * 获取该用户的行为
     * @return 登入`login`或登出`logout`
     */
    public String getAction() {
        return action;
    }
}
