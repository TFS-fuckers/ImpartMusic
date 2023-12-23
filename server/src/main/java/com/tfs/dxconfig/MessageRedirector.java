package com.tfs.dxconfig;

/**
 * 字符串输出信息重定向
 */
public interface MessageRedirector {
    /**
     * 重定向方法
     * @param message 输出字符串
     */
    public void log(String message);
}
