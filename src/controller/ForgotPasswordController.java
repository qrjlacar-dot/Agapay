package controller;

import java.io.IOException;

import Utility.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.registrationModel;
import services.AuthServices;

public class ForgotPasswordController {

    @FXML private TextField emailField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label statusLabel;

    private final AuthServices authService = new AuthServices();

    @FXML
    private void handleResetPassword(ActionEvent event) {
        String email = emailField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (email.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
            setStatus("Please fill in all fields.", true);
            return;
        }

        if (newPassword.length() < 6) {
            setStatus("Password must be at least 6 characters long.", true);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            setStatus("Passwords do not match.", true);
            return;
        }

        // Fetch user from SQLite database
        registrationModel user = authService.getUserByEmail(emailField.getText());

        if (user != null) {
            // Apply password update via the model setter
            user.setPassword(newPassword);

            boolean isUpdated = authService.updatePassword(user);

            if (isUpdated) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Your password has been successfully reset.");
                try {
                    SceneSwitcher.switchTo("Login.fxml");
                } catch (IOException e) {
                    setStatus("Failed to navigate back to login.", true);
                }
            } else {
                setStatus("Failed to update password in database.", true);
            }
        } else {
            setStatus("Email not found.", true);
        }
    }

    @FXML
    private void goToLogin() throws IOException {
        SceneSwitcher.switchTo("Login.fxml");
    }

    private void setStatus(String message, boolean isError) {
        if (statusLabel != null) {
            statusLabel.setText(message);
            statusLabel.setStyle(isError ? "-fx-text-fill: #d93025; -fx-font-size: 0.95em;" 
                                         : "-fx-text-fill: #2e7d32; -fx-font-size: 0.95em;");
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