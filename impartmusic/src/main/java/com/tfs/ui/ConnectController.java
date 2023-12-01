package com.tfs.ui;

import com.tfs.client.Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ConnectController {
    @FXML
    private Label IP_label;

    @FXML
    private TextField IP_text;

    @FXML
    private Button cancel_link;

    @FXML
    private Button make_link_button;

    @FXML
    private Label port_label;

    @FXML
    private TextField port_text;

    @FXML
    private Label user_name_label;

    @FXML
    private TextField users_name_text;

    @FXML
    void cancel_link(ActionEvent event) {
        closeWindow(event);
    }

    @FXML
    void make_link(ActionEvent event) {
        if(Client.INSTANCE() != null) {
             Client.INSTANCE().connect(IP_text.getText(), Integer.parseInt(port_text.getText()), users_name_text.getText());
        }
        closeWindow(event);
    }

    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
