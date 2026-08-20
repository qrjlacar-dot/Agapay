package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;

public class NavBarController {

    private static int currentFontSize = 14; // Default base size
    private final int MAX_FONT_SIZE = 22;
    private final int MIN_FONT_SIZE = 10;

    @FXML
    private void decreaseTextSize(ActionEvent event) {
        if (currentFontSize > MIN_FONT_SIZE) {
            currentFontSize -= 2;
            applyGlobalFontSize(event);
        }
    }

    @FXML
    private void increaseTextSize(ActionEvent event) {
        if (currentFontSize < MAX_FONT_SIZE) {
            currentFontSize += 2;
            applyGlobalFontSize(event);
        }
    }

    @FXML
    private void resetTextSize(ActionEvent event) {
        currentFontSize = 14;
        applyGlobalFontSize(event);
    }

    private void applyGlobalFontSize(ActionEvent event) {
        Node source = (Node) event.getSource();
        Scene scene = source.getScene();
        
        if (scene != null && scene.getRoot() != null) {
            scene.getRoot().setStyle("-fx-font-size: " + currentFontSize + "px;");
            System.out.println("Global font size updated to: " + currentFontSize + "px");
        }
    }
}