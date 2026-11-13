
package screens;

import components.MovieCard;
import data.MovieData;
import models.Category;
import models.Movie;
import services.NavigationService;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class SearchScreen {

    private final NavigationService navigationService =
            new NavigationService();

    private final FlowPane results = new FlowPane();
    private final Label resultMessage = new Label();

    private final List<Movie> allMovies = new ArrayList<>();

    public VBox create() {

        VBox root = new VBox(20);
        root.getStyleClass().add("main-root");
        root.setPadding(new Insets(25, 30, 30, 30));

        loadMovies();

        Label heading = new Label("Search Movies");
        heading.getStyleClass().add("details-title");

        Label backLabel = new Label("Back to Home");
        backLabel.getStyleClass().add("player-back");
        backLabel.setOnMouseClicked(event ->
                navigationService.showHome()
        );

        TextField searchField = new TextField();
        searchField.setPromptText("Search for a movie...");
        searchField.getStyleClass().add("search-field");
        searchField.setMaxWidth(500);

        searchField.textProperty().addListener(
                (observable, oldValue, newValue) ->
                        searchMovies(newValue)
        );

        results.setHgap(18);
        results.setVgap(25);
        results.setPadding(new Insets(10));
        results.setAlignment(Pos.TOP_LEFT);

        resultMessage.getStyleClass().add("player-description");

        ScrollPane scrollPane = new ScrollPane(results);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );
        scrollPane.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );
        scrollPane.getStyleClass().add("main-scroll");
        VBox.setVgrow(scrollPane, javafx.scene.layout.Priority.ALWAYS);

        root.getChildren().addAll(
                backLabel,
                heading,
                searchField,
                resultMessage,
                scrollPane
        );

        searchMovies("");

        return root;
    }

    private void loadMovies() {

        allMovies.clear();

        Category[] categories = {
            Category.ACTION,
            Category.COMEDY,
            Category.HORROR,
            Category.SCIENCE_FICTION,
            Category.ANIMATION
        };

        for (Category category : categories) {
            allMovies.addAll(
                    MovieData.getMoviesByCategory(category)
            );
        }
    }

    private void searchMovies(String searchText) {

        results.getChildren().clear();

        String query = searchText == null
                ? ""
                : searchText.trim().toLowerCase();

        int count = 0;

        for (Movie movie : allMovies) {

            if (query.isEmpty()
                    || movie.getTitle().toLowerCase().contains(query)) {

                MovieCard card = new MovieCard();
                results.getChildren().add(card.create(movie));
                count++;
            }
        }

        if (count == 0) {
            resultMessage.setText("No movies found.");
        } else {
            resultMessage.setText(
                    count + " movie(s) found."
            );
        }
    }
}
