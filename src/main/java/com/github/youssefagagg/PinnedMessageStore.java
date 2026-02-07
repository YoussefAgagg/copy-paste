package com.github.youssefagagg;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Handles persistence of pinned clipboard messages.
 * Messages are stored in a plain-text file under the user's home directory,
 * separated by a unique delimiter to support multi-line content.
 */
public final class PinnedMessageStore {

    private static final String STORAGE_DIR =
            System.getProperty("user.home") + File.separator + "copy-past" + File.separator + ".pinedcopies";
    private static final String STORAGE_FILE = STORAGE_DIR + File.separator + "pinned_messages.txt";
    private static final String SEPARATOR = "\n###PINNED_MESSAGE_SEPARATOR###\n";

    private PinnedMessageStore() {
        // utility class
    }

    /**
     * Saves the given list of pinned messages to persistent storage.
     *
     * @param messages the messages to persist
     */
    public static void save(List<String> messages) {
        try {
            Path dirPath = Paths.get(STORAGE_DIR);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            StringBuilder content = new StringBuilder();
            for (int i = 0; i < messages.size(); i++) {
                String escaped = messages.get(i)
                        .replace("\n", "\\n")
                        .replace("\r", "\\r");
                content.append(escaped);
                if (i < messages.size() - 1) {
                    content.append(SEPARATOR);
                }
            }

            Files.write(Paths.get(STORAGE_FILE), content.toString().getBytes());
        } catch (IOException e) {
            System.err.println("Error saving pinned messages: " + e.getMessage());
        }
    }

    /**
     * Loads previously saved pinned messages from persistent storage.
     *
     * @return an unmodifiable list of pinned messages, or an empty list if none exist
     */
    public static List<String> load() {
        try {
            Path filePath = Paths.get(STORAGE_FILE);
            if (!Files.exists(filePath)) {
                return Collections.emptyList();
            }

            String content = new String(Files.readAllBytes(filePath));
            if (content.trim().isEmpty()) {
                return Collections.emptyList();
            }

            String[] parts = content.split("\n###PINNED_MESSAGE_SEPARATOR###\n");

            List<String> messages = new ArrayList<>();
            for (String part : parts) {
                if (part != null && !part.trim().isEmpty()) {
                    String restored = part
                            .replace("\\n", "\n")
                            .replace("\\r", "\r");
                    messages.add(restored);
                }
            }
            return Collections.unmodifiableList(messages);
        } catch (IOException e) {
            System.err.println("Error loading pinned messages: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
