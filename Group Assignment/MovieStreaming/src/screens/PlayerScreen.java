
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
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

public class PlayerScreen {

    private final YouTubeService youtubeService = new YouTubeService();
    private final NavigationService navigationService = new NavigationService();

    private boolean playbackStopped = false;

    public BorderPane create(Movie movie) {

        BorderPane root = new BorderPane();
        root.getStyleClass().add("player-screen");
        root.setPadding(new Insets(18, 24, 20, 24));

        WebView webView = youtubeService.getWebView();

        // Allow the video to resize with its available space.
        webView.setMinSize(0, 0);
        webView.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Movie heading.
        Label title = new Label(movie.getTitle());
        title.getStyleClass().add("player-title");
        title.setWrapText(true);

        Label subtitle = new Label("Official trailer");
        subtitle.getStyleClass().add("screen-subheading");

        VBox header = new VBox(6, title, subtitle);
        header.setPadding(new Insets(0, 0, 8, 0));

        // Loading indicator.
        StackPane message = LoadingAnimation.create(
                "Loading trailer inside Watch Z..."
        );

        boolean hasTrailer = movie.getYoutubeUrl() != null
                && !movie.getYoutubeUrl().isBlank();

        message.setVisible(hasTrailer);
        message.setManaged(hasTrailer);

        // Responsive video container.
        StackPane video = new StackPane(webView, message);
        video.setMinSize(0, 180);
        video.setPrefSize(960, 540);
        video.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        video.getStyleClass().add("video-container");

        // Movie information.
        Label details = new Label(
                movie.getYear() + "  |  Rating: " + movie.getRating()
                        + "  |  " + movie.getDuration() + " minutes"
                        + "  |  " + movie.getCategory().getDisplayName()
        );
        details.getStyleClass().add("player-details");
        details.setWrapText(true);

        Label description = new Label(movie.getDescription());
        description.setWrapText(true);
        description.getStyleClass().add("player-description");

        Label playbackNote = new Label(
                hasTrailer
                        ? "Trailer playback is embedded in Watch Z. Use the YouTube controls inside the player."
                        : "No trailer link has been assigned to this movie yet."
        );
        playbackNote.setWrapText(true);
        playbackNote.getStyleClass().add("screen-subheading");

        VBox information = new VBox(
                10,
                details,
                description,
                playbackNote
        );
        information.setPadding(new Insets(14, 0, 10, 0));

        // Stop playback only when the screen is actually being left.
        webView.getEngine().getLoadWorker().stateProperty().addListener(
                (observable, oldState, newState) -> {

                    if (playbackStopped) {
                        return;
                    }

                    if (newState == Worker.State.SUCCEEDED) {
                        message.setVisible(false);
                        message.setManaged(false);

                    } else if (newState == Worker.State.FAILED
                            || newState == Worker.State.CANCELLED) {

                        message.setVisible(false);
                        message.setManaged(false);

                        playbackNote.setText(
                                "The trailer page could not load. "
                                + "Check your internet connection and try Reload Trailer."
                        );
                    }
                }
        );

        // Reload Trailer button.
        CustomButton controlsBuilder = new CustomButton();

        StackPane reloadTrailer = controlsBuilder.create(
                "Reload Trailer",
                "primary-button",
                () -> {

                    if (!hasTrailer) {
                        playbackNote.setText(
                                "No trailer link has been assigned to this movie yet."
                        );
                        return;
                    }

                    playbackStopped = false;

                    message.setVisible(true);
                    message.setManaged(true);

                    youtubeService.loadVideo(movie.getYoutubeUrl());
                }
        );

        // Movie Details button.
        StackPane detailsButton = controlsBuilder.create(
                "Movie Details",
                "secondary-button",
                () -> leavePlayer(
                        () -> navigationService.showMovieDetails(movie)
                )
        );

        // Back button.
        StackPane backButton = controlsBuilder.create(
                "Back",
                "secondary-button",
                () -> leavePlayer(navigationService::goBack)
        );

        // FlowPane allows buttons to wrap on narrower screens.
        FlowPane controls = new FlowPane(12, 10);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setMaxWidth(Double.MAX_VALUE);
        controls.getChildren().addAll(
                reloadTrailer,
                detailsButton,
                backButton
        );

        // Put the content inside a vertical scroll pane for smaller screens.
        VBox pageContent = new VBox(
                12,
                header,
                video,
                information,
                controls
        );

        pageContent.setFillWidth(true);
        pageContent.setMaxWidth(Double.MAX_VALUE);
        pageContent.setPadding(new Insets(4));

        ScrollPane scrollPane = new ScrollPane(pageContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("main-scroll");

        root.setCenter(scrollPane);

        // Adjust the video height to the available screen space.
        scrollPane.viewportBoundsProperty().addListener(
                (observable, oldBounds, newBounds) -> {

                    double availableWidth = newBounds.getWidth();

                    if (availableWidth <= 0) {
                        return;
                    }

                    double heightBasedOnWidth = availableWidth * 9.0 / 16.0;

                    double availableHeight = root.getHeight() > 0
                            ? root.getHeight() * 0.55
                            : 540;

                    double responsiveHeight = Math.max(
                            180,
                            Math.min(heightBasedOnWidth, availableHeight)
                    );

                    video.setPrefHeight(responsiveHeight);
                }
        );

        // Stop playback automatically when this screen is removed
        // from the active JavaFX scene.
        root.sceneProperty().addListener(
                (observable, oldScene, newScene) -> {

                    if (oldScene != null && newScene == null) {
                        stopPlayback();
                    }
                }
        );

        // Load the trailer.
        if (hasTrailer) {
            playbackStopped = false;
            youtubeService.loadVideo(movie.getYoutubeUrl());
        }

        return root;
    }

    /**
     * Stops the video before navigating away from the player.
     */
    private void leavePlayer(Runnable navigationAction) {

        stopPlayback();
        navigationAction.run();
    }

    /**
     * Stops the YouTube player.
     * Prevents repeated stop calls when navigation also removes the screen.
     */
    private void stopPlayback() {

        if (!playbackStopped) {
            playbackStopped = true;
            youtubeService.stopVideo();
        }
    }
}
