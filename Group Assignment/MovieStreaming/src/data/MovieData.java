package data;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import models.Category;
import models.Movie;

public class MovieData {

    private static final List<Movie> movies = new ArrayList<>();

    static {
        loadMovies();
    }

    private static void loadMovies() {

        // ================= ACTION =================

        add(1, "The Dark Knight",
                "A hero protects Gotham City.",
                Category.ACTION, 2008, 9.0, 152,
                "https://www.youtube.com/watch?v=kmJLuwP3MbY");

        add(2, "Mad Max: Fury Road",
                "A high-speed adventure across a dangerous wasteland.",
                Category.ACTION, 2015, 8.1, 120,
                "https://www.youtube.com/watch?v=hEJnMQG9ev8");

        add(3, "John Wick",
                "An ex-assassin returns to a dangerous world.",
                Category.ACTION, 2014, 7.4, 101,
                "https://www.youtube.com/watch?v=2AUmvWm5ZDQ");

        add(4, "Gladiator",
                "A Roman general fights for justice.",
                Category.ACTION, 2000, 8.5, 155,
                "https://www.youtube.com/watch?v=P5ieIbInFpg");

        add(5, "Mission Impossible: Fallout",
                "A secret agent faces an international threat.",
                Category.ACTION, 2018, 7.7, 147,
                "https://www.youtube.com/watch?v=MEOOas3JZt0");

        add(6, "Black Panther",
                "A king protects his technologically advanced nation.",
                Category.ACTION, 2018, 7.3, 134,
                "https://www.youtube.com/watch?v=xjDjIWPwcPU");

        add(7, "Top Gun: Maverick",
                "A pilot returns to train a new generation.",
                Category.ACTION, 2022, 8.2, 130,
                "https://www.youtube.com/watch?v=qSqVVswa420");

        add(8, "The Equalizer",
                "A former intelligence officer helps people in danger.",
                Category.ACTION, 2014, 7.2, 132,
                "https://www.youtube.com/watch?v=VjctHUEmutw");


        // ================= COMEDY =================

        add(9, "Mr. Bean's Holiday",
                "Mr. Bean takes an unforgettable holiday.",
                Category.COMEDY, 2007, 6.4, 90,
                "https://www.youtube.com/watch?v=LZfIzJ6XwPQ");

        add(10, "Home Alone",
                "A young boy protects his home during Christmas.",
                Category.COMEDY, 1990, 7.7, 103,
                "https://www.youtube.com/watch?v=jEDaVHmw7r4");

        add(11, "The Mask",
                "A mysterious mask transforms an ordinary man.",
                Category.COMEDY, 1994, 6.9, 101,
                "https://www.youtube.com/watch?v=Ic7rUX6pcmA");

        add(12, "Jumanji: Welcome to the Jungle",
                "Friends discover a dangerous video game.",
                Category.COMEDY, 2017, 6.9, 119,
                "https://www.youtube.com/watch?v=2QKg5SZ_35I");

        add(13, "Free Guy",
                "A video game character discovers his world is not real.",
                Category.COMEDY, 2021, 7.1, 115,
                "https://www.youtube.com/watch?v=X2m-08cOAbc");

        add(14, "The Intern",
                "An older intern joins a modern company.",
                Category.COMEDY, 2015, 7.1, 121,
                "https://www.youtube.com/watch?v=ZU3Xban0Y6A");

        add(15, "Night at the Museum",
                "Museum exhibits come alive at night.",
                Category.COMEDY, 2006, 6.5, 108,
                "https://www.youtube.com/watch?v=uM9qgwB2atU");

        add(16, "Central Intelligence",
                "An accountant reunites with an old school friend.",
                Category.COMEDY, 2016, 6.3, 107,
                "https://www.youtube.com/watch?v=MxEw3elSJ8M");


        // ================= HORROR =================

        add(17, "The Conjuring",
                "Investigators encounter a frightening mystery.",
                Category.HORROR, 2013, 7.5, 112,
                "https://www.youtube.com/watch?v=k10ETZ41q5o");

        add(18, "A Quiet Place",
                "A family must remain silent to survive.",
                Category.HORROR, 2018, 7.5, 90,
                "https://www.youtube.com/watch?v=WR7cc5t7tv8");

        add(19, "Insidious",
                "A family faces unexplained supernatural events.",
                Category.HORROR, 2010, 6.8, 103,
                "https://www.youtube.com/watch?v=5Z29IDZRYw0");

        add(20, "It",
                "A group of children face a mysterious evil.",
                Category.HORROR, 2017, 7.3, 135,
                "https://www.youtube.com/watch?v=xKJmEC5ieOk");

        add(21, "The Nun",
                "A mysterious presence haunts an old abbey.",
                Category.HORROR, 2018, 5.3, 96,
                "https://www.youtube.com/watch?v=pzD9zGcUNrw");

        add(22, "Get Out",
                "A weekend visit becomes increasingly disturbing.",
                Category.HORROR, 2017, 7.8, 104,
                "https://www.youtube.com/watch?v=AHEl7Pji0f8");

        add(23, "Smile",
                "A doctor experiences a series of disturbing events.",
                Category.HORROR, 2022, 6.5, 115,
                "https://www.youtube.com/watch?v=BcDK7lkzzsU");

        add(24, "Annabelle",
                "A mysterious doll brings strange events.",
                Category.HORROR, 2014, 5.4, 99,
                "https://www.youtube.com/watch?v=paFgQNPGlsg");


        // ================= SCIENCE FICTION =================

        add(25, "Interstellar",
                "Explorers travel through space to find a new home.",
                Category.SCIENCE_FICTION, 2014, 8.7, 169,
                "https://www.youtube.com/watch?v=zSWdZVtXT7E");

        add(26, "Inception",
                "A specialist enters people's dreams.",
                Category.SCIENCE_FICTION, 2010, 8.8, 148,
                "https://www.youtube.com/watch?v=ig1GrJI5dSg");

        add(27, "The Matrix",
                "A computer programmer discovers a hidden reality.",
                Category.SCIENCE_FICTION, 1999, 8.7, 136,
                "https://www.youtube.com/watch?v=vKQi3bBA1y8");

        add(28, "Avatar",
                "A journey to an extraordinary alien world.",
                Category.SCIENCE_FICTION, 2009, 7.9, 162,
                "https://www.youtube.com/watch?v=5PSNL1qE6VY");

        add(29, "Dune",
                "A young man faces the destiny of a desert planet.",
                Category.SCIENCE_FICTION, 2021, 8.0, 155,
                "https://www.youtube.com/watch?v=n9xhJrPXop4");

        add(30, "The Martian",
                "An astronaut tries to survive on Mars.",
                Category.SCIENCE_FICTION, 2015, 8.0, 144,
                "https://www.youtube.com/watch?v=ej3ioOneTy8");

        add(31, "Ready Player One",
                "A teenager explores a massive virtual world.",
                Category.SCIENCE_FICTION, 2018, 7.4, 140,
                "https://www.youtube.com/watch?v=cSp1dM2Vj48");

        add(32, "Edge of Tomorrow",
                "A soldier experiences the same battle repeatedly.",
                Category.SCIENCE_FICTION, 2014, 7.9, 113,
                "https://www.youtube.com/watch?v=YEM3H6s2xB4");


        // ================= ANIMATION =================

        add(33, "The Lion King",
                "A young lion discovers his destiny.",
                Category.ANIMATION, 1994, 8.5, 88,
                "https://www.youtube.com/watch?v=eHcZlPpNt0Q");

        add(34, "Finding Nemo",
                "A father searches the ocean for his son.",
                Category.ANIMATION, 2003, 8.2, 100,
                "https://www.youtube.com/watch?v=2zLkasScy7A");

        add(35, "Toy Story",
                "Toys come alive when humans are away.",
                Category.ANIMATION, 1995, 8.3, 81,
                "https://www.youtube.com/watch?v=v-PjgYDrg70");

        add(36, "Frozen",
                "Two sisters discover the meaning of family.",
                Category.ANIMATION, 2013, 7.4, 102,
                "https://www.youtube.com/watch?v=TbQm5doF_Uc");

        add(37, "Coco",
                "A boy discovers the importance of family and music.",
                Category.ANIMATION, 2017, 8.4, 105,
                "https://www.youtube.com/watch?v=Rvr68u6k5sI");

        add(38, "Shrek",
                "An ogre embarks on an unexpected adventure.",
                Category.ANIMATION, 2001, 7.9, 90,
                "https://youtu.be/P6antjcBFZ4?si=gFPBHiuJhzW_L0PH");

        add(39, "Inside Out",
                "Emotions guide a young girl's experiences.",
                Category.ANIMATION, 2015, 8.1, 95,
                "https://www.youtube.com/watch?v=yRUAzGQ3nSY");

        add(40, "Kung Fu Panda",
                "An unlikely hero discovers his potential.",
                Category.ANIMATION, 2008, 7.6, 92,
                "https://www.youtube.com/watch?v=PXi3Mv6KMzY");
    }


