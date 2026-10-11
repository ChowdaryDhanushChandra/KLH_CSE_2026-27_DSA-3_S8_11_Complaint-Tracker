package com.texthack.controller;

import com.texthack.config.AppConfig;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;

/**
 * Primary layout controller orchestrating application header, navigation sidebar,
 * status bar, and central view container.
 */
public class MainController {

    private final BorderPane root;
    private final StackPane centerContainer;
    private final NavigationController navigationController;

    private final Label statusCurrentAlgo;
    private final Label statusMemory;

    // View controllers
    private Module1Controller module1Controller;
    private Module2Controller module2Controller;
    private Module3Controller module3Controller;
    private ComparisonController comparisonController;
    private CorpusController corpusController;
    private CanonController canonController;
    private DesignQuizController designQuizController;

    public MainController() {
        this.root = new BorderPane();

        // 1. Top Header
        root.setTop(buildHeader());

        // 2. Center View Stack
        this.centerContainer = new StackPane();
        centerContainer.setStyle("-fx-background-color: #0f172a;");
        root.setCenter(centerContainer);

        // 3. Bottom Status Bar (Initialized before Navigation to receive route events)
        this.statusCurrentAlgo = new Label("Ready");
        this.statusCurrentAlgo.getStyleClass().add("status-accent");
        this.statusMemory = new Label();
        root.setBottom(buildStatusBar());

        // 4. Navigation Sidebar
        this.navigationController = new NavigationController(this::handleRouteSwitch);
        root.setLeft(navigationController.getView());

        // Initial view
        handleRouteSwitch(NavigationController.ViewRoute.DASHBOARD);
        updateMemoryStatus();
    }

    private HBox buildHeader() {
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("app-header");

        VBox titleBox = new VBox(2);
        Label title = new Label(AppConfig.APP_TITLE);
        title.getStyleClass().add("app-title");
        Label sub = new Label(AppConfig.COURSE_NAME + " • Advanced Educational Analytics Engine");
        sub.getStyleClass().add("app-subtitle");
        titleBox.getChildren().addAll(title, sub);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label badgeLts = new Label("Java 25 / FX 21");
        badgeLts.getStyleClass().add("app-badge");

        Label badgeOffline = new Label("100% Offline Desktop");
        badgeOffline.getStyleClass().add("app-badge");
        badgeOffline.setStyle("-fx-background-color: #0d9488; -fx-text-fill: white;");

        header.getChildren().addAll(titleBox, spacer, badgeLts, badgeOffline);
        return header;
    }

    private HBox buildStatusBar() {
        HBox bar = new HBox(16);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.getStyleClass().add("status-bar");

        Label lbl1 = new Label("Active Mode: ");
        lbl1.getStyleClass().add("status-text");

        Label teamLbl = new Label("Team: " + String.join(" | ", AppConfig.TEAM_MEMBERS));
        teamLbl.getStyleClass().add("status-text");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        statusMemory.getStyleClass().add("status-text");

        bar.getChildren().addAll(lbl1, statusCurrentAlgo, new Separator(), teamLbl, spacer, statusMemory);
        return bar;
    }

    private void handleRouteSwitch(NavigationController.ViewRoute route) {
        centerContainer.getChildren().clear();
        updateMemoryStatus();

        switch (route) {
            case DASHBOARD:
                statusCurrentAlgo.setText("Dashboard Overview");
                centerContainer.getChildren().add(buildDashboardView());
                break;
            case MODULE_1_SYSTEM:
                statusCurrentAlgo.setText("Module 1: TextHack System");
                if (module1Controller == null) module1Controller = new Module1Controller();
                centerContainer.getChildren().add(module1Controller.getView());
                break;
            case MODULE_2_STRINGS:
                statusCurrentAlgo.setText("Module 2: String Algorithms");
                if (module2Controller == null) module2Controller = new Module2Controller();
                centerContainer.getChildren().add(module2Controller.getView());
                break;
            case MODULE_3_DP:
                statusCurrentAlgo.setText("Module 3: Advanced Dynamic Programming");
                if (module3Controller == null) module3Controller = new Module3Controller();
                centerContainer.getChildren().add(module3Controller.getView());
                break;
            case ALGORITHM_COMPARISON:
                statusCurrentAlgo.setText("Algorithm Comparison Hub");
                if (comparisonController == null) comparisonController = new ComparisonController();
                centerContainer.getChildren().add(comparisonController.getView());
                break;
            case CORPUS_EXPLORER:
                statusCurrentAlgo.setText("Indian-Language Corpus Explorer");
                if (corpusController == null) corpusController = new CorpusController();
                centerContainer.getChildren().add(corpusController.getView());
                break;
            case ALGORITHM_CANON:
                statusCurrentAlgo.setText("Algorithm Canon Reference");
                if (canonController == null) canonController = new CanonController();
                centerContainer.getChildren().add(canonController.getView());
                break;
            case ALGORITHM_DESIGN_QA:
                statusCurrentAlgo.setText("Algorithm Design Studio");
                if (designQuizController == null) designQuizController = new DesignQuizController();
                centerContainer.getChildren().add(designQuizController.getView());
                break;
            case ABOUT_PROJECT:
                statusCurrentAlgo.setText("About TextHack Project");
                centerContainer.getChildren().add(buildAboutView());
                break;
        }
    }

    private Parent buildDashboardView() {
        VBox box = new VBox(20);
        box.setPadding(new Insets(24));

        Label head = new Label("TextHack Algorithmic Command Center");
        head.getStyleClass().add("card-title");
        head.setStyle("-fx-font-size: 24px; -fx-text-fill: #38bdf8;");

        Label intro = new Label("Welcome to TextHack – an advanced offline algorithmic educational workbench for Data Structures and Algorithms – 3.\n" +
                "All algorithm engines and underlying data structures are implemented from scratch with zero java.util.* collections.");
        intro.setStyle("-fx-text-fill: #cbd5e1; -fx-font-size: 13px;");

        // Module Launch Cards Grid
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);

