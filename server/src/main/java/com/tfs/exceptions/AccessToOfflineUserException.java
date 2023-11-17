package com.tfs.exceptions;

/**
 * 当一个用户User实例已经离线，但是仍然尝试获取其处理器时抛出此异常
 */
public class AccessToOfflineUserException extends Exception {
    public AccessToOfflineUserException() {
        super("Trying to access an offline user");
    }
}
