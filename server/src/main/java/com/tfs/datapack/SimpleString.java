package com.tfs.datapack;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import com.tfs.logger.Logger;

/**
 * 聊天显示信息
 */
public class SimpleString {
    private String encoding;
    private byte[] data;

    /**
     * 获取字符串格式的信息
     * @return 字符串
     */
    public String getString() {
        try {
            return new String(this.data, encoding);
        } catch (UnsupportedEncodingException e) {
            Logger.logError("encoding %s is not supported", encoding);
        }
        return null;
    }

    /**
     * 以要求的编码格式解析聊天信息
     * @param charset 编码格式
     * @return 聊天信息
     */
    public String getString(Charset charset) {
        try {
            return new String(this.data, charset);
        } catch (Exception e) {
            Logger.logError("encoding %s is not supported", charset);
        }
        return null;
    }

    /**
     * 构建一个简单字符串数据包
     * @param string 字符串内容
     * @param encoding 编码格式
     */
    public SimpleString(String string, String encoding) {
        this.data = string.getBytes(Charset.forName(encoding));
        this.encoding = encoding;
    }
}
