package com.tfs.client;
import javafx.embed.swing.JFXPanel;
import com.tfs.dxconfig.ImpartConfigReader;

public class Main {
    public static void main(String[] args) {
        ImpartConfigReader.initialize();
        new JFXPanel();
        new Client();
    }
}