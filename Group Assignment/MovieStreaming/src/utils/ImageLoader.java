package utils;

import javafx.scene.image.Image;

import java.io.File;
import java.net.URL;

/** Centralized image loading for local project assets and classpath resources. */
public final class ImageLoader {
    private ImageLoader() { }

    public static Image load(String location) {
        if (location == null || location.isBlank()) return null;
        try {
            if (location.startsWith("file:") || location.startsWith("http:") || location.startsWith("https:")) {
                return new Image(location, true);
            }
            File file = new File(location);
            if (file.isFile()) return new Image(file.toURI().toString(), true);
            URL resource = ImageLoader.class.getResource(location.startsWith("/") ? location : "/" + location);
            return resource == null ? null : new Image(resource.toExternalForm(), true);
        } catch (RuntimeException exception) {
            System.err.println("Unable to load image: " + location + " - " + exception.getMessage());
            return null;
        }
    }
}
