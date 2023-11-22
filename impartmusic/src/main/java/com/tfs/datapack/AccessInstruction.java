package com.tfs.datapack;

public class AccessInstruction {
    private String result;
    private String cause;

    public String getResult() {
        return result;
    }

    public String getCause() {
        return cause;
    }

    public AccessInstruction(String result, String cause) {
        this.result = result;
        this.cause = cause;
    }
}
