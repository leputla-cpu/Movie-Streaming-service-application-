package screens;

import components.FeaturedMovie;
import components.MovieRow;
import data.MovieData;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.layout.StackPane;
import models.Category;
import models.Movie;

import java.util.ArrayList;
import java.util.List;

/** Landing page: featured title followed by the five horizontally scrolling genres. */
public class HomeScreen {
    public ScrollPane create() {
        VBox content = new VBox(23);
        content.getStyleClass().add("home-content");
        content.setPadding(new Insets(26, 30, 38, 30));

        Label welcome = new Label("Welcome to your movie universe");
        welcome.getStyleClass().add("home-welcome");
        Label intro = new Label("Find your next favourite story.");
        intro.getStyleClass().add("home-intro");
        VBox introBlock = new VBox(5, welcome, intro);

        // Rotate the featured movie through the catalogue automatically.
        // A copy is used so the original MovieData collection is never modified.
        List<Movie> featuredMovies = new ArrayList<>(MovieData.getAllMovies());
        if (!featuredMovies.isEmpty()) {
            StackPane featuredContainer = new StackPane();
            featuredContainer.getStyleClass().add("featured-rotation-container");
            featuredContainer.getChildren().setAll(
                    new FeaturedMovie().create(featuredMovies.get(0)));

            final int[] currentIndex = {0};
            Timeline featuredRotation = new Timeline(new KeyFrame(
                    Duration.seconds(8), event -> {
                        currentIndex[0] = (currentIndex[0] + 1) % featuredMovies.size();
                        StackPane nextFeatured = new FeaturedMovie()
                                .create(featuredMovies.get(currentIndex[0]));
                        nextFeatured.setOpacity(0);
                        featuredContainer.getChildren().setAll(nextFeatured);

                        FadeTransition fade = new FadeTransition(
                                Duration.millis(650), nextFeatured);
                        fade.setFromValue(0);
                        fade.setToValue(1);
                        fade.play();
                    }
            ));
            featuredRotation.setCycleCount(Timeline.INDEFINITE);

            // Pause while the visitor is interacting with the featured movie.
            featuredContainer.setOnMouseEntered(event -> featuredRotation.pause());
            featuredContainer.setOnMouseExited(event -> featuredRotation.play());

            // Stop the timer when Home is no longer part of the displayed scene.
            featuredContainer.sceneProperty().addListener((observable, oldScene, newScene) -> {
                if (newScene == null) {
                    featuredRotation.stop();
                } else {
                    featuredRotation.playFromStart();
                }
            });

            content.getChildren().addAll(introBlock, featuredContainer);
        } else {
            content.getChildren().add(introBlock);
        }

        HBox heading = new HBox(12);
        heading.setAlignment(Pos.CENTER_LEFT);
        Label catalogueTitle = new Label("Explore the catalogue");
        catalogueTitle.getStyleClass().add("catalogue-heading");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label count = new Label(MovieData.getMovieCount() + " MOVIES");
        count.getStyleClass().add("catalogue-count");
        heading.getChildren().addAll(catalogueTitle, spacer, count);

        VBox rows = new VBox(24);
        rows.getStyleClass().add("catalogue-sections");
        for (Category category : Category.values()) rows.getChildren().add(new MovieRow().create(category));

        content.getChildren().addAll(heading, rows);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setPannable(true);
        scroll.getStyleClass().add("main-scroll");
        return scroll;
    }
}
