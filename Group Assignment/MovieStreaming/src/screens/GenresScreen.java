package screens;

import components.MovieRow;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import models.Category;

/** Genre browser showing each category and its own horizontally scrollable movie row. */
public class GenresScreen {
    public VBox create() { return create(null); }

    public VBox create(Category selectedCategory) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(28, 30, 35, 30));
        content.getStyleClass().add("screen-content");
        Label heading = new Label(selectedCategory == null ? "Explore by Genre" : selectedCategory.getDisplayName());
        heading.getStyleClass().add("screen-heading");
        Label subtitle = new Label(selectedCategory == null
                ? "Choose a genre and discover its movie collection."
                : "Movies in the " + selectedCategory.getDisplayName() + " category.");
        subtitle.getStyleClass().add("screen-subheading");
        VBox rows = new VBox(24);
        for (Category category : Category.values()) {
            if (selectedCategory == null || selectedCategory == category) rows.getChildren().add(new MovieRow().create(category));
        }
        ScrollPane scroll = new ScrollPane(new VBox(heading, subtitle, rows));
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.getStyleClass().add("main-scroll");
        content.getChildren().add(scroll);
        VBox.setVgrow(scroll, javafx.scene.layout.Priority.ALWAYS);
        return content;
    }
}
