package screens;

import animations.LoadingAnimation;
import components.CustomButton;
import models.Movie;
import services.NavigationService;
import services.YouTubeService;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class PlayerScreen {
    private final YouTubeService youtubeService = new YouTubeService();
    private final NavigationService navigationService = new NavigationService();

    public BorderPane create(Movie movie) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("player-screen");
        root.setPadding(new Insets(24, 30, 28, 30));

        Label title = new Label(movie.getTitle());
        title.getStyleClass().add("player-title");
        Label subtitle = new Label("Official trailer");
        subtitle.getStyleClass().add("screen-subheading");
        VBox header = new VBox(6, title, subtitle);
        header.setPadding(new Insets(0, 0, 16, 0));

        StackPane message = LoadingAnimation.create("Loading trailer inside Watch Z...");
        StackPane video = new StackPane(youtubeService.getWebView(), message);
        video.setMinHeight(360);
        video.setPrefHeight(480);
        video.getStyleClass().add("video-container");
        message.setVisible(!movie.getYoutubeUrl().isBlank());
        message.setManaged(!movie.getYoutubeUrl().isBlank());

        Label details = new Label(movie.getYear() + "  |  Rating: " + movie.getRating()
                + "  |  " + movie.getDuration() + " minutes  |  " + movie.getCategory().getDisplayName());
        details.getStyleClass().add("player-details");
        Label description = new Label(movie.getDescription());
        description.setWrapText(true);
        description.getStyleClass().add("player-description");
        Label playbackNote = new Label(movie.getYoutubeUrl().isBlank()
                ? "No trailer link has been assigned to this movie yet."
                : "Trailer playback is embedded in Watch Z. Use the YouTube controls inside the player.");
        playbackNote.setWrapText(true);
        playbackNote.getStyleClass().add("screen-subheading");
        VBox information = new VBox(10, details, description, playbackNote);
        information.setPadding(new Insets(18, 0, 16, 0));

        youtubeService.getWebView().getEngine().getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                message.setVisible(false);
                message.setManaged(false);
            } else if (newState == Worker.State.FAILED || newState == Worker.State.CANCELLED) {
                message.setVisible(false);
                message.setManaged(false);
                playbackNote.setText("The trailer page could not load. Check your internet connection and try Reload Trailer.");
            }
        });

        CustomButton controlsBuilder = new CustomButton();
        StackPane reloadTrailer = controlsBuilder.create("Reload Trailer", "primary-button", () -> {
            if (movie.getYoutubeUrl().isBlank()) {
                playbackNote.setText("No trailer link has been assigned to this movie yet.");
                return;
            }
            message.setVisible(true);
            message.setManaged(true);
            youtubeService.loadVideo(movie.getYoutubeUrl());
        });
        StackPane detailsButton = controlsBuilder.create("Movie Details", "secondary-button",
                () -> navigationService.showMovieDetails(movie));
        StackPane backButton = controlsBuilder.create("Back", "secondary-button", navigationService::goBack);
        HBox controls = new HBox(12, reloadTrailer, detailsButton, backButton);
        controls.setAlignment(Pos.CENTER_LEFT);

        VBox bottom = new VBox(information, controls);
        root.setTop(header);
        root.setCenter(video);
        root.setBottom(bottom);

        if (!movie.getYoutubeUrl().isBlank()) {
            youtubeService.loadVideo(movie.getYoutubeUrl());
        }
        return root;
    }
}
