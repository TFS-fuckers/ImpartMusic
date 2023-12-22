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

    /**
     * 取消删除音乐
     * @param event 事件
     */
    @FXML
    void cancel_delete(ActionEvent event) {
        this.closeWindow(event);
    }

    /**
     * 确认删除音乐
     * @param event 事件
     */
    @FXML
    void confirm_delete(ActionEvent event) {
        onDeleteAction.run();
        this.closeWindow(event);
    }

    private Runnable onDeleteAction;
    /**
     * 删除的委托事件
     * @param onDeleteAction 删除行为
     */
    public void setDeleteAction(Runnable onDeleteAction) {
        this.onDeleteAction = onDeleteAction;
    }

    /**
     * 关闭当前窗口
     * @param event 事件
     */
    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}

