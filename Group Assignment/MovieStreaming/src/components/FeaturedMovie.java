
package components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import models.Movie;
import services.NavigationService;
import services.YouTubeService;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

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

        // Icon controls.
        Label playButton = createIconControl(
                "play.png",
                "Play Trailer",
                () -> navigationService.showPlayer(movie)
        );

        Label moreInfoButton = createIconControl(
                "details.png",
                "More Info",
                () -> navigationService.showMovieDetails(movie)
        );

        HBox controls = new HBox(12, playButton, moreInfoButton);
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

    /**
     * Creates an icon-based clickable control.
     */
    private Label createIconControl(String filename, String tooltipText, Runnable action) {
        Label control = new Label();

        ImageView icon = loadIcon(filename);

        if (icon != null) {
            icon.setFitWidth(23);
            icon.setFitHeight(23);
            icon.setPreserveRatio(true);
            icon.setSmooth(true);
            control.setGraphic(icon);
        }

        control.setTooltip(new Tooltip(tooltipText));
        control.getStyleClass().add("player-icon-button");
        control.setCursor(Cursor.HAND);
        control.setFocusTraversable(false);

        control.setOnMouseClicked(event -> action.run());

        return control;
    }

    /**
     * Loads icons from the project's resources/images/icons folder
     * or from the classpath.
     */
    private ImageView loadIcon(String filename) {
        String[] paths = {
                "resources/images/icons/" + filename,
                "src/resources/images/icons/" + filename,
                "src/main/resources/images/icons/" + filename
        };

        for (String path : paths) {
            try {
                Path iconPath = Path.of(path);

                if (Files.isRegularFile(iconPath)) {
                    Image image = new Image(iconPath.toUri().toString());

                    if (!image.isError()) {
                        return new ImageView(image);
                    }
                }
            } catch (Exception ignored) {
                // Try the next location.
            }
        }

        String[] resourcePaths = {
                "/images/icons/" + filename,
                "/resources/images/icons/" + filename
        };

        for (String resourcePath : resourcePaths) {
            try (InputStream stream = getClass().getResourceAsStream(resourcePath)) {
                if (stream != null) {
                    Image image = new Image(stream);

                    if (!image.isError()) {
                        return new ImageView(image);
                    }
                }
            } catch (Exception ignored) {
                // Try the next resource location.
            }
        }

        System.err.println("Missing featured movie icon: " + filename);
        return null;
    }
}