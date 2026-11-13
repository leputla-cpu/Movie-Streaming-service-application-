
package services;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import models.Movie;
import screens.HomeScreen;
import screens.MovieDetailsScreen;
import screens.PlayerScreen;
import screens.SearchScreen;
import screens.MyListScreen;

public class NavigationService {

    private static Stage primaryStage;

    private static final double WINDOW_WIDTH = 1050;
    private static final double WINDOW_HEIGHT = 650;

    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    private void showScreen(Parent screen) {

        if (primaryStage == null) {
            System.out.println("Navigation error: Stage has not been set.");
            return;
        }

        Scene scene = primaryStage.getScene();

        if (scene == null) {
            scene = new Scene(
                screen,
                WINDOW_WIDTH,
                WINDOW_HEIGHT
            );

            primaryStage.setScene(scene);

        } else {
            scene.setRoot(screen);
        }
    }

    public void showHome() {
        HomeScreen homeScreen = new HomeScreen();
        showScreen(homeScreen.create());
    }

    public void showMovieDetails(Movie movie) {
        MovieDetailsScreen detailsScreen = new MovieDetailsScreen();
        showScreen(detailsScreen.create(movie));
    }

    public void showPlayer(Movie movie) {
        PlayerScreen playerScreen = new PlayerScreen();
        showScreen(playerScreen.create(movie));
    }

    public void showSearch() {
        SearchScreen searchScreen = new SearchScreen();
        showScreen(searchScreen.create());
    }

    public void showMyList() {
        MyListScreen myListScreen = new MyListScreen();
        showScreen(myListScreen.create());
    }
}
