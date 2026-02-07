# Copy-Paste

A lightweight JavaFX clipboard manager that keeps a running history of text you copy and lets you pin frequently used snippets for quick access. The app runs locally, uses a dark themed UI, persists pinned items between sessions, and supports GraalVM native-image compilation for instant startup.

## Features
- **Clipboard history** – Automatically tracks text copied to your system clipboard.
- **Pinned items** – Pin favorite snippets to a dedicated list; pinned items persist between runs.
- **One-click copy** – Copy any selected entry back to the clipboard via the Copy button or Ctrl+C (⌘+C on macOS).
- **Manage history** – Remove selected entries or clear the entire history.
- **Dual lists with exclusive selection** – "Pinned Text" (left) and "Copy History" (right).
- **Keyboard shortcuts** – Ctrl+C / ⌘+C copies the currently selected item (from either list).
- **GraalVM native image** – Build a self-contained native executable with instant startup.

## How it works
- A background daemon thread polls the system clipboard roughly every 100 ms and adds new textual content to the top of the Copy History.
- When you copy an item from the app (button or keyboard shortcut), it won't be re-added to the history thanks to an internal flag.
- Pinned items are stored locally in your home directory so they're available the next time you open the app.

Pinned storage path (created automatically on first use):
- macOS/Linux: `~/copy-past/.pinedcopies/pinned_messages.txt`
- Windows: `C:\Users\<you>\copy-past\.pinedcopies\pinned_messages.txt`

Note: The directory and file names are intentionally spelled as above to match the application's current settings.

## Requirements
- Java 21 or newer (GraalVM 21+ recommended for native-image builds)
- Gradle Wrapper is included; you don't need a local Gradle installation.

## Build and Run

### Run on JVM (recommended for development)
```bash
# macOS/Linux
./gradlew run

# Windows
gradlew.bat run
```

### Build JAR
```bash
./gradlew jar
```
Runnable JAR will be at: `build/libs/copy-paste-1.0-SNAPSHOT.jar`

### Build GraalVM Native Image
Requires [GraalVM 21+](https://www.graalvm.org/) with `native-image` installed.

```bash
# Step 1 – Run the tracing agent to collect native-image metadata
./gradlew nativeRunAgent

# Step 2 – Compile to a native executable
./gradlew nativeCompile

# Step 3 - Link the native executable
./gradlew nativeLink
```

The native executable will be generated in the `build/gluonfx/` directory.

```bash
# Run the native executable (macOS/Linux)
./build/gluonfx/*/copy-paste
```

### Distributions via the Application plugin
```bash
./gradlew installDist

# macOS/Linux
build/install/copy-paste/bin/copy-paste

# Windows
build\install\copy-paste\bin\copy-paste.bat
```

## Usage
- Launch the app. The window shows:
  - Left: Pinned Text
  - Right: Copy History plus action buttons
- Copy any text in your system; it will appear at the top of Copy History.
- To reuse an entry:
  - Select it, then click **Copy** or press Ctrl+C / ⌘+C. The text is placed on the system clipboard.
- To pin a frequently used entry:
  - Select an item in Copy History and click **Pin**. It moves to Pinned Text and will persist between runs.
- To unpin:
  - Select a pinned item and click **Unpin**. It moves back to the top of Copy History.
- To tidy up:
  - **Remove**: deletes the selected history entry.
  - **Remove All**: clears the entire history list.

## Notes and Limitations
- Clipboard monitoring is text-only.
- Polling every ~100 ms is simple and reliable but may use a small amount of CPU in the background.
- Pinned items are stored locally in plain text with escaped newlines. To reset pinned items, close the app and delete the file at the path above.

## Project Structure
```
src/main/java/com/github/youssefagagg/
├── CopyPasteApp.java            – JavaFX Application entry point and UI
├── ClipboardMonitorService.java – Background clipboard polling service
└── PinnedMessageStore.java      – Pinned messages file persistence

src/main/resources/
├── com/github/youssefagagg/
│   └── dark-theme.css           – Dark theme stylesheet
└── META-INF/native-image/       – GraalVM native-image configuration

build.gradle                     – JavaFX + GluonFX (GraalVM) plugin configuration
```

## Development
- Java version: 21+
- UI toolkit: JavaFX 21
- Native compilation: GluonFX Gradle plugin (GraalVM native-image)

## Troubleshooting
- **No entries appear**: Ensure you're copying text (not images or files) and that Java can access the system clipboard.
- **Wayland/Linux nuances**: Depending on desktop/clipboard managers, behavior may vary; try running under XWayland.
- **Permissions**: If the app can't create the pinned storage directory, check write permissions for your user home directory.
- **Native image build fails**: Make sure `GRAALVM_HOME` is set and `native-image` is on your PATH. Run `./gradlew nativeRunAgent` first to collect metadata.
