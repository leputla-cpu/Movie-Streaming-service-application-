package components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import models.Movie;
import services.NavigationService;
import utils.ImageLoader;

/** Featured movie banner. The assigned trailer opens from the dedicated player screen. */
public class FeaturedMovie {

    private final NavigationService navigationService = new NavigationService();

    public StackPane create(Movie movie) {
        StackPane featured = new StackPane();
        featured.getStyleClass().add("featured-movie");
        featured.setMaxWidth(Double.MAX_VALUE);
        featured.setMinHeight(300);
        featured.setPrefHeight(380);

        ImageView background = new ImageView();
        if (movie.getPosterUrl() != null && !movie.getPosterUrl().isBlank()) {
            background.setImage(ImageLoader.load(movie.getPosterUrl()));
        }
        background.setPreserveRatio(false);
        background.setSmooth(true);
        background.fitWidthProperty().bind(featured.widthProperty());
        background.fitHeightProperty().bind(featured.heightProperty());
        background.getStyleClass().add("featured-background");

        Label title = new Label(movie.getTitle());
        title.setWrapText(true);
        title.setMaxWidth(Double.MAX_VALUE);
        title.getStyleClass().add("featured-title");

        Label details = new Label(movie.getYear() + "  |  Rating: " + movie.getRating()
                + "  |  " + movie.getDuration());
        details.setWrapText(true);
        details.getStyleClass().add("featured-details");

        Label description = new Label(movie.getDescription());
        description.setWrapText(true);
        description.setMaxWidth(500);
        description.getStyleClass().add("featured-description");

        CustomButton customButton = new CustomButton();
        StackPane playButton = customButton.create("Play Trailer", "primary-button",
                () -> navigationService.showPlayer(movie));
        StackPane moreInfoButton = customButton.create("More Info", "secondary-button",
                () -> navigationService.showMovieDetails(movie));

        HBox controls = new HBox(15, playButton, moreInfoButton);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setMaxWidth(Double.MAX_VALUE);

        VBox information = new VBox(15, title, details, description, controls);
        information.setAlignment(Pos.CENTER_LEFT);
        information.setMaxWidth(600);
        information.setPadding(new Insets(35, 40, 35, 40));
        StackPane.setAlignment(information, Pos.CENTER_LEFT);

        featured.getChildren().addAll(background, information);
        return featured;
    }
}
