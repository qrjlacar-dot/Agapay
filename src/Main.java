import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.InputStream;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. Load Custom Fonts globally into JavaFX Memory
            loadCustomFont("/Assets/Fonts/AtkinsonHyperlegibleNext-ExtraBold.ttf");
            loadCustomFont("/Assets/Fonts/AtkinsonHyperlegibleNext-SemiBold.ttf");
            loadCustomFont("/Assets/Fonts/AtkinsonHyperlegibleNext-Bold.ttf");

            // 2. Load the Login Screen FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Login.fxml"));
            Parent root = loader.load();

            // 3. Set up the Primary Stage
            Scene scene = new Scene(root, 1280, 720); // Matches standard project size
            primaryStage.setTitle("Agapay");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Error initializing application:");
            e.printStackTrace();
        }
    }

    /**
     * Helper method to safely load custom TTF fonts.
     */
    private void loadCustomFont(String fontPath) {
        try (InputStream is = getClass().getResourceAsStream(fontPath)) {
            if (is != null) {
                Font.loadFont(is, 12);
            } else {
                System.err.println("Font file not found: " + fontPath);
            }
        } catch (Exception e) {
            System.err.println("Failed to load font at " + fontPath + ": " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}