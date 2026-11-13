
package models;

public class Movie {

    private final int id;
    private final String title;
    private final String description;
    private final Category category;
    private final int year;
    private final double rating;
    private final int duration;
    private final String posterUrl;
    private final String youtubeVideoId;

    public Movie(int id, String title, String description,
                 Category category, int year, double rating,
                 int duration, String posterUrl, String youtubeVideoId) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.year = year;
        this.rating = rating;
        this.duration = duration;
        this.posterUrl = posterUrl;
        this.youtubeVideoId = youtubeVideoId;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public int getYear() {
        return year;
    }

    public double getRating() {
        return rating;
    }

    public int getDuration() {
        return duration;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getYoutubeVideoId() {
        return youtubeVideoId;
    }

    @Override
    public String toString() {
        return title + " (" + year + ")";
    }
}
