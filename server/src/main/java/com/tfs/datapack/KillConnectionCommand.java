package com.tfs.datapack;

public class KillConnectionCommand {
    private String cause;
    public String getCause() {
        return cause;
    }

    public KillConnectionCommand(String cause) {
        this.cause = cause;
    }
}
