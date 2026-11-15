package screens;

import components.MovieCard;
import data.MovieData;
import models.Movie;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

/** In-memory saved-movie collection for the current application session. */
public class MyListScreen {
    private static final List<Movie> myMovies = new ArrayList<>();

    public static void addMovie(Movie movie) {
        if (movie != null && !contains(movie)) myMovies.add(movie);
    }

    public static void removeMovie(Movie movie) {
        if (movie != null) myMovies.removeIf(existing -> existing.getId() == movie.getId());
    }

    public static boolean contains(Movie movie) {
        return movie != null && myMovies.stream().anyMatch(existing -> existing.getId() == movie.getId());
    }

    public static List<Movie> getMyMovies() { return new ArrayList<>(myMovies); }

    public VBox create() {
        VBox root = new VBox(16);
        root.setPadding(new Insets(28, 30, 35, 30));
        root.getStyleClass().add("screen-content");

        Label heading = new Label("My List");
        heading.getStyleClass().add("screen-heading");
        Label subtitle = new Label("Movies you have saved for later.");
        subtitle.getStyleClass().add("screen-subheading");

        FlowPane grid = new FlowPane();
        grid.setHgap(19);
        grid.setVgap(25);
        grid.setAlignment(Pos.TOP_LEFT);
        grid.setPadding(new Insets(8, 0, 15, 0));

        List<Movie> saved = getMyMovies();
        if (saved.isEmpty()) {
            Label empty = new Label("Your list is empty. Open a movie and select Add to My List.");
            empty.getStyleClass().add("my-list-empty");
            grid.getChildren().add(empty);
        } else {
            for (Movie movie : saved) grid.getChildren().add(new MovieCard().create(movie));
        }

        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.getStyleClass().add("main-scroll");
        VBox.setVgrow(scroll, javafx.scene.layout.Priority.ALWAYS);
        root.getChildren().addAll(heading, subtitle, scroll);
        return root;
    }
}
