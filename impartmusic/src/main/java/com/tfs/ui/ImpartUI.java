package com.tfs.ui;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class ImpartUI extends Application {
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
    }

    @Override
    public void stop() throws Exception{
        super.stop();
        System.out.println("stop()...");
    }

    public static void showUI() {
        new Thread(() -> Application.launch(ImpartUI.class)).start();
    }
}