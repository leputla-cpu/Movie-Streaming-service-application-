package utils;

import javafx.scene.media.AudioClip;

import java.io.File;
import java.net.URL;

/** Optional UI sound effects. Missing files are safely ignored. */
public final class SoundManager {
    private static AudioClip click;
    private static AudioClip hover;
    private static boolean loaded;

    private SoundManager() { }

    private static void loadSounds() {
        if (loaded) return;
        loaded = true;
        click = load("click.wav");
        hover = load("hover.wav");
    }

    private static AudioClip load(String fileName) {
        try {
            File file = new File("resources/sounds/" + fileName);
            URL url = file.isFile() ? file.toURI().toURL() : SoundManager.class.getResource("/sounds/" + fileName);
            return url == null ? null : new AudioClip(url.toExternalForm());
        } catch (Exception exception) {
            System.err.println("UI sound unavailable: " + fileName);
            return null;
        }
    }

    public static void playClick() {
        loadSounds();
        if (click != null) click.play(0.45);
    }

    public static void playHover() {
        loadSounds();
        if (hover != null) hover.play(0.18);
    }
}
