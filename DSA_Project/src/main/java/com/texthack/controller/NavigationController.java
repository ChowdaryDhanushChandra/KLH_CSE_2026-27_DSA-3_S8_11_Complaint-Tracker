package com.texthack.controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

/**
 * Navigation sidebar controller managing route selection and visual button active states.
 */
public class NavigationController {

    public enum ViewRoute {
        DASHBOARD,
        MODULE_1_SYSTEM,
        MODULE_2_STRINGS,
        MODULE_3_DP,
        ALGORITHM_COMPARISON,
        CORPUS_EXPLORER,
        ALGORITHM_CANON,
        ALGORITHM_DESIGN_QA,
        ABOUT_PROJECT
    }

    private final VBox sidebar;
    private final Consumer<ViewRoute> routeConsumer;
    private final Button[] navButtons = new Button[9];
    private ViewRoute currentRoute = ViewRoute.DASHBOARD;

    public NavigationController(Consumer<ViewRoute> routeConsumer) {
        this.routeConsumer = routeConsumer;
        this.sidebar = new VBox(6);
        this.sidebar.getStyleClass().add("nav-sidebar");

        Label navHead = new Label("NAVIGATION");
        navHead.getStyleClass().add("nav-section-title");
        sidebar.getChildren().add(navHead);

        navButtons[0] = createNavButton("🏠 Dashboard", ViewRoute.DASHBOARD);
        navButtons[1] = createNavButton("⚙ Module 1: System", ViewRoute.MODULE_1_SYSTEM);
        navButtons[2] = createNavButton("🔤 Module 2: Strings", ViewRoute.MODULE_2_STRINGS);
        navButtons[3] = createNavButton("⚡ Module 3: Adv. DP", ViewRoute.MODULE_3_DP);

        Label toolsHead = new Label("LABORATORY");
        toolsHead.getStyleClass().add("nav-section-title");
        toolsHead.setStyle("-fx-padding: 12px 10px 4px 10px;");

        navButtons[4] = createNavButton("📊 Comparison Hub", ViewRoute.ALGORITHM_COMPARISON);
        navButtons[5] = createNavButton("📚 Corpus Explorer", ViewRoute.CORPUS_EXPLORER);

        Label canonHead = new Label("THEORY & CANON");
        canonHead.getStyleClass().add("nav-section-title");
        canonHead.setStyle("-fx-padding: 12px 10px 4px 10px;");

        navButtons[6] = createNavButton("📖 Algorithm Canon", ViewRoute.ALGORITHM_CANON);
        navButtons[7] = createNavButton("🎯 Algorithm Design", ViewRoute.ALGORITHM_DESIGN_QA);
        navButtons[8] = createNavButton("ℹ About TextHack", ViewRoute.ABOUT_PROJECT);

        sidebar.getChildren().addAll(
                navButtons[0], navButtons[1], navButtons[2], navButtons[3],
                toolsHead, navButtons[4], navButtons[5],
                canonHead, navButtons[6], navButtons[7], navButtons[8]
        );

        selectRoute(ViewRoute.DASHBOARD);
    }

    private Button createNavButton(String title, ViewRoute route) {
        Button btn = new Button(title);
        btn.getStyleClass().add("nav-button");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setOnAction(e -> selectRoute(route));
        return btn;
    }

    public void selectRoute(ViewRoute route) {
        this.currentRoute = route;
        for (int i = 0; i < navButtons.length; i++) {
            if (navButtons[i] != null) {
                navButtons[i].getStyleClass().remove("nav-button-selected");
            }
        }

        int idx = route.ordinal();
        if (idx >= 0 && idx < navButtons.length && navButtons[idx] != null) {
            navButtons[idx].getStyleClass().add("nav-button-selected");
        }

        if (routeConsumer != null) {
            routeConsumer.accept(route);
        }
    }

    public VBox getView() {
        return sidebar;
    }

    public ViewRoute getCurrentRoute() {
        return currentRoute;
    }
}
