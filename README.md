# Copy-Paste

A lightweight Java Swing clipboard manager that keeps a running history of text you copy and lets you pin frequently used snippets for quick access. The app runs locally, uses a simple dark UI, and persists pinned items between sessions.

## Features
- Clipboard history: Automatically tracks text copied to your system clipboard.
- Pinned items: Pin favorite snippets to a dedicated list; pinned items persist between runs.
- One‑click copy: Copy any selected entry back to the clipboard via the Copy button or Ctrl+C.
- Manage history: Remove selected entries or clear the entire history.
- Dual lists with exclusive selection: “Pinned Text” (left) and “Copy History” (right).
- Keyboard shortcuts: Ctrl+C copies the currently selected item (from either list).

## How it works
- A background thread polls the system clipboard roughly every 100 ms and adds new textual content to the top of the Copy History.
- When you copy an item from the app (button or Ctrl+C), it won’t be re‑added immediately to the history thanks to an internal flag.
- Pinned items are stored locally in your home directory so they’re available the next time you open the app.

Pinned storage path (created automatically on first use):
- macOS/Linux: `~/copy-past/.pinedcopies/pinned_messages.txt`
- Windows: `C:\Users\<you>\copy-past\.pinedcopies\pinned_messages.txt`

Note: The directory and file names are intentionally spelled as above to match the application’s current settings.

## Requirements
- Java 8 or newer (Java 11+ recommended)
- Gradle Wrapper is included; you don’t need a local Gradle installation.

## Build and Run
Using the Gradle Wrapper (recommended):
- macOS/Linux:
  - Run: `./gradlew run`
  - Build JAR: `./gradlew jar`
- Windows (PowerShell/CMD):
  - Run: `gradlew.bat run`
  - Build JAR: `gradlew.bat jar`

Runnable JAR will be at:
- `build/libs/copy-paste-1.0-SNAPSHOT.jar`

Run the JAR directly:
- `java -jar build/libs/copy-paste-1.0-SNAPSHOT.jar`

Distributions via the Application plugin:
- Install local distribution: `./gradlew installDist`
- Then run:
  - macOS/Linux: `build/install/copy-paste/bin/copy-paste`
  - Windows: `build\install\copy-paste\bin\copy-paste.bat`

## Usage
- Launch the app. The window shows:
  - Left: Pinned Text
  - Right: Copy History plus action buttons
- Copy any text in your system; it will appear at the top of Copy History.
- To reuse an entry:
  - Select it, then click Copy or press Ctrl+C. The text is placed on the system clipboard.
- To pin a frequently used entry:
  - Select an item in Copy History and click Pin. It moves to Pinned Text and will persist between runs.
- To unpin:
  - Select a pinned item and click Unpin. It moves back to the top of Copy History.
- To tidy up:
  - Remove: deletes the selected history entry.
  - Remove All: clears the entire history list.

## Notes and limitations
- Clipboard monitoring is text‑only.
- Polling every ~100 ms is simple and reliable but may use a small amount of CPU in the background.
- Pinned items are stored locally in plain text with escaped newlines. To reset pinned items, close the app and delete the file at the path above.
- The UI attempts to use Nimbus Look & Feel if available; otherwise the platform default is used.

## Project structure
- `src/main/java/com/github/youssefagagg/Main.java` — Application entry point; boots the Swing UI.
- `src/main/java/com/github/youssefagagg/CopyPasteFrame.java` — Main panel containing both lists, buttons, clipboard monitor, persistence, and rendering.
- `build.gradle` — Java + Application plugins with `mainClassName = com.github.youssefagagg.Main`.

## Development
- Java version: compatible with Java 8+.

## Troubleshooting
- No entries appear: Ensure you’re copying text (not images or files) and that Java can access the system clipboard (some remote or headless environments restrict it).
- Wayland/Linux nuances: Depending on desktop/clipboard managers, behavior may vary; try running under XWayland or ensure your environment grants clipboard access to Java/Swing apps.
- Permissions: If the app can’t create the pinned storage directory, check write permissions for your user home directory.

