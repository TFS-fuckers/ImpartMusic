package com.tfs.datapack;

/**
 * 踢出客户端的数据包
 */
public class ControlConnect {
    private String cause;
    
    /**
     * 获取原因
     * @return 原因
     */
    public String getCause() {
        return cause;
    }

    /**
     * 生成一个通知客户端被踢出的数据包
     * @param cause 原因
     */
    public ControlConnect (String cause) {
        this.cause = cause;
    }
}
