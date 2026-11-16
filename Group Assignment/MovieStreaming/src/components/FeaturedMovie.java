
package components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import models.Movie;
import services.NavigationService;
import services.YouTubeService;

public class FeaturedMovie {

    private final NavigationService navigationService = new NavigationService();
    private final YouTubeService youtubeService = new YouTubeService();

    public StackPane create(Movie movie) {
        StackPane featured = new StackPane();
        featured.getStyleClass().add("featured-movie");
        featured.setMaxWidth(Double.MAX_VALUE);
        featured.setMinHeight(300);
        featured.setPrefHeight(380);

        // Trailer preview replaces the poster background.
        WebView trailerPreview = youtubeService.getWebView();
        trailerPreview.setMouseTransparent(true);
        trailerPreview.setMinSize(0, 300);
        trailerPreview.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        trailerPreview.prefWidthProperty().bind(featured.widthProperty());
        trailerPreview.prefHeightProperty().bind(featured.heightProperty());

        // Load the trailer without automatically playing it.
        youtubeService.loadPreviewVideo(movie.getYoutubeUrl());

        // Play silently when the mouse enters the featured banner.
        featured.setOnMouseEntered(event -> youtubeService.playVideo());

        // Pause the preview when the mouse leaves the banner.
        featured.setOnMouseExited(event -> youtubeService.pauseVideo());

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

        // Actual playback still opens the dedicated player screen.
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

        featured.getChildren().addAll(trailerPreview, information);

        return featured;
    }
}
