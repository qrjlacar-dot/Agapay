package controller;

import java.io.IOException;

import Utility.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import services.AuthServices;


public class ApplyNowController {

    @FXML private Button backButton;
    @FXML private Button applyButton;
    @FXML private StackPane popupOverlay; 

    @FXML
    public void initialize() {
    
        if (popupOverlay != null) {
        
            popupOverlay.setVisible(false);
        }

        if (backButton != null) {
            backButton.setOnAction(e -> handleBack());
        }
        
        if (applyButton != null) {
            applyButton.setOnAction(e -> handleConfirmApplication());
        }
    }

    @FXML
    private void handleConfirmApplication() {
        if (AuthServices.activeUserId == -1) {
            showAlert(Alert.AlertType.ERROR, "Session Expired", "Please log back in to submit your application.");
            return;
        }


        if (popupOverlay != null) {
            popupOverlay.setVisible(true);
        }
        
        System.out.println("Application confirmation overlay triggered for User ID: " + AuthServices.activeUserId);
    }

    @FXML
    private void handleBack() {
        System.out.println("Navigating back...");
        try {
            SceneSwitcher.switchTo("JobDetails.fxml");
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    // --- ALERT HELPER METHOD ---
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}