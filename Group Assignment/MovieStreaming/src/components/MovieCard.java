
package components;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
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

    // Default method retained for compatibility with existing code.
    public VBox create(Movie movie) {
        return create(movie, new SimpleDoubleProperty(CARD_WIDTH));
    }

    // Responsive method: receives its width from MovieRow.
    public VBox create(Movie movie, DoubleProperty responsiveWidth) {

        VBox card = new VBox(8);
        card.setAlignment(Pos.TOP_LEFT);

        card.minWidthProperty().bind(responsiveWidth);
        card.prefWidthProperty().bind(responsiveWidth);
        card.maxWidthProperty().bind(responsiveWidth);

        card.getStyleClass().add("movie-card");
        card.setCursor(Cursor.HAND);

        // Poster image
        ImageView poster = new ImageView();

        String imageUrl = movie.getPosterUrl();

        if (imageUrl != null && !imageUrl.isBlank()) {
            poster.setImage(ImageLoader.load(imageUrl));
        }

        poster.fitWidthProperty().bind(responsiveWidth);

        poster.fitHeightProperty().bind(
                responsiveWidth.multiply(POSTER_HEIGHT / CARD_WIDTH)
        );

        poster.setPreserveRatio(false);
        poster.setSmooth(true);
        poster.getStyleClass().add("movie-poster");

        // Responsive poster container
        StackPane posterContainer = new StackPane(poster);

        posterContainer.minWidthProperty().bind(responsiveWidth);
        posterContainer.prefWidthProperty().bind(responsiveWidth);
        posterContainer.maxWidthProperty().bind(responsiveWidth);

        posterContainer.minHeightProperty().bind(
                responsiveWidth.multiply(POSTER_HEIGHT / CARD_WIDTH)
        );

        posterContainer.prefHeightProperty().bind(
                responsiveWidth.multiply(POSTER_HEIGHT / CARD_WIDTH)
        );

        posterContainer.maxHeightProperty().bind(
                responsiveWidth.multiply(POSTER_HEIGHT / CARD_WIDTH)
        );

        posterContainer.getStyleClass().add("poster-container");

        // Responsive movie title
        Label title = new Label(movie.getTitle());
        title.setWrapText(true);
        title.maxWidthProperty().bind(responsiveWidth);
        title.prefWidthProperty().bind(responsiveWidth);
        title.getStyleClass().add("movie-card-title");

        card.getChildren().addAll(
                posterContainer,
                title
        );

        // Shared hover animation and interface sound.
        HoverAnimation.apply(card);

        card.addEventHandler(
                MouseEvent.MOUSE_ENTERED,
                event -> SoundManager.playHover()
        );

        // Open movie details when clicked.
        card.setOnMouseClicked(event -> {
            SoundManager.playClick();
            navigationService.showMovieDetails(movie);
        });

        return card;
    }
}
