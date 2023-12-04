package com.tfs.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Delete_music_Controller {

    @FXML
    private Button cancel_delete_button;

    @FXML
    private Button confirm_delete_button;

    @FXML
    void cancel_delete(ActionEvent event) {
        this.closeWindow(event);
    }

    @FXML
    void confirm_delete(ActionEvent event) {
        onDeleteAction.run();
        this.closeWindow(event);
    }

    private Runnable onDeleteAction;
    public void setDeleteAction(Runnable onDeleteAction) {
        this.onDeleteAction = onDeleteAction;
    }

    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}

