package services;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Supplier;

import components.AppShell;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.Category;
import models.Movie;
import screens.GenresScreen;
import screens.HomeScreen;
import screens.MovieDetailsScreen;
import screens.MoviesScreen;
import screens.MyListScreen;
import screens.PlayerScreen;
import screens.SearchScreen;
import animations.TransitionAnimation;

public class NavigationService {
    private static Stage primaryStage;
    private static final double WINDOW_WIDTH = 1280;
    private static final double WINDOW_HEIGHT = 820;
    private static final Deque<Route> history = new ArrayDeque<>();
    private static Route currentRoute;

    private record Route(Supplier<Parent> pageFactory, String activePage, String title) {}

    public static void setStage(Stage stage) {
        primaryStage = stage;
        if (currentRoute == null) {
            currentRoute = new Route(() -> new HomeScreen().create(), "home", "Home");
        }
    }

    private void navigate(Supplier<Parent> pageFactory, String activePage, String title) {
        if (currentRoute != null) history.push(currentRoute);
        display(new Route(pageFactory, activePage, title));
    }

    private void display(Route route) {
        if (primaryStage == null) {
            System.err.println("Navigation error: Stage has not been set.");
            return;
        }
        currentRoute = route;
        Parent application = new AppShell().create(route.pageFactory().get(), route.activePage(), route.title());
        Scene scene = primaryStage.getScene();
        if (scene == null) {
            scene = new Scene(application, WINDOW_WIDTH, WINDOW_HEIGHT);
            primaryStage.setScene(scene);
        } else {
            scene.setRoot(application);
        }
        TransitionAnimation.playIn(application);
    }

    public static boolean canGoBack() { return !history.isEmpty(); }

    public void goBack() {
        if (!history.isEmpty()) display(history.pop());
        else showHome();
    }

    public void showHome() { navigate(() -> new HomeScreen().create(), "home", "Home"); }
    public void showMovies() { navigate(() -> new MoviesScreen().create(), "movies", "Movies"); }
    public void showGenres() { navigate(() -> new GenresScreen().create(), "genres", "Genres"); }
    public void showSearch() { showSearch(""); }
    public void showSearch(String query) { navigate(() -> new SearchScreen().create(query), "search", "Search"); }
    public void showMyList() { navigate(() -> new MyListScreen().create(), "my-list", "My List"); }
    public void showMovieDetails(Movie movie) { navigate(() -> new MovieDetailsScreen().create(movie), "", "Movie Details"); }
    public void showPlayer(Movie movie) { navigate(() -> new PlayerScreen().create(movie), "", "Trailer Player"); }
    public void showCategory(Category category) {
        navigate(() -> new GenresScreen().create(category), "genres", category.getDisplayName());
    }
}
