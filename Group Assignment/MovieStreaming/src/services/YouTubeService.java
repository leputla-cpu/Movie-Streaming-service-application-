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
 * Loads YouTube's embedded player inside JavaFX WebView.
 *
 * A tiny loopback HTTP server is used instead of WebEngine.loadContent().
 * This gives the iframe a real HTTP page origin and lets WebKit send a
 * Referer header when it requests the HTTPS YouTube embed. Loading the same
 * HTML from a file:// URL or with loadContent() can result in YouTube Error 153.
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

    /** Loads the selected movie's YouTube video in the embedded JavaFX player. */
    public void loadVideo(String youtubeUrl) {
        String videoId = extractVideoId(youtubeUrl);
        if (videoId.isEmpty()) {
            webView.getEngine().loadContent("<!DOCTYPE html><html><body style='margin:0;background:#10151f;color:white;font-family:Arial;display:grid;place-items:center;height:100vh;text-align:center'><div>No valid YouTube trailer has been assigned to this movie.</div></body></html>");
            return;
        }

        try {
            String playerUrl = getLocalPlayerServer().getPlayerUrl(videoId);
            webView.getEngine().load(playerUrl);
        } catch (IOException exception) {
            System.err.println("Could not start the local YouTube player page: " + exception.getMessage());
            webView.getEngine().loadContent("<!DOCTYPE html><html><body style='margin:0;background:#10151f;color:white;font-family:Arial;display:grid;place-items:center;height:100vh;text-align:center'><div>Unable to start the embedded trailer player. Check that local connections are permitted.</div></body></html>");
        }
    }

    /** Reloads the current embedded player page. */
    public void playVideo() {
        webView.getEngine().reload();
    }

    public void pauseVideo() {
        // YouTube's native player controls remain available inside the iframe.
    }

    public void stopVideo() {
        webView.getEngine().load("about:blank");
    }

    /** Stops the shared loopback server when the JavaFX application exits. */
    public static synchronized void shutdown() {
        if (localServer != null) {
            localServer.close();
            localServer = null;
        }
    }

    private static synchronized LocalPlayerServer getLocalPlayerServer() throws IOException {
        if (localServer == null || !localServer.isRunning()) {
            localServer = new LocalPlayerServer();
        }
        return localServer;
    }

    private static String extractVideoId(String value) {
        if (value == null || value.isBlank()) return "";
        String trimmed = value.trim();
        if (RAW_ID_PATTERN.matcher(trimmed).matches()) return trimmed;
        Matcher matcher = VIDEO_ID_PATTERN.matcher(trimmed);
        return matcher.find() ? matcher.group(1) : "";
    }

    /** Minimal HTTP server that serves only the local YouTube iframe page. */
    private static final class LocalPlayerServer {
        private final ServerSocket serverSocket;
        private final Thread serverThread;
        private volatile boolean running = true;

        private LocalPlayerServer() throws IOException {
            serverSocket = new ServerSocket(0, 20, InetAddress.getByName("127.0.0.1"));
            serverThread = new Thread(this::serve, "WatchZ-YouTube-Local-Page");
            serverThread.setDaemon(true);
            serverThread.start();
        }

        private boolean isRunning() {
            return running && !serverSocket.isClosed();
        }

        private String getPlayerUrl(String videoId) {
            return "http://localhost:" + serverSocket.getLocalPort() + "/player?videoId=" + videoId;
        }

        private void serve() {
            while (running) {
                try {
                    Socket socket = serverSocket.accept();
                    handle(socket);
                } catch (IOException exception) {
                    if (running) {
                        System.err.println("Watch Z local player server: " + exception.getMessage());
                    }
                }
            }
        }

        private void handle(Socket socket) {
            try (socket;
                 BufferedReader reader = new BufferedReader(new InputStreamReader(
                         socket.getInputStream(), StandardCharsets.US_ASCII))) {
                String requestLine = reader.readLine();
                String line;
                while ((line = reader.readLine()) != null && !line.isEmpty()) {
                    // Consume request headers before writing the response.
                }

                String videoId = "";
                if (requestLine != null) {
                    Matcher matcher = Pattern.compile("(?:\\?|&)videoId=([A-Za-z0-9_-]{11})(?:&|\\s|$)")
                            .matcher(requestLine);
                    if (matcher.find()) videoId = matcher.group(1);
                }

                byte[] body = createPlayerHtml(videoId).getBytes(StandardCharsets.UTF_8);
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
                System.err.println("Could not serve the local YouTube player page: " + exception.getMessage());
            }
        }

        private String createPlayerHtml(String videoId) {
            if (videoId == null || videoId.isBlank()) {
                return "<!DOCTYPE html><html><body style='margin:0;background:#10151f;color:white;font-family:Arial;display:grid;place-items:center;height:100vh'>Invalid YouTube video ID.</body></html>";
            }
            String origin = "http://localhost:" + serverSocket.getLocalPort();
            String embedUrl = "https://www.youtube.com/embed/" + videoId
                    + "?playsinline=1&rel=0&enablejsapi=1&origin=" + origin;

            return "<!DOCTYPE html>"
                    + "<html><head><meta charset='UTF-8'>"
                    + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                    + "<meta name='referrer' content='strict-origin-when-cross-origin'>"
                    + "<title>Watch Z Trailer</title>"
                    + "<style>"
                    + "html,body{width:100%;height:100%;margin:0;padding:0;overflow:hidden;background:#080b12;}"
                    + "#player{position:absolute;inset:0;width:100%;height:100%;}"
                    + "iframe{display:block;width:100%;height:100%;border:0;background:#080b12;}"
                    + "</style></head><body>"
                    + "<div id='player'><iframe src='" + embedUrl + "' "
                    + "title='YouTube video player' "
                    + "referrerpolicy='strict-origin-when-cross-origin' "
                    + "allow='accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share' "
                    + "allowfullscreen></iframe></div>"
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
