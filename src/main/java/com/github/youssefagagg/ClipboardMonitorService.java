
package com.github.youssefagagg;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.input.Clipboard;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Background service that polls the system clipboard at a fixed interval
 * and adds new text entries to the supplied observable list.
 *
 * <p>The monitor skips entries that match the last internally-copied value
 * (set via {@link #setLastCopied(String)}) to prevent the app's own
 * copy operations from being re-added to the history.</p>
 */
public final class ClipboardMonitorService {

    private static final long POLL_INTERVAL_MS = 100;

    private final ObservableList<String> historyItems;
    private volatile String lastCopied;
    private volatile boolean running = true;

    /**
     * Creates a new monitor that appends detected clipboard text to {@code historyItems}.
     *
     * @param historyItems the observable list that drives the history view
     */
    public ClipboardMonitorService(ObservableList<String> historyItems) {
        this.historyItems = historyItems;
    }

    /**
     * Sets the "flag" value so the next clipboard read matching this text is ignored.
     *
     * @param text the text that was just copied from within the app
     */
    public void setLastCopied(String text) {
        this.lastCopied = text;
    }

    /**
     * Stops the polling loop.
     */
    public void stop() {
        running = false;
    }

    /**
     * Starts polling the system clipboard on a daemon thread.
     * New text entries are pushed to the JavaFX application thread.
     */
    public void start() {
        Thread thread = new Thread(() -> {
            String previous = getClipboardText();
            while (running) {
                try {
                    Thread.sleep(POLL_INTERVAL_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

                String current = getClipboardText();
                if (current != null && !current.equals(previous) && !current.equals(lastCopied)) {
                    String captured = current;
                    Platform.runLater(() -> historyItems.add(0, captured));
                    previous = current;
                }
            }
        }, "clipboard-monitor");
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Reads the current text content from the system clipboard.
     * Uses the JavaFX clipboard via {@code Platform.runLater()} to avoid
     * the AWT dependency that is unavailable in GraalVM native images.
     *
     * @return the clipboard text, or {@code null} if unavailable
     */
    private String getClipboardText() {
        try {
            CompletableFuture<String> future = new CompletableFuture<>();
            Platform.runLater(() -> {
                try {
                    Clipboard clipboard = Clipboard.getSystemClipboard();
                    if (clipboard.hasString()) {
                        future.complete(clipboard.getString());
                    } else {
                        future.complete(null);
                    }
                } catch (Exception e) {
                    future.complete(null);
                }
            });
            return future.get(1, TimeUnit.SECONDS);
        } catch (Exception e) {
            return null;
        }
    }
}