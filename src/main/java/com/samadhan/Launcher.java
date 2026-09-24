package com.samadhan;

import com.samadhan.ui.App;

/**
 * Main launcher entry point that does not directly extend javafx.application.Application.
 * This allows execution via standard java -jar or fat JAR without module path constraints.
 */
public class Launcher {
    public static void main(String[] args) {
        App.main(args);
    }
}
