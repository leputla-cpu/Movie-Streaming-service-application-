package models;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Movie catalogue model. Each movie stores its normal YouTube watch-page URL. */
public class Movie {

    private static final Pattern YOUTUBE_ID_PATTERN = Pattern.compile(
            "(?:youtube\\.com/(?:watch\\?(?:.*&)?v=|embed/)|youtu\\.be/)([A-Za-z0-9_-]{11})",
            Pattern.CASE_INSENSITIVE);

    private final int id;
    private final String title;
    private final String description;
    private final Category category;
    private final int year;
    private final double rating;
    private final int duration;
    private final String posterUrl;
    private final String youtubeUrl;

    public Movie(int id, String title, String description,
                 Category category, int year, double rating,
                 int duration, String posterUrl, String youtubeUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.year = year;
        this.rating = rating;
        this.duration = duration;
        this.posterUrl = posterUrl;
        this.youtubeUrl = normalizeWatchUrl(youtubeUrl);
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Category getCategory() { return category; }
    public int getYear() { return year; }
    public double getRating() { return rating; }
    public int getDuration() { return duration; }
    public String getPosterUrl() { return posterUrl; }

    /** Returns the direct YouTube watch-page URL assigned to this movie. */
    public String getYoutubeUrl() { return youtubeUrl; }

    /** Compatibility accessor for older components that still need an embed URL. */
    public String getYoutubeEmbedUrl() {
        String id = getYoutubeVideoId();
        return id.isEmpty() ? "" : "https://www.youtube.com/embed/" + id;
    }

    public String getYoutubeVideoId() {
        if (youtubeUrl.isBlank()) return "";
        Matcher matcher = YOUTUBE_ID_PATTERN.matcher(youtubeUrl);
        return matcher.find() ? matcher.group(1) : "";
    }

    private static String normalizeWatchUrl(String value) {
        if (value == null || value.isBlank() || value.startsWith("SEARCH:")) return "";
        String trimmed = value.trim();
        if (trimmed.matches("[A-Za-z0-9_-]{11}")) {
            return "https://www.youtube.com/watch?v=" + trimmed;
        }
        Matcher matcher = YOUTUBE_ID_PATTERN.matcher(trimmed);
        if (matcher.find() && (trimmed.startsWith("https://") || trimmed.startsWith("http://"))) {
            return "https://www.youtube.com/watch?v=" + matcher.group(1);
        }
        return "";
    }

    @Override
    public String toString() { return title + " (" + year + ")"; }
}
