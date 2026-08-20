package controller;

import java.io.IOException;

import Utility.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import services.AuthServices;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordTextField;
    @FXML private ImageView eyeIcon;
    @FXML private Label errorLabel; 

    private boolean isPasswordVisible = false;
    private final AuthServices authService = new AuthServices();

    @FXML
    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;
        if (isPasswordVisible) {
            passwordTextField.setText(passwordField.getText());
            passwordTextField.setVisible(true);
            passwordTextField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
        } else {
            passwordField.setText(passwordTextField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            passwordTextField.setVisible(false);
            passwordTextField.setManaged(false);
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String finalPassword = isPasswordVisible ? passwordTextField.getText() : passwordField.getText();

        if (email.isBlank() || finalPassword.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Missing Fields", "Please enter both your email and password.");
            return;
        }

        boolean success = authService.login(email, finalPassword);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Welcome Back!", "Login successful.");
            try {
                SceneSwitcher.switchTo("LandingPage.fxml");
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load the landing page.");
            }
        } else {
            if (errorLabel != null) errorLabel.setText("Invalid email or password.");
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Incorrect email or password. Please try again.");
        }
    }

    @FXML
    private void goToRegister() throws IOException {
        SceneSwitcher.switchTo("Register.fxml");
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