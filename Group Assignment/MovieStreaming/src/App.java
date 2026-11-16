
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import screens.HomeScreen;
import components.AppShell;
import animations.TransitionAnimation;
import services.NavigationService;
import services.YouTubeService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class App extends Application {

    private static final double WINDOW_WIDTH = 1180;
    private static final double WINDOW_HEIGHT = 760;

    private static final double MIN_WINDOW_WIDTH = 900;
    private static final double MIN_WINDOW_HEIGHT = 600;

    @Override
    public void start(Stage stage) {
        NavigationService.setStage(stage);

        HomeScreen homeScreen = new HomeScreen();
        AppShell appShell = new AppShell();

        Scene scene = new Scene(
                appShell.create(homeScreen.create(), "home", "Home")
        );

        Path cssPath = Paths.get("resources", "css", "style.css");

        if (Files.exists(cssPath)) {
            scene.getStylesheets().add(
                    cssPath.toAbsolutePath().toUri().toString()
            );
            System.out.println(
                    "CSS loaded successfully: " + cssPath.toAbsolutePath()
            );
        } else {
            System.out.println(
                    "CSS FILE NOT FOUND: " + cssPath.toAbsolutePath()
            );
        }

        stage.setTitle("Watch Z - Movie Streaming");
        stage.setScene(scene);

        // Get the usable screen area, excluding the taskbar.
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        // Leave a small margin around the application window.
        double availableWidth = screenBounds.getWidth() - 40;
        double availableHeight = screenBounds.getHeight() - 60;

        // Use the preferred size when possible, otherwise fit the screen.
        double initialWidth = Math.min(WINDOW_WIDTH, availableWidth);
        double initialHeight = Math.min(WINDOW_HEIGHT, availableHeight);

        stage.setMinWidth(
                Math.min(MIN_WINDOW_WIDTH, availableWidth)
        );
        stage.setMinHeight(
                Math.min(MIN_WINDOW_HEIGHT, availableHeight)
        );

        stage.setWidth(initialWidth);
        stage.setHeight(initialHeight);

        stage.setResizable(true);

        // Centre the window within the usable screen area.
        stage.setX(
                screenBounds.getMinX()
                        + (screenBounds.getWidth() - initialWidth) / 2
        );

        stage.setY(
                screenBounds.getMinY()
                        + (screenBounds.getHeight() - initialHeight) / 2
        );

        stage.show();

        TransitionAnimation.playIn(scene.getRoot());
    }

    @Override
    public void stop() {
        YouTubeService.shutdown();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
