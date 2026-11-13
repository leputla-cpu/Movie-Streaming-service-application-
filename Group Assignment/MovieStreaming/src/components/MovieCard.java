
package components;

import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import models.Movie;
import services.NavigationService;

public class MovieCard {

    private static final double CARD_WIDTH = 130;
    private static final double POSTER_HEIGHT = 195;

    private final NavigationService navigationService =
            new NavigationService();

    public VBox create(Movie movie) {

        VBox card = new VBox(8);
        card.setAlignment(Pos.TOP_LEFT);

        card.setMinWidth(CARD_WIDTH);
        card.setPrefWidth(CARD_WIDTH);
        card.setMaxWidth(CARD_WIDTH);

        card.getStyleClass().add("movie-card");
        card.setCursor(Cursor.HAND);

        // Poster image
        ImageView poster = new ImageView();

        String imageUrl = movie.getPosterUrl();

        if (imageUrl != null && !imageUrl.isBlank()) {
            try {
                Image image = new Image(imageUrl, true);
                poster.setImage(image);

            } catch (IllegalArgumentException exception) {
                System.out.println(
                    "Invalid poster URL for " + movie.getTitle()
                );
            }
        }

        poster.setFitWidth(CARD_WIDTH);
        poster.setFitHeight(POSTER_HEIGHT);
        poster.setPreserveRatio(false);
        poster.getStyleClass().add("movie-poster");

        // Poster container
        StackPane posterContainer = new StackPane(poster);

        posterContainer.setMinWidth(CARD_WIDTH);
        posterContainer.setPrefWidth(CARD_WIDTH);
        posterContainer.setMaxWidth(CARD_WIDTH);

        posterContainer.setMinHeight(POSTER_HEIGHT);
        posterContainer.setPrefHeight(POSTER_HEIGHT);
        posterContainer.setMaxHeight(POSTER_HEIGHT);

        posterContainer.getStyleClass().add("poster-container");

        // Movie title
        Label title = new Label(movie.getTitle());
        title.setWrapText(true);
        title.setMaxWidth(CARD_WIDTH);
        title.getStyleClass().add("movie-card-title");

        card.getChildren().addAll(
            posterContainer,
            title
        );

        // Open movie details when clicked
        card.setOnMouseClicked(event -> {
            navigationService.showMovieDetails(movie);
        });

        // Smooth hover animation
        card.setOnMouseEntered(event -> {

            ScaleTransition transition =
                new ScaleTransition(Duration.millis(180), card);

            transition.setToX(1.06);
            transition.setToY(1.06);
            transition.play();
        });

        card.setOnMouseExited(event -> {

            ScaleTransition transition =
                new ScaleTransition(Duration.millis(180), card);

            transition.setToX(1.0);
            transition.setToY(1.0);
            transition.play();
        });

        return card;
    }
}
