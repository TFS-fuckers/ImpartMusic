package com.tfs.ui;

import java.net.URL;
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
import javafx.scene.image.Image;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ImpartUI extends Application {
    static class ThreadDispatcher {
        public static void invoke(Runnable function) {
            Platform.runLater(function);
        }
    }

    /**
     * 缓存的音乐详情信息
     */
    public static final HashMap<String, MusicDetails> MUSIC_DETAILS_CACHE = new HashMap<>();
    private static Scene primaryScene = null;

    /**
     * 启动UI
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/music_table.fxml"));
        try {
            URL url = getClass().getResource("/image/icon.jpg");
            Image icon = new Image(url.toString());
            primaryStage.getIcons().add(icon);
        } catch (Exception e) {
            Logger.logWarning("Load icon failed");
        }
        primaryStage.setTitle("Impart Music");
        primaryScene = new Scene(root);
        primaryStage.setScene(primaryScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * 获取主舞台
     * @return 主舞台
     */
    public static Scene getPrimaryScene() {
        return primaryScene;
    }

    /**
     * 初始化UI
     */
    @Override
    public void init() throws Exception{
        super.init();
        System.out.println("init()...");
    }

    /**
     * 终止UI
     */
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
    
    /**
     * 实际调用，显示UI
     */
    public static void showUI() {
        new Thread(() -> {
            Thread.currentThread().setName("UIThread");
            Logger.logInfo("UI started");
            Application.launch(ImpartUI.class);
        }).start();
    }

    private static StringBuilder messageStringBuilder = new StringBuilder();

    /**
     * 在聊天框中显示信息
     * @param message 消息
     * @param append 是否追加信息
     */
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
            MusicTvController.instance().getOnline_information_text().setScrollTop(Double.MAX_VALUE);
        });
    }

    /**
     * 在聊天框中追加显示信息
     * @param message 消息
     */
    public static void infoToUI(String message) {
        infoToUI(message, true);
    }

    /**
     * 加入用户列表
     * @param userList 用户列表
     */
    public static void displayUserList(List<UserSimpleInfo> userList) {
        ThreadDispatcher.invoke(() -> {
            MusicTvController.instance().getOnlineusers_lists().setItems(
                FXCollections.observableList(userList)
            );
        });
    }

    /**
     * 清空用户列表
     */
    public static void clearUserList() {
        ThreadDispatcher.invoke(() -> {
            MusicTvController.instance().clearUserList();
        });
    }

    /**
     * 显示指定音乐列表
     * @param idList 音乐id列表
     */
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
            Logger.logInfo("trying to set list to a %d element list", songsDetails.size());
            MusicTvController.instance().setDataList(songsDetails);
            MusicTvController.instance().refreshTableView();
        });
    }

    /**
     * 让某任务在UI线程下执行
     * @param task 任务
     */
    public static void delegatedInvoke(Runnable task) {
        ThreadDispatcher.invoke(task);
    }

    /**
     * 绑定歌曲全长
     * @param duration 歌曲全长
     */
    public static void bindLabel(Duration duration) {
        ThreadDispatcher.invoke(() -> {
            MusicTvController.instance().bindLabel(duration);
        });
    }

    /**
     * 绑定音乐进度监听
     * @param property 音乐进度属性实体
     */
    public static void bindProgressDisplay(ReadOnlyObjectProperty<Duration> property) {
        ThreadDispatcher.invoke(() -> {
            MusicTvController.instance().bindProgressDisplay(property);
        });
    }

    /**
     * 绑定音乐进度设置对象
     * @param target 设置对象
     */
    public static void bindProgressSetter(MusicPlayer target) {
        MusicTvController.instance().bindTraceTarget(target);
    }

    /**
     * 清空音乐进度设置对象
     */
    public static void removeProgressSetter() {
        MusicTvController.instance().removeProgressSetter();
    }

    /**
     * 绑定音乐名称显示
     * @param index 在表中的序号
     */
    @Deprecated
    public static void bindShower(int index) {
        MusicTvController.instance().bindShower(index);
    }

    /**
     * 绑定音乐名称显示
     * @param id 音乐id
     */
    @Deprecated
    public static void bindShower(String id) {
        MusicTvController.instance().bindShower(id);
    }

    /**
     * 清空音乐列表
     */
    public static void clearMusicList() {
        MusicTvController.instance().clearMusicList();
    }

    /**
     * 重置播放器UI
     */
    public static void resetPlayerUIDisplay() {
        ThreadDispatcher.invoke(() -> {
            MusicTvController.instance().resetPlayerUIDisplay();
        });
    }

    /**
     * 刷新滑杆进度
     * @param max 最大值
     * @param cur 当前值
     */
    public static void refreshPlayerSlider(double max, double cur) {
        ThreadDispatcher.invoke(() -> {
            MusicTvController.instance().refreshPlayerSlider(max, cur);
        });
    }

    /**
     * 刷新播放按钮状态
     * @param playing 是否正在播放
     */
    public static void refreshPlayButton(boolean playing) {
        ThreadDispatcher.invoke(() -> {
            MusicTvController.instance().refreshPlayButton(playing);
        });
    }

    /**
     * 获取当前滑杆指定音量
     * @return 音量（0~1.0f）
     */
    public static double getVolume() {
        return MusicTvController.instance().getVolume();
    }
}