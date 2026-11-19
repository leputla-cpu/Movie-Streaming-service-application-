
package screens;

import animations.LoadingAnimation;
import models.Movie;
import services.NavigationService;
import services.YouTubeService;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class PlayerScreen {

    private final YouTubeService youtubeService = new YouTubeService();
    private final NavigationService navigationService = new NavigationService();

    private boolean playbackStopped = false;
    private boolean muted = false;
    private boolean playing = false;

    private Label playPauseControl;
    private Label muteControl;

    public BorderPane create(Movie movie) {

        playbackStopped = false;
        muted = false;
        playing = false;

        BorderPane root = new BorderPane();
        root.getStyleClass().add("player-screen");
        root.setPadding(new Insets(14, 22, 18, 22));

        // Allow the player root to receive keyboard focus.
        root.setFocusTraversable(true);

        boolean hasTrailer = movie.getYoutubeUrl() != null
                && !movie.getYoutubeUrl().isBlank();

        // -------------------------------------------------
        // TOP BAR
        // -------------------------------------------------

        Label backControl = createIconControl(
                "back.png", "Go back"
        );

        Label movieTitle = new Label(movie.getTitle());
        movieTitle.getStyleClass().add("player-title");
        movieTitle.setWrapText(true);
        movieTitle.setAlignment(Pos.CENTER);

        Label detailsControl = createIconControl(
                "details.png", "Movie information"
        );

        backControl.setOnMouseClicked(event ->
                leavePlayer(navigationService::goBack)
        );

        detailsControl.setOnMouseClicked(event ->
                leavePlayer(() ->
                        navigationService.showMovieDetails(movie)
                )
        );

        BorderPane topBar = new BorderPane();
        topBar.setLeft(backControl);
        topBar.setCenter(movieTitle);
        topBar.setRight(detailsControl);
        topBar.setPadding(new Insets(0, 0, 14, 0));
        topBar.getStyleClass().add("player-top-bar");

        root.setTop(topBar);

        // -------------------------------------------------
        // VIDEO PLAYER
        // -------------------------------------------------

        WebView webView = youtubeService.getWebView();

        webView.setMinSize(0, 0);
        webView.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        StackPane loadingMessage = LoadingAnimation.create(
                "Loading trailer inside Watch Z..."
        );

        loadingMessage.setVisible(hasTrailer);
        loadingMessage.setManaged(hasTrailer);

        StackPane video = new StackPane(
                webView,
                loadingMessage
        );

        video.setMinSize(0, 180);
        video.setPrefSize(960, 540);
        video.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        video.getStyleClass().add("video-container");

        // -------------------------------------------------
        // MOVIE INFORMATION
        // -------------------------------------------------

        Label subtitle = new Label("OFFICIAL TRAILER");
        subtitle.getStyleClass().add("screen-subheading");

        Label movieDetails = new Label(
                movie.getYear() + "  |  Rating: " + movie.getRating()
                        + "  |  " + movie.getDuration() + " minutes"
                        + "  |  " + movie.getCategory().getDisplayName()
        );

        movieDetails.getStyleClass().add("player-details");
        movieDetails.setWrapText(true);

        Label description = new Label(movie.getDescription());
        description.setWrapText(true);
        description.getStyleClass().add("player-description");

        Label playbackNote = new Label(
                hasTrailer
                        ? "Ready to play. Use the controls below."
                        : "No trailer link has been assigned to this movie yet."
        );

        playbackNote.setWrapText(true);
        playbackNote.getStyleClass().add("screen-subheading");

        VBox information = new VBox(
                8,
                subtitle,
                movieDetails,
                description,
                playbackNote
        );

        information.setPadding(new Insets(15, 2, 5, 2));

        // -------------------------------------------------
        // PLAY / PAUSE TOGGLE
        // -------------------------------------------------

        playPauseControl = createIconControl(
                "play.png", "Play trailer"
        );

        playPauseControl.setOnMouseClicked(event ->
                togglePlayback(hasTrailer, playbackNote)
        );

        // -------------------------------------------------
        // RELOAD CONTROL
        // -------------------------------------------------

        Label reloadControl = createIconControl(
                "reload.png", "Reload trailer"
        );

        Slider volumeSlider = new Slider(0, 100, 70);

        volumeSlider.setMinWidth(85);
        volumeSlider.setPrefWidth(125);
        volumeSlider.setMaxWidth(155);
        volumeSlider.setShowTickLabels(false);
        volumeSlider.setShowTickMarks(false);
        volumeSlider.setBlockIncrement(5);
        volumeSlider.getStyleClass().add("player-volume-slider");

        reloadControl.setOnMouseClicked(event ->
                reloadTrailer(
                        movie,
                        hasTrailer,
                        playbackNote,
                        loadingMessage,
                        volumeSlider
                )
        );

        // -------------------------------------------------
        // FULLSCREEN CONTROL
        // -------------------------------------------------

        Label fullscreenControl = createIconControl(
                "fullscreen.png", "Toggle full screen"
        );

        fullscreenControl.setOnMouseClicked(event ->
                toggleFullscreen(root)
        );

        // -------------------------------------------------
        // MUTE / UNMUTE TOGGLE
        // -------------------------------------------------

        muteControl = createIconControl(
                "volume.png", "Mute trailer"
        );

        muteControl.setOnMouseClicked(event ->
                toggleMute(volumeSlider)
        );

        // -------------------------------------------------
        // VOLUME SLIDER
        // -------------------------------------------------

        volumeSlider.valueProperty().addListener(
                (observable, oldValue, newValue) -> {

                    youtubeService.setVolume(
                            newValue.intValue()
                    );

                    if (newValue.intValue() > 0 && muted) {

                        muted = false;

                        youtubeService.setMuted(false);

                        updateMuteIcon();
                    }

                    if (newValue.intValue() == 0 && !muted) {

                        muted = true;

                        youtubeService.setMuted(true);

                        updateMuteIcon();
                    }
                }
        );

        // -------------------------------------------------
        // VLC-STYLE CONTROL BAR
        // -------------------------------------------------

        HBox leftControls = new HBox(
                12,
                playPauseControl,
                reloadControl,
                fullscreenControl
        );

        leftControls.setAlignment(Pos.CENTER_LEFT);

        HBox rightControls = new HBox(
                10,
                muteControl,
                volumeSlider
        );

        rightControls.setAlignment(Pos.CENTER_RIGHT);

        Region controlSpacer = new Region();
        HBox.setHgrow(controlSpacer, Priority.ALWAYS);

        HBox mediaControls = new HBox(
                15,
                leftControls,
                controlSpacer,
                rightControls
        );

        mediaControls.setAlignment(Pos.CENTER);
        mediaControls.setPadding(
                new Insets(12, 16, 12, 16)
        );

        mediaControls.setMaxWidth(Double.MAX_VALUE);
        mediaControls.getStyleClass().add(
                "player-controls-bar"
        );

        // -------------------------------------------------
        // PLAYER CONTENT
        // -------------------------------------------------

        VBox playerContent = new VBox(
                12,
                video,
                mediaControls,
                information
        );

        playerContent.setFillWidth(true);
        playerContent.setMaxWidth(Double.MAX_VALUE);
        playerContent.setPadding(new Insets(4));

        ScrollPane scrollPane = new ScrollPane(playerContent);

        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);

        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scrollPane.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scrollPane.getStyleClass().add("main-scroll");

        root.setCenter(scrollPane);

        // -------------------------------------------------
        // RESPONSIVE VIDEO SIZE
        // -------------------------------------------------

        scrollPane.viewportBoundsProperty().addListener(
                (observable, oldBounds, newBounds) -> {

                    double availableWidth = newBounds.getWidth();

                    if (availableWidth <= 0) {
                        return;
                    }

                    double heightBasedOnWidth =
                            availableWidth * 9.0 / 16.0;

                    double availableHeight = root.getHeight() > 0
                            ? root.getHeight() * 0.55
                            : 540;

                    double responsiveHeight = Math.max(
                            180,
                            Math.min(
                                    heightBasedOnWidth,
                                    availableHeight
                            )
                    );

                    video.setPrefHeight(responsiveHeight);
                }
        );

        // -------------------------------------------------
        // YOUTUBE LOADING STATUS
        // -------------------------------------------------

        webView.getEngine().getLoadWorker().stateProperty().addListener(
                (observable, oldState, newState) -> {

                    if (playbackStopped) {
                        return;
                    }

                    if (newState == Worker.State.SUCCEEDED) {

                        loadingMessage.setVisible(false);
                        loadingMessage.setManaged(false);

                        playbackNote.setText(
                                "Trailer loaded. Press K or Play to start."
                        );

                    } else if (newState == Worker.State.FAILED
                            || newState == Worker.State.CANCELLED) {

                        loadingMessage.setVisible(false);
                        loadingMessage.setManaged(false);

                        playbackNote.setText(
                                "The trailer page could not load. "
                                + "Check your internet connection and reload."
                        );
                    }
                }
        );

        // -------------------------------------------------
        // KEYBOARD SHORTCUTS
        // -------------------------------------------------

        EventHandler<KeyEvent> keyboardHandler = event -> {

            if (playbackStopped) {
                return;
            }

            // Do not intercept keys when the user is typing
            // into a search field or another text input.
            if (event.getTarget() instanceof TextInputControl) {
                return;
            }

            KeyCode key = event.getCode();

            switch (key) {

                case M -> {
                    toggleMute(volumeSlider);
                    event.consume();
                }

                case K -> {
                    togglePlayback(hasTrailer, playbackNote);
                    event.consume();
                }

                case F -> {
                    toggleFullscreen(root);
                    event.consume();
                }

                case R -> {
                    reloadTrailer(
                            movie,
                            hasTrailer,
                            playbackNote,
                            loadingMessage,
                            volumeSlider
                    );
                    event.consume();
                }

                case UP -> {
                    volumeSlider.setValue(
                            Math.min(100, volumeSlider.getValue() + 5)
                    );
                    event.consume();
                }

                case DOWN -> {
                    volumeSlider.setValue(
                            Math.max(0, volumeSlider.getValue() - 5)
                    );
                    event.consume();
                }

                default -> {
                    // Other keys continue normally.
                }
            }
        };

        // Attach the keyboard handler to the Scene.
        // This allows shortcuts to work even when the WebView has focus.
        root.sceneProperty().addListener(
                (observable, oldScene, newScene) -> {

                    if (oldScene != null) {
                        oldScene.removeEventFilter(
                                KeyEvent.KEY_PRESSED,
                                keyboardHandler
                        );
                    }

                    if (newScene != null) {

                        newScene.addEventFilter(
                                KeyEvent.KEY_PRESSED,
                                keyboardHandler
                        );

                        // Move focus away from the top search field
                        // and into the player when it opens.
                        Platform.runLater(root::requestFocus);

                    } else if (oldScene != null) {
                        stopPlayback();
                    }
                }
        );

        // Load the selected movie trailer.
        if (hasTrailer) {

            playbackStopped = false;

            youtubeService.setVolume(70);
            youtubeService.setMuted(false);

            youtubeService.loadVideo(
                    movie.getYoutubeUrl()
            );

        } else {

            loadingMessage.setVisible(false);
            loadingMessage.setManaged(false);
        }

        return root;
    }

    // -------------------------------------------------
    // PLAY / PAUSE
    // -------------------------------------------------

    private void togglePlayback(
            boolean hasTrailer,
            Label playbackNote
    ) {

        if (!hasTrailer) {
            playbackNote.setText(
                    "This movie does not have a trailer assigned."
            );
            return;
        }

        if (playing) {

            youtubeService.pauseVideo();
            playing = false;

            playPauseControl.setGraphic(
                    loadIcon("play.png")
            );

            playPauseControl.setTooltip(
                    new Tooltip("Play trailer (K)")
            );

        } else {

            youtubeService.playVideo();
            playing = true;

            playPauseControl.setGraphic(
                    loadIcon("pause.png")
            );

            playPauseControl.setTooltip(
                    new Tooltip("Pause trailer (K)")
            );
        }
    }

    // -------------------------------------------------
    // MUTE / UNMUTE
    // -------------------------------------------------

    private void toggleMute(Slider volumeSlider) {

        if (muted) {

            // Restore an audible volume if the slider is at zero.
            if (volumeSlider.getValue() == 0) {
                volumeSlider.setValue(70);
            }

            muted = false;

        } else {

            muted = true;
        }

        youtubeService.setMuted(muted);

        updateMuteIcon();
    }

    private void updateMuteIcon() {

        muteControl.setGraphic(
                loadIcon(muted ? "mute.png" : "volume.png")
        );

        muteControl.setTooltip(
                new Tooltip(
                        muted ? "Unmute trailer (M)" : "Mute trailer (M)"
                )
        );
    }

    // -------------------------------------------------
    // RELOAD TRAILER
    // -------------------------------------------------

    private void reloadTrailer(
            Movie movie,
            boolean hasTrailer,
            Label playbackNote,
            StackPane loadingMessage,
            Slider volumeSlider
    ) {

        if (!hasTrailer) {
            playbackNote.setText(
                    "No trailer link has been assigned."
            );
            return;
        }

        playbackStopped = false;
        playing = false;
        muted = false;

        playPauseControl.setGraphic(
                loadIcon("play.png")
        );

        playPauseControl.setTooltip(
                new Tooltip("Play trailer (K)")
        );

        volumeSlider.setValue(70);

        youtubeService.setMuted(false);
        youtubeService.setVolume(70);

        updateMuteIcon();

        loadingMessage.setVisible(true);
        loadingMessage.setManaged(true);

        youtubeService.loadVideo(movie.getYoutubeUrl());

        playbackNote.setText("Reloading trailer...");
    }

    // -------------------------------------------------
    // FULLSCREEN
    // -------------------------------------------------

    private void toggleFullscreen(BorderPane root) {

        Scene scene = root.getScene();

        if (scene != null && scene.getWindow() instanceof Stage stage) {
            stage.setFullScreen(!stage.isFullScreen());
        }
    }

    // -------------------------------------------------
    // CUSTOM IMAGE CONTROL
    // -------------------------------------------------

    private Label createIconControl(
            String filename,
            String tooltipText
    ) {

        Label control = new Label();

        control.setGraphic(loadIcon(filename));
        control.setTooltip(new Tooltip(tooltipText));

        control.getStyleClass().add(
                "player-icon-button"
        );

        control.setCursor(Cursor.HAND);
        control.setFocusTraversable(false);

        return control;
    }

    // -------------------------------------------------
    // LOAD ICONS FROM resources/images/icons/
    // -------------------------------------------------

    private ImageView loadIcon(String filename) {

        String resourcePath = "/images/icons/" + filename;

        Path[] possiblePaths = {

                Path.of(
                        "resources", "images", "icons", filename
                ),

                Path.of(
                        "src", "resources", "images", "icons", filename
                ),

                Path.of(
                        "src", "main", "resources", "images", "icons", filename
                )
        };

        for (Path path : possiblePaths) {

            if (Files.isRegularFile(path)) {

                try {

                    Image image = new Image(
                            path.toAbsolutePath().toUri().toString(),
                            23,
                            23,
                            true,
                            true,
                            false
                    );

                    if (!image.isError()) {

                        System.out.println(
                                "Loaded player icon: "
                                + path.toAbsolutePath()
                        );

                        return createImageView(image);
                    }

                } catch (Exception exception) {

                    System.err.println(
                            "Error loading player icon: " + path
                    );

                    exception.printStackTrace();
                }
            }
        }

        // Classpath fallback.
        String[] classpathPaths = {
                resourcePath,
                "/resources/images/icons/" + filename
        };

        for (String classpathPath : classpathPaths) {

            try (InputStream stream =
                         getClass().getResourceAsStream(classpathPath)) {

                if (stream != null) {

                    Image image = new Image(
                            stream,
                            23,
                            23,
                            true,
                            true
                    );

                    if (!image.isError()) {
                        return createImageView(image);
                    }
                }

            } catch (Exception exception) {

                System.err.println(
                        "Error reading icon: " + classpathPath
                );

                exception.printStackTrace();
            }
        }

        System.err.println(
                "Missing player icon: " + resourcePath
        );

        return new ImageView();
    }

    private ImageView createImageView(Image image) {

        ImageView imageView = new ImageView(image);

        imageView.setFitWidth(23);
        imageView.setFitHeight(23);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        return imageView;
    }

    // -------------------------------------------------
    // NAVIGATION AND PLAYBACK CLEANUP
    // -------------------------------------------------

    private void leavePlayer(Runnable navigationAction) {

        stopPlayback();
        navigationAction.run();
    }

    private void stopPlayback() {

        if (!playbackStopped) {

            playbackStopped = true;
            youtubeService.stopVideo();
        }
    }
}
