package com.texthack.ui.shell;

import com.texthack.config.AppConfig;
import com.texthack.core.util.MemoryEstimator;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

/**
 * Root container combining TopBar, Sidebar, Router animated content stack,
 * and bottom status telemetry bar.
 */
public class MainWindow {
    private final BorderPane root;
    private final Router router;
    private final StackPane centerContent;
    private final Label statusRouteLabel;
    private final Label statusMemoryLabel;

    public MainWindow() {
        this.root = new BorderPane();
        this.centerContent = new StackPane();
        centerContent.setStyle("-fx-background-color: #0F172A;");

        this.router = new Router(centerContent);

        // Status Bar components
        this.statusRouteLabel = new Label("Ready");
        this.statusRouteLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #38BDF8;");
        this.statusMemoryLabel = new Label();
        this.statusMemoryLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #94A3B8;");

        // Layout assembly
        root.setTop(new TopBar(router));
        root.setLeft(new Sidebar(router));
        root.setCenter(centerContent);
        root.setBottom(buildStatusBar());

        router.setRouteChangeListener(route -> {
            statusRouteLabel.setText(route.getTitle() + " • " + route.getSubtitle());
            updateMemoryTelemetry();
        });

        updateMemoryTelemetry();
    }

    public Router getRouter() {
        return router;
    }

    public Parent getView() {
        return root;
    }

    private HBox buildStatusBar() {
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: #1E293B; -fx-padding: 8px 16px; -fx-border-color: #334155; -fx-border-width: 1px 0 0 0;");

        Label modeLabel = new Label("Active View: ");
        modeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");

        Label teamLabel = new Label("Team: " + String.join(" | ", AppConfig.TEAM_MEMBERS));
        teamLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        bar.getChildren().addAll(modeLabel, statusRouteLabel, new Separator(), teamLabel, spacer, statusMemoryLabel);
        return bar;
    }

    private void updateMemoryTelemetry() {
        long usedBytes = MemoryEstimator.estimateCurrentUsedMemory();
        long totalBytes = Runtime.getRuntime().totalMemory();
        long usedMb = usedBytes / (1024 * 1024);
        long totalMb = totalBytes / (1024 * 1024);
        statusMemoryLabel.setText("Heap: " + usedMb + " MB / " + totalMb + " MB");
    }
}
