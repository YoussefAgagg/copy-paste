package com.github.youssefagagg;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * A JavaFX clipboard manager that keeps a running history of copied text
 * and lets the user pin frequently used snippets for quick access.
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Automatic clipboard monitoring (~100 ms polling)</li>
 *   <li>Pinned items persisted between sessions</li>
 *   <li>One-click copy back to system clipboard</li>
 *   <li>Keyboard shortcut (Ctrl+C) support for both lists</li>
 * </ul>
 */
public class CopyPasteApp extends Application {

    private final ObservableList<String> historyItems = FXCollections.observableArrayList();
    private final ObservableList<String> pinnedItems = FXCollections.observableArrayList();

    private ListView<String> historyList;
    private ListView<String> pinnedList;

    private ClipboardMonitorService clipboardMonitor;

    // ── Application lifecycle ───────────────────────────────────────────

    @Override
    public void start(Stage primaryStage) {
        pinnedItems.addAll(PinnedMessageStore.load());

        historyList = createHistoryListView();
        pinnedList = createPinnedListView();

        setupCrossSelectionClearing();

        SplitPane splitPane = buildSplitPane();
        Scene scene = new Scene(splitPane, 900, 500);
        applyDarkTheme(scene);

        primaryStage.setTitle("Copy-Paste");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event -> shutdown());
        primaryStage.show();

