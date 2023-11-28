package com.tfs.ui;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

public class MusicTvController {

    @FXML
    private Button connect_button;

    @FXML
    private Label connection_state_info_label;

    @FXML
    private Label connection_state_label;

    @FXML
    private Button disconnect_button;

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
    private TableColumn<?, ?> tColumn_first;

    @FXML
    private TableColumn<?, ?> tColumn_music;

    @FXML
    private TableColumn<?, ?> tColumn_operate;

    @FXML
    private TableView<?> tableView;

    @FXML
    private TextArea text_to_online_textarea;

    @FXML
    void buildnewInputPane(ActionEvent event) {
        
    }
}
