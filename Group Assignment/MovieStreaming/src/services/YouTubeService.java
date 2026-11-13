
package services;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class YouTubeService {

    private final WebView webView;
    private final WebEngine webEngine;

    public YouTubeService() {

        webView = new WebView();
        webEngine = webView.getEngine();

        webView.setContextMenuEnabled(false);
        webView.setMinSize(0, 300);
    }

    public WebView getWebView() {
        return webView;
    }


    // =========================================
    // LOAD TRAILER
    // =========================================

    public void loadVideo(String videoReference) {

        if (videoReference == null || videoReference.isBlank()) {

            showMessage("Trailer reference is unavailable.");

            return;
        }

        videoReference = videoReference.trim();

        // SEARCH REFERENCE
        if (videoReference.startsWith("SEARCH:")) {

            String searchQuery =
                    videoReference.substring("SEARCH:".length());

            loadTrailerSearch(searchQuery);

            return;
        }

        // DIRECT YOUTUBE VIDEO ID
        if (!videoReference.matches("[a-zA-Z0-9_-]{11}")) {

            showMessage("Invalid YouTube trailer reference.");

            System.out.println(
                    "Invalid YouTube reference: " + videoReference
            );

            return;
        }

        loadEmbeddedVideo(videoReference);
    }


    // =========================================
    // LOAD EMBEDDED YOUTUBE VIDEO
    // =========================================

    private void loadEmbeddedVideo(String videoId) {

        String videoUrl =
                "https://www.youtube.com/embed/"
                + videoId
                + "?autoplay=1&controls=1&rel=0";

        String html = """
                <!DOCTYPE html>
                <html>
                <head>

                    <meta name="viewport"
                          content="width=device-width, initial-scale=1.0">

                    <style>

                        * {
                            margin: 0;
                            padding: 0;
                            box-sizing: border-box;
                        }

                        html, body {
                            width: 100%;
                            height: 100%;
                            background: #000000;
                            overflow: hidden;
                        }

                        iframe {
                            display: block;
                            width: 100%;
                            height: 100%;
                            border: none;
                        }

                    </style>

                </head>

                <body>

                    <iframe
                        src="%s"
                        title="Movie Trailer"
                        allow="autoplay; encrypted-media; picture-in-picture"
                        referrerpolicy="strict-origin-when-cross-origin"
                        allowfullscreen>
                    </iframe>

                </body>
                </html>
                """.formatted(videoUrl);

        webEngine.loadContent(html);
    }


    // =========================================
    // LOAD YOUTUBE SEARCH FOR A MOVIE
    // =========================================

    private void loadTrailerSearch(String searchQuery) {

        try {

            String encodedQuery = URLEncoder.encode(
                    searchQuery,
                    StandardCharsets.UTF_8
            );

            String searchUrl =
                    "https://www.youtube.com/results?search_query="
                    + encodedQuery;

            webEngine.load(searchUrl);

        } catch (Exception exception) {

            showMessage("Unable to open trailer search.");

            exception.printStackTrace();
        }
    }


    // =========================================
    // STOP VIDEO
    // =========================================

    public void stopVideo() {
        webEngine.load("about:blank");
    }


    // =========================================
    // DISPLAY MESSAGE
    // =========================================

    private void showMessage(String message) {

        String html = """
                <!DOCTYPE html>
                <html>
                <head>

                    <style>

                        html, body {
                            width: 100%;
                            height: 100%;
                            margin: 0;
                            background: #141414;
                            color: white;
                            font-family: Arial, sans-serif;
                        }

                        body {
                            display: flex;
                            justify-content: center;
                            align-items: center;
                            text-align: center;
                        }

                        h2 {
                            font-size: 20px;
                            font-weight: normal;
                        }

                    </style>

                </head>

                <body>
                    <h2>%s</h2>
                </body>

                </html>
                """.formatted(message);

        webEngine.loadContent(html);
    }
}
