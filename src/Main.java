import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create a simple button
        Button btn = new Button("Hello! I finally work!");

        // Put the button inside a layout
        StackPane root = new StackPane();
        root.getChildren().add(btn);

        // Create the scene (the window content)
        Scene scene = new Scene(root, 300, 250);

        // Set up the main window and show it
        primaryStage.setTitle("My JavaFX App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}