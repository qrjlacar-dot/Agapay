package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Utility.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
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

        recommendedJobs.clear();
        recommendedJobs.addAll(recommendationService.getRecommendedJobsForUser(AuthServices.activeUserId));
        populateCards();
    }

    private void populateCards() {
        if (cardsContainer == null || recommendedJobs.isEmpty()) {
            return;
        }

        int cardIndex = 0;
        for (Node cardNode : cardsContainer.getChildren()) {
            if (!(cardNode instanceof VBox card)) {
                continue;
            }

            if (cardIndex >= recommendedJobs.size()) {
                card.setVisible(false);
                card.setManaged(false);
                continue;
            }

            Job job = recommendedJobs.get(cardIndex);
            
            List<Label> infoLabels = findLabels(card, "card-info-text");
            Label titleLabel = findFirstLabel(card, "card-job-title");
            Label locationLabel = findFirstLabel(card, "card-location");
            Label matchScoreLabel = findFirstLabel(card, "card-badge-label");
            Button actionButton = findFirstButton(card, "card-action-btn");

            // Combine the database match score with the hardcoded FXML text
            if (matchScoreLabel != null) {
                String currentText = matchScoreLabel.getText();
                // Check prevents double-appending if the method runs multiple times
                if (!currentText.contains("% Match")) {
                    matchScoreLabel.setText(String.format("%.0f%% Match • %s", job.getMatchScore(), currentText));
                }
            }
            
            if (titleLabel != null) {
                titleLabel.setText(job.getTitle());
            }
            if (locationLabel != null) {
                locationLabel.setText(job.getEmployerName() + " • " + job.getLocation());
            }
            if (!infoLabels.isEmpty()) {
                infoLabels.get(0).setText(job.getPayInfo());
            }
            if (infoLabels.size() > 1) {
                infoLabels.get(1).setText(job.getScheduleInfo());
            }
            if (actionButton != null) {
                actionButton.setOnAction(event -> openJobDetails(job));
            }

            cardIndex++;
        }
    }
    
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
            SceneSwitcher.switchTo("getRecommendedJobs.fxml");
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

    private Label findFirstLabel(VBox card, String styleClass) {
        List<Label> labels = new ArrayList<>();
        collectLabels(card, styleClass, labels);
        return labels.isEmpty() ? null : labels.get(0);
    }

    private List<Label> findLabels(VBox card, String styleClass) {
        List<Label> labels = new ArrayList<>();
        collectLabels(card, styleClass, labels);
        return labels;
    }

    private Button findFirstButton(VBox card, String styleClass) {
        List<Button> buttons = new ArrayList<>();
        collectButtons(card, styleClass, buttons);
        return buttons.isEmpty() ? null : buttons.get(0);
    }

    private void collectLabels(Node node, String styleClass, List<Label> output) {
        if (node instanceof Label label && label.getStyleClass().contains(styleClass)) {
            output.add(label);
        }
        if (node instanceof Pane pane) {
            for (Node child : pane.getChildren()) {
                collectLabels(child, styleClass, output);
            }
        }
    }

    private void collectButtons(Node node, String styleClass, List<Button> output) {
        if (node instanceof Button button && button.getStyleClass().contains(styleClass)) {
            output.add(button);
        }
        if (node instanceof Pane pane) {
            for (Node child : pane.getChildren()) {
                collectButtons(child, styleClass, output);
            }
        }
    }
}