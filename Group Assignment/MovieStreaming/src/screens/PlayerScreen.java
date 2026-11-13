
package screens;

import components.CustomButton;
import models.Movie;
import services.NavigationService;
import services.YouTubeService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class PlayerScreen {

    private final YouTubeService youtubeService =
            new YouTubeService();

    private final NavigationService navigationService =
            new NavigationService();

    public BorderPane create(Movie movie) {

        BorderPane root = new BorderPane();
        root.getStyleClass().add("player-screen");

        // Top section
        Label backLabel = new Label("Back to Details");
        backLabel.getStyleClass().add("player-back");

        backLabel.setOnMouseClicked(event -> {
            youtubeService.stopVideo();
            navigationService.showMovieDetails(movie);
        });

        Label title = new Label(movie.getTitle());
        title.getStyleClass().add("player-title");

        VBox topSection = new VBox(12, backLabel, title);
        topSection.setPadding(new Insets(20, 30, 15, 30));

        // YouTube video
        StackPane videoContainer = new StackPane(
                youtubeService.getWebView()
        );

        videoContainer.getStyleClass().add("video-container");
        videoContainer.setMinSize(0, 300);

        root.setCenter(videoContainer);

        // Movie information
        Label details = new Label(
                movie.getYear()
                + "  |  Rating: " + movie.getRating()
                + "  |  " + movie.getDuration() + " minutes"
        );
        details.getStyleClass().add("player-details");

        Label description = new Label(movie.getDescription());
        description.setWrapText(true);
        description.getStyleClass().add("player-description");

        VBox information = new VBox(10, details, description);
        information.setPadding(new Insets(20, 30, 15, 30));

        // Custom controls
        CustomButton customButton = new CustomButton();

        StackPane playButton = customButton.create(
                "Play Trailer",
                "primary-button",
                () -> youtubeService.loadVideo(
                        movie.getYoutubeVideoId()
                )
        );

        StackPane stopButton = customButton.create(
                "Stop",
                "secondary-button",
                () -> youtubeService.stopVideo()
        );

        HBox controls = new HBox(12, playButton, stopButton);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setPadding(new Insets(0, 30, 25, 30));

        VBox bottomSection = new VBox(information, controls);

        root.setTop(topSection);
        root.setBottom(bottomSection);

        // Load the selected trailer
        youtubeService.loadVideo(movie.getYoutubeVideoId());

        return root;
    }
}
