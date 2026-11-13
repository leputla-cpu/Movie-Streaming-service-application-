
package models;

public enum Category {

    ACTION("Action"),
    COMEDY("Comedy"),
    HORROR("Horror"),
    SCIENCE_FICTION("Science Fiction"),
    ANIMATION("Animation");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
