package com.tfs.ui;
import javafx.scene.media.Media;
import java.io.File;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.io.IOException;

import com.tfs.client.Client;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MusicTvController {
    private static class ThreadDispatcher {
        
    }
    public static final ThreadDispatcher THREAD_DISPATCHER = new ThreadDispatcher();

    private static MusicTvController instance = null;
    public MusicTvController() {
        instance = this;
        Platform.runLater(new UIInitializationTask());
    }

    private class UIInitializationTask implements Runnable {
        @Override
        public void run() {
            connection_state_info_label.setText("未连接");
            ImpartUI.infoToUI("欢迎使用Impart Music", false);
        }
    }

    public static MusicTvController instance() {
        return instance;
    }
/*******************/
    private MediaPlayer mediaPlayer;
    private Duration duration;
/*******************/

    @FXML
    private Button connect_button;

    @FXML
    private Label connection_state_info_label;
    public Label getConnection_state_info_label() {
        return connection_state_info_label;
    }
    @FXML
    private Label connection_state_label;
    public Label getConnection_state_label() {
        return connection_state_label;
    }

    @FXML
    private Button disconnect_button;

    @FXML
    private TableColumn<?, ?> host;

    @FXML
    private Button last_button;

    @FXML
    private ImageView music_image;

    @FXML
    private Label music_introduction_label;

    @FXML
    private TextArea music_introduction_text;

    @FXML
    private Pagination music_lists;

    @FXML
    private Label music_name_label;

    @FXML
    private BorderPane music_pane;

    @FXML
    private Label music_playing_time_label;

    @FXML
    private Button music_playmode_button;

    @FXML
    private Slider music_slider;

    @FXML
    private Label music_title;

    @FXML
    private Label music_whole_time_label;

    @FXML
    private Button next_button;

    @FXML
    private TextArea online_information_text;

    @FXML
    private TableView<?> onlineusers_lists;

    @FXML
    private Button playmusic_button;

    @FXML
    private Button store_button;

    @FXML
    private TableColumn<?, ?> tColumn_add_to_queue;

    @FXML
    private TableColumn<?, ?> tColumn_collect;

    @FXML
    private TableColumn<?, ?> tColumn_first;

    @FXML
    private TableColumn<?, ?> tColumn_music;

    @FXML
    private TableView<?> tableView;

    @FXML
    private TextArea text_to_online_textarea;

    @FXML
    private TableColumn<?, ?> users_name;

    @FXML
    void Collect_music(ActionEvent event) {

    }

    @FXML
    void Music_Sort(ActionEvent event) {

    }

    @FXML
    void Play_music(ActionEvent event) {
        music_playing_time_label.setText("0:00");
        File audioFile = new File("./data/436346833.mp3");
        String path = "file:///" + audioFile.getAbsolutePath().replace("\\", "/");
        Media media = new Media(path);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(false);
         // 获取媒体总时长
        duration = media.getDuration();
        music_whole_time_label.setText(formatDuration(duration));

        //music_slider.setShowTickLabels(true);
        //music_slider.setShowTickMarks(true);
        music_slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (music_slider.isValueChanging()) {
                    // 用户正在拖动Slider，设置播放位置
                    mediaPlayer.seek(Duration.seconds(newValue.doubleValue()));
                }
            }
        });
        // 监听MediaPlayer的当前时间变化事件
        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                // 更新Slider和Label显示
                music_slider.setValue(newValue.toSeconds());
                music_playing_time_label.setText(formatDuration(newValue));
            }
        });
        mediaPlayer.play();
    }

    private String formatDuration(Duration duration) {
        int minutes = (int) duration.toMinutes();
        int seconds = (int) (duration.toSeconds() % 60);
        return String.format("%d:%02d", minutes, seconds);
    }
   

    @FXML
    void Playmode_change(ActionEvent event) {

    }

    @FXML
    void To_last_music(ActionEvent event) {

    }

    @FXML
    void To_next_music(ActionEvent event) {

    }

    @FXML
    void cut_link(ActionEvent event) {
        if(Client.INSTANCE() != null && Client.INSTANCE().getConnection() != null) {
                    Client.INSTANCE().getConnection().killConnection();
        }
    }
    @FXML
    void buildnewInputPane(ActionEvent event) {
        try {
            AnchorPane root = FXMLLoader.load(getClass().getResource("/connect.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("连接到服务端");
            stage.show();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}