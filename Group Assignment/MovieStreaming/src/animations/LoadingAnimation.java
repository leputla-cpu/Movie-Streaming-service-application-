package animations;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/** Small animated loading indicator for embedded trailer loading. */
public final class LoadingAnimation {
    private LoadingAnimation() { }

    public static StackPane create(String message) {
        HBox dots = new HBox(9);
        dots.setAlignment(javafx.geometry.Pos.CENTER);
        for (int i = 0; i < 3; i++) {
            Circle dot = new Circle(5);
            dot.getStyleClass().add("loading-dot");
            FadeTransition pulse = new FadeTransition(Duration.millis(520), dot);
            pulse.setFromValue(0.28);
            pulse.setToValue(1.0);
            pulse.setAutoReverse(true);
            pulse.setCycleCount(FadeTransition.INDEFINITE);
            pulse.setDelay(Duration.millis(i * 170));
            pulse.setInterpolator(Interpolator.EASE_BOTH);
            pulse.play();
            dots.getChildren().add(dot);
        }
        javafx.scene.control.Label label = new javafx.scene.control.Label(message == null ? "Loading..." : message);
        label.getStyleClass().add("loading-label");
        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(13, dots, label);
        content.setAlignment(javafx.geometry.Pos.CENTER);
        StackPane container = new StackPane(content);
        container.getStyleClass().add("loading-overlay");
        return container;
    }
}
