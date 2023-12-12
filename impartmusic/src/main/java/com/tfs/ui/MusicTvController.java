package com.tfs.ui;

import javafx.util.Duration;
import java.io.IOException;
import java.util.List;

import com.tfs.client.Client;
import com.tfs.datapack.UserSimpleInfo;
import com.tfs.musicplayer.MusicPlayer;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.Slider;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MusicTvController {
    private List<MusicDetails> data; 
    public static final int ITEMS_PER_PAGE = 10;

    private static MusicTvController instance = null;

    public MusicTvController() {
        instance = this;
        Platform.runLater(new UIInitializationTask());
    }

    private class UIInitializationTask implements Runnable {
        @Override
        public void run() {
            music_lists.currentPageIndexProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> value, Number oldVal, Number newVal) {
                    setMusicListDisplayPage(newVal.intValue());
                }
            });
            setMusicListDisplayPage(0);
            connection_state_info_label.setText("未连接");
            ImpartUI.infoToUI("欢迎使用Impart Music", false);
            users_name.setCellValueFactory((data) -> {
                return new SimpleStringProperty(data.getValue().getUserName());
            });
            host.setCellValueFactory((data) -> {
                return new SimpleStringProperty(data.getValue().getUserIP());
            });
            tableViewButton.setCellFactory((tableColumn) -> new TableCell<MusicDetails, Button>() {
                private String getTargetID() {
                    return ((MusicDetails) getTableRow().getItem()).getId();
                }
                
                private final Button button = new Button("×");
                {
                    button.setOnAction(event -> {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/delete_music.fxml"));
                            AnchorPane root = loader.load();
                            Delete_music_Controller controller = loader.getController();
                            controller.setDeleteAction(() -> {
                                Client.INSTANCE().deleteMusic(this.getTargetID());
                            });
                            Stage stage = new Stage();
                            stage.setScene(new Scene(root));
                            stage.setTitle("删除确认");
                            stage.show();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    });
                    setGraphic(button);
                }

                @Override
                public void updateItem(Button item, boolean empty) {
                    setGraphic(empty ? null : button);
                }
            });
            tableViewMusicID.setCellValueFactory((data) -> new SimpleStringProperty(data.getValue().getId()));
            tableViewMusicTitle.setCellValueFactory((data) -> new SimpleStringProperty(data.getValue().getName()));
        }
    }

    public void setMusicListDisplayPage(int page) {
        if(data == null) {
            return;
        }

        int fromIndex = page * ITEMS_PER_PAGE;

        if(data.size() == 0) {
            tableView.setItems(null);    
            return;
        }
        
        while(fromIndex >= data.size()) {
            fromIndex -= ITEMS_PER_PAGE;
            page--;
        }
        this.music_lists.setCurrentPageIndex(page);
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, data.size());
        tableView.setItems(FXCollections.observableArrayList(data.subList(fromIndex, toIndex)));
    }

    public void refreshTableView() {
        this.setMusicListDisplayPage(
            this.music_lists.getCurrentPageIndex()
        );
    }

    public static MusicTvController instance() {
        return instance;
    }

    public void setDataList(List<MusicDetails> data) {
        this.data = data;
    }

    @FXML
    private Button add_music_to_pack;

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
    private TableView<UserSimpleInfo> onlineusers_lists;

    public TableView<UserSimpleInfo> getOnlineusers_lists() {
        return onlineusers_lists;
    }

    @FXML
    private TableColumn<UserSimpleInfo, String> users_name;

    @FXML
    private TableColumn<UserSimpleInfo, String> host;

    @FXML
    private Button last_button;

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
    public Label getMusic_playing_time_label() {
        return music_playing_time_label;
    }

    @FXML
    private Button music_playmode_button;

    @FXML
    private Slider music_slider;
    public Slider getMusic_slider() {
        return music_slider;
    }

    @FXML
    private Label music_title;

    @FXML
    private Label music_whole_time_label;

    @FXML
    private Button next_button;

    @FXML
    private TextArea online_information_text;

    public TextArea getOnline_information_text() {
        return online_information_text;
    }

    @FXML
    private Button playmusic_button;

    @FXML
    private Button store_button;

    @FXML
    private TableColumn<MusicDetails, Button> tableViewButton;

    @FXML
    private TableColumn<MusicDetails, String> tableViewMusicID;

    @FXML
    private TableColumn<MusicDetails, String> tableViewMusicTitle;

    @FXML
    private TableView<MusicDetails> tableView;
    public TableView<MusicDetails> getMusicTableView() {
        return tableView;
    }

    @FXML
    private TextArea text_to_online_textarea;

    @FXML
    void Collect_music(ActionEvent event) {

    }

    @FXML
    void Music_Sort(ActionEvent event) {

    }

    @FXML
    void Play_music(ActionEvent event) {
        music_slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (music_slider.isValueChanging()) {
                    // 用户正在拖动Slider，设置播放位置
                    MusicPlayer currentPlaying = Client.INSTANCE().getCurrentMusic();
                    if(currentPlaying != null) {
                        currentPlaying.setPositionMusic(newValue.doubleValue());
                    }
                }
            }
        });
        
    }

    public static String formatDuration(Duration duration) {
        if(duration == null) {
            return "xx:xx";
        }
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
        Client.INSTANCE().disconnect();
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

    @FXML
    void add_music_to_pack(ActionEvent event) {
        if(!Client.INSTANCE().isConnected()) {
            ImpartUI.infoToUI("请先连接服务器！");
            return;
        }
        try {
            AnchorPane root = FXMLLoader.load(getClass().getResource("/add_music.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("添加歌曲");
            stage.show();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}