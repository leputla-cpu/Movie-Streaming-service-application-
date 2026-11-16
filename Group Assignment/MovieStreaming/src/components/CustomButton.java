package components;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import utils.SoundManager;

public class CustomButton {

    public StackPane create(String text, String styleClass, Runnable action) {

        Label buttonText = new Label(text);
        buttonText.getStyleClass().add("custom-button-text");

        StackPane button = new StackPane(buttonText);
        button.getStyleClass().addAll("custom-button", styleClass);

        button.setAlignment(Pos.CENTER);
        button.setCursor(Cursor.HAND);

        button.setOnMouseEntered(event -> SoundManager.playHover());
        button.setOnMouseClicked(event -> {
            SoundManager.playClick();
            if (action != null) action.run();
        });

        return button;
    }
}
