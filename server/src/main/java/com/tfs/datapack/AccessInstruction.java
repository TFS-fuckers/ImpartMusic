package com.tfs.datapack;

/**
 * 服务端的准许连接信息
 */
public class AccessInstruction {
    private String result;
    private String cause;

    /**
     * 获取连接结果
     * @return 结果，若为允许，为Granted，否则为Denied
     */
    public String getResult() {
        return result;
    }

    /**
     * 获取不允许连接的原因
     * @return 描述字符串
     */
    public String getCause() {
        return cause;
    }

    public AccessInstruction(String result, String cause) {
        this.result = result;
        this.cause = cause;
    }
}
