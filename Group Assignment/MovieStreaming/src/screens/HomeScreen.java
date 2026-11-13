
package screens;

import components.NavigationBar;
import components.FeaturedMovie;
import components.MovieRow;
import data.MovieData;
import models.Category;
import models.Movie;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import services.NavigationService;

import java.util.List;

public class HomeScreen {

    private final NavigationService navigationService =
            new NavigationService();

    public VBox create() {

        VBox root = new VBox();
        root.getStyleClass().add("main-root");

        NavigationBar navigationBar = new NavigationBar();
        HBox navigation = navigationBar.create();

        VBox content = new VBox(20);
        content.setPadding(new Insets(20, 30, 30, 30));
        content.setMaxWidth(Double.MAX_VALUE);
        content.getStyleClass().add("home-content");

        List<Movie> actionMovies =
                MovieData.getMoviesByCategory(Category.ACTION);

        if (!actionMovies.isEmpty()) {
            FeaturedMovie featuredMovie = new FeaturedMovie();
            content.getChildren().add(
                    featuredMovie.create(actionMovies.get(0))
            );
        }

        Category[] categories = {
            Category.ACTION,
            Category.COMEDY,
            Category.HORROR,
            Category.SCIENCE_FICTION,
            Category.ANIMATION
        };

        MovieRow movieRow = new MovieRow();

        for (Category category : categories) {
            content.getChildren().add(movieRow.create(category));
        }

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );
        scrollPane.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );
        scrollPane.getStyleClass().add("main-scroll");

        root.getChildren().addAll(navigation, scrollPane);

        return root;
    }
}
