package components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import services.NavigationService;

/** Legacy reusable top navigation component retained for compatibility. */
public class NavigationBar {
    private final NavigationService navigation = new NavigationService();

    public HBox create() {
        HBox bar = new HBox(22);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(16, 28, 16, 28));
        bar.getStyleClass().add("navigation-bar");

        Label logo = link("WATCH Z", navigation::showHome, "brand");
        Label home = link("Home", navigation::showHome, "nav-link");
        Label movies = link("Movies", navigation::showMovies, "nav-link");
        Label genres = link("Genres", navigation::showGenres, "nav-link");
        Label myList = link("My List", navigation::showMyList, "nav-link");

        TextField search = new TextField();
        search.setPromptText("Search movies...");
        search.setPrefWidth(200);
        search.getStyleClass().add("search-field");
        search.setOnAction(event -> navigation.showSearch(search.getText()));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label profile = new Label("GUEST");
        profile.getStyleClass().add("profile-label");
        bar.getChildren().addAll(logo, home, movies, genres, myList, spacer, search, profile);
        return bar;
    }

    private Label link(String text, Runnable action, String styleClass) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        label.setCursor(Cursor.HAND);
        label.setOnMouseClicked(event -> action.run());
        return label;
    }
}