        clipboardMonitor = new ClipboardMonitorService(historyItems);
        clipboardMonitor.start();
    }

    @Override
    public void stop() {
        shutdown();
    }

    // ── UI construction ─────────────────────────────────────────────────

    private SplitPane buildSplitPane() {
        VBox pinnedPane = buildPinnedPane();
        VBox historyPane = buildHistoryPane();

        SplitPane splitPane = new SplitPane(pinnedPane, historyPane);
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.setDividerPositions(0.45);
        SplitPane.setResizableWithParent(pinnedPane, true);
        SplitPane.setResizableWithParent(historyPane, true);
        splitPane.getStyleClass().add("split-pane");
        return splitPane;
    }

    private VBox buildPinnedPane() {
        Label title = new Label("Pinned Text");
        title.getStyleClass().add("section-title");
        title.getStyleClass().add("pinned-title");

        VBox.setVgrow(pinnedList, Priority.ALWAYS);
        VBox pane = new VBox(8, title, pinnedList);
        pane.setPadding(new Insets(8));
        pane.getStyleClass().add("pane-background");
        return pane;
    }

    private VBox buildHistoryPane() {
        Label title = new Label("Copy History");
        title.getStyleClass().add("section-title");

        HBox buttons = createButtonBar();

        VBox.setVgrow(historyList, Priority.ALWAYS);
        VBox pane = new VBox(8, title, buttons, historyList);
        pane.setPadding(new Insets(8));
        pane.getStyleClass().add("pane-background");
        return pane;
    }

    private HBox createButtonBar() {
        Button copyBtn = createButton("Copy", "Copy selected item to system clipboard");
        Button removeBtn = createButton("Remove", "Remove selected item from history");
        Button removeAllBtn = createButton("Remove All", "Clear entire clipboard history");
        Button pinBtn = createButton("Pin", "Pin selected item for quick access");
        Button unpinBtn = createButton("Unpin", "Move selected pinned item back to history");

        pinBtn.getStyleClass().add("pin-button");
        unpinBtn.getStyleClass().add("unpin-button");

        copyBtn.setOnAction(event -> handleCopy());
        removeBtn.setOnAction(event -> handleRemove());
        removeAllBtn.setOnAction(event -> historyItems.clear());
        pinBtn.setOnAction(event -> handlePin());
        unpinBtn.setOnAction(event -> handleUnpin());

        HBox bar = new HBox(8, copyBtn, removeBtn, removeAllBtn, pinBtn, unpinBtn);
        bar.setPadding(new Insets(4, 0, 4, 0));
        return bar;
    }

    private Button createButton(String text, String tooltipText) {
        Button button = new Button(text);
        button.setTooltip(new Tooltip(tooltipText));
        button.setFocusTraversable(false);
        return button;
    }

    // ── ListView setup ──────────────────────────────────────────────────

    private ListView<String> createHistoryListView() {
        ListView<String> listView = new ListView<>(historyItems);
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listView.setCellFactory(lv -> new ClipboardCell());
        listView.setFixedCellSize(50);
        listView.getStyleClass().add("history-list");

        listView.setOnKeyPressed(event -> {
            if (event.isShortcutDown() && event.getCode() == KeyCode.C) {
                copySelectedFromList(listView, historyItems);
            }
        });

        return listView;
    }

    private ListView<String> createPinnedListView() {
        ListView<String> listView = new ListView<>(pinnedItems);
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listView.setCellFactory(lv -> new ClipboardCell());
        listView.setFixedCellSize(30);
        listView.getStyleClass().add("pinned-list");

        listView.setOnKeyPressed(event -> {
            if (event.isShortcutDown() && event.getCode() == KeyCode.C) {
                copySelectedFromList(listView, pinnedItems);
            }
        });

        return listView;
    }

    /**
     * Ensures that selecting an item in one list clears the selection
     * in the other list, providing mutually exclusive selection behavior.
     */
    private void setupCrossSelectionClearing() {
        historyList.getSelectionModel().selectedIndexProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal.intValue() != -1) {
                        pinnedList.getSelectionModel().clearSelection();
                    }
                });

        pinnedList.getSelectionModel().selectedIndexProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal.intValue() != -1) {
                        historyList.getSelectionModel().clearSelection();
                    }
                });
    }

    // ── Action handlers ─────────────────────────────────────────────────

    private void handleCopy() {
        String selected = getSelectedFromEitherList();
        if (selected != null) {
            copyToSystemClipboard(selected);
        }
    }

    private void handleRemove() {
        int index = historyList.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            historyItems.remove(index);
        }
    }

    private void handlePin() {
        int index = historyList.getSelectionModel().getSelectedIndex();
        if (index == -1) {
            return;
        }
        String text = historyItems.get(index);
        if (!pinnedItems.contains(text)) {
            pinnedItems.add(text);
            historyItems.remove(index);
            savePinnedMessages();
        }
    }

    private void handleUnpin() {
        int index = pinnedList.getSelectionModel().getSelectedIndex();
        if (index == -1) {
            return;
        }
        String text = pinnedItems.get(index);
        pinnedItems.remove(index);
        historyItems.add(0, text);
        savePinnedMessages();
    }

    // ── Clipboard helpers ───────────────────────────────────────────────

    private void copySelectedFromList(ListView<String> listView, ObservableList<String> items) {
        int index = listView.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            copyToSystemClipboard(items.get(index));
        }
    }

    private void copyToSystemClipboard(String text) {
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        Clipboard.getSystemClipboard().setContent(content);
        if (clipboardMonitor != null) {
            clipboardMonitor.setLastCopied(text);
        }
    }

    /**
     * Returns the selected text from whichever list currently has a selection.
     */
    private String getSelectedFromEitherList() {
        int historyIndex = historyList.getSelectionModel().getSelectedIndex();
        if (historyIndex != -1) {
            return historyItems.get(historyIndex);
        }
        int pinnedIndex = pinnedList.getSelectionModel().getSelectedIndex();
        if (pinnedIndex != -1) {
            return pinnedItems.get(pinnedIndex);
        }
        return null;
    }

    // ── Persistence ─────────────────────────────────────────────────────

    private void savePinnedMessages() {
        PinnedMessageStore.save(new ArrayList<>(pinnedItems));
    }

    private void shutdown() {
        if (clipboardMonitor != null) {
            clipboardMonitor.stop();
        }
        savePinnedMessages();
    }

    // ── Theming ─────────────────────────────────────────────────────────

    private void applyDarkTheme(Scene scene) {
        String css = getClass().getResource("dark-theme.css").toExternalForm();
        scene.getStylesheets().add(css);
    }

    // ── Custom list cell ────────────────────────────────────────────────

    /**
     * A simple cell that truncates long text to a single line for display.
     */
    private static class ClipboardCell extends ListCell<String> {
        private static final int MAX_DISPLAY_LENGTH = 120;

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
            } else {
                String singleLine = item.replace("\n", " ").replace("\r", "");
                if (singleLine.length() > MAX_DISPLAY_LENGTH) {
                    setText(singleLine.substring(0, MAX_DISPLAY_LENGTH) + "…");
                } else {
                    setText(singleLine);
                }
                setTooltip(new Tooltip(item));
            }
        }
    }

    // ── Entry point ─────────────────────────────────────────────────────

    public static void main(String[] args) {
        launch(args);
    }
}
