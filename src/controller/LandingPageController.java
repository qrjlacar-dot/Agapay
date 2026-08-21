package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Utility.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import model.Job;
import services.AuthServices;
import services.JobRecommendationServices;
import services.JobSelectionState;

public class LandingPageController {

    @FXML private TextField searchField;
    @FXML private HBox cardsContainer;

    private final JobRecommendationServices recommendationService = new JobRecommendationServices();
    private final List<Job> recommendedJobs = new ArrayList<>();

    @FXML
    public void initialize() {
        if (AuthServices.activeUserId == -1) {
            System.err.println("No active user session found!");
            return;
        }

        List<Job> allJobs = recommendationService.getRecommendedJobsForUser(AuthServices.activeUserId);
        
        // Filter out jobs with a 0% match score
        List<Job> matchedJobs = allJobs.stream()
            .filter(job -> job.getMatchScore() > 0.0)
            .toList();

        recommendedJobs.clear();
        recommendedJobs.addAll(matchedJobs);
        populateCards();
    }

    private void populateCards() {
        if (cardsContainer == null) {
            return;
        }

        // Clear any hardcoded placeholder cards from the FXML
        cardsContainer.getChildren().clear();

        // Display up to 3 jobs on the landing page to fit nicely in the HBox
        int displayCount = Math.min(3, recommendedJobs.size());
        
        for (int i = 0; i < displayCount; i++) {
            cardsContainer.getChildren().add(createJobCard(recommendedJobs.get(i)));
        }

        // If no jobs match, show a friendly prompt
        if (recommendedJobs.isEmpty()) {
            Label emptyLabel = new Label("Complete your profile or adjust your skills to see matched jobs!");
            emptyLabel.getStyleClass().add("card-location");
            cardsContainer.getChildren().add(emptyLabel);
        }
    }

    /**
     * Dynamically generates the job card to match the new prominent badge design.
     */
    private VBox createJobCard(Job job) {
        VBox card = new VBox();
        card.getStyleClass().add("job-card");
        card.setSpacing(10);
        
        // Set fixed width so they look uniform side-by-side in the HBox
        card.setPrefWidth(320);
        card.setMinWidth(320); 

        // --- TOP BADGE HEADER ---
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

        boolean isVolunteer = (job.getPayInfo() != null && job.getPayInfo().toLowerCase().contains("volunteer")) 
                           || (job.getTitle() != null && job.getTitle().toLowerCase().contains("volunteer"));

        Label greenPill = new Label(isVolunteer ? "Volunteer" : "Recommended");
        greenPill.getStyleClass().add("badge-green-pill");

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, javafx.scene.layout.Priority.ALWAYS);

        topBadgeRow.getChildren().addAll(matchBadgeContainer, topSpacer, greenPill);

        // --- TITLES & TEXT ---
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
    
    // --- NAVIGATION HANDLERS ---

    @FXML
    private void handleLogout() {
        AuthServices.activeUserId = -1; 
        try {
            SceneSwitcher.switchTo("Login.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch() {
        String query = searchField != null ? searchField.getText().trim() : "";
        JobSelectionState.setSearchQuery(query);
        try {
            SceneSwitcher.switchTo("getRecommendedJobs.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleFindMatchedJobs() {
        JobSelectionState.clearSearchQuery();
        try {
            SceneSwitcher.switchTo("ProfileStep1.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewAll() {
        JobSelectionState.clearSearchQuery();
        try {
            SceneSwitcher.switchTo("ViewAll.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOpenJobDetails() {
        if (!recommendedJobs.isEmpty()) {
            JobSelectionState.setSelectedJob(recommendedJobs.get(0));
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
}