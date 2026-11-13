
package components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import models.Movie;

public class FeaturedMovie {

    public StackPane create(Movie movie) {

        // =========================================
        // MAIN FEATURED CONTAINER
        // =========================================

        StackPane featured = new StackPane();

        featured.getStyleClass().add("featured-movie");

        featured.setMaxWidth(Double.MAX_VALUE);
        featured.setMinHeight(300);
        featured.setPrefHeight(380);


        // =========================================
        // BACKGROUND IMAGE
        // =========================================

        ImageView background = new ImageView();

        if (movie.getPosterUrl() != null
                && !movie.getPosterUrl().isBlank()) {

            background.setImage(
                new Image(movie.getPosterUrl(), true)
            );
        }

        background.setPreserveRatio(false);
        background.setSmooth(true);

        background.fitWidthProperty().bind(
            featured.widthProperty()
        );

        background.fitHeightProperty().bind(
            featured.heightProperty()
        );

        background.getStyleClass().add(
            "featured-background"
        );


        // =========================================
        // MOVIE TITLE
        // =========================================

        Label title = new Label(movie.getTitle());

        title.setWrapText(true);
        title.setMaxWidth(Double.MAX_VALUE);

        title.getStyleClass().add("featured-title");


        // =========================================
        // MOVIE DETAILS
        // =========================================

        Label details = new Label(
            movie.getYear()
            + "  |  Rating: "
            + movie.getRating()
            + "  |  "
            + movie.getDuration()
        );

        details.setWrapText(true);

        details.getStyleClass().add(
            "featured-details"
        );


        // =========================================
        // MOVIE DESCRIPTION
        // =========================================

        Label description = new Label(
            movie.getDescription()
        );

        description.setWrapText(true);
        description.setMaxWidth(500);

        description.getStyleClass().add(
            "featured-description"
        );


        // =========================================
        // CUSTOM BUTTONS
        // =========================================

        CustomButton customButton = new CustomButton();

        StackPane playButton = customButton.create(
            "Play Trailer",
            "primary-button",
            () -> System.out.println(
                "Play trailer: " + movie.getTitle()
            )
        );

        StackPane moreInfoButton = customButton.create(
            "More Info",
            "secondary-button",
            () -> System.out.println(
                "More information: " + movie.getTitle()
            )
        );


        // =========================================
        // BUTTON CONTAINER
        // =========================================

        HBox controls = new HBox(
            15,
            playButton,
            moreInfoButton
        );

        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setMaxWidth(Double.MAX_VALUE);


        // =========================================
        // MOVIE INFORMATION
        // =========================================

        VBox information = new VBox(
            15,
            title,
            details,
            description,
            controls
        );

        information.setAlignment(Pos.CENTER_LEFT);

        information.setMaxWidth(600);
        information.setPadding(
            new Insets(35, 40, 35, 40)
        );

        StackPane.setAlignment(
            information,
            Pos.CENTER_LEFT
        );


        // =========================================
        // ADD CONTENT
        // =========================================

        featured.getChildren().addAll(
            background,
            information
        );

        return featured;
    }
}
