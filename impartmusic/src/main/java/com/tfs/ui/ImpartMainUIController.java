package com.tfs.ui;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

public class ImpartMainUIController {

    @FXML
    private ImageView imgViewIcon;

    @FXML
    private TextArea jfxTextAreaIntro;

    @FXML
    private Label labTitle;

    @FXML
    private BorderPane music_pane;

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
    void buildnewInputPane(ActionEvent event) {
        
    }
}
