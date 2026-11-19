
package screens;

import components.CustomButton;
import models.Movie;
import services.NavigationService;
import services.YouTubeService;
import utils.SoundManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class MovieDetailsScreen {

    private final NavigationService navigationService =
            new NavigationService();

    private final YouTubeService youtubeService =
            new YouTubeService();

    public BorderPane create(Movie movie) {

        BorderPane root = new BorderPane();
        root.getStyleClass().add("movie-details-screen");
        root.setPadding(new Insets(28, 30, 35, 30));

        // Back icon - same icon used in PlayerScreen.
        Label backButton = new Label();

        backButton.setGraphic(loadBackIcon());
        backButton.setTooltip(new Tooltip("Go back"));
        backButton.getStyleClass().add("player-icon-button");
        backButton.setCursor(Cursor.HAND);
        backButton.setFocusTraversable(false);

        backButton.setOnMouseEntered(event ->
                SoundManager.playHover()
        );

        backButton.setOnMouseClicked(event -> {
            SoundManager.playClick();
            navigationService.goBack();
        });

        HBox backContainer = new HBox(backButton);
        backContainer.setAlignment(Pos.CENTER_LEFT);
        backContainer.setPadding(new Insets(0, 0, 15, 0));

        root.setTop(backContainer);

        // Trailer preview for the selected movie.
        WebView trailerPreview = youtubeService.getWebView();

        trailerPreview.setMinSize(0, 270);
        trailerPreview.setPrefSize(480, 270);
        trailerPreview.setMaxSize(
                Double.MAX_VALUE,
                Double.MAX_VALUE
        );

        trailerPreview.setMouseTransparent(true);

        StackPane trailerContainer = new StackPane(trailerPreview);
        trailerContainer.getStyleClass().add("details-poster-container");
        trailerContainer.setMinSize(0, 270);
        trailerContainer.setPrefSize(480, 270);
        trailerContainer.setMaxWidth(480);

        // Load the selected movie's trailer without autoplay.
        youtubeService.loadPreviewVideo(movie.getYoutubeUrl());

        // Play the muted trailer when the mouse enters the preview.
        trailerContainer.setOnMouseEntered(event ->
                youtubeService.playVideo()
        );

        // Pause the trailer when the mouse leaves the preview.
        trailerContainer.setOnMouseExited(event ->
                youtubeService.pauseVideo()
        );

        // Movie information.
        Label title = new Label(movie.getTitle());
        title.getStyleClass().add("details-title");
        title.setWrapText(true);

        Label information = new Label(
                movie.getYear() + "  |  Rating: " + movie.getRating()
                        + "  |  " + movie.getDuration() + " minutes"
        );
        information.getStyleClass().add("player-details");

        Label category = new Label(
                "Genre: " + movie.getCategory().getDisplayName()
        );
        category.getStyleClass().add("details-category");

        Label description = new Label(movie.getDescription());
        description.setWrapText(true);
        description.getStyleClass().add("player-description");

        CustomButton buttons = new CustomButton();

        // Actual playback opens the dedicated player screen.
        StackPane watch = buttons.create(
                "Watch Trailer",
                "primary-button",
                () -> navigationService.showPlayer(movie)
        );

        // Existing My List functionality remains unchanged.
        StackPane save = buttons.create(
                MyListScreen.contains(movie)
                        ? "Remove from My List"
                        : "Add to My List",
                "secondary-button",
                () -> {
                    if (MyListScreen.contains(movie)) {
                        MyListScreen.removeMovie(movie);
                    } else {
                        MyListScreen.addMovie(movie);
                    }

                    navigationService.showMovieDetails(movie);
                }
        );

        HBox controls = new HBox(12, watch, save);
        controls.setAlignment(Pos.CENTER_LEFT);

        VBox details = new VBox(
                17,
                title,
                information,
                category,
                description,
                controls
        );

        details.setAlignment(Pos.CENTER_LEFT);
        details.setPadding(new Insets(20));
        details.setMaxWidth(650);

        HBox main = new HBox(
                35,
                trailerContainer,
                details
        );

        main.setAlignment(Pos.CENTER_LEFT);
        main.setPadding(new Insets(20, 0, 20, 0));

        root.setCenter(main);

        return root;
    }

    /**
     * Loads the same back.png icon used in PlayerScreen.
     */
    private ImageView loadBackIcon() {

        String filename = "back.png";

        Path[] possiblePaths = {
                Path.of("resources", "images", "icons", filename),
                Path.of("src", "resources", "images", "icons", filename),
                Path.of("src", "main", "resources", "images", "icons", filename)
        };

        for (Path path : possiblePaths) {

            if (Files.isRegularFile(path)) {

                Image image = new Image(
                        path.toAbsolutePath().toUri().toString(),
                        23,
                        23,
                        true,
                        true
                );

                if (!image.isError()) {
                    return createImageView(image);
                }
            }
        }

        String[] resourcePaths = {
                "/images/icons/back.png",
                "/resources/images/icons/back.png"
        };

        for (String resourcePath : resourcePaths) {

            try (InputStream stream =
                         getClass().getResourceAsStream(resourcePath)) {

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
                exception.printStackTrace();
            }
        }

        System.err.println(
                "Missing back icon: resources/images/icons/back.png"
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
}
