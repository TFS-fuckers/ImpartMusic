    package com.tfs.ui;
    import javafx.util.Duration;
    import java.io.IOException;
    import java.util.ArrayList;
    import java.util.List;

    import com.tfs.client.Client;
    import com.tfs.datapack.UserSimpleInfo;
    import com.tfs.logger.Logger;

    import javafx.application.Platform;
    import javafx.beans.property.SimpleStringProperty;
    import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.scene.Scene;
    import javafx.scene.control.Button;
    import javafx.scene.control.Label;
    import javafx.scene.control.Pagination;
    import javafx.scene.control.Slider;
    import javafx.scene.control.*;
    import javafx.scene.control.TableCell;
    import javafx.scene.control.TableColumn;
    import javafx.scene.control.TableView;
    import javafx.scene.control.TextArea;
    import javafx.scene.image.ImageView;
    import javafx.scene.layout.AnchorPane;
    import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

    public class MusicTvController {
        private static class ThreadDispatcher {
            
        }
        /*****************/
        ObservableList<String> data = FXCollections.observableArrayList();
        private static final int ITEMS_PER_PAGE = 10;
        private static final int TOTAL_ITEMS = 100;

        
        /*****************/
        public static final ThreadDispatcher THREAD_DISPATCHER = new ThreadDispatcher();

        private static MusicTvController instance = null;
        public MusicTvController() {
            instance = this;
            Platform.runLater(new UIInitializationTask());
        }

        private class UIInitializationTask implements Runnable {
            @Override
            public void run() {

                /**********************/
                music_lists.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
                    System.out.println("Pagination clicked");
                    event.consume(); // 阻止事件传递给下一层的节点
                });
                tableView.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
                    System.out.println("TableView clicked");
                    event.consume(); // 阻止事件传递给下一层的节点
                });
                    for(int i = 1; i <= TOTAL_ITEMS; i++)
                    {
                        data.add("Item " + i);
                    }
                    int pageIndex = music_lists.getCurrentPageIndex();
                    int fromIndex = pageIndex * ITEMS_PER_PAGE;
                    int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, data.size());
                    tableView.setItems(FXCollections.observableArrayList(data.subList(fromIndex, toIndex))); 
                /**********************/
                connection_state_info_label.setText("未连接");
                ImpartUI.infoToUI("欢迎使用Impart Music", false);
                users_name.setCellValueFactory((data) -> {
                    return new SimpleStringProperty(data.getValue().getUserName());
                });
                host.setCellValueFactory((data) -> {
                    return new SimpleStringProperty(data.getValue().getUserIP());
                });
                tableView.setFocusTraversable(false);
                tColumn_collect.setCellFactory((tableColumn) -> new TableCell<MusicDetails, Button>() {
                    private final Button button = new Button("fuck");
                    {   
                        button.setOnAction(event -> {
                            Logger.logInfo("Clicked");
                            /******************************/
                            try {
                                AnchorPane root = FXMLLoader.load(getClass().getResource("/delete_music.fxml"));
                                Stage stage = new Stage();
                                stage.setScene(new Scene(root));
                                stage.setTitle("删除歌曲");
                                stage.show();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            /******************************/
                        });
                        setGraphic(button);
                    }
                });
                tableView.getItems().add("Fuck mc de da xia");
            }
        }

        public static MusicTvController instance() {
            return instance;
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
        public TextArea getOnline_information_text() {
            return online_information_text;
        }

        @FXML
        private Button playmusic_button;

        @FXML
        private Button store_button;

        @FXML
        private TableColumn<?, ?> tColumn_add_to_queue;

        @FXML
        private TableColumn<MusicDetails, Button> tColumn_collect;

        @FXML
        private TableColumn<?, ?> tColumn_first;

        @FXML
        private TableColumn<?, ?> tColumn_music;

    @FXML
    private TableView<MusicDetails> tableView;

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
            try {
                AnchorPane root = FXMLLoader.load(getClass().getResource("/add_music.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("收藏歌曲");
                stage.show();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }