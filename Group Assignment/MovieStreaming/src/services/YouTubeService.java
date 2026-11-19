
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

public class YouTubeService {

    private static final Pattern VIDEO_ID_PATTERN = Pattern.compile(
            "(?:youtube\\.com/(?:watch\\?(?:.*&)?v=|embed/)|youtu\\.be/)([A-Za-z0-9_-]{11})",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern RAW_ID_PATTERN =
            Pattern.compile("^[A-Za-z0-9_-]{11}$");

    private static LocalPlayerServer localServer;

    private final WebView webView = new WebView();

    public YouTubeService() {
        webView.setContextMenuEnabled(false);
        webView.setMinSize(0, 0);
        webView.setPrefSize(900, 480);
    }

    public WebView getWebView() {
        return webView;
    }

    public void loadVideo(String youtubeUrl) {
        load(youtubeUrl, false);
    }

    public void loadPreviewVideo(String youtubeUrl) {
        load(youtubeUrl, true);
    }

    private void load(String youtubeUrl, boolean preview) {

        String videoId = extractVideoId(youtubeUrl);

        if (videoId.isEmpty()) {
            webView.getEngine().loadContent(
                    "<html><body style='background:#080b12;color:white;"
                    + "font-family:Arial;text-align:center;padding:40px'>"
                    + "No valid YouTube trailer has been assigned."
                    + "</body></html>"
            );
            return;
        }

        try {
            String playerUrl =
                    getLocalPlayerServer().getPlayerUrl(videoId, preview);

            webView.getEngine().load(playerUrl);

        } catch (IOException exception) {
            System.err.println(
                    "Could not start YouTube player: "
                    + exception.getMessage()
            );

            webView.getEngine().loadContent(
                    "<html><body style='background:#080b12;color:white;"
                    + "font-family:Arial;text-align:center;padding:40px'>"
                    + "Unable to start the embedded trailer player."
                    + "</body></html>"
            );
        }
    }

    public void playVideo() {
        executeScript("window.watchZSetPlaying(true);");
    }

    public void pauseVideo() {
        executeScript("window.watchZSetPlaying(false);");
    }

    public void setVolume(int volume) {
        int safeVolume = Math.max(0, Math.min(100, volume));
        executeScript("window.watchZSetVolume(" + safeVolume + ");");
    }

    public void setMuted(boolean muted) {
        executeScript("window.watchZSetMuted(" + muted + ");");
    }

    public void stopVideo() {
        webView.getEngine().load("about:blank");
    }

    private void executeScript(String script) {
        if (webView.getEngine().getDocument() == null) {
            return;
        }

        try {
            webView.getEngine().executeScript(script);
        } catch (Exception exception) {
            System.err.println(
                    "YouTube player command failed: "
                    + exception.getMessage()
            );
        }
    }

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

    private static final class LocalPlayerServer {

        private final ServerSocket serverSocket;
        private final Thread serverThread;
        private volatile boolean running = true;

        private LocalPlayerServer() throws IOException {

            serverSocket = new ServerSocket(
                    0, 20, InetAddress.getByName("127.0.0.1")
            );

            serverThread = new Thread(
                    this::serve,
                    "WatchZ-YouTube-Local-Page"
            );

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
                                "Watch Z server: " + exception.getMessage()
                        );
                    }
                }
            }
        }

        private void handle(Socket socket) {

            try (
                    socket;
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(
                                    socket.getInputStream(),
                                    StandardCharsets.US_ASCII
                            )
                    )
            ) {

                String requestLine = reader.readLine();
                String line;

                while ((line = reader.readLine()) != null && !line.isEmpty()) {
                    // Consume request headers.
                }

                String videoId = "";
                boolean preview = false;

                if (requestLine != null) {

                    Matcher videoMatcher = Pattern.compile(
                            "(?:\\?|&)videoId=([A-Za-z0-9_-]{11})(?:&|\\s|$)"
                    ).matcher(requestLine);

                    Matcher previewMatcher = Pattern.compile(
                            "(?:\\?|&)preview=1(?:&|\\s|$)"
                    ).matcher(requestLine);

                    if (videoMatcher.find()) {
                        videoId = videoMatcher.group(1);
                    }

                    preview = previewMatcher.find();
                }

                byte[] body = createPlayerHtml(videoId, preview)
                        .getBytes(StandardCharsets.UTF_8);

                OutputStream output = socket.getOutputStream();

                String headers =
                        "HTTP/1.1 200 OK\r\n"
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
                        "Could not serve player page: " + exception.getMessage()
                );
            }
        }

        private String createPlayerHtml(String videoId, boolean preview) {

            if (videoId == null || videoId.isBlank()) {
                return "<html><body style='background:#080b12;color:white'>"
                        + "Invalid YouTube video ID.</body></html>";
            }

            String previewOptions = preview
                    ? "controls:0, mute:1, loop:1, playlist:'"
                            + videoId + "', "
                    : "controls:0, mute:0, loop:0, ";

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
                    + "iframe{display:block;width:100%;height:100%;border:0;}"
                    + "</style></head><body>"

                    + "<div id='watchz-player'></div>"

                    + "<script>"
                    + "var watchZPlayer=null;"
                    + "var watchZPlayerReady=false;"
                    + "var watchZShouldPlay=false;"
                    + "var watchZMuted=" + preview + ";"
                    + "var watchZVolume=70;"

                    + "function onYouTubeIframeAPIReady(){"
                    + "watchZPlayer=new YT.Player('watchz-player',{"
                    + "videoId:'" + videoId + "',"
                    + "playerVars:{"
                    + "autoplay:0,"
                    + "playsinline:1,"
                    + "rel:0,"
                    + "modestbranding:1,"
                    + "enablejsapi:1,"
                    + previewOptions
                    + "},"
                    + "events:{"
                    + "onReady:function(event){"
                    + "watchZPlayerReady=true;"
                    + "event.target.setVolume(watchZVolume);"
                    + "if(watchZMuted){event.target.mute();}"
                    + previewSetup
                    + "if(watchZShouldPlay){event.target.playVideo();}"
                    + "}"
                    + "}"
                    + "});"
                    + "}"

                    + "window.watchZSetPlaying=function(playing){"
                    + "watchZShouldPlay=playing;"
                    + "if(!watchZPlayerReady||!watchZPlayer)return;"
                    + "if(playing){watchZPlayer.playVideo();}"
                    + "else{watchZPlayer.pauseVideo();}"
                    + "};"

                    + "window.watchZSetVolume=function(value){"
                    + "watchZVolume=Math.max(0,Math.min(100,value));"
                    + "if(watchZPlayerReady&&watchZPlayer){"
                    + "watchZPlayer.setVolume(watchZVolume);"
                    + "}"
                    + "};"

                    + "window.watchZSetMuted=function(muted){"
                    + "watchZMuted=muted;"
                    + "if(!watchZPlayerReady||!watchZPlayer)return;"
                    + "if(muted){watchZPlayer.mute();}"
                    + "else{"
                    + "watchZPlayer.unMute();"
                    + "watchZPlayer.setVolume(watchZVolume);"
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
