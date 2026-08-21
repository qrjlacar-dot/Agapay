package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Utility.SceneSwitcher;
import database.DatabaseManager;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import services.AuthServices;

public class ProfileStep2Controller {

    @FXML private GridPane accommodationsContainer; 
    private final DatabaseManager dbManager = new DatabaseManager();

    @FXML
    private void handleCompleteProfile() {
        if (AuthServices.activeUserId == -1) {
            showAlert(Alert.AlertType.ERROR, "Authentication Error", "No active user session found. Please log in again.");
            return;
        }

        // Validation: Verify that at least one toggle button has been selected
        boolean hasSelection = false;
        if (accommodationsContainer != null) {
            for (Node node : accommodationsContainer.getChildren()) {
                if (node instanceof ToggleButton) {
                    if (((ToggleButton) node).isSelected()) {
                        hasSelection = true;
                        break;
                    }
                }
            }
        }

        if (!hasSelection) {
            showAlert(Alert.AlertType.WARNING, "No Selections Made", "Please select at least one accommodation requirement before completing your profile.");
            return;
        }

        Connection conn = null;
        try {
            conn = dbManager.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            String deleteSql = "DELETE FROM user_accommodations WHERE user_id = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, AuthServices.activeUserId);
                deleteStmt.executeUpdate();
            }
            
            if (accommodationsContainer != null) {
                for (Node node : accommodationsContainer.getChildren()) {
                    if (node instanceof ToggleButton) {
                        ToggleButton btn = (ToggleButton) node;
                        if (btn.isSelected()) {
                            linkAccommodationToUser(conn, btn.getText().trim());
                        }
                    }
                }
            }
            
            conn.commit(); // Save changes
            
            showAlert(Alert.AlertType.INFORMATION, "Profile Complete!", "Your profile is 100% set up. Let's find your perfect match!");
            SceneSwitcher.switchTo("getRecommendedJobs.fxml"); 

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Transaction rolled back due to an error.");
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while saving your accommodations.");
            e.printStackTrace();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load the landing page.");
            e.printStackTrace();
        } finally {
            // CLEANUP: Only reset auto-commit, DO NOT close the shared connection
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void linkAccommodationToUser(Connection conn, String accommodationName) throws SQLException {
        if (accommodationName == null || accommodationName.isEmpty()) {
            return;
        }

        String findIdSql = "SELECT accommodation_id FROM accommodations WHERE name = ?";
        int accommId = -1;
        
        try (PreparedStatement findStmt = conn.prepareStatement(findIdSql)) {
            findStmt.setString(1, accommodationName);
            try (ResultSet rs = findStmt.executeQuery()) {
                if (rs.next()) {
                    accommId = rs.getInt("accommodation_id");
                }
            }
        }

        if (accommId != -1) {
            String insertSql = "INSERT OR IGNORE INTO user_accommodations (user_id, accommodation_id) VALUES (?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, AuthServices.activeUserId);
                insertStmt.setInt(2, accommId);
                insertStmt.executeUpdate();
            }
        } else {
            System.err.println("Warning: Accommodation '" + accommodationName + "' not found in database.");
        }
    }
    
    @FXML
    private void handleBack() {
        try {
            SceneSwitcher.switchTo("ProfileStep1.fxml");
        } catch (IOException e) {
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

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}