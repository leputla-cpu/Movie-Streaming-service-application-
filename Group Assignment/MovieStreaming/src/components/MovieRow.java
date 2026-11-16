
package components;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import models.Category;
import models.Movie;
import data.MovieData;
import services.NavigationService;

import java.util.List;

public class MovieRow {

    private static final double CARD_GAP = 14;
    private static final double DEFAULT_CARD_WIDTH = 130;
    private static final double POSTER_HEIGHT = 195;

    private static final int VISIBLE_MOVIES = 5;

    public VBox create(Category category) {

        VBox section = new VBox(12);
        section.setPadding(new Insets(10, 0, 15, 0));
        section.setMaxWidth(Double.MAX_VALUE);
        section.getStyleClass().add("movie-row-section");

        // Category heading
        Label heading = new Label(category.getDisplayName());
        heading.getStyleClass().add("movie-row-heading");

        List<Movie> movies = MovieData.getMoviesByCategory(category);

        Label count = new Label(movies.size() + " titles");
        count.getStyleClass().add("movie-row-count");

        HBox titleLine = new HBox(10, heading, count);
        titleLine.setAlignment(Pos.CENTER_LEFT);
        titleLine.setCursor(javafx.scene.Cursor.HAND);

        titleLine.setOnMouseClicked(event ->
                new NavigationService().showCategory(category)
        );

        // Horizontal movie container
        HBox cards = new HBox(CARD_GAP);
        cards.setAlignment(Pos.CENTER_LEFT);
        cards.setPadding(new Insets(12, 6, 12, 6));

        // Shared responsive width for all movie cards in this row.
        SimpleDoubleProperty cardWidth =
                new SimpleDoubleProperty(DEFAULT_CARD_WIDTH);

        for (Movie movie : movies) {

            MovieCard movieCard = new MovieCard();

            cards.getChildren().add(
                    movieCard.create(movie, cardWidth)
            );
        }

        // Horizontal scrolling container
        ScrollPane scrollPane = new ScrollPane(cards);

        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(false);

        scrollPane.setMinWidth(0);
        scrollPane.setMaxWidth(Double.MAX_VALUE);

        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scrollPane.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scrollPane.setPannable(true);
        scrollPane.getStyleClass().add("movie-horizontal-scroll");

        // Make the row occupy the available width of its parent.
        scrollPane.prefWidthProperty().bind(
                section.widthProperty()
        );

        /*
         * Calculate the card width from the actual visible viewport.
         * Five cards + four gaps + horizontal card-container padding.
         */
        scrollPane.viewportBoundsProperty().addListener(
                (observable, oldBounds, newBounds) -> {

                    double viewportWidth = newBounds.getWidth();

                    if (viewportWidth <= 0) {
                        return;
                    }

                    double horizontalPadding = 12;

                    double totalGap =
                            CARD_GAP * (VISIBLE_MOVIES - 1);

                    double availableWidth =
                            viewportWidth
                            - horizontalPadding
                            - totalGap;

                    double newCardWidth =
                            availableWidth / VISIBLE_MOVIES;

                    // Prevent cards from becoming excessively small.
                    newCardWidth = Math.max(55, newCardWidth);

                    cardWidth.set(newCardWidth);
                }
        );

        VBox.setVgrow(scrollPane, Priority.NEVER);

        section.getChildren().addAll(
                titleLine,
                scrollPane
        );

        return section;
    }
}
