package com.tfs.ui;

import com.tfs.client.Client;
import com.tfs.client.ParamVertifier;
import com.tfs.dxconfig.IMConfig;
import com.tfs.dxconfig.ImpartConfigReader;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ConnectController {

    public ConnectController() {
        Platform.runLater(() -> {
            this.IP_text.setText(ImpartConfigReader.instance().get("DEFAULT_HOST").getAsString());
            this.port_text.setText(ImpartConfigReader.instance().get("DEFAULT_PORT").getAsString());
            this.users_name_text.setText(ImpartConfigReader.instance().get("DEFAULT_USERNAME").getAsString());
        });
    }

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
        if(!ParamVertifier.isValidName(users_name_text.getText())) {
            ImpartUI.infoToUI("您输入的名称不符合要求！\n【名称规则】：\n·只能由大小写英文字母，下划线\"_\"和数字构成");
            return;
        }

        if(Client.INSTANCE() != null) {
            Client.INSTANCE().connect(IP_text.getText(), Integer.parseInt(port_text.getText()), users_name_text.getText());
        }
        closeWindow(event);
    }

    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.close();

        IMConfig defaultVal = ImpartConfigReader.instance().getDeafult();
        defaultVal.DEFAULT_USERNAME = this.users_name_text.getText();
        defaultVal.DEFAULT_HOST = this.IP_text.getText();
        defaultVal.DEFAULT_PORT = this.port_text.getText();
        ImpartConfigReader.instance().save(defaultVal);
    }
}
