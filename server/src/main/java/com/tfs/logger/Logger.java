package com.tfs.logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private Logger() {}
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void logError(String message){
        log(message, "ERROR");
        return;
    }

    public static void logInfo(String message){
        log(message, "INFO");
        return;
    }

    public static void logWarning(String message){
        log(message, "WARNING");
        return;
    }

    public static void logError(String format, Object... args){
        log(String.format(format, args), "ERROR");
        return;
    }

    public static void logInfo(String format, Object... args){
        log(String.format(format, args), "INFO");
        return;
    }

    public static void logWarning(String format, Object... args){
        log(String.format(format, args), "WARNING");
        return;
    }

    private static void log(String message, String type){
        LocalDateTime current = LocalDateTime.now();
        String time = current.format(formatter);
        System.out.println(messageBuilder(time, type, Thread.currentThread().getName(), message));
        return;
    }

    private static String messageBuilder(String time, String logType, String threadName, String message){
        return String.format("[%s][%s:%s] %s", time, logType, threadName, message);
    }
}
