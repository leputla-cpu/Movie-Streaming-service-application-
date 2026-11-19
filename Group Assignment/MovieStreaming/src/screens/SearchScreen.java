
package screens;

import components.MovieCard;
import data.MovieData;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import models.Movie;

import java.util.List;
import java.util.Locale;

/** Displays live search results from the shared top-bar search field. */
public class SearchScreen {

    private final FlowPane results = new FlowPane();
    private final Label resultMessage = new Label();

    public VBox create() {
        return create("");
    }

    public VBox create(String initialQuery) {
        VBox root = new VBox(16);
        root.setPadding(new Insets(28, 30, 35, 30));
        root.getStyleClass().add("screen-content");

        Label heading = new Label("Search Results");
        heading.getStyleClass().add("screen-heading");

        Label subtitle = new Label(
                "Browse movies matching your search."
        );
        subtitle.getStyleClass().add("screen-subheading");

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

        VBox.setVgrow(scroll, Priority.ALWAYS);

        root.getChildren().addAll(
                heading,
                subtitle,
                resultMessage,
                scroll
        );

        searchMovies(initialQuery);

        return root;
    }

    /**
     * Updates the results using text from the shared top-bar search field.
     */
    public void setQuery(String query) {
        searchMovies(query);
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

        if (query.isEmpty()) {
            resultMessage.setText(
                    matches.size() + " movies available. Search using the top bar."
            );
        } else if (matches.isEmpty()) {
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
