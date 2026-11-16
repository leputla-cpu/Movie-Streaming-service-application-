
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

    private static SearchScreen activeSearchScreen;
    private static String currentSearchQuery = "";

    private record Route(
            Supplier<Parent> pageFactory,
            String activePage,
            String title
    ) {}

    public static void setStage(Stage stage) {
        primaryStage = stage;

        if (currentRoute == null) {
            currentRoute = new Route(
                    () -> new HomeScreen().create(),
                    "home",
                    "Home"
            );
        }
    }

    private void navigate(
            Supplier<Parent> pageFactory,
            String activePage,
            String title
    ) {
        if (currentRoute != null) {
            history.push(currentRoute);
        }

        display(new Route(pageFactory, activePage, title));
    }

    private void display(Route route) {
        if (primaryStage == null) {
            System.err.println(
                    "Navigation error: Stage has not been set."
            );
            return;
        }

        currentRoute = route;

        Parent page = route.pageFactory().get();

        String searchText = route.activePage().equals("search")
                ? currentSearchQuery
                : "";

        Parent application = new AppShell().create(
                page,
                route.activePage(),
                route.title(),
                searchText
        );

        Scene scene = primaryStage.getScene();

        if (scene == null) {
            scene = new Scene(
                    application,
                    WINDOW_WIDTH,
                    WINDOW_HEIGHT
            );
            primaryStage.setScene(scene);
        } else {
            scene.setRoot(application);
        }

        TransitionAnimation.playIn(application);
    }

    public static boolean canGoBack() {
        return !history.isEmpty();
    }

    public void goBack() {
        if (!history.isEmpty()) {
            display(history.pop());
        } else {
            showHome();
        }
    }

    public void showHome() {
        activeSearchScreen = null;
        currentSearchQuery = "";

        navigate(
                () -> new HomeScreen().create(),
                "home",
                "Home"
        );
    }

    public void showMovies() {
        activeSearchScreen = null;
        currentSearchQuery = "";

        navigate(
                () -> new MoviesScreen().create(),
                "movies",
                "Movies"
        );
    }

    public void showGenres() {
        activeSearchScreen = null;
        currentSearchQuery = "";

        navigate(
                () -> new GenresScreen().create(),
                "genres",
                "Genres"
        );
    }

    public void showSearch() {
        showSearch("");
    }

    public void showSearch(String query) {
        currentSearchQuery = query == null ? "" : query;

        SearchScreen searchScreen = new SearchScreen();
        activeSearchScreen = searchScreen;

        navigate(
                () -> searchScreen.create(currentSearchQuery),
                "search",
                "Search"
        );
    }

    /**
     * Called by the shared top search field as the user types.
     * Navigates to Search once, then updates the existing SearchScreen.
     */
    public void searchFromTopBar(String query) {
        currentSearchQuery = query == null ? "" : query;

        if (currentRoute != null
                && "search".equals(currentRoute.activePage())
                && activeSearchScreen != null) {

            activeSearchScreen.setQuery(currentSearchQuery);

        } else {
            showSearchFromTopBar(currentSearchQuery);
        }
    }

    private void showSearchFromTopBar(String query) {
        currentSearchQuery = query;

        SearchScreen searchScreen = new SearchScreen();
        activeSearchScreen = searchScreen;

        navigate(
                () -> searchScreen.create(currentSearchQuery),
                "search",
                "Search"
        );
    }

    public void showMyList() {
        activeSearchScreen = null;
        currentSearchQuery = "";

        navigate(
                () -> new MyListScreen().create(),
                "my-list",
                "My List"
        );
    }

    public void showMovieDetails(Movie movie) {
        activeSearchScreen = null;
        currentSearchQuery = "";

        navigate(
                () -> new MovieDetailsScreen().create(movie),
                "",
                "Movie Details"
        );
    }

    public void showPlayer(Movie movie) {
        activeSearchScreen = null;
        currentSearchQuery = "";

        navigate(
                () -> new PlayerScreen().create(movie),
                "",
                "Trailer Player"
        );
    }

    public void showCategory(Category category) {
        activeSearchScreen = null;
        currentSearchQuery = "";

        navigate(
                () -> new GenresScreen().create(category),
                "genres",
                category.getDisplayName()
        );
    }
}
