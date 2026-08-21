package controller;

import java.io.IOException;
import java.util.List;

import Utility.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import model.Job;
import services.AuthServices;
import services.JobRecommendationServices;
import services.JobSelectionState;

public class JobDetailsController {

    @FXML private BorderPane rootPane; // Needed for background blur
    @FXML private StackPane popupOverlay; // The popup container

    @FXML private Button backButton;
    @FXML private Button applyButton;
    @FXML private Label jobTitleLabel;
    @FXML private Label locationLabel;
    @FXML private Label payInfoLabel;
    @FXML private Label scheduleInfoLabel;
    @FXML private Label descriptionLabel;

    private final JobRecommendationServices recommendationService = new JobRecommendationServices();
    private Job currentJob;

    @FXML
    private void initialize() {
        // Ensure popup is hidden when the page loads
        if (popupOverlay != null) {
            popupOverlay.setVisible(false);
        }

        loadJob();

        if (backButton != null) {
            backButton.setOnAction(e -> handleBack());
        }
        if (applyButton != null) {
            applyButton.setOnAction(e -> handleApply());
        }
    }

    private void loadJob() {
        currentJob = JobSelectionState.getSelectedJob();

        if (currentJob == null && AuthServices.activeUserId != -1) {
            List<Job> jobs = recommendationService.getRecommendedJobsForUser(AuthServices.activeUserId);
            if (!jobs.isEmpty()) {
                currentJob = jobs.get(0);
            }
        }

        if (currentJob == null) {
            return;
        }

        if (jobTitleLabel != null) jobTitleLabel.setText(currentJob.getTitle());
        if (locationLabel != null) locationLabel.setText(currentJob.getEmployerName() + " • " + currentJob.getLocation());
        if (payInfoLabel != null) payInfoLabel.setText(currentJob.getPayInfo());
        if (scheduleInfoLabel != null) scheduleInfoLabel.setText(currentJob.getScheduleInfo());
        if (descriptionLabel != null) descriptionLabel.setText(currentJob.getDescription());
    }

    @FXML
    private void handleApply() {
        if (AuthServices.activeUserId == -1) {
            showAlert(Alert.AlertType.ERROR, "Authentication Required", "Please log in before applying for positions.");
            return;
        }

        String appliedJobTitle = currentJob != null ? currentJob.getTitle() : "Unknown Role";
        System.out.println("User ID " + AuthServices.activeUserId + " applied for: " + appliedJobTitle);

        // Show the success popup and blur the background instead of switching scenes
        if (popupOverlay != null) {
            popupOverlay.setVisible(true);
            if (rootPane != null) {
                rootPane.getStyleClass().add("blurred");
                rootPane.setDisable(true); // Prevents clicking buttons behind the popup
            }
        }
    }

    @FXML
    private void closePopupAndReturn() {
        // Un-blur the background
        if (rootPane != null) {
            rootPane.getStyleClass().remove("blurred");
            rootPane.setDisable(false);
        }
        
        // Navigate back to the dashboard/landing page
        try {
            SceneSwitcher.switchTo("LandingPage.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack() {
        System.out.println("Returning to View All...");
        try {
            SceneSwitcher.switchTo("getRecommendedJobs.fxml"); 
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