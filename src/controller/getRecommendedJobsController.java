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
    card.setSpacing(10);
    card.setPrefWidth(320);

    HBox topBadgeRow = new HBox();
    topBadgeRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
    topBadgeRow.setSpacing(10);


    HBox matchBadgeContainer = new HBox();
    matchBadgeContainer.getStyleClass().add("card-badge-container");
    matchBadgeContainer.setAlignment(javafx.geometry.Pos.CENTER);

    Label matchIconLabel = new Label("★ ");
    matchIconLabel.setStyle("-fx-text-fill: #000666; -fx-font-weight: bold;");

    Label badgeLabel = new Label(String.format("%.0f%% Match", job.getMatchScore()));
    badgeLabel.getStyleClass().add("badge-yellow-text");
    badgeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 0.95em;");

    matchBadgeContainer.getChildren().addAll(matchIconLabel, badgeLabel);

    // Optional: Dynamic Tag check for Volunteer or standard badge
    boolean isVolunteer = (job.getPayInfo() != null && job.getPayInfo().toLowerCase().contains("volunteer")) 
                       || (job.getTitle() != null && job.getTitle().toLowerCase().contains("volunteer"));

    Label greenPill = new Label(isVolunteer ? "Volunteer" : "Recommended");
    greenPill.getStyleClass().add("badge-green-pill");

    Region topSpacer = new Region();
    HBox.setHgrow(topSpacer, javafx.scene.layout.Priority.ALWAYS);

    topBadgeRow.getChildren().addAll(matchBadgeContainer, topSpacer, greenPill);

    
    Label titleLabel = new Label(job.getTitle());
    titleLabel.getStyleClass().add("card-job-title");
    titleLabel.setWrapText(true);

    Label locationLabel = new Label(job.getEmployerName() + " • " + job.getLocation());
    locationLabel.getStyleClass().add("card-location");
    locationLabel.setWrapText(true);

    
    HBox payRow = new HBox();
    payRow.setSpacing(8);
    payRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
    payRow.getStyleClass().add("card-info-row");

    ImageView payIcon = new ImageView(new Image(getClass().getResourceAsStream("/Assets/Icons/pesosign.png")));
    payIcon.setFitWidth(16);
    payIcon.setFitHeight(16);

    Label payLabel = new Label(job.getPayInfo() != null && !job.getPayInfo().isBlank() ? job.getPayInfo() : "Volunteer / To be Discussed");
    payLabel.getStyleClass().add("card-info-text");
    payRow.getChildren().addAll(payIcon, payLabel);

    HBox scheduleRow = new HBox();
    scheduleRow.setSpacing(8);
    scheduleRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
    scheduleRow.getStyleClass().add("card-info-row");

    ImageView clockIcon = new ImageView(new Image(getClass().getResourceAsStream("/Assets/Icons/clock.png")));
    clockIcon.setFitWidth(16);
    clockIcon.setFitHeight(16);

    Label scheduleLabel = new Label(job.getScheduleInfo() != null && !job.getScheduleInfo().isBlank() ? job.getScheduleInfo() : "Flexible");
    scheduleLabel.getStyleClass().add("card-info-text");
    scheduleRow.getChildren().addAll(clockIcon, scheduleLabel);

    // --- 4. FOOTER & ACTION BUTTON ---
    Region grow = new Region();
    VBox.setVgrow(grow, javafx.scene.layout.Priority.ALWAYS);

    Button actionButton = new Button("View & Apply");
    actionButton.setMaxWidth(Double.MAX_VALUE);
    actionButton.getStyleClass().add("card-action-btn");
    actionButton.setOnAction(event -> openJobDetails(job));

    card.getChildren().addAll(
        topBadgeRow,
        titleLabel,
        locationLabel,
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