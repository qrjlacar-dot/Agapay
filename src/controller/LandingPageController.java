package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;
import java.io.IOException;

public class LandingPageController {

    @FXML
    private Hyperlink viewAllLink; // Connects to your "View All" link in LandingPage.fxml

    @FXML
    public void initialize() {
        if (viewAllLink != null) {
            viewAllLink.setOnAction(event -> {
                try {
                    // Loads ViewAll.fxml from your GUI folder directory
                    Parent root = FXMLLoader.load(getClass().getResource("/GUI/ViewAll.fxml"));
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}