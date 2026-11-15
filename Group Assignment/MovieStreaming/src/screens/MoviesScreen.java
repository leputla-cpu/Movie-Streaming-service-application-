package screens;

import components.MovieCard;
import data.MovieData;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

/** Dedicated all-movies catalogue. It does not repeat the Home featured banner or genre rows. */
public class MoviesScreen {
    public VBox create() {
        VBox root = new VBox(16);
        root.setPadding(new Insets(28, 30, 35, 30));
        root.getStyleClass().add("screen-content");

        Label heading = new Label("All Movies");
        heading.getStyleClass().add("screen-heading");
        Label subtitle = new Label("Browse the complete Watch Z movie collection.");
        subtitle.getStyleClass().add("screen-subheading");
        Label count = new Label(MovieData.getMovieCount() + " movies available");
        count.getStyleClass().add("search-message");

        FlowPane grid = new FlowPane();
        grid.setHgap(19);
        grid.setVgap(25);
        grid.setAlignment(Pos.TOP_LEFT);
        for (var movie : MovieData.getAllMovies()) grid.getChildren().add(new MovieCard().create(movie));

        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.getStyleClass().add("main-scroll");
        VBox.setVgrow(scroll, javafx.scene.layout.Priority.ALWAYS);
        root.getChildren().addAll(heading, subtitle, count, scroll);
        return root;
    }
}
