package com.github.youssefagagg;

/**
 * Non-Application entry point that avoids the "JavaFX runtime components are missing"
 * error when running from the classpath (e.g. during the GraalVM native-image agent run).
 */
public class Launcher {
    public static void main(String[] args) {
        CopyPasteApp.main(args);
    }
}