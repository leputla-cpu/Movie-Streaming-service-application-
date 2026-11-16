# Watch Z - Movie Streaming Demo

Watch Z is a JavaFX movie-catalogue and trailer application built for the Interactive Multimedia group assignment.

## Features

- Five movie genres: Action, Comedy, Horror, Science Fiction and Animation.
- Eight movie entries per genre (40 movies total), with local poster images.
- Home page with a featured movie and horizontally scrolling genre rows.
- A dedicated Movies page showing the complete movie grid only.
- A Genres page showing category-specific collections. Select a genre heading from a Home row to open that genre on its own.
- Search across movie titles, descriptions and genre names.
- My List with Add/Remove controls on the movie details page (saved for the current application session).
- Movie details and embedded YouTube trailer playback.
- Play, Pause and Stop trailer controls, plus a custom animated loading indicator.
- Shared left sidebar navigation that remains available on every application screen.
- Reusable hover and page-transition animations.
- Optional click and hover sound effects.
- Custom clickable controls built with JavaFX layout and label nodes; the interface does not use JavaFX `Button` controls.

## Project structure

```text
MovieStreaming/
├── .vscode/                 VS Code launch and project settings
├── resources/
│   ├── css/style.css        External application styling
│   ├── images/posters/      Local movie poster assets
│   └── sounds/              Click and hover WAV effects
├── src/
│   ├── animations/          Hover, loading and page transition animations
│   ├── components/          Shared application shell, cards and custom controls
│   ├── data/                Movie catalogue data
│   ├── models/              Movie and Category models
│   ├── screens/             Home, Movies, Genres, Search, My List, Details and Player
│   ├── services/            Navigation and YouTube playback
│   ├── utils/               Image and sound helpers
│   └── App.java              JavaFX application entry point
└── README.md
```

## Requirements

- JDK 21 or later.
- JavaFX SDK compatible with the installed JDK.
- JavaFX modules: `javafx.controls`, `javafx.fxml`, `javafx.web` and `javafx.media`.
- Internet access for YouTube trailer playback.

## Run in VS Code

1. Open the `MovieStreaming` folder (the folder containing `src`, `resources` and `.vscode`).
2. Confirm the JavaFX SDK path in `.vscode/settings.json` and `.vscode/launch.json` matches the SDK installed on your computer.
3. The launch configuration includes the JavaFX `controls`, `fxml`, `web` and `media` modules.
4. Run the `App` launch configuration.
5. Run the application with `MovieStreaming` as the working directory so the relative `resources/` paths resolve correctly.

The application loads its stylesheet from `resources/css/style.css` and movie posters from `resources/images/posters/`.

## Assignment rubric coverage

| Rubric area | Implementation |
|---|---|
| Genre categories | Five categories, each containing eight movie records |
| Movie thumbnails and titles | Poster cards with titles |
| Horizontal scrolling | Each Home genre row displays five poster cards in its viewport and scrolls to the remaining cards |
| Hover effects | Reusable scale animation on movie cards and hover styling on navigation/custom controls |
| Trailer playback | Embedded YouTube player in the application using JavaFX WebView |
| Playback controls | Custom Play, Pause and Stop controls |
| Loading animation | Animated three-dot indicator over the trailer view |
| Sound | Short click and hover WAV effects through `SoundManager` |
| Object-oriented design | Separate models, data, screens, components, services, animations and utilities |
| External styling | Shared CSS stylesheet in `resources/css/style.css` |
| Navigation | Persistent sidebar routes to distinct application screens |

## Trailer links and navigation
Each movie stores a direct YouTube embed URL in its `Movie` record. The player does not search YouTube. To assign a trailer, set the movie's trailer value in `MovieData.java` to an 11-character YouTube video ID or a full `https://www.youtube.com/embed/VIDEO_ID` URL. The featured banner uses a muted preview when an embed URL is assigned; **Play Trailer** opens the full player. A shared **Back** control returns to the previous screen.

