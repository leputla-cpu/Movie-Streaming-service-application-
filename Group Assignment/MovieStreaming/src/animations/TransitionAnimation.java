package animations;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.scene.Node;
import javafx.util.Duration;

/** Short fade-in used when switching application pages. */
public final class TransitionAnimation {
    private TransitionAnimation() { }

    public static void playIn(Node node) {
        if (node == null) return;
        node.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(230), node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setInterpolator(Interpolator.EASE_OUT);
        fade.play();
    }
}
