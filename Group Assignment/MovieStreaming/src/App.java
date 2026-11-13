
import data.MovieData;
import models.Category;
import models.Movie;

import services.NavigationService;

import components.NavigationBar;
import components.FeaturedMovie;
import components.MovieRow;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class App extends Application {

    private static final double WINDOW_WIDTH = 1050;
    private static final double WINDOW_HEIGHT = 650;

    @Override
    public void start(Stage stage) {

        // =========================================
        // REGISTER NAVIGATION STAGE
        // =========================================

        NavigationService.setStage(stage);


        // =========================================
        // NAVIGATION BAR
        // =========================================

        NavigationBar navigationBar = new NavigationBar();

        HBox navigation = navigationBar.create();

        navigation.setMaxWidth(Double.MAX_VALUE);


        // =========================================
        // FEATURED MOVIE
        // =========================================

        Movie featuredMovie =
            MovieData.getMoviesByCategory(Category.ACTION).get(0);

        FeaturedMovie featuredComponent = new FeaturedMovie();

        VBox featuredSection = new VBox(
            featuredComponent.create(featuredMovie)
        );

        featuredSection.setMaxWidth(Double.MAX_VALUE);

        featuredSection.setPadding(
            new Insets(10, 0, 10, 0)
        );


        // =========================================
        // BRANDING
        // =========================================

        Label title = new Label("WATCH Z");
        title.getStyleClass().add("title");

        Label subtitle = new Label(
            "Your Entertainment Universe"
        );

        subtitle.getStyleClass().add("subtitle");

        VBox branding = new VBox(
            5,
            title,
            subtitle
        );

        branding.setAlignment(Pos.CENTER_LEFT);
        branding.setMaxWidth(Double.MAX_VALUE);


        // =========================================
        // MOVIE COUNT
        // =========================================

        Label totalMovies = new Label(
            "Explore " + MovieData.getMovieCount() + " Movies"
        );

        totalMovies.getStyleClass().add("total-movies");


        // =========================================
        // MOVIE CATEGORY ROWS
        // =========================================

        VBox movieSections = new VBox(30);

        movieSections.setPadding(
            new Insets(10, 0, 20, 0)
        );

        movieSections.setMaxWidth(Double.MAX_VALUE);

        for (Category category : Category.values()) {

            MovieRow movieRow = new MovieRow();

            VBox row = movieRow.create(category);

            row.setMaxWidth(Double.MAX_VALUE);

            movieSections.getChildren().add(row);
        }


        // =========================================
        // FOOTER
        // =========================================

        Label footer = new Label(
            "Watch Z | Movie Streaming Demo"
        );

        footer.getStyleClass().add("footer");

        VBox footerContainer = new VBox(footer);

        footerContainer.setAlignment(Pos.CENTER);

        footerContainer.setMaxWidth(Double.MAX_VALUE);

        footerContainer.setPadding(
            new Insets(25, 0, 15, 0)
        );


        // =========================================
        // MAIN PAGE CONTENT
        // =========================================

        VBox content = new VBox(
            20,
            branding,
            totalMovies,
            featuredSection,
            movieSections,
            footerContainer
        );

        content.setPadding(
            new Insets(25, 35, 20, 35)
        );

        content.setMaxWidth(Double.MAX_VALUE);

        content.getStyleClass().add("home-content");


        // =========================================
        // VERTICAL SCROLLING
        // =========================================

        ScrollPane scrollPane = new ScrollPane(content);

        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);

        scrollPane.setHbarPolicy(
            ScrollPane.ScrollBarPolicy.NEVER
        );

        scrollPane.setVbarPolicy(
            ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scrollPane.setPannable(true);

        scrollPane.getStyleClass().add("main-scroll");

        VBox.setVgrow(
            scrollPane,
            Priority.ALWAYS
        );


        // =========================================
        // MAIN APPLICATION LAYOUT
        // =========================================

        VBox root = new VBox(
            navigation,
            scrollPane
        );

        root.setFillWidth(true);

        root.getStyleClass().add("main-root");


        // =========================================
        // SCENE
        // =========================================

        Scene scene = new Scene(
            root,
            WINDOW_WIDTH,
            WINDOW_HEIGHT
        );


        // =========================================
        // LOAD EXTERNAL CSS
        // =========================================

        Path cssPath = Paths.get(
            "resources",
            "css",
            "style.css"
        );

        if (Files.exists(cssPath)) {

            scene.getStylesheets().add(
                cssPath.toAbsolutePath().toUri().toString()
            );

            System.out.println("CSS loaded successfully!");

            System.out.println(
                "CSS location: " + cssPath.toAbsolutePath()
            );

        } else {

            System.out.println(
                "CSS FILE NOT FOUND: "
                + cssPath.toAbsolutePath()
            );
        }


        // =========================================
        // STAGE CONFIGURATION
        // =========================================

        stage.setTitle("Watch Z - Movie Streaming");

        stage.setScene(scene);

        stage.setMinWidth(850);
        stage.setMinHeight(550);

        stage.setResizable(true);

        stage.centerOnScreen();

        stage.show();
    }


    // =========================================
    // APPLICATION ENTRY POINT
    // =========================================

    public static void main(String[] args) {

        launch(args);
    }
}
