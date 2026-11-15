package screens;

import components.CustomButton;
import models.Movie;
import services.NavigationService;
import utils.ImageLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MovieDetailsScreen {
    private final NavigationService navigationService = new NavigationService();

    public BorderPane create(Movie movie) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("movie-details-screen");
        root.setPadding(new Insets(28, 30, 35, 30));

        ImageView poster = new ImageView(ImageLoader.load(movie.getPosterUrl()));
        poster.setFitWidth(235);
        poster.setFitHeight(350);
        poster.setPreserveRatio(false);
        poster.getStyleClass().add("details-poster");
        StackPane posterContainer = new StackPane(poster);
        posterContainer.getStyleClass().add("details-poster-container");

        Label title = new Label(movie.getTitle());
        title.getStyleClass().add("details-title");
        title.setWrapText(true);
        Label information = new Label(movie.getYear() + "  |  Rating: " + movie.getRating()
                + "  |  " + movie.getDuration() + " minutes");
        information.getStyleClass().add("player-details");
        Label category = new Label("Genre: " + movie.getCategory().getDisplayName());
        category.getStyleClass().add("details-category");
        Label description = new Label(movie.getDescription());
        description.setWrapText(true);
        description.getStyleClass().add("player-description");

        CustomButton buttons = new CustomButton();
        StackPane watch = buttons.create("Watch Trailer", "primary-button", () -> navigationService.showPlayer(movie));
        StackPane save = buttons.create(
                MyListScreen.contains(movie) ? "Remove from My List" : "Add to My List",
                "secondary-button",
                () -> {
                    if (MyListScreen.contains(movie)) MyListScreen.removeMovie(movie);
                    else MyListScreen.addMovie(movie);
                    navigationService.showMovieDetails(movie);
                }
        );
        HBox controls = new HBox(12, watch, save);
        controls.setAlignment(Pos.CENTER_LEFT);

        VBox details = new VBox(17, title, information, category, description, controls);
        details.setAlignment(Pos.CENTER_LEFT);
        details.setPadding(new Insets(20));
        details.setMaxWidth(650);

        HBox main = new HBox(35, posterContainer, details);
        main.setAlignment(Pos.CENTER_LEFT);
        main.setPadding(new Insets(20, 0, 20, 0));
        root.setCenter(main);
        return root;
    }
}
