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

    
            Scene scene = new Scene(root, 1920, 1080); 
            primaryStage.setTitle("Agapay");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.show();
    }

}

