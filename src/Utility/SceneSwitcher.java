package Utility;


import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
            primaryStage.setResizable(true);
            primaryStage.show();

            primaryStage.setMaximized(true);
            primaryStage.show();
    }

}

