package com.tfs.logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**控制台Logger */
public class Logger {
    private Logger() {}
    
    /**
     * 格式化器，用于格式化生成时间字符串
     */
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * 向控制台发送一个错误信息
     * @param message 错误信息
     */
    public static void logError(String message){
        log(message, "ERROR");
        return;
    }

    /**
     * 向控制台发送一个提示信息
     * @param message 提示信息
     */
    public static void logInfo(String message){
        log(message, "INFO");
        return;
    }

    /**
     * 向控制台发送一个警告信息
     * @param message 警告信息
     */
    public static void logWarning(String message){
        log(message, "WARNING");
        return;
    }

    /**
     * 向控制台格式化发送一个错误信息
     * @param format 错误信息格式化字符串
     * @param args 参数列表
     */
    public static void logError(String format, Object... args){
        log(String.format(format, args), "ERROR");
        return;
    }

    /**
     * 向控制台格式化发送一个提示信息
     * @param format 提示信息格式化字符串
     * @param args 参数列表
     */
    public static void logInfo(String format, Object... args){
        log(String.format(format, args), "INFO");
        return;
    }

    /**
     * 向控制台格式化发送一个警告信息
     * @param format 警告信息格式化字符串
     * @param args 参数列表
     */
    public static void logWarning(String format, Object... args){
        log(String.format(format, args), "WARNING");
        return;
    }

    /**
     * 内部辅助方法
     * @param message 信息主体
     * @param type 在信息开头加入的前缀
     */
    private static void log(String message, String type){
        LocalDateTime current = LocalDateTime.now();
        String time = current.format(formatter);
        System.out.println(messageBuilder(time, type, Thread.currentThread().getName(), message));
        return;
    }

    /**
     * 内部方法，构造信息字符串
     * @param time 发送时间
     * @param logType 发送类型前缀
     * @param threadName 发送源线程名称
     * @param message 发送内容
     * @return 构造后的字符串
     */
    private static String messageBuilder(String time, String logType, String threadName, String message){
        return String.format("[%s][%s:%s] %s", time, logType, threadName, message);
    }
}
