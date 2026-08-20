package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import Utility.SceneSwitcher;
import database.DatabaseManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import services.AuthServices;

public class ProfileStep1Controller {

    @FXML private TextField locationField;
    @FXML private ToggleGroup categoryGroup;
    @FXML private ToggleGroup setupGroup;
    @FXML private ToggleGroup typeGroup;
    
    private final DatabaseManager dbManager = new DatabaseManager();

    @FXML
    private void handleNextStep() {
        if (AuthServices.activeUserId == -1) {
            showAlert(Alert.AlertType.ERROR, "Authentication Error", "No active user session found. Please log in again.");
            return;
        }

        String location = (locationField != null && locationField.getText() != null) ? locationField.getText().trim() : "";
        
        // Validation: Ensure all toggle groups have a selection and the text field is not empty
        if (location.isEmpty() || categoryGroup.getSelectedToggle() == null || 
            setupGroup.getSelectedToggle() == null || typeGroup.getSelectedToggle() == null) {
            showAlert(Alert.AlertType.WARNING, "Incomplete Form", "Please fill out your location and select an option for all categories before continuing.");
            return;
        }

        String category = getSelectedToggleText(categoryGroup);
        String setup = getSelectedToggleText(setupGroup);
        String type = getSelectedToggleText(typeGroup);

        String sql = "UPDATE userAccount SET preferred_category = ?, work_setup = ?, employment_type = ?, preferred_location = ? WHERE user_id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, category);
            pstmt.setString(2, setup);
            pstmt.setString(3, type);
            pstmt.setString(4, location);
            pstmt.setInt(5, AuthServices.activeUserId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Preferences Saved", "Your work preferences have been securely saved!");
                SceneSwitcher.switchTo("ProfileStep2.fxml");
            } else {
                showAlert(Alert.AlertType.WARNING, "Update Failed", "Could not find your account record to update.");
            }
            
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save preferences. Please try again.");
            e.printStackTrace();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load the next screen.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        try {
            SceneSwitcher.switchTo("LandingPage.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private String getSelectedToggleText(ToggleGroup group) {
        if (group != null && group.getSelectedToggle() != null) {
            return ((ToggleButton) group.getSelectedToggle()).getText().trim();
        }
        return "Any";
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}