    // =========================================
    // Each movie stores a direct YouTube watch-page URL. Add verified URLs here for the remaining movies.
    // =========================================




    // =========================================
    // CREATE MOVIE
    // =========================================

    private static void add(
            int id,
            String title,
            String description,
            Category category,
            int year,
            double rating,
            int duration,
            String youtubeUrl) {

        String posterUrl = getLocalPoster(id);

        movies.add(new Movie(
                id,
                title,
                description,
                category,
                year,
                rating,
                duration,
                posterUrl,
                youtubeUrl
        ));
    }


    // =========================================
    // GET LOCAL POSTER
    // =========================================

    private static String getLocalPoster(int id) {

        File poster = new File(
                "resources/images/posters/movie-" + id + ".jpg"
        );

        if (poster.exists() && poster.isFile()) {
            return poster.toURI().toString();
        }

        System.out.println("Poster not found: " + poster.getPath());

        return "";
    }


    // =========================================
    // RETURN ALL MOVIES
    // =========================================

    public static List<Movie> getAllMovies() {
        return Collections.unmodifiableList(movies);
    }


    // =========================================
    // RETURN MOVIES BY CATEGORY
    // =========================================

    public static List<Movie> getMoviesByCategory(Category category) {

        return movies.stream()
                .filter(movie -> movie.getCategory() == category)
                .collect(Collectors.toList());
    }


    // =========================================
    // SEARCH MOVIES
    // =========================================

    public static List<Movie> searchMovies(String keyword) {

        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String search = keyword.toLowerCase().trim();

        return movies.stream()
                .filter(movie -> movie.getTitle()
                        .toLowerCase()
                        .contains(search))
                .collect(Collectors.toList());
    }


    // =========================================
    // FIND MOVIE BY ID
    // =========================================

    public static Movie getMovieById(int id) {

        return movies.stream()
                .filter(movie -> movie.getId() == id)
                .findFirst()
                .orElse(null);
    }


    // =========================================
    // TOTAL MOVIES
    // =========================================

    public static int getMovieCount() {
        return movies.size();
    }
}