        grid.add(createModuleCard("⚙ Module 1: TextHack as a System",
                "Working text-analytics query engine covering Pattern Search, Fuzzy Matching, Document Similarity, Citation Flow (Max-Flow), Project Scheduling (CPM), and Randomized Primality Testing.",
                "Launch Module 1", () -> navigationController.selectRoute(NavigationController.ViewRoute.MODULE_1_SYSTEM)), 0, 0);

        grid.add(createModuleCard("🔤 Module 2: String Algorithms",
                "Explore exact and multi-pattern stringology: Naive O(nm), KMP failure function, Z-algorithm, Rabin-Karp rolling hash, Aho-Corasick automaton, Suffix Arrays O(n log² n), and Kasai linear LCP.",
                "Launch Module 2", () -> navigationController.selectRoute(NavigationController.ViewRoute.MODULE_2_STRINGS)), 1, 0);

        grid.add(createModuleCard("⚡ Module 3: Advanced Dynamic Programming",
                "Exhaustive DP suite: Wagner-Fischer, Damerau-Levenshtein, Weighted Edit, Needleman-Wunsch, Smith-Waterman, Matrix-Chain, Optimal BST, Bitmask TSP, Tree DP, and SOS DP.",
                "Launch Module 3", () -> navigationController.selectRoute(NavigationController.ViewRoute.MODULE_3_DP)), 0, 1);

        grid.add(createModuleCard("📊 Algorithm Comparison Hub",
                "Run multi-algorithm benchmarks simultaneously on identical input texts. Compare microsecond execution times, comparison operations, and memory complexities side-by-side.",
                "Open Comparison Hub", () -> navigationController.selectRoute(NavigationController.ViewRoute.ALGORITHM_COMPARISON)), 1, 1);

        grid.add(createModuleCard("📚 Indian-Language Corpus Explorer",
                "Inspect and query local multilingual text corpuses (English, Hindi Devanagari, and Telugu) completely offline without web browser components or external databases.",
                "Open Corpus Explorer", () -> navigationController.selectRoute(NavigationController.ViewRoute.CORPUS_EXPLORER)), 0, 2);

        grid.add(createModuleCard("📖 Algorithm Canon & Design Studio",
                "Concordance covering CLRS Part VII, Kleinberg-Tardos Ch 6-13, and Erickson Ch 4-12, combined with interactive algorithmic scenario quizzes.",
                "Open Canon & Design", () -> navigationController.selectRoute(NavigationController.ViewRoute.ALGORITHM_CANON)), 1, 2);

        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(50);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(c1, c2);

        box.getChildren().addAll(head, intro, grid);

        ScrollPane sp = new ScrollPane(box);
        sp.setFitToWidth(true);
        return sp;
    }

    private VBox createModuleCard(String title, String desc, String btnText, Runnable onAction) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card-panel");

        Label lblTitle = new Label(title);
        lblTitle.getStyleClass().add("card-title");

        Label lblDesc = new Label(desc);
        lblDesc.getStyleClass().add("card-subtitle");
        lblDesc.setWrapText(true);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btn = new Button(btnText);
        btn.getStyleClass().add("btn-primary");
        btn.setOnAction(e -> onAction.run());

        card.getChildren().addAll(lblTitle, lblDesc, spacer, btn);
        return card;
    }

    private Parent buildAboutView() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(24));

        Label head = new Label("About TextHack – Advanced Algorithm Question Bank");
        head.getStyleClass().add("card-title");
        head.setStyle("-fx-font-size: 22px; -fx-text-fill: #38bdf8;");

        VBox infoCard = new VBox(10);
        infoCard.getStyleClass().add("card-panel");

        Label teamHead = new Label("PROJECT AUTHORS & CONTRIBUTORS (DSA-3):");
        teamHead.setStyle("-fx-font-weight: bold; -fx-text-fill: #38bdf8;");
        Label member1 = new Label("• B Surya Prakash Reddy");
        Label member2 = new Label("• CH Meghanad Sai Reddy");
        Label member3 = new Label("• C Dhanush Chandra");

        Label courseLbl = new Label("Course: Data Structures and Algorithms – 3 (DSA-3)");
        courseLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #e2e8f0;");

        Label archLbl = new Label("Architecture & Strict Restrictions:");
        archLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #38bdf8; -fx-padding: 10px 0 0 0;");
        Label archText = new Label(
                "1. Pure Native Desktop Application: Implemented exclusively using JavaFX 21 controls, scenes, and styling. No web browser, HTML, or REST components.\n" +
                "2. Zero java.util.* in Engine: All dynamic arrays, stacks, queues, hash maps, heaps, tries, disjoint sets, and sorting are implemented manually.\n" +
                "3. Multilingual Offline Text Analytics: Includes Indian-language corpus support for English, Hindi, and Telugu without internet access."
        );
        archText.setStyle("-fx-text-fill: #cbd5e1;");
        archText.setWrapText(true);

        infoCard.getChildren().addAll(courseLbl, teamHead, member1, member2, member3, archLbl, archText);
        box.getChildren().addAll(head, infoCard);

        ScrollPane sp = new ScrollPane(box);
        sp.setFitToWidth(true);
        return sp;
    }

    private void updateMemoryStatus() {
        if (statusMemory == null) return;
        Runtime rt = Runtime.getRuntime();
        long used = (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024);
        long total = rt.totalMemory() / (1024 * 1024);
        statusMemory.setText("Heap: " + used + " MB / " + total + " MB");
    }

    public Parent getView() {
        return root;
    }
}
