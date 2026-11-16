
package services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.web.WebView;

/**
 * Loads YouTube videos inside JavaFX WebView.
 *
 * A local HTTP server gives the embedded YouTube player a real HTTP origin
 * and allows WebKit to send a Referer header.
 *
 * Supports:
 * - Normal video playback.
 * - Muted featured trailer previews.
 * - JavaScript-controlled play and pause.
 */
public class YouTubeService {

    private static final Pattern VIDEO_ID_PATTERN = Pattern.compile(
            "(?:youtube\\.com/(?:watch\\?(?:.*&)?v=|embed/)|youtu\\.be/)([A-Za-z0-9_-]{11})",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern RAW_ID_PATTERN = Pattern.compile("^[A-Za-z0-9_-]{11}$");

    private static LocalPlayerServer localServer;

    private final WebView webView = new WebView();

    public YouTubeService() {
        webView.setContextMenuEnabled(false);
        webView.setMinSize(0, 300);
        webView.setPrefSize(900, 480);
    }

    public WebView getWebView() {
        return webView;
    }

    /**
     * Loads a normal YouTube video for the dedicated player screen.
     */
    public void loadVideo(String youtubeUrl) {
        load(youtubeUrl, false);
    }

    /**
     * Loads a muted trailer preview for the featured movie banner.
     * The video does not automatically play until the mouse enters.
     */
    public void loadPreviewVideo(String youtubeUrl) {
        load(youtubeUrl, true);
    }

    private void load(String youtubeUrl, boolean preview) {
        String videoId = extractVideoId(youtubeUrl);

        if (videoId.isEmpty()) {
            webView.getEngine().loadContent(
                    "<!DOCTYPE html><html><body style='margin:0;background:#10151f;color:white;"
                    + "font-family:Arial;display:grid;place-items:center;height:100vh;text-align:center'>"
                    + "<div>No valid YouTube trailer has been assigned to this movie.</div>"
                    + "</body></html>");
            return;
        }

        try {
            String playerUrl = getLocalPlayerServer().getPlayerUrl(videoId, preview);
            webView.getEngine().load(playerUrl);

        } catch (IOException exception) {
            System.err.println(
                    "Could not start the local YouTube player page: " + exception.getMessage());

            webView.getEngine().loadContent(
                    "<!DOCTYPE html><html><body style='margin:0;background:#10151f;color:white;"
                    + "font-family:Arial;display:grid;place-items:center;height:100vh;text-align:center'>"
                    + "<div>Unable to start the embedded trailer player. "
                    + "Check that local connections are permitted.</div>"
                    + "</body></html>");
        }
    }

    /**
     * Starts or resumes the currently loaded video.
     */
    public void playVideo() {
        executePlayerCommand("playVideo");
    }

    /**
     * Pauses the currently loaded video.
     */
    public void pauseVideo() {
        executePlayerCommand("pauseVideo");
    }

    private void executePlayerCommand(String command) {
        if (webView.getEngine().getDocument() == null) {
            return;
        }

        webView.getEngine().executeScript(
                "if (window.watchZSetPlaying) { window.watchZSetPlaying("
                + command.equals("playVideo") + "); }");
    }

    /**
     * Stops the current video and clears the WebView.
     */
    public void stopVideo() {
        webView.getEngine().load("about:blank");
    }

    /**
     * Stops the shared loopback server when the JavaFX application exits.
     */
    public static synchronized void shutdown() {
        if (localServer != null) {
            localServer.close();
            localServer = null;
        }
    }

    private static synchronized LocalPlayerServer getLocalPlayerServer()
            throws IOException {

        if (localServer == null || !localServer.isRunning()) {
            localServer = new LocalPlayerServer();
        }

        return localServer;
    }

    private static String extractVideoId(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        String trimmed = value.trim();

        if (RAW_ID_PATTERN.matcher(trimmed).matches()) {
            return trimmed;
        }

        Matcher matcher = VIDEO_ID_PATTERN.matcher(trimmed);

        return matcher.find() ? matcher.group(1) : "";
    }

    /**
     * Minimal HTTP server that serves the local YouTube player page.
     */
    private static final class LocalPlayerServer {

        private final ServerSocket serverSocket;
        private final Thread serverThread;
        private volatile boolean running = true;

        private LocalPlayerServer() throws IOException {
            serverSocket = new ServerSocket(
                    0, 20, InetAddress.getByName("127.0.0.1"));

            serverThread = new Thread(
                    this::serve, "WatchZ-YouTube-Local-Page");

            serverThread.setDaemon(true);
            serverThread.start();
        }

        private boolean isRunning() {
            return running && !serverSocket.isClosed();
        }

        private String getPlayerUrl(String videoId, boolean preview) {
            return "http://localhost:" + serverSocket.getLocalPort()
                    + "/player?videoId=" + videoId
                    + "&preview=" + (preview ? "1" : "0");
        }

        private void serve() {
            while (running) {
                try {
                    Socket socket = serverSocket.accept();
                    handle(socket);

                } catch (IOException exception) {
                    if (running) {
                        System.err.println(
                                "Watch Z local player server: "
                                + exception.getMessage());
                    }
                }
            }
        }

        private void handle(Socket socket) {
            try (socket;
                 BufferedReader reader = new BufferedReader(
                         new InputStreamReader(
                                 socket.getInputStream(),
                                 StandardCharsets.US_ASCII))) {

                String requestLine = reader.readLine();
                String line;

                while ((line = reader.readLine()) != null && !line.isEmpty()) {
                    // Consume request headers.
                }

                String videoId = "";
                boolean preview = false;

                if (requestLine != null) {
                    Matcher videoMatcher = Pattern.compile(
                            "(?:\\?|&)videoId=([A-Za-z0-9_-]{11})(?:&|\\s|$)")
                            .matcher(requestLine);

                    Matcher previewMatcher = Pattern.compile(
                            "(?:\\?|&)preview=1(?:&|\\s|$)")
                            .matcher(requestLine);

                    if (videoMatcher.find()) {
                        videoId = videoMatcher.group(1);
                    }

                    preview = previewMatcher.find();
                }

                byte[] body = createPlayerHtml(videoId, preview)
                        .getBytes(StandardCharsets.UTF_8);

                OutputStream output = socket.getOutputStream();

                String headers = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: text/html; charset=UTF-8\r\n"
                        + "Content-Length: " + body.length + "\r\n"
                        + "Cache-Control: no-store\r\n"
                        + "Referrer-Policy: strict-origin-when-cross-origin\r\n"
                        + "Connection: close\r\n\r\n";

                output.write(headers.getBytes(StandardCharsets.US_ASCII));
                output.write(body);
                output.flush();

            } catch (IOException exception) {
                System.err.println(
                        "Could not serve the local YouTube player page: "
                        + exception.getMessage());
            }
        }

        /**
         * Builds the embedded YouTube player.
         *
         * Preview mode:
         * - No visible YouTube controls.
         * - Muted playback.
         * - Loops the trailer.
         * - Starts only when requested by the JavaFX application.
         *
         * Normal mode:
         * - Displays YouTube controls.
         * - Allows normal playback.
         */
        private String createPlayerHtml(String videoId, boolean preview) {

            if (videoId == null || videoId.isBlank()) {
                return "<!DOCTYPE html><html><body style='margin:0;background:#10151f;"
                        + "color:white;font-family:Arial;display:grid;place-items:center;"
                        + "height:100vh'>Invalid YouTube video ID.</body></html>";
            }

            String playerOptions = preview
                    ? "controls:0, mute:1, loop:1, playlist:'" + videoId + "', "
                    : "controls:1, mute:0, loop:0, ";

            String previewSetup = preview
                    ? "event.target.mute();"
                    : "";

            return "<!DOCTYPE html>"
                    + "<html><head>"
                    + "<meta charset='UTF-8'>"
                    + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                    + "<meta name='referrer' content='strict-origin-when-cross-origin'>"
                    + "<title>Watch Z Trailer</title>"
                    + "<style>"
                    + "html,body{width:100%;height:100%;margin:0;padding:0;"
                    + "overflow:hidden;background:#080b12;}"
                    + "#watchz-player{position:absolute;inset:0;width:100%;height:100%;}"
                    + "iframe{display:block;width:100%;height:100%;border:0;"
                    + "background:#080b12;}"
                    + "</style>"
                    + "</head><body>"
                    + "<div id='watchz-player'></div>"
                    + "<script>"
                    + "var watchZPlayer=null;"
                    + "var watchZPlayerReady=false;"
                    + "var watchZShouldPlay=false;"
                    + "function onYouTubeIframeAPIReady(){"
                    + "watchZPlayer=new YT.Player('watchz-player',{"
                    + "videoId:'" + videoId + "',"
                    + "playerVars:{"
                    + "autoplay:0,"
                    + "playsinline:1,"
                    + "rel:0,"
                    + "modestbranding:1,"
                    + "enablejsapi:1,"
                    + "origin:location.origin,"
                    + playerOptions
                    + "},"
                    + "events:{"
                    + "onReady:function(event){"
                    + "watchZPlayerReady=true;"
                    + previewSetup
                    + "if(watchZShouldPlay){"
                    + "event.target.playVideo();"
                    + "}"
                    + "}"
                    + "}"
                    + "});"
                    + "}"
                    + "window.watchZSetPlaying=function(playing){"
                    + "watchZShouldPlay=playing;"
                    + "if(!watchZPlayerReady||!watchZPlayer){return;}"
                    + "if(playing){"
                    + (preview ? "watchZPlayer.mute();" : "")
                    + "watchZPlayer.playVideo();"
                    + "}else{"
                    + "watchZPlayer.pauseVideo();"
                    + "}"
                    + "};"
                    + "</script>"
                    + "<script src='https://www.youtube.com/iframe_api'></script>"
                    + "</body></html>";
        }

        private void close() {
            running = false;

            try {
                serverSocket.close();

            } catch (IOException ignored) {
                // Server is already stopping.
            }
        }
    }
}
