
package components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import services.NavigationService;

public class NavigationBar {

    private final NavigationService navigationService =
            new NavigationService();

    public HBox create() {

        HBox navigation = new HBox(30);
        navigation.setAlignment(Pos.CENTER_LEFT);
        navigation.setPadding(new Insets(18, 45, 18, 45));
        navigation.getStyleClass().add("navigation-bar");

        // Logo
        Label logo = new Label("WATCH Z");
        logo.getStyleClass().add("brand");
        logo.setCursor(Cursor.HAND);
        logo.setOnMouseClicked(event ->
                navigationService.showHome()
        );

        // Navigation links
        Label home = createLink("Home");
        home.setOnMouseClicked(event ->
                navigationService.showHome()
        );

        Label movies = createLink("Movies");
        movies.setOnMouseClicked(event ->
                navigationService.showHome()
        );

        Label tvShows = createLink("TV Shows");
        tvShows.setOnMouseClicked(event -> showUnavailableMessage());

        Label myList = createLink("My List");
        myList.setOnMouseClicked(event ->
                navigationService.showMyList()
        );

        // Search field
        TextField search = new TextField();
        search.setPromptText("Search movies...");
        search.setPrefWidth(190);
        search.getStyleClass().add("search-field");

        search.setOnAction(event ->
                navigationService.showSearch()
        );

        // Profile
        Label profile = new Label("PROFILE");
        profile.getStyleClass().add("profile-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        navigation.getChildren().addAll(
                logo,
                home,
                movies,
                tvShows,
                myList,
                spacer,
                search,
                profile
        );

        return navigation;
    }

    private Label createLink(String text) {

        Label link = new Label(text);
        link.getStyleClass().add("nav-link");
        link.setCursor(Cursor.HAND);

        return link;
    }

    private void showUnavailableMessage() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Watch Z");
        alert.setHeaderText("TV Shows");
        alert.setContentText(
                "TV Shows are not currently available."
        );
        alert.showAndWait();
    }
}
