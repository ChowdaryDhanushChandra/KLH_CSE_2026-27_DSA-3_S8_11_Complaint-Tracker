package com.texthack.ui.theme;

import javafx.scene.Scene;
import java.net.URL;

/**
 * Manages application themes (Dark / Light) and stylesheet injection.
 */
public class ThemeManager {
    public enum Theme {
        DARK("/css/dark.css"),
        LIGHT("/css/light.css");

        private final String cssPath;
        Theme(String cssPath) {
            this.cssPath = cssPath;
        }
        public String getCssPath() { return cssPath; }
    }

    private static Theme currentTheme = Theme.DARK;

    public static Theme getCurrentTheme() {
        return currentTheme;
    }

    public static void applyTheme(Scene scene, Theme theme) {
        if (scene == null || theme == null) return;
        currentTheme = theme;

        scene.getStylesheets().clear();
        URL url = ThemeManager.class.getResource(theme.getCssPath());
        if (url != null) {
            scene.getStylesheets().add(url.toExternalForm());
        }
    }

    public static void toggleTheme(Scene scene) {
        Theme next = (currentTheme == Theme.DARK) ? Theme.LIGHT : Theme.DARK;
        applyTheme(scene, next);
    }
}
