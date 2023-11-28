package com.tfs.ui;
import com.tfs.client.Client;
import com.tfs.logger.Logger;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class ImpartUI extends Application {
    public static boolean isAlive = false;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("music_table.fxml"));
        primaryStage.setTitle("hello world");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void init() throws Exception{
        super.init();
        System.out.println("init()...");
        isAlive = true;
    }

    @Override
    public void stop() throws Exception{
        super.stop();
        Client.INSTANCE().saveMusicList();
        System.out.println("stop()...");
        isAlive = false;
    }

    public static void showUI() {
        new Thread(() -> {
            Thread.currentThread().setName("UIThread");
            Logger.logInfo("Client UI started");
            Application.launch(ImpartUI.class);
        }).start();
    }
}