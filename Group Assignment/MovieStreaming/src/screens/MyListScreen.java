
package screens;

import components.MovieCard;
import data.MovieData;
import models.Movie;
import services.NavigationService;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class MyListScreen {

    private static final List<Movie> myMovies =
            new ArrayList<>();

    private final NavigationService navigationService =
            new NavigationService();

    public static void addMovie(Movie movie) {

        if (movie == null) {
            return;
        }

        boolean alreadyExists = myMovies.stream()
                .anyMatch(existing ->
                        existing.getId() == movie.getId()
                );

        if (!alreadyExists) {
            myMovies.add(movie);
        }
    }

    public static void removeMovie(Movie movie) {

        if (movie != null) {
            myMovies.removeIf(existing ->
                    existing.getId() == movie.getId()
            );
        }
    }

    public static List<Movie> getMyMovies() {
        return new ArrayList<>(myMovies);
    }

    public VBox create() {

        VBox root = new VBox(20);
        root.getStyleClass().add("main-root");
        root.setPadding(new Insets(25, 30, 30, 30));

        Label backLabel = new Label("Back to Home");
        backLabel.getStyleClass().add("player-back");

        backLabel.setOnMouseClicked(event ->
                navigationService.showHome()
        );

        Label heading = new Label("My List");
        heading.getStyleClass().add("details-title");

        FlowPane movieContainer = new FlowPane();
        movieContainer.setHgap(18);
        movieContainer.setVgap(25);
        movieContainer.setPadding(new Insets(10));

        if (myMovies.isEmpty()) {

            Label emptyMessage = new Label(
                    "You haven't added any movies to your list yet."
            );

            emptyMessage.getStyleClass().add(
                    "player-description"
            );

            movieContainer.getChildren().add(emptyMessage);

        } else {

            for (Movie movie : myMovies) {

                MovieCard movieCard = new MovieCard();

                movieContainer.getChildren().add(
                        movieCard.create(movie)
                );
            }
        }

        ScrollPane scrollPane = new ScrollPane(movieContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );
        scrollPane.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );
        scrollPane.getStyleClass().add("main-scroll");

        VBox.setVgrow(
                scrollPane,
                javafx.scene.layout.Priority.ALWAYS
        );

        root.getChildren().addAll(
                backLabel,
                heading,
                scrollPane
        );

        return root;
    }
}
