import java.io.IOException;
import java.io.InputStream;

import Utility.SceneSwitcher;
import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {

            // 1. Load Custom Fonts globally into JavaFX Memory
            loadCustomFont("/Assets/Fonts/AtkinsonHyperlegibleNext-ExtraBold.ttf");
            loadCustomFont("/Assets/Fonts/AtkinsonHyperlegibleNext-SemiBold.ttf");
            loadCustomFont("/Assets/Fonts/AtkinsonHyperlegibleNext-Bold.ttf");
            SceneSwitcher.setPrimaryStage(primaryStage);

            SceneSwitcher.switchTo("Login.fxml");
            
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