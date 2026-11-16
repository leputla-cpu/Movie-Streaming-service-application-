
package screens;

import components.MovieCard;
import data.MovieData;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import models.Movie;

import java.util.List;
import java.util.Locale;

/** Live search across movie titles, descriptions and genre names. */
public class SearchScreen {

    private final FlowPane results = new FlowPane();
    private final Label resultMessage = new Label();

    private TextField searchField;

    public VBox create() {
        return create("");
    }

    public VBox create(String initialQuery) {
        VBox root = new VBox(16);
        root.setPadding(new Insets(28, 30, 35, 30));
        root.getStyleClass().add("screen-content");

        Label heading = new Label("Search Movies");
        heading.getStyleClass().add("screen-heading");

        Label subtitle = new Label(
                "Search by title, story description or genre."
        );
        subtitle.getStyleClass().add("screen-subheading");

        searchField = new TextField();
        searchField.setPromptText("Type a movie title or genre...");
        searchField.setMaxWidth(560);
        searchField.getStyleClass().add("search-field");

        searchField.setText(
                initialQuery == null ? "" : initialQuery
        );

        searchField.textProperty().addListener(
                (observable, oldValue, newValue) ->
                        searchMovies(newValue)
        );

        results.setHgap(19);
        results.setVgap(25);
        results.setPadding(new Insets(10, 3, 20, 3));
        results.setAlignment(Pos.TOP_LEFT);

        resultMessage.getStyleClass().add("search-message");

        ScrollPane scroll = new ScrollPane(results);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.getStyleClass().add("main-scroll");

        VBox.setVgrow(scroll, javafx.scene.layout.Priority.ALWAYS);

        root.getChildren().addAll(
                heading,
                subtitle,
                searchField,
                resultMessage,
                scroll
        );

        searchMovies(searchField.getText());

        return root;
    }

    /**
     * Updates the Search screen from the shared top search field.
     */
    public void setQuery(String query) {
        if (searchField == null) {
            return;
        }

        String newQuery = query == null ? "" : query;

        if (!searchField.getText().equals(newQuery)) {
            searchField.setText(newQuery);
        }
    }

    private void searchMovies(String text) {
        results.getChildren().clear();

        String query = text == null
                ? ""
                : text.trim().toLowerCase(Locale.ROOT);

        List<Movie> matches = MovieData.getAllMovies()
                .stream()
                .filter(movie -> {

                    String title = movie.getTitle() == null
                            ? ""
                            : movie.getTitle().toLowerCase(Locale.ROOT);

                    String description = movie.getDescription() == null
                            ? ""
                            : movie.getDescription().toLowerCase(Locale.ROOT);

                    String genre = movie.getCategory() == null
                            ? ""
                            : movie.getCategory()
                                    .getDisplayName()
                                    .toLowerCase(Locale.ROOT);

                    return query.isEmpty()
                            || title.contains(query)
                            || description.contains(query)
                            || genre.contains(query);
                })
                .toList();

        for (Movie movie : matches) {
            results.getChildren().add(
                    new MovieCard().create(movie)
            );
        }

        if (matches.isEmpty()) {
            resultMessage.setText(
                    "No matching movies. Try another title or genre."
            );
        } else {
            resultMessage.setText(
                    matches.size()
                            + (matches.size() == 1
                            ? " movie found."
                            : " movies found.")
            );
        }
    }
}
