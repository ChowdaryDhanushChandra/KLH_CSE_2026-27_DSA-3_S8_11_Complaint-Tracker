package com.texthack.controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Interactive Algorithm Design QA Screen.
 * Enables students to evaluate algorithmic scenarios, pick optimal algorithms,
 * justify selections, and evaluate asymptotic time and space complexities.
 */
public class DesignQuizController {

    private final VBox root;

    public static class Scenario {
        public final String title;
        public final String problemDescription;
        public final String correctAlgorithm;
        public final String correctRationale;
        public final String correctTime;
        public final String correctSpace;

        public Scenario(String title, String desc, String algo, String rationale, String time, String space) {
            this.title = title;
            this.problemDescription = desc;
            this.correctAlgorithm = algo;
            this.correctRationale = rationale;
            this.correctTime = time;
            this.correctSpace = space;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    private final Scenario[] scenarios = new Scenario[]{
            new Scenario(
                    "Scenario 1: Lexical Multi-Pattern Intrusion Detection",
                    "Scan a live streaming network log against 50,000 known malicious signatures simultaneously in linear time without backtracking.",
                    "Aho-Corasick",
                    "Constructs a Trie with BFS failure and dictionary links, achieving O(n + m + z) streaming multi-pattern matching.",
                    "O(n + m + z)",
                    "O(m · Σ)"
            ),
            new Scenario(
                    "Scenario 2: Bioinformatics Local Conserved Motif Discovery",
                    "Identify the most strongly matching conserved local sub-segments between two divergent genomic sequences, discarding dissimilar flanking regions.",
                    "Smith-Waterman",
                    "Uses dynamic programming with negative score bounds reset to zero (max(0, ...)), isolating optimal local alignments.",
                    "O(n · m)",
                    "O(n · m)"
            ),
            new Scenario(
                    "Scenario 3: University Academic Citation Flow Bottlenecks",
                    "Model how influence cascades from seminal publications to emergent research fields and calculate maximum information throughput capacity.",
                    "Edmonds-Karp (Max-Flow)",
                    "Applies Ford-Fulkerson with BFS augmenting paths to guarantee polynomial convergence in O(V · E²).",
                    "O(V · E²)",
                    "O(V + E)"
            ),
            new Scenario(
                    "Scenario 4: Cryptographic Key Candidate Primality Verification",
                    "Verify whether a 512-bit randomly generated integer is prime with failure probability strictly bounded below 4^(-100).",
                    "Miller-Rabin Primality",
                    "Randomized probabilistic test checking nontrivial square roots of 1 modulo n, with error bound (1/4)^k.",
                    "O(k · log³ n)",
                    "O(log n)"
            ),
            new Scenario(
                    "Scenario 5: Optimal Distributed Data Center Server Tree Placement",
                    "Find the node in a tree network that minimizes the sum of network latencies to all other nodes in linear O(V) time.",
                    "Rerooting DP",
                    "Uses 2-pass tree DP: one bottom-up pass for root 0, then one top-down rerooting pass computing ans[v] = ans[u] - sz[v] + (n - sz[v]).",
                    "O(V)",
                    "O(V)"
            )
    };

    public DesignQuizController() {
        root = new VBox(16);
        root.setPadding(new Insets(20));

        Label title = new Label("Algorithm Design & Selection Studio");
        title.getStyleClass().add("card-title");
        title.setStyle("-fx-font-size: 20px; -fx-text-fill: #38bdf8;");

        Label desc = new Label("Select a real-world problem scenario, formulate the design choice, and verify your analytical reasoning.");
        desc.getStyleClass().add("card-subtitle");

        ComboBox<Scenario> scenarioCombo = new ComboBox<>();
        scenarioCombo.getItems().addAll(scenarios);
        scenarioCombo.getSelectionModel().select(0);
        scenarioCombo.setMaxWidth(Double.MAX_VALUE);

        TextArea descArea = new TextArea();
        descArea.setEditable(false);
        descArea.setPrefRowCount(3);
        descArea.setWrapText(true);
        descArea.getStyleClass().add("text-area");

        // Form Fields
        VBox formBox = new VBox(12);
        formBox.getStyleClass().add("card-panel");

        Label q1Label = new Label("1. Which algorithm solves this query?");
        q1Label.setStyle("-fx-font-weight: bold; -fx-text-fill: #38bdf8;");
        ComboBox<String> algoChoice = new ComboBox<>();
        algoChoice.getItems().addAll(
                "Aho-Corasick", "KMP", "Smith-Waterman", "Needleman-Wunsch",
                "Edmonds-Karp (Max-Flow)", "Critical Path Method (CPM)",
                "Miller-Rabin Primality", "Travelling Salesman (TSP DP)",
                "Rerooting DP", "Kasai LCP"
        );
        algoChoice.setMaxWidth(Double.MAX_VALUE);

        Label q2Label = new Label("2. Why was this algorithm selected (Design Rationale)?");
        q2Label.setStyle("-fx-font-weight: bold; -fx-text-fill: #38bdf8;");
        TextField rationaleField = new TextField();
        rationaleField.setPromptText("Enter your rationale or click 'Check' to compare with canon...");

        HBox compBox = new HBox(16);
        VBox timeBox = new VBox(4);
        Label q3Label = new Label("3. Asymptotic Time Complexity:");
        q3Label.setStyle("-fx-font-weight: bold; -fx-text-fill: #38bdf8;");
        ComboBox<String> timeChoice = new ComboBox<>();
        timeChoice.getItems().addAll("O(V)", "O(n + m + z)", "O(n · m)", "O(V · E²)", "O(k · log³ n)", "O(2^n · n²)");
        timeBox.getChildren().addAll(q3Label, timeChoice);
        HBox.setHgrow(timeBox, Priority.ALWAYS);

        VBox spaceBox = new VBox(4);
        Label q4Label = new Label("4. Space Complexity:");
        q4Label.setStyle("-fx-font-weight: bold; -fx-text-fill: #38bdf8;");
        ComboBox<String> spaceChoice = new ComboBox<>();
        spaceChoice.getItems().addAll("O(V)", "O(m · Σ)", "O(n · m)", "O(V + E)", "O(log n)", "O(2^n · n)");
        spaceBox.getChildren().addAll(q4Label, spaceChoice);
        HBox.setHgrow(spaceBox, Priority.ALWAYS);
        compBox.getChildren().addAll(timeBox, spaceBox);

        HBox btnBar = new HBox(12);
        btnBar.setAlignment(Pos.CENTER_LEFT);
        Button checkBtn = new Button("Verify & Grade Answer");
        checkBtn.getStyleClass().add("btn-primary");
        Button revealBtn = new Button("Reveal Canonical Solution");
        revealBtn.getStyleClass().add("btn-secondary");
        btnBar.getChildren().addAll(checkBtn, revealBtn);

        TextArea feedbackArea = new TextArea();
        feedbackArea.setEditable(false);
        feedbackArea.setPrefRowCount(7);
        feedbackArea.setWrapText(true);
        feedbackArea.getStyleClass().add("code-area");

        Runnable updateScenario = () -> {
            Scenario s = scenarioCombo.getValue();
            if (s != null) {
                descArea.setText(s.problemDescription);
                feedbackArea.clear();
                rationaleField.clear();
            }
        };

        scenarioCombo.setOnAction(e -> updateScenario.run());
        updateScenario.run();

        checkBtn.setOnAction(e -> {
            Scenario s = scenarioCombo.getValue();
            if (s == null) return;

            boolean algoMatch = s.correctAlgorithm.equalsIgnoreCase(algoChoice.getValue());
            boolean timeMatch = s.correctTime.equalsIgnoreCase(timeChoice.getValue());
            boolean spaceMatch = s.correctSpace.equalsIgnoreCase(spaceChoice.getValue());

            StringBuilder fb = new StringBuilder();
            fb.append("EVALUATION REPORT FOR ").append(s.title).append(":\n");
            fb.append("--------------------------------------------------\n");
            fb.append("1. Algorithm Selection: ").append(algoMatch ? "CORRECT (+40 pts)" : "INCORRECT (Selected: " + algoChoice.getValue() + ", Correct: " + s.correctAlgorithm + ")").append("\n");
            fb.append("2. Time Complexity:     ").append(timeMatch ? "CORRECT (+30 pts)" : "INCORRECT (Selected: " + timeChoice.getValue() + ", Correct: " + s.correctTime + ")").append("\n");
            fb.append("3. Space Complexity:    ").append(spaceMatch ? "CORRECT (+30 pts)" : "INCORRECT (Selected: " + spaceChoice.getValue() + ", Correct: " + s.correctSpace + ")").append("\n\n");
            fb.append("Canonical Design Rationale:\n").append(s.correctRationale).append("\n");

            feedbackArea.setText(fb.toString());
        });

        revealBtn.setOnAction(e -> {
            Scenario s = scenarioCombo.getValue();
            if (s == null) return;
            algoChoice.setValue(s.correctAlgorithm);
            timeChoice.setValue(s.correctTime);
            spaceChoice.setValue(s.correctSpace);
            rationaleField.setText(s.correctRationale);

            feedbackArea.setText("CANONICAL SOLUTION REVEALED:\n" +
                    "- Algorithm: " + s.correctAlgorithm + "\n" +
                    "- Rationale: " + s.correctRationale + "\n" +
                    "- Time Complexity: " + s.correctTime + "\n" +
                    "- Space Complexity: " + s.correctSpace + "\n");
        });

        formBox.getChildren().addAll(
                new Label("Problem Scenario:"), scenarioCombo, descArea,
                q1Label, algoChoice,
                q2Label, rationaleField,
                compBox,
                btnBar,
                new Label("Feedback & Canonical Explanation:"), feedbackArea
        );

        ScrollPane scroll = new ScrollPane(formBox);
        scroll.setFitToWidth(true);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        root.getChildren().addAll(title, desc, scroll);
    }

    public Parent getView() {
        return root;
    }
}
