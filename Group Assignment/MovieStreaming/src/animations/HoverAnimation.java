package animations;

import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.util.Duration;

/** Reusable subtle scale animation for interactive movie and navigation cards. */
public final class HoverAnimation {
    private HoverAnimation() { }

    public static void apply(Node node) {
        if (node == null) return;
        node.setOnMouseEntered(event -> animate(node, 1.045));
        node.setOnMouseExited(event -> animate(node, 1.0));
    }

    private static void animate(Node node, double scale) {
        ScaleTransition transition = new ScaleTransition(Duration.millis(170), node);
        transition.setToX(scale);
        transition.setToY(scale);
        transition.setInterpolator(Interpolator.EASE_BOTH);
        transition.play();
    }
}
