
package components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import models.Category;
import models.Movie;
import data.MovieData;
import services.NavigationService;
import javafx.scene.Cursor;

public class MovieRow {

    private static final double CARD_GAP = 14;

    public VBox create(Category category) {

        VBox section = new VBox(12);
        section.setPadding(new Insets(10, 0, 15, 0));
        section.setMaxWidth(Double.MAX_VALUE);
        section.getStyleClass().add("movie-row-section");

        Label heading = new Label(category.getDisplayName());
        heading.getStyleClass().add("movie-row-heading");
        Label count = new Label(MovieData.getMoviesByCategory(category).size() + " titles");
        count.getStyleClass().add("movie-row-count");
        javafx.scene.layout.HBox titleLine = new javafx.scene.layout.HBox(10, heading, count);
        titleLine.setAlignment(Pos.CENTER_LEFT);
        titleLine.setCursor(Cursor.HAND);
        titleLine.setOnMouseClicked(event -> new NavigationService().showCategory(category));

        HBox cards = new HBox(CARD_GAP);
        cards.setAlignment(Pos.CENTER_LEFT);
        cards.setPadding(new Insets(12, 6, 12, 6));

        for (Movie movie :
                MovieData.getMoviesByCategory(category)) {

            MovieCard movieCard = new MovieCard();
            cards.getChildren().add(movieCard.create(movie));
        }

        ScrollPane scrollPane = new ScrollPane(cards);

        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(false);

        // Five poster cards fit in the visible viewport; the remaining titles scroll horizontally.
        scrollPane.setPrefWidth(720);
        scrollPane.setMaxWidth(720);
        scrollPane.setMinWidth(0);

        scrollPane.setHbarPolicy(
            ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scrollPane.setVbarPolicy(
            ScrollPane.ScrollBarPolicy.NEVER
        );

        scrollPane.setPannable(true);
        scrollPane.getStyleClass().add("movie-horizontal-scroll");

        section.getChildren().addAll(titleLine, scrollPane);

        return section;
    }
}
