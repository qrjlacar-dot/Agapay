package controller;

import java.io.IOException;
import java.util.List;

import Utility.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.Job;
import services.AuthServices;
import services.JobRecommendationServices;
import services.JobSelectionState;

public class ViewAllController {

    @FXML private VBox jobsContainer; 
    private final JobRecommendationServices recommendationService = new JobRecommendationServices();

    @FXML
    public void initialize() {
        System.out.println("View All Page Initialized with cards.");
    }

    @FXML
    private void handleOpenJobDetails(ActionEvent event) {
        try {
            Job clickedJob = resolveJobFromEvent(event);
            if (clickedJob != null) {
                JobSelectionState.setSelectedJob(clickedJob);
            }
            System.out.println("Routing to Job Details...");
            SceneSwitcher.switchTo("JobDetails.fxml");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load the job details page.");
        }
    }

    private Job resolveJobFromEvent(ActionEvent event) {
        if (!(event.getSource() instanceof Button button)) {
            return null;
        }

        VBox card = null;
        if (button.getParent() instanceof VBox parentCard) {
            card = parentCard;
        }

        if (card == null) {
            return null;
        }

        String title = null;
        for (var node : card.lookupAll(".card-job-title")) {
            if (node instanceof Label label) {
                title = label.getText();
                break;
            }
        }

        if (title == null || title.isBlank() || AuthServices.activeUserId == -1) {
            return null;
        }

        List<Job> jobs = recommendationService.getRecommendedJobsForUser(AuthServices.activeUserId);
        for (Job job : jobs) {
            if (title.equalsIgnoreCase(job.getTitle())) {
                return job;
            }
        }

        return null;
    }

    @FXML
    private void goBack(ActionEvent event) {
        try {
            System.out.println("Returning to Landing Page...");
            SceneSwitcher.switchTo("LandingPage.fxml");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load the landing page.");
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