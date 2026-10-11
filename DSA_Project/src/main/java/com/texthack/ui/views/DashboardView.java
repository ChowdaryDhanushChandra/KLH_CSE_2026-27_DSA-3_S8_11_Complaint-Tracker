package com.texthack.ui.views;

import com.texthack.config.AppConfig;
import com.texthack.ui.components.Card;
import com.texthack.ui.components.ComplexityBadge;
import com.texthack.ui.shell.Router;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

/**
 * Main application dashboard displaying module cards, system stats,
 * and quick-launch entry points.
 */
public class DashboardView extends ScrollPane {

    public DashboardView(Router router) {
        setFitToWidth(true);
        setStyle("-fx-background-color: transparent;");

        VBox content = new VBox(20);
        content.setPadding(new Insets(24));

        // 1. Hero Header
        Label title = new Label("TextHack – Advanced Algorithm Workbench");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #38BDF8;");

        Label subtitle = new Label("Data Structures and Algorithms – 3 • 100% Offline Educational Engine");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #94A3B8;");

        VBox hero = new VBox(4, title, subtitle);

        // 2. Metrics Bar
        HBox statsBar = new HBox(16);
        statsBar.getChildren().addAll(
                buildMetricCard("6", "Core Modules", "#38BDF8"),
                buildMetricCard("20+", "Algorithms Implemented", "#22C55E"),
                buildMetricCard("0", "java.util.* in Engine", "#F59E0B"),
                buildMetricCard("3", "Indian Languages Supported", "#A855F7")
        );

        // 3. Modules Grid
        Label sectionTitle = new Label("Curriculum Algorithmic Modules");
        sectionTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #F8FAFC;");

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);

        grid.add(buildModuleCard("Module 1: TextHack System",
                "Text-analytics query engine covering exact matching, fuzzy distance, document similarity, citation flow, CPM scheduling, and primality testing.",
                "O(V · E²)", "System", router, Router.Route.MODULE1_SYSTEM), 0, 0);

        grid.add(buildModuleCard("Module 2: String Algorithms (Active)",
                "Exact and multi-pattern matching: Naive O(nm), KMP (LPS), Z-Algorithm, Rabin-Karp (Double Hash), Aho-Corasick, Suffix Arrays O(n log² n), Kasai LCP O(n), Suffix Automata/Tree.",
                "O(n + m)", "Production Ready", router, Router.Route.MODULE2_STRINGS), 1, 0);

        grid.add(buildModuleCard("Module 3: Advanced Dynamic Programming",
                "Wagner-Fischer, Damerau-Levenshtein, Needleman-Wunsch, Smith-Waterman, Matrix-Chain, Optimal BST, Bitmask TSP, Tree DP, SOS DP.",
                "O(n²)", "Curriculum", router, Router.Route.MODULE3_DP), 2, 0);

        grid.add(buildModuleCard("Module 4: Network Flow & Reductions",
                "Ford-Fulkerson, Edmonds-Karp, Dinic's blocking flow, Min-Cut theorem, Bipartite Matching, Image Segmentation reductions.",
                "O(V² E)", "Curriculum", router, Router.Route.MODULE4_FLOW), 0, 1);

        grid.add(buildModuleCard("Module 5: NP-Completeness & Approximation",
                "3-SAT, reduction chain 3SAT → CLIQUE → IS → VC, Subset Sum, TSP 2-approximation, FPTAS Knapsack, FPT Vertex Cover.",
                "O(2ⁿ)", "Curriculum", router, Router.Route.MODULE5_NP), 1, 1);

        grid.add(buildModuleCard("Module 6: Randomized & Parallel",
                "Randomized Quicksort, Miller-Rabin Primality, Universal Hashing, Reservoir Sampling, Blelloch Work-Span parallel prefix scan.",
                "O(n log n)", "Curriculum", router, Router.Route.MODULE6_RANDOMIZED), 2, 1);

        ColumnConstraints col1 = new ColumnConstraints(); col1.setPercentWidth(33.3);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setPercentWidth(33.3);
        ColumnConstraints col3 = new ColumnConstraints(); col3.setPercentWidth(33.3);
        grid.getColumnConstraints().addAll(col1, col2, col3);

        content.getChildren().addAll(hero, statsBar, sectionTitle, grid);
        setContent(content);
    }

    private Node buildMetricCard(String value, String label, String colorHex) {
        VBox box = new VBox(4);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #1E293B; -fx-padding: 16px; -fx-background-radius: 12px; -fx-border-color: #334155; -fx-border-radius: 12px;");
        HBox.setHgrow(box, Priority.ALWAYS);

        Label valLbl = new Label(value);
        valLbl.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + colorHex + ";");

        Label descLbl = new Label(label);
        descLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #94A3B8;");

        box.getChildren().addAll(valLbl, descLbl);
        return box;
    }

    private Card buildModuleCard(String title, String desc, String complexity, String status, Router router, Router.Route route) {
        Label titleLbl = new Label(title);
        titleLbl.getStyleClass().add("card-title");

        Label descLbl = new Label(desc);
        descLbl.setWrapText(true);
        descLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #94A3B8;");

        ComplexityBadge badge = new ComplexityBadge(complexity);
        Label statusLbl = new Label(status);
        statusLbl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #22C55E;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox topRow = new HBox(8, badge, spacer, statusLbl);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Button launchBtn = new Button("Open Module →");
        launchBtn.getStyleClass().add("primary-btn");
        launchBtn.setMaxWidth(Double.MAX_VALUE);
        launchBtn.setOnAction(e -> router.navigateTo(route));

        VBox content = new VBox(10, topRow, titleLbl, descLbl, launchBtn);
        return new Card(content);
    }
}
