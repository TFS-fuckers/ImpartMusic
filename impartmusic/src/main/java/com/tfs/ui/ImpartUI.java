package com.tfs.ui;

import java.util.List;

import com.tfs.datapack.UserSimpleInfo;
import com.tfs.logger.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class ImpartUI extends Application {
    static class ThreadDispatcher {
        public static void invoke(Runnable function) {
            Platform.runLater(function);
        }
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/music_table.fxml"));
        primaryStage.setTitle("hello world");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @Override
    public void init() throws Exception{
        super.init();
        
        System.out.println("init()...");
    }

    @Override
    public void stop() throws Exception{
        super.stop();
        System.out.println("stop()...");
    }
    
    public static void showUI() {
        new Thread(() -> {
            Thread.currentThread().setName("UIThread");
            Logger.logInfo("UI started");
            Application.launch(ImpartUI.class);
        }).start();
    }

    private static StringBuilder messageStringBuilder = new StringBuilder();

    public static void infoToUI(String message, boolean append) {
        ThreadDispatcher.invoke(() -> {
            if(!append) {
                messageStringBuilder.delete(0, messageStringBuilder.length());
            }
            messageStringBuilder.append(message);
            messageStringBuilder.append('\n');
            //TODO: fix
        });
    }

    public static void infoToUI(String message) {
        infoToUI(message, true);
    }

    public static void displayUserList(List<UserSimpleInfo> userList) {
        ThreadDispatcher.invoke(() -> {
            //TODO: fix
        });
    }
}