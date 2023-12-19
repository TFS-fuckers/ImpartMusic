package com.tfs.datapack;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import com.tfs.logger.Logger;

public class SimpleString {
    private String encoding;
    private byte[] data;

    public String getString() {
        try {
            return new String(this.data, encoding);
        } catch (UnsupportedEncodingException e) {
            Logger.logError("encoding %s is not supported", encoding);
        }
        return null;
    }

    public String getString(Charset charset) {
        try {
            return new String(this.data, charset);
        } catch (Exception e) {
            Logger.logError("encoding %s is not supported", charset);
        }
        return null;
    }

    public SimpleString(String string, String encoding) {
        this.data = string.getBytes(Charset.forName(encoding));
        this.encoding = encoding;
    }
}
