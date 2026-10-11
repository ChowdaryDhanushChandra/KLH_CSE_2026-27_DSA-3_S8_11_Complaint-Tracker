package com.texthack.controller;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Controller and view for the Algorithm Canon reference screen.
 * References CLRS Part VII, Kleinberg-Tardos Chapters 6-13, and Erickson Chapters 4-12.
 */
public class CanonController {

    private final VBox root;

    public CanonController() {
        root = new VBox(16);
        root.setPadding(new Insets(20));

        Label title = new Label("Algorithm Canon – Core Theoretical Curriculum");
        title.getStyleClass().add("card-title");
        title.setStyle("-fx-font-size: 20px; -fx-text-fill: #38bdf8;");

        Label desc = new Label("Standardized reference concordance mapping TextHack modules to canonical textbooks.");
        desc.getStyleClass().add("card-subtitle");

        TextArea canonContent = new TextArea();
        canonContent.setEditable(false);
        canonContent.setWrapText(true);
        canonContent.getStyleClass().add("code-area");
        VBox.setVgrow(canonContent, Priority.ALWAYS);

        canonContent.setText(
                "========================================================================================\n" +
                "TEXTHACK ALGORITHM CANON REFERENCE MATRIX (DSA-3 CURRICULUM)\n" +
                "========================================================================================\n\n" +
                "1. CORMEN, LEISERSON, RIVEST, STEIN (CLRS) - INTRODUCTION TO ALGORITHMS (4TH EDITION)\n" +
                "   ---------------------------------------------------------------------------------\n" +
                "   * PART VII: SELECTED TOPICS\n" +
                "     - Chapter 31: Number-Theoretic Algorithms\n" +
                "       Sections 31.4-31.8: Modular Exponentiation, Fermat's Primality, Miller-Rabin Primality Testing.\n" +
                "       (Maps to TextHack Module 1.6: Large-Prime Primality Testing).\n" +
                "     - Chapter 32: String Matching\n" +
                "       Section 32.1: The Naive string-matching algorithm.\n" +
                "       Section 32.2: The Rabin-Karp algorithm (Polynomial Rolling Hash).\n" +
                "       Section 32.4: The Knuth-Morris-Pratt algorithm (Prefix Function / LPS array).\n" +
                "       (Maps to TextHack Module 2.1 - 2.4).\n" +
                "     - Chapter 24 & 26: Maximum Flow & Network Flow\n" +
                "       Section 26.1-26.2: Flow Networks, Ford-Fulkerson Method, Edmonds-Karp Shortest Augmenting Path.\n" +
                "       (Maps to TextHack Module 1.4: Citation-Flow Analysis).\n" +
                "     - Chapter 14: Dynamic Programming\n" +
                "       Section 14.2: Matrix-chain multiplication (Interval DP).\n" +
                "       Section 14.5: Optimal binary search trees.\n" +
                "       (Maps to TextHack Module 3.7 & 3.8).\n\n" +
                "2. KLEINBERG & TARDOS - ALGORITHM DESIGN\n" +
                "   ------------------------------------\n" +
                "   * Chapter 6: Dynamic Programming\n" +
                "     - 6.5: Sequence Alignment (Needleman-Wunsch Global & Smith-Waterman Local Alignment).\n" +
                "     - 6.6: Sequence Alignment in Linear Space (Hirschberg's Algorithm).\n" +
                "     - 6.8: Shortest Paths and Bellman-Ford.\n" +
                "   * Chapter 7: Network Flow\n" +
                "     - 7.1-7.3: The Maximum-Flow Problem and the Ford-Fulkerson / Edmonds-Karp Algorithm.\n" +
                "     - 7.5: Bipartite Matching and Disjoint Paths.\n" +
                "   * Chapter 8: NP and Computational Intractability\n" +
                "     - 8.1-8.5: Reductions, Vertex Cover, Set Cover, TSP, Hamiltonian Cycle.\n" +
                "   * Chapter 11: Approximation Algorithms\n" +
                "     - 11.1: Greedy Algorithms and Bounds: A 2-Approximation for Center Selection.\n" +
                "     - 11.2: Set Cover approximation.\n" +
                "   * Chapter 13: Randomized Algorithms\n" +
                "     - 13.8: Primality Testing and Monte Carlo Methods.\n\n" +
                "3. JEFF ERICKSON - ALGORITHMS (2019)\n" +
                "   ---------------------------------\n" +
                "   * Chapter 3: Dynamic Programming\n" +
                "     - 3.7: Edit Distance (Wagner-Fischer Levenshtein & Transposition extensions).\n" +
                "     - 3.9: Optimal Binary Search Trees.\n" +
                "     - 3.10: Dynamic Programming on Trees (Independent Set, Tree Diameter, Rerooting).\n" +
                "   * Chapter 4: Greedy Algorithms\n" +
                "     - Contrast against DP: Matroids, Exchange arguments, Huffman Coding.\n" +
                "   * Chapter 10: Maximum Flow & Minimum Cut\n" +
                "     - Augmenting paths, integrality theorem, min-cut citation bottleneck.\n" +
                "   * Chapter 12: NP-Hardness\n" +
                "     - Bitmask DP for TSP (Held-Karp O(2^n · n²)), Hamiltonian Paths, Subset Sum.\n"
        );

        root.getChildren().addAll(title, desc, canonContent);
    }

    public Parent getView() {
        return root;
    }
}
