package Utility;


import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneSwitcher {
    
    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchTo(String fxmlFileName) throws IOException {
            FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource("/GUI/" + fxmlFileName));
            Parent root = loader.load();

    
        primaryStage.setTitle("Agapay");

        Scene currentScene = primaryStage.getScene();
        if (currentScene == null) {
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
        } else {
            currentScene.setRoot(root);
        }

        primaryStage.setResizable(true);
        primaryStage.setMaximized(true);

        primaryStage.show();
    }

}

