package controller;

import java.io.IOException;
import java.util.List;

import Utility.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.Job;
import services.AuthServices;
import services.JobRecommendationServices;
import services.JobSelectionState;

public class JobDetailsController {

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

        if (jobTitleLabel != null) {
            jobTitleLabel.setText(currentJob.getTitle());
        }
        if (locationLabel != null) {
            locationLabel.setText(currentJob.getEmployerName() + " • " + currentJob.getLocation());
        }
        if (payInfoLabel != null) {
            payInfoLabel.setText(currentJob.getPayInfo());
        }
        if (scheduleInfoLabel != null) {
            scheduleInfoLabel.setText(currentJob.getScheduleInfo());
        }
        if (descriptionLabel != null) {
            descriptionLabel.setText(currentJob.getDescription());
        }
    }

    @FXML
    private void handleApply() {
        if (AuthServices.activeUserId == -1) {
            showAlert(Alert.AlertType.ERROR, "Authentication Required", "Please log in before applying for positions.");
            return;
        }

        String appliedJobTitle = currentJob != null ? currentJob.getTitle() : "Unknown Role";
        System.out.println("User ID " + AuthServices.activeUserId + " applied for: " + appliedJobTitle);
        
        showAlert(Alert.AlertType.INFORMATION, "Application Submitted", "Your application has been successfully sent!");
        
        try {
            SceneSwitcher.switchTo("applynow.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack() {
        System.out.println("Returning to View All / Landing Page...");
        try {
            SceneSwitcher.switchTo("ViewAll.fxml");
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