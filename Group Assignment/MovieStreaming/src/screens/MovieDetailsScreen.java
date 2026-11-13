
package screens;

import components.CustomButton;
import models.Movie;
import services.NavigationService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MovieDetailsScreen {

    private final NavigationService navigationService =
            new NavigationService();

    public BorderPane create(Movie movie) {

        BorderPane root = new BorderPane();
        root.getStyleClass().add("movie-details-screen");

        // Back navigation
        Label backLabel = new Label("Back to Home");
        backLabel.getStyleClass().add("player-back");

        backLabel.setOnMouseClicked(event ->
                navigationService.showHome()
        );

        VBox topSection = new VBox(backLabel);
        topSection.setPadding(new Insets(25, 30, 15, 30));

        // Movie poster
        ImageView poster = new ImageView();

        if (movie.getPosterUrl() != null
                && !movie.getPosterUrl().isBlank()) {

            poster.setImage(
                    new Image(movie.getPosterUrl(), true)
            );
        }

        poster.setFitWidth(230);
        poster.setFitHeight(340);
        poster.setPreserveRatio(false);
        poster.getStyleClass().add("details-poster");

        StackPane posterContainer = new StackPane(poster);
        posterContainer.getStyleClass().add("details-poster-container");

        // Movie information
        Label title = new Label(movie.getTitle());
        title.getStyleClass().add("details-title");
        title.setWrapText(true);

        Label information = new Label(
                movie.getYear()
                + "  |  Rating: " + movie.getRating()
                + "  |  " + movie.getDuration() + " minutes"
        );
        information.getStyleClass().add("player-details");

        Label category = new Label(
                "Category: " + movie.getCategory().toString()
        );
        category.getStyleClass().add("player-details");

        Label description = new Label(movie.getDescription());
        description.setWrapText(true);
        description.getStyleClass().add("player-description");

        CustomButton customButton = new CustomButton();

        StackPane watchButton = customButton.create(
                "Watch Trailer",
                "primary-button",
                () -> navigationService.showPlayer(movie)
        );

        StackPane backButton = customButton.create(
                "Back to Home",
                "secondary-button",
                () -> navigationService.showHome()
        );

        HBox controls = new HBox(12, watchButton, backButton);
        controls.setAlignment(Pos.CENTER_LEFT);

        VBox details = new VBox(
                18,
                title,
                information,
                category,
                description,
                controls
        );

        details.setAlignment(Pos.CENTER_LEFT);
        details.setPadding(new Insets(20));
        details.setMaxWidth(650);

        HBox mainContent = new HBox(35, posterContainer, details);
        mainContent.setAlignment(Pos.CENTER);
        mainContent.setPadding(new Insets(30));

        root.setTop(topSection);
        root.setCenter(mainContent);

        return root;
    }
}
