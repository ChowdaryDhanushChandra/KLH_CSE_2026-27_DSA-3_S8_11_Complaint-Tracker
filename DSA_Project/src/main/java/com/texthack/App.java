package com.texthack;

import com.texthack.config.AppConfig;
import com.texthack.ui.shell.MainWindow;
import com.texthack.ui.shell.Router;
import com.texthack.ui.theme.ThemeManager;
import com.texthack.ui.views.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main JavaFX Application entry point for TextHack desktop workbench.
 */
public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        MainWindow mainWindow = new MainWindow();
        Router router = mainWindow.getRouter();

        // Register View Routes
        router.registerView(Router.Route.DASHBOARD, () -> new DashboardView(router));
        router.registerView(Router.Route.MODULE1_SYSTEM, () -> new PlaceholderModuleView(
                "Module 1: TextHack as a System",
                "Text-analytics query engine covering exact matching, fuzzy distance, document similarity, citation flow, CPM scheduling, and primality testing.",
                "O(V · E²)",
                new String[]{
                        "Query Router & Paradigm Dispatcher",
                        "Exact Pattern Search (Naive, KMP, Z-Algorithm, Rabin-Karp)",
                        "Fuzzy Matching (Wagner-Fischer, Damerau-Levenshtein, Weighted Edit)",
                        "Document Similarity (Generalized Suffix Array + Kasai LCP + Jaccard)",
                        "Citation Flow Network (Edmonds-Karp Max-Flow)",
                        "Project Scheduling (Critical Path Method & Graham's 2-Approximation)",
                        "Primality Testing (Fermat & Miller-Rabin Probabilistic Tests)"
                },
                router
        ));
        router.registerView(Router.Route.MODULE2_STRINGS, Module2View::new);
        router.registerView(Router.Route.MODULE3_DP, () -> new PlaceholderModuleView(
                "Module 3: Advanced Dynamic Programming",
                "12 Comprehensive topics in foundational, interval, bitmask, and tree dynamic programming.",
                "O(n²)",
                new String[]{
                        "DP Framework (Optimal Substructure & Recurrences)",
                        "Wagner-Fischer Levenshtein Edit Distance",
                        "Damerau-Levenshtein with Adjacent Transpositions",
                        "Weighted Edit Distance with Custom Costs",
                        "Needleman-Wunsch Global Pairwise Alignment",
                        "Smith-Waterman Local Sequence Alignment",
                        "Matrix-Chain Multiplication Interval DP",
                        "Optimal Binary Search Tree (OBST)",
                        "Held-Karp TSP Bitmask DP",
                        "Tree Diameter, Centroid, and Rerooting DP",
                        "Sum Over Subsets (SOS DP) O(n · 2ⁿ)",
                        "When DP is the Wrong Tool: Greedy & Complexity Limits"
                },
                router
        ));
        router.registerView(Router.Route.MODULE4_FLOW, () -> new PlaceholderModuleView(
                "Module 4: Network Flow & Reductions",
                "Maximum flow algorithms, min-cut theorem, and polynomial reductions.",
                "O(V · E²)",
                new String[]{
                        "Ford-Fulkerson Method with Residual Graphs",
                        "Edmonds-Karp Algorithm via BFS Augmenting Paths",
                        "Dinic's Algorithm with Level Graphs and Blocking Flows",
                        "Max-Flow Min-Cut Theorem Verification",
                        "Bipartite Matching Reduction",
                        "Min-Cost Max-Flow",
                        "Image Segmentation Graph-Cut Reduction",
                        "Project Selection Problem Reduction"
                },
                router
        ));
        router.registerView(Router.Route.MODULE5_NP, () -> new PlaceholderModuleView(
                "Module 5: NP-Completeness & Reductions",
                "Intractability, NP-complete reductions, and polynomial-time approximation schemes.",
                "O(2ⁿ)",
                new String[]{
                        "Boolean Satisfiability (SAT & 3-SAT Verification)",
                        "Reduction Chain: 3-SAT → CLIQUE → Independent Set → Vertex Cover",
                        "Subset Sum Problem Reduction",
                        "0/1 Knapsack Problem",
                        "Hamiltonian Cycle → TSP Reduction",
                        "Vertex Cover 2-Approximation Heuristic",
                        "FPTAS for Knapsack",
                        "FPT Parameterized Vertex Cover (O(2ᵏ · V))"
                },
                router
        ));
        router.registerView(Router.Route.MODULE6_RANDOMIZED, () -> new PlaceholderModuleView(
                "Module 6: Randomized & Parallel Algorithms",
                "Probabilistic analysis, randomized number theory, and work-span DAG simulations.",
                "O(n log n)",
                new String[]{
                        "Randomized Quicksort (Expected O(n log n))",
                        "Miller-Rabin Randomized Primality Testing (Error <= 4⁻ᵏ)",
                        "Universal Hashing Hash Functions",
                        "Perfect Hashing (O(1) worst-case lookup)",
                        "Reservoir Sampling Streaming Algorithm",
                        "Parallel Tree Reduce Simulation",
                        "Blelloch Parallel Prefix Scan",
                        "Work-Span DAG Performance Simulator"
                },
                router
        ));
        router.registerView(Router.Route.COMPARISON_HUB, ComparisonHubView::new);
        router.registerView(Router.Route.CORPUS_EXPLORER, CorpusExplorerView::new);
        router.registerView(Router.Route.ALGORITHM_CANON, CanonView::new);
        router.registerView(Router.Route.ALGORITHM_DESIGN, DesignStudioView::new);
        router.registerView(Router.Route.ABOUT, AboutView::new);

        // Initial landing page
        router.navigateTo(Router.Route.DASHBOARD);

        Scene scene = new Scene(mainWindow.getView(), 1280, 820);
        ThemeManager.applyTheme(scene, ThemeManager.Theme.DARK);

        primaryStage.setTitle(AppConfig.APP_TITLE);
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
