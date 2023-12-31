package com.tfs.ui;

import javafx.util.Duration;
import java.io.IOException;
import java.util.List;

import com.tfs.client.Client;
import com.tfs.datapack.UserSimpleInfo;
import com.tfs.logger.Logger;
import com.tfs.musicplayer.MusicPlayer;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.Slider;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MusicTvController {
    private List<MusicDetails> detailedMusic; 
    public static final int ITEMS_PER_PAGE = 10;

    private static MusicTvController instance = null;

    public MusicTvController() {
        instance = this;
        Platform.runLater(new UIInitializationTask());
    }

    private class UIInitializationTask implements Runnable {
        @Override
        public void run() {
            volumeSlider.setOrientation(Orientation.VERTICAL);
            volumeSlider.setVisible(false);
            volumeSlider.setDisable(false);
            volumeSlider.setMax(1.0);
            volumeSlider.setValue(1.0);
            ImpartUI.getPrimaryScene().setOnMouseClicked((event1) -> {
                if (event1.getTarget() != volume_button && event1.getTarget() != volumeSlider) {
                    volumeSlider.setVisible(false);
                }
            });
            volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                if(Client.INSTANCE().getCurrentMusic() != null) {
                    Client.INSTANCE().onSetVolume(newVal.floatValue());
                }
            });

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

            MusicTvController.this.music_slider.setOnMouseClicked((event) -> {
                if(Client.INSTANCE().getCurrentMusic() == null) {
                    return;
                }
                Client.INSTANCE().requestStandardUser();
            });

            MusicTvController.this.music_slider.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> value, Number oldVal, Number newVal) {
                    if(music_slider.isValueChanging() && MusicTvController.this.traceTarget != null) {
                        MusicTvController.this.traceTarget.setPositionMusic(newVal.doubleValue());
                    }
                }
            });
            
            MusicTvController.this.music_slider.setOnMouseDragReleased((event) -> {
                if(Client.INSTANCE().getCurrentMusic() == null) {
                    return;
                }
                Client.INSTANCE().requestStandardUser();
            });

            tableViewButton.setCellFactory((tableColumn) -> new TableCell<MusicDetails, Button>() {
                private String getTargetID() {
                    return ((MusicDetails) getTableRow().getItem()).getId();
                }
                
                private final Button button = new Button("×");
                {
                    button.setOnAction(event -> {
                        try {
                            button.setAlignment(Pos.CENTER);
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/delete_music.fxml"));
                            AnchorPane root = loader.load();
                            Delete_music_Controller controller = loader.getController();
                            controller.setDeleteAction(() -> {
                                Client instance = Client.INSTANCE();
                                instance.deleteMusic(this.getTargetID());
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
            
            tableView.setOnMouseClicked(event -> {
                if(event.getClickCount() == 1) {
                    MusicDetails selected = tableView.getSelectionModel().getSelectedItem();
                    if(selected == null) {
                        return;
                    }
                    boolean isPlaying = Client.INSTANCE().isPlaying();
                    Client.INSTANCE().pauseMusic(true);
                    Client.INSTANCE().useTargetMusic(selected.getId(), false);
                    if(isPlaying) {
                        Client.INSTANCE().playMusic(false);
                    }
                    MusicTvController.this.refreshPlayButton(isPlaying);
                    MusicTvController.this.refreshPlayerSlider(Client.INSTANCE().getCurrentMusic().getTotalTimeDuration().toSeconds(), 0.0);
                }
            });
            
            online_information_text.setEditable(false);
            text_to_onlineinfo.setText("");
        }
    }

    /**
     * 设置显示页
     * @param page 页数
     */
    public void setMusicListDisplayPage(int page) {
        if(detailedMusic == null) {
            return;
        }

        int fromIndex = page * ITEMS_PER_PAGE;

        if(detailedMusic.size() == 0) {
            tableView.setItems(null);    
            return;
        }
        
        while(fromIndex >= detailedMusic.size()) {
            fromIndex -= ITEMS_PER_PAGE;
            page--;
        }
        this.music_lists.setCurrentPageIndex(page);
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, detailedMusic.size());
        tableView.setItems(FXCollections.observableArrayList(detailedMusic.subList(fromIndex, toIndex)));
    }

    /**
     * 刷新列表的显示页
     */
    public void refreshTableView() {
        this.setMusicListDisplayPage(
            this.music_lists.getCurrentPageIndex()
        );
    }

    /**
     * 获取主UI单例
     * @return 主UI控制器
     */
    public static MusicTvController instance() {
        return instance;
    }

    /**
     * 设置音乐详情列表
     * @param data 音乐详情列表
     */
    public void setDataList(List<MusicDetails> data) {
        this.detailedMusic = data;
    }

    @FXML
    private Pagination music_lists;

    @FXML
    private ToggleButton add_music_to_pack;

    @FXML
    private Button connect_button;

    @FXML
    private Label connection_state_info_label;

    /**
     * 获取连接状态显示标签
     * @return 显示标签
     */
    public Label getConnection_state_info_label() {
        return connection_state_info_label;
    }

    @FXML
    private Label connection_state_label;

    @FXML
    private TextField text_to_onlineinfo;

    @FXML
    private Button text_to_onlineinfo_button;

    @FXML
    private Button disconnect_button;

    @FXML
    private TableView<UserSimpleInfo> onlineusers_lists;

    /**
     * 获取用户列表
     * @return TableView下的用户列表
     */
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
    private Label music_name_label;

    @FXML
    private BorderPane music_pane;

    @FXML
    private Label music_playing_time_label;
    
    /**
     * 获取音乐播放进度标签
     * @return 音乐播放进度标签
     */
    public Label getMusic_playing_time_label() {
        return music_playing_time_label;
    }


    @FXML
    private Slider music_slider;

    /**
     * 获取音乐播放进度滑杆
     * @return 滑杆
     */
    public Slider getMusic_slider() {
        return music_slider;
    }


    @FXML
    private Label music_whole_time_label;

    @FXML
    private Button next_button;

    @FXML
    private TextArea online_information_text;

    /**
     * 获取聊天信息框
     * @return 聊天信息框
     */
    public TextArea getOnline_information_text() {
        return online_information_text;
    }

    @FXML
    private ToggleButton playmusic_button;

    @FXML
    private TableColumn<MusicDetails, Button> tableViewButton;

    @FXML
    private TableColumn<MusicDetails, String> tableViewMusicID;

    @FXML
    private TableColumn<MusicDetails, String> tableViewMusicTitle;

    @FXML
    private TableView<MusicDetails> tableView;
    /**
     * 获取音乐详情列表
     * @return 音乐详情列表
     */
    public TableView<MusicDetails> getMusicTableView() {
        return tableView;
    }

    @FXML
    void Collect_music(ActionEvent event) {

    }

    @FXML
    void Music_Sort(ActionEvent event) {

    }

    /**
     * 播放音乐按钮自动调用事件
     * @param event 事件
     */
    @FXML
    void Play_music(ActionEvent event) {
        if(!Client.INSTANCE().isConnected()) {
            ImpartUI.infoToUI("请先登录服务器！");
            this.playmusic_button.setSelected(false);
            return;
        }
        if(Client.INSTANCE().isPlaying()) {
            Client.INSTANCE().pauseMusic(true);
            return;
        }
        Client.INSTANCE().playMusic(true);
    }

    /**
     * 格式化显示Duration
     * @param duration 时间对象
     * @return 格式化字符串
     */
    public static String formatDuration(Duration duration) {
        if(duration == null) {
            return "xx:xx";
        }
        int minutes = (int) duration.toMinutes();
        int seconds = (int) (duration.toSeconds() % 60);
        return String.format("%d:%02d", minutes, seconds);
    }

    /**
     * 切换到上一首歌曲按钮自动调用
     * @param event 点击事件
     */
    @FXML
    void To_last_music(ActionEvent event) {
        if(!Client.INSTANCE().isConnected()) {
            ImpartUI.infoToUI("请先登录服务器！");
            return;
        }
        Client.INSTANCE().goPreviousMusic(true);
    }

    /**
     * 切换到下一首歌曲按钮自动调用
     * @param event 点击事件
     */
    @FXML
    void To_next_music(ActionEvent event) {
        if(!Client.INSTANCE().isConnected()) {
            ImpartUI.infoToUI("请先登录服务器！");
            return;
        }
        Client.INSTANCE().goNextMusic(true);
    }

    /**
     * 断开连接按钮自动调用
     * @param event 点击事件
     */
    @FXML
    void cut_link(ActionEvent event) {
        if(!Client.INSTANCE().isConnected()) {
            ImpartUI.infoToUI("你还没有连接服务器！");
            return;
        }
        Client.INSTANCE().disconnect();
    }

    /**
     * 创建连接对话框
     * @param event 点击事件
     */
    @FXML
    void buildnewInputPane(ActionEvent event) {
        if(Client.INSTANCE() != null && Client.INSTANCE().isConnected()) {
            ImpartUI.infoToUI("你已经连接服务器了！");
            return;
        }

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

    /**
     * 加入音乐按钮自动调用
     * @param event 点击事件
     */
    @FXML
    void add_music_to_pack(ActionEvent event) {
        if(!Client.INSTANCE().isConnected()) {
            ImpartUI.infoToUI("请先登录服务器！");
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

    /**
     * 将总时间标签绑定为某时间
     * @param total 总时间
     */
    public void bindLabel(Duration total) {
        this.music_slider.setMax(total.toSeconds());
        this.music_whole_time_label.setText(String.format(
            "%02d:%02d",
            (int)total.toMinutes(),
            (int)total.toSeconds() % 60
        ));
    }
    
    /**
     * 滑杆监听器
     */
    public final ChangeListener<Duration> progressBarSubscriber = new ChangeListener<Duration>() {
        @Override
        public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
            MusicTvController.this.music_slider.setValue(newValue.toSeconds());
            MusicTvController.this.music_playing_time_label.setText(String.format(
                "%02d:%02d",
                (int)newValue.toMinutes(),
                (int)newValue.toSeconds() % 60
            ));
        }
    };
    private ReadOnlyObjectProperty<Duration> subscribedProperty = null;

    /**
     * 使滑杆监听某进度属性
     * @param value 进度属性
     */
    public void bindProgressDisplay(ReadOnlyObjectProperty<Duration> value) {
        if(subscribedProperty != null) {
            subscribedProperty.removeListener(progressBarSubscriber);
        }
        subscribedProperty = value;
        if(value == null) {
            this.music_slider.setValue(0);
        }
        value.addListener(progressBarSubscriber);
    }

    private MusicPlayer traceTarget = null;
    /**
     * 绑定音乐播放进度监听对象
     * @param target 音乐播放器
     */
    public void bindTraceTarget(MusicPlayer target) {
        this.traceTarget = target;
    }

    /**
     * 移除音乐播放进度监听
     */
    public void removeProgressSetter() {
        this.traceTarget = null;
    }

    @Deprecated
    public void bindShower(int index) {
        // this.music_title.setText(this.detailedMusic.get(index).getName());
    }

    @Deprecated
    public void bindShower(String id) {
        // synchronized(this.detailedMusic) {
        //     boolean found = false;
        //     for(int i = 0; i < this.detailedMusic.size(); i++) {
        //         if(detailedMusic.get(i).getId().equals(id)) {
        //             this.bindShower(i);
        //             found = true;
        //             break;
        //         }
        //     }
        //     if(!found) {
        //         this.music_title.setText(
        //             Netease.getMusicDetails(id).getName()
        //         );
        //     }
        // }
    }

    /**
     * 清空显示的音乐列表
     */
    public void clearMusicList() {
        try {
            this.tableView.getItems().clear();
        } catch (NullPointerException e) {
            Logger.logWarning("Nothing to clear in music list");
        }
    }

    /**
     * 重置音乐播放器UI
     */
    public void resetPlayerUIDisplay() {
        this.music_slider.setValue(0);
        this.music_slider.setMax(999);
        this.music_playing_time_label.setText("当前时间");
        this.music_whole_time_label.setText("全曲时间");
        // this.music_title.setText("");
    }

    /**
     * 清空显示的用户列表
     */
    public void clearUserList() {
        this.getOnlineusers_lists().getItems().clear();
    }

    /**
     * 发送聊天信息自动调用
     * @param event 点击事件
     */
    @FXML
    void text_to_onlineinfo(ActionEvent event) {
        if(!Client.INSTANCE().isConnected()) {
            ImpartUI.infoToUI("请先连接服务器！");
        }
        String input = this.text_to_onlineinfo.getText();
        this.text_to_onlineinfo.setText("");
        if(!input.isEmpty()) {
            Client.INSTANCE().sendChatMessage(input);
        }
    }

    @FXML
    private Button volume_button;

    @FXML
    private Slider volumeSlider;

    /**
     * 设置音乐音量自动调用
     * @param event 点击事件
     */
    @FXML
    void set_volume(ActionEvent event) {
        if (volumeSlider.isVisible()) {
            volumeSlider.setVisible(false); // 点击按钮时隐藏/显示音量调节滑动条
        } else {
            volumeSlider.setVisible(true);
        }
    }
    
    /**
     * 刷新音乐播放器滑杆显示
     * @param max 最大值
     * @param cur 当前值
     */
    public void refreshPlayerSlider(double max, double cur) {
        this.music_slider.setMax(max);
        this.music_slider.setValue(cur);
    }

    /**
     * 刷新播放按钮显示状态
     * @param playing 是否正在播放
     */
    public void refreshPlayButton(boolean playing) {
        this.playmusic_button.setSelected(playing);
    }

    /**
     * 获取当前音量
     * @return 音量（0~1.0f）
     */
    public double getVolume() {
        return this.volumeSlider.getValue();
    }
}