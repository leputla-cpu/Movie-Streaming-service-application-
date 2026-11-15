import javafx.application.Application;
import javafx.scene.Scene;
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

    @Override
    public void start(Stage stage) {
        NavigationService.setStage(stage);

        HomeScreen homeScreen = new HomeScreen();
        AppShell appShell = new AppShell();
        Scene scene = new Scene(appShell.create(homeScreen.create(), "home", "Home"), WINDOW_WIDTH, WINDOW_HEIGHT);

        Path cssPath = Paths.get("resources", "css", "style.css");
        if (Files.exists(cssPath)) {
            scene.getStylesheets().add(cssPath.toAbsolutePath().toUri().toString());
            System.out.println("CSS loaded successfully: " + cssPath.toAbsolutePath());
        } else {
            System.out.println("CSS FILE NOT FOUND: " + cssPath.toAbsolutePath());
        }

        stage.setTitle("Watch Z - Movie Streaming");
        stage.setScene(scene);
        stage.setMinWidth(1000);
        stage.setMinHeight(650);
        stage.setResizable(true);
        stage.centerOnScreen();
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
