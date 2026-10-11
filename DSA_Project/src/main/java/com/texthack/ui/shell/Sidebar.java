package com.texthack.ui.shell;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

/**
 * Collapsible left navigation sidebar with icons, sections, and active highlight.
 */
public class Sidebar extends VBox {
    private final Router router;
    private final Map<Router.Route, Button> navButtons = new HashMap<>();
    private boolean isCollapsed = false;
    private final VBox itemsContainer;

    public Sidebar(Router router) {
        super(8);
        this.router = router;

        getStyleClass().add("sidebar");

        // Toggle collapse button
        HBox topBox = new HBox(8);
        topBox.setAlignment(Pos.CENTER_LEFT);

        Button toggleBtn = new Button("☰");
        toggleBtn.getStyleClass().add("icon-btn");
        toggleBtn.setTooltip(new Tooltip("Toggle Sidebar"));
        toggleBtn.setOnAction(e -> toggleCollapse());

        Label menuLbl = new Label("Navigation");
        menuLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #94A3B8;");

        topBox.getChildren().addAll(toggleBtn, menuLbl);
        getChildren().add(topBox);

        itemsContainer = new VBox(4);

        // Sections
        addSectionHeader("OVERVIEW");
        addNavItem("📊", "Dashboard", Router.Route.DASHBOARD);

        addSectionHeader("ALGORITHM MODULES");
        addNavItem("🔤", "Module 1: System", Router.Route.MODULE1_SYSTEM);
        addNavItem("⚡", "Module 2: Strings", Router.Route.MODULE2_STRINGS);
        addNavItem("🧩", "Module 3: Adv DP", Router.Route.MODULE3_DP);
        addNavItem("🌊", "Module 4: Flow", Router.Route.MODULE4_FLOW);
        addNavItem("🎲", "Module 5: NP & Red.", Router.Route.MODULE5_NP);
        addNavItem("🎯", "Module 6: Randomized", Router.Route.MODULE6_RANDOMIZED);

        addSectionHeader("LABORATORY");
        addNavItem("🔬", "Comparison Hub", Router.Route.COMPARISON_HUB);
        addNavItem("🌐", "Corpus Explorer", Router.Route.CORPUS_EXPLORER);

        addSectionHeader("THEORY & CANON");
        addNavItem("📜", "Algorithm Canon", Router.Route.ALGORITHM_CANON);
        addNavItem("🎓", "Algorithm Design", Router.Route.ALGORITHM_DESIGN);

        addSectionHeader("SYSTEM");
        addNavItem("ℹ", "About & Credits", Router.Route.ABOUT);

        ScrollPane sp = new ScrollPane(itemsContainer);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(sp, Priority.ALWAYS);

        getChildren().add(sp);

        router.setRouteChangeListener(this::updateActiveHighlight);
    }

    private void addSectionHeader(String title) {
        Label header = new Label(title);
        header.getStyleClass().add("sidebar-section-header");
        itemsContainer.getChildren().add(header);
    }

    private void addNavItem(String icon, String labelText, Router.Route route) {
        Button btn = new Button(icon + "  " + labelText);
        btn.getStyleClass().add("nav-button");
        btn.setTooltip(new Tooltip(labelText));
        btn.setOnAction(e -> router.navigateTo(route));

        navButtons.put(route, btn);
        itemsContainer.getChildren().add(btn);
    }

    public void updateActiveHighlight(Router.Route activeRoute) {
        for (Map.Entry<Router.Route, Button> entry : navButtons.entrySet()) {
            Button btn = entry.getValue();
            if (entry.getKey() == activeRoute) {
                if (!btn.getStyleClass().contains("nav-button-active")) {
                    btn.getStyleClass().add("nav-button-active");
                }
            } else {
                btn.getStyleClass().remove("nav-button-active");
            }
        }
    }

    private void toggleCollapse() {
        isCollapsed = !isCollapsed;
        setPrefWidth(isCollapsed ? 70 : 240);
        for (Map.Entry<Router.Route, Button> entry : navButtons.entrySet()) {
            Button btn = entry.getValue();
            String fullText = btn.getText();
            if (isCollapsed) {
                // Keep only icon (first 2 chars)
                btn.setUserData(fullText);
                btn.setText(fullText.substring(0, Math.min(2, fullText.length())).trim());
            } else {
                if (btn.getUserData() != null) {
                    btn.setText((String) btn.getUserData());
                }
            }
        }
    }
}
