package com.tfs.ui;

import com.tfs.client.Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Add_music_Controller {

    @FXML
    private Button add_confirm_button;

    @FXML
    private Button cancel_add_button;

    @FXML
    private Label id_label;

    @FXML
    private TextField id_text;

    /**
     * 确认加入音乐
     * @param event 事件
     */
    @FXML
    void add_confirm(ActionEvent event) {
        Client.INSTANCE().addMusic(id_text.getText());
        this.closeWindow(event);
    }

    /**
     * 取消加入音乐
     * @param event 事件
     */
    @FXML
    void cancel_add(ActionEvent event) {
        this.closeWindow(event);
    }

    /**
     * 关闭此窗口
     * @param event 事件
     */
    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
