
package components;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import services.NavigationService;
import utils.SoundManager;

/** Shared application frame. The sidebar remains visible on every screen. */
public class AppShell {

    private final NavigationService navigation = new NavigationService();

    public HBox create(Parent page, String activePage, String pageTitle) {
        return create(page, activePage, pageTitle, "");
    }

    public HBox create(
            Parent page,
            String activePage,
            String pageTitle,
            String initialSearchText
    ) {
        HBox root = new HBox();
        root.getStyleClass().add("application-layout");

        VBox sidebar = createSidebar(activePage);

        VBox main = new VBox();
        main.getStyleClass().add("home-main-area");
        HBox.setHgrow(main, Priority.ALWAYS);

        HBox topBar = createTopBar(pageTitle, initialSearchText);

        VBox contentHolder = new VBox(page);
        contentHolder.getStyleClass().add("page-content-holder");
        contentHolder.setFillWidth(true);

        VBox.setVgrow(contentHolder, Priority.ALWAYS);
        VBox.setVgrow(page, Priority.ALWAYS);

        main.getChildren().addAll(topBar, contentHolder);
        root.getChildren().addAll(sidebar, main);

        return root;
    }

    private VBox createSidebar(String activePage) {
        VBox sidebar = new VBox(12);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(210);
        sidebar.setMinWidth(190);
        sidebar.setMaxWidth(225);

        Label brand = new Label("WATCH Z");
        brand.getStyleClass().add("sidebar-brand");
        brand.setCursor(Cursor.HAND);
        brand.setOnMouseClicked(event -> navigation.showHome());

        Label tagline = new Label("MOVIE STREAMING");
        tagline.getStyleClass().add("sidebar-tagline");

        VBox brandBlock = new VBox(5, brand, tagline);
        brandBlock.getStyleClass().add("sidebar-brand-block");

        Label menuHeading = new Label("DISCOVER");
        menuHeading.getStyleClass().add("sidebar-section-heading");

        VBox menu = new VBox(7);
        menu.getStyleClass().add("sidebar-menu");

        // Search has been removed from the sidebar.
        // Use the search field in the top bar instead.
        menu.getChildren().addAll(
                navItem("Home", "home", activePage),
                navItem("Movies", "movies", activePage),
                navItem("Genres", "genres", activePage),
                navItem("My List", "my-list", activePage)
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox footer = new VBox(6);
        footer.getStyleClass().add("sidebar-bottom");

        Label footerTitle = new Label("YOUR ENTERTAINMENT HUB");
        footerTitle.getStyleClass().add("sidebar-bottom-title");

        Label footerText = new Label(
                "Explore stories across five movie genres."
        );
        footerText.setWrapText(true);
        footerText.getStyleClass().add("sidebar-bottom-text");

        footer.getChildren().addAll(footerTitle, footerText);

        sidebar.getChildren().addAll(
                brandBlock,
                menuHeading,
                menu,
                spacer,
                footer
        );

        return sidebar;
    }

    private HBox navItem(String title, String key, String activePage) {
        Label label = new Label(title);
        label.getStyleClass().add("sidebar-link-text");

        HBox item = new HBox(label);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(12, 14, 12, 16));
        item.getStyleClass().add("sidebar-link");

        if (key.equals(activePage)) {
            item.getStyleClass().add("sidebar-link-active");
        }

        item.setCursor(Cursor.HAND);
        item.setOnMouseEntered(event -> SoundManager.playHover());

        item.setOnMouseClicked(event -> {
            SoundManager.playClick();

            switch (key) {
                case "home" -> navigation.showHome();
                case "movies" -> navigation.showMovies();
                case "genres" -> navigation.showGenres();
                case "my-list" -> navigation.showMyList();
                default -> navigation.showHome();
            }
        });

        return item;
    }

    private HBox createTopBar(String pageTitle, String initialSearchText) {
        HBox topBar = new HBox(14);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(16, 28, 16, 28));
        topBar.getStyleClass().add("home-top-bar");

        HBox heading = new HBox(12);
        heading.setAlignment(Pos.CENTER_LEFT);

        Label pageLabel = new Label(pageTitle.toUpperCase());
        pageLabel.getStyleClass().add("topbar-page-label");
        heading.getChildren().add(pageLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        TextField search = new TextField();
        search.setPromptText("Search movies here...");
        search.setPrefWidth(235);
        search.getStyleClass().add("search-field");

        // Keep the search text when the Search screen is opened.
        search.setText(initialSearchText == null ? "" : initialSearchText);

        // Search immediately whenever the user types or deletes a character.
        search.textProperty().addListener((observable, oldValue, newValue) ->
                navigation.searchFromTopBar(newValue)
        );

        // Enter also keeps the user on the Search screen.
        search.setOnAction(event ->
                navigation.searchFromTopBar(search.getText())
        );

        // Restore focus after the first character opens the Search screen.
        if (initialSearchText != null && !initialSearchText.isEmpty()) {
            Platform.runLater(() -> {
                search.requestFocus();
                search.positionCaret(search.getText().length());
            });
        }

        topBar.getChildren().addAll(heading, spacer, search);

        return topBar;
    }
}
