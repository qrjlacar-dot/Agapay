package controller;

import java.io.File;
import java.io.IOException;

import Utility.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import services.AuthServices;
import services.ResumeJobPipeline;

public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField govIdField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordTextField;
    @FXML private ImageView eyeIcon;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField confirmPasswordTextField;
    @FXML private ImageView confirmEyeIcon;
    @FXML private Label dropText;

    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;
    private File selectedFile;
    
    private final AuthServices authService = new AuthServices();
    private final ResumeJobPipeline resumeJobPipeline = new ResumeJobPipeline();

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
    private void toggleConfirmPasswordVisibility() {
        isConfirmPasswordVisible = !isConfirmPasswordVisible;
        if (isConfirmPasswordVisible) {
            confirmPasswordTextField.setText(confirmPasswordField.getText());
            confirmPasswordTextField.setVisible(true);
            confirmPasswordTextField.setManaged(true);
            confirmPasswordField.setVisible(false);
            confirmPasswordField.setManaged(false);
        } else {
            confirmPasswordField.setText(confirmPasswordTextField.getText());
            confirmPasswordField.setVisible(true);
            confirmPasswordField.setManaged(true);
            confirmPasswordTextField.setVisible(false);
            confirmPasswordTextField.setManaged(false);
        }
    }

    @FXML
    private void handleChooseFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Upload Resume");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Resume Documents (*.pdf, *.docx)", "*.pdf", "*.docx"));
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            selectedFile = file;
            if (dropText != null) dropText.setText("Selected: " + file.getName());
        }
    }

    @FXML
    private void handleDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) event.acceptTransferModes(TransferMode.COPY);
        event.consume();
    }

    @FXML
    private void handleDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            selectedFile = db.getFiles().get(0);
            if (dropText != null) dropText.setText("Selected: " + selectedFile.getName());
            event.setDropCompleted(true);
        } else {
            event.setDropCompleted(false);
        }
        event.consume();
    }

    @FXML
    private void handleRegister() {
        // 1. Validate the Government ID first
        String rawGovId = govIdField.getText();
        String idType = determineIdType(rawGovId);
        
        if (idType.equals("INVALID")) {
            showAlert(Alert.AlertType.WARNING, "Invalid ID Format", 
                "Please enter a valid PWD ID (12-16 digits) or a Senior Citizen OSCA ID (5-8 digits).");
            return;
        }

        System.out.println("Valid " + idType + " ID detected.");

        // 2. Parse the Resume
        String rawCvText = "";
        if (selectedFile != null) {
            try {
                rawCvText = resumeJobPipeline.getResumeText(selectedFile);
            } catch (Exception e) {
                showAlert(Alert.AlertType.WARNING, "File Error", "Could not read the resume file. Please try again.");
                return;
            }
        }

        // 3. Process Passwords
        String finalPassword = isPasswordVisible ? passwordTextField.getText() : passwordField.getText();
        String finalConfirm = isConfirmPasswordVisible ? confirmPasswordTextField.getText() : confirmPasswordField.getText();

        // 4. Attempt Registration
        boolean success = authService.register(
            nameField.getText(), emailField.getText(), rawGovId,
            finalPassword, finalConfirm, rawCvText
        );

        if (success) {
            if (selectedFile != null && AuthServices.activeUserId > 0) {
                try {
                    resumeJobPipeline.processText(rawCvText, AuthServices.activeUserId);
                } catch (IllegalArgumentException e) {
                    showAlert(
                        Alert.AlertType.WARNING,
                        "Resume Processing Warning",
                        "Your account was created, but the resume could not be fully processed."
                    );
                }
            }
            
            showAlert(Alert.AlertType.INFORMATION, "Registration Successful!", "Your account has been created. Let's set up your profile.");
            
            try {
                SceneSwitcher.switchTo("LandingPage.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Please check your inputs and ensure passwords match and meet requirements.");
        }
    }

    /**
     * Identifies if the provided ID is a valid Senior Citizen or PWD ID based on digit length.
     * Strips out dashes and spaces before counting.
     */
    private String determineIdType(String id) {
        if (id == null || id.isBlank()) return "INVALID";
        
        // Remove spaces and dashes for standard counting (e.g., "12-3456-789" becomes "123456789")
        String cleanId = id.replaceAll("[ -]", "");
        
        // Ensure the ID contains only numbers after stripping formatting
        if (!cleanId.matches("\\d+")) {
            return "INVALID"; 
        }
        
        int length = cleanId.length();
        
        // PWD IDs typically have 12 to 16 digits (DOH Registry format)
        if (length >= 12 && length <= 16) {
            return "PWD";
        } 
        // Senior Citizen (OSCA) IDs typically have 5 to 8 digits depending on the municipality
        else if (length >= 5 && length <= 8) {
            return "SENIOR";
        }
        
        return "INVALID";
    }

    @FXML
    private void goToLogin() {
        try {
            SceneSwitcher.switchTo("Login.fxml");
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