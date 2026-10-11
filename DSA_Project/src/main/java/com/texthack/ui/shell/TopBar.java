package com.texthack.ui.shell;

import com.texthack.ui.theme.ThemeManager;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Top bar header with app branding, quick-jump search bar, and Dark/Light theme toggle.
 */
public class TopBar extends HBox {
    private final Router router;

    public TopBar(Router router) {
        super(16);
        this.router = router;

        getStyleClass().add("top-bar");
        setAlignment(Pos.CENTER_LEFT);

        // App Branding
        VBox titleBox = new VBox(2);
        Label title = new Label("TextHack – Advanced Algorithm Question Bank");
        title.getStyleClass().add("top-bar-title");

        Label sub = new Label("Data Structures and Algorithms – 3 • 100% Offline Workbench");
        sub.getStyleClass().add("top-bar-sub");
        titleBox.getChildren().addAll(title, sub);

        // Quick Jump Search Field
        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Jump to any algorithm (KMP, Z-func, Aho-Corasick, Suffix Array)...");
        searchField.getStyleClass().add("search-field");
        searchField.setOnAction(e -> handleSearch(searchField.getText()));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Theme Toggle Button
        Button themeBtn = new Button("🌙 Dark Mode");
        themeBtn.getStyleClass().add("secondary-btn");
        themeBtn.setTooltip(new Tooltip("Toggle Dark / Light Theme"));
        themeBtn.setOnAction(e -> {
            if (getScene() != null) {
                ThemeManager.toggleTheme(getScene());
                boolean isDark = ThemeManager.getCurrentTheme() == ThemeManager.Theme.DARK;
                themeBtn.setText(isDark ? "🌙 Dark Mode" : "☀️ Light Mode");
            }
        });

        getChildren().addAll(titleBox, searchField, spacer, themeBtn);
    }

    private void handleSearch(String query) {
        if (query == null) return;
        String q = query.trim().toLowerCase();

        if (q.contains("naive") || q.contains("kmp") || q.contains("z") ||
            q.contains("rabin") || q.contains("aho") || q.contains("suffix") ||
            q.contains("lcp") || q.contains("kasai") || q.contains("automaton") || q.contains("tree")) {
            router.navigateTo(Router.Route.MODULE2_STRINGS);
        } else if (q.contains("dp") || q.contains("edit") || q.contains("matrix") || q.contains("tsp")) {
            router.navigateTo(Router.Route.MODULE3_DP);
        } else if (q.contains("flow") || q.contains("karp") || q.contains("cut")) {
            router.navigateTo(Router.Route.MODULE4_FLOW);
        } else if (q.contains("np") || q.contains("sat") || q.contains("approx")) {
            router.navigateTo(Router.Route.MODULE5_NP);
        } else if (q.contains("random") || q.contains("quicksort") || q.contains("rabin-miller") || q.contains("hash")) {
            router.navigateTo(Router.Route.MODULE6_RANDOMIZED);
        } else if (q.contains("comp") || q.contains("bench")) {
            router.navigateTo(Router.Route.COMPARISON_HUB);
        } else if (q.contains("corp") || q.contains("hindi") || q.contains("telugu")) {
            router.navigateTo(Router.Route.CORPUS_EXPLORER);
        } else if (q.contains("canon") || q.contains("clrs")) {
            router.navigateTo(Router.Route.ALGORITHM_CANON);
        } else if (q.contains("quiz") || q.contains("design")) {
            router.navigateTo(Router.Route.ALGORITHM_DESIGN);
        } else {
            router.navigateTo(Router.Route.DASHBOARD);
        }
    }
}
