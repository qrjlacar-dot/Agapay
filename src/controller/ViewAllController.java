package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

public class ViewAllController {

    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        if (backButton != null) {
            backButton.setOnAction(event -> {
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/GUI/LandingPage.fxml"));
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