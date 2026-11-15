
package components;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import models.Movie;
import services.NavigationService;
import animations.HoverAnimation;
import utils.ImageLoader;
import utils.SoundManager;

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
            poster.setImage(ImageLoader.load(imageUrl));
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

        // Shared hover animation and optional interface sound.
        HoverAnimation.apply(card);
        card.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> SoundManager.playHover());

        // Open movie details when clicked.
        card.setOnMouseClicked(event -> {
            SoundManager.playClick();
            navigationService.showMovieDetails(movie);
        });

        return card;
    }
}
