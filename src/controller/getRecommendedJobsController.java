package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Utility.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox; 
import model.Job;
import services.AuthServices;
import services.JobRecommendationServices;
import services.JobSelectionState;

public class getRecommendedJobsController {

    @FXML private FlowPane jobsFlowPane;

    private final JobRecommendationServices recommendationService = new JobRecommendationServices();
    private final List<Job> renderedJobs = new ArrayList<>();

    @FXML
    public void initialize() {
        if (AuthServices.activeUserId == -1) {
            showAlert(Alert.AlertType.ERROR, "Session Error", "No active user session found. Please log in again.");
            return;
        }

        String query = JobSelectionState.getSearchQuery();
        List<Job> allJobs;

        if (query != null && !query.isBlank()) {
            allJobs = recommendationService.searchRecommendedJobsForUser(AuthServices.activeUserId, query);
        } else {
            allJobs = recommendationService.getRecommendedJobsForUser(AuthServices.activeUserId);
        }

        renderedJobs.clear();
        renderedJobs.addAll(allJobs);
        renderCards();

        System.out.println("View All Page: Loaded " + renderedJobs.size() + " jobs for display.");
    }

    private void renderCards() {
        if (jobsFlowPane == null) {
            return;
        }

        jobsFlowPane.getChildren().clear();

        for (Job job : renderedJobs) {
            jobsFlowPane.getChildren().add(createJobCard(job));
        }

        if (renderedJobs.isEmpty()) {
            Label emptyLabel = new Label("No jobs matched your search.");
            emptyLabel.getStyleClass().add("card-location");
            jobsFlowPane.getChildren().add(emptyLabel);
        }
    }

    private VBox createJobCard(Job job) {
        VBox card = new VBox();
        card.getStyleClass().add("job-card");
        card.setSpacing(8);
        card.setPrefWidth(320);

        Label badgeLabel = new Label(String.format("%.0f%% Match", job.getMatchScore()));
        badgeLabel.getStyleClass().add("card-badge-label");

        HBox titleRow = new HBox();
        titleRow.setSpacing(8);
        titleRow.getStyleClass().add("card-title-row");

        Label titleLabel = new Label(job.getTitle());
        titleLabel.getStyleClass().add("card-job-title");

        Label typeLabel = new Label("Recommended");
        typeLabel.getStyleClass().add("badge-green-pill");
        titleRow.getChildren().addAll(titleLabel, typeLabel);

        Label locationLabel = new Label(job.getEmployerName() + " • " + job.getLocation());
        locationLabel.getStyleClass().add("card-location");

        Region spacerTop = new Region();
        spacerTop.setPrefHeight(10);

        HBox payRow = new HBox();
        payRow.setSpacing(8);
        payRow.getStyleClass().add("card-info-row");
        ImageView payIcon = new ImageView(new Image(getClass().getResourceAsStream("/Assets/Icons/pesosign.png")));
        payIcon.setFitWidth(16);
        payIcon.setFitHeight(16);
        Label payLabel = new Label(job.getPayInfo());
        payLabel.getStyleClass().add("card-info-text");
        payRow.getChildren().addAll(payIcon, payLabel);

        HBox scheduleRow = new HBox();
        scheduleRow.setSpacing(8);
        scheduleRow.getStyleClass().add("card-info-row");
        ImageView clockIcon = new ImageView(new Image(getClass().getResourceAsStream("/Assets/Icons/clock.png")));
        clockIcon.setFitWidth(16);
        clockIcon.setFitHeight(16);
        Label scheduleLabel = new Label(job.getScheduleInfo());
        scheduleLabel.getStyleClass().add("card-info-text");
        scheduleRow.getChildren().addAll(clockIcon, scheduleLabel);

        Region grow = new Region();
        VBox.setVgrow(grow, javafx.scene.layout.Priority.ALWAYS);

        Button actionButton = new Button("View & Apply");
        actionButton.setMaxWidth(Double.MAX_VALUE);
        actionButton.getStyleClass().add("card-action-btn");
        actionButton.setOnAction(event -> openJobDetails(job));

        card.getChildren().addAll(
            badgeLabel,
            titleRow,
            locationLabel,
            spacerTop,
            payRow,
            scheduleRow,
            grow,
            actionButton
        );

        return card;
    }

    @FXML
    private void goBack() {
        System.out.println("Returning to Landing Page...");
        try {
            SceneSwitcher.switchTo("LandingPage.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOpenJobDetails() {
        if (!renderedJobs.isEmpty()) {
            JobSelectionState.setSelectedJob(renderedJobs.get(0));
        }
        try {
            SceneSwitcher.switchTo("JobDetails.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openJobDetails(Job job) {
        try {
            JobSelectionState.setSelectedJob(job);
            SceneSwitcher.switchTo("JobDetails.fxml");
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