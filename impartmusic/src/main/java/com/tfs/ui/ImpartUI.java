package com.tfs.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.tfs.client.Client;
import com.tfs.datapack.ControlConnect;
import com.tfs.datapack.Datapack;
import com.tfs.datapack.UserSimpleInfo;
import com.tfs.logger.Logger;
import com.tfs.musicplayer.MusicPlayer;
import com.tfs.musicplayer.Netease;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ImpartUI extends Application {
    static class ThreadDispatcher {
        public static void invoke(Runnable function) {
            Platform.runLater(function);
        }
    }

    public static final HashMap<String, MusicDetails> MUSIC_DETAILS_CACHE = new HashMap<>();

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
        if(Client.INSTANCE().isConnected()) {
            Client.INSTANCE().getConnection().sendMessageImmediately(
                new Datapack("ControlConnect", new ControlConnect("Disconnected")
            ));
            Thread.sleep(100);
            Client.INSTANCE().disconnect();
        }
        Client.INSTANCE().kill();
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
            MusicTvController.instance().getOnline_information_text().setText(
                messageStringBuilder.toString()
            );
        });
    }

    public static void infoToUI(String message) {
        infoToUI(message, true);
    }

    public static void displayUserList(List<UserSimpleInfo> userList) {
        ThreadDispatcher.invoke(() -> {
            MusicTvController.instance().getOnlineusers_lists().setItems(
                FXCollections.observableList(userList)
            );
        });
    }

    public static void clearUserList() {
        ThreadDispatcher.invoke(() -> {
            MusicTvController.instance().getOnlineusers_lists().setItems(null);
        });
    }

    public static void displaySongList(List<String> idList) {
        List<MusicDetails> songsDetails = new ArrayList<>();
        for(String id : idList) {
            if(!MUSIC_DETAILS_CACHE.containsKey(id)) {
                MusicDetails downloaded = Netease.getMusicDetails(id);
                if(downloaded == null) {
                    Logger.logError("Music details not exists, ignoring.");
                    Client.INSTANCE().deleteMusic(id);
                    return;
                }
                MUSIC_DETAILS_CACHE.put(id, downloaded);
            }
            songsDetails.add(MUSIC_DETAILS_CACHE.get(id));
        }
        ThreadDispatcher.invoke(() -> {
            MusicTvController.instance().setDataList(songsDetails);
            MusicTvController.instance().refreshTableView();
        });
    }

    public static void delegatedInvoke(Runnable task) {
        ThreadDispatcher.invoke(task);
    }

    public static void bindLabel(Duration duration) {
        ThreadDispatcher.invoke(() -> {
            MusicTvController.instance().bindLabel(duration);
        });
    }

    public static void bindProgressDisplay(ReadOnlyObjectProperty<Duration> property) {
        ThreadDispatcher.invoke(() -> {
            MusicTvController.instance().bindProgressDisplay(property);
        });
    }

    public static void bindProgressSetter(MusicPlayer target) {
        MusicTvController.instance().bindProgressSetter(target);
    }

    public static void removeProgressSetter() {
        MusicTvController.instance().removeProgressSetter();
    }

    public static void bindShower(int index) {
        MusicTvController.instance().bindShower(index);
    }

    public static void bindShower(String id) {
        MusicTvController.instance().bindShower(id);
    }

    public static void clearMusicList() {
        MusicTvController.instance().clearMusicList();
    }
}