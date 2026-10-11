package com.texthack.controller;

import com.texthack.engine.module1.QueryRouter;
import com.texthack.model.AlgorithmResult;
import com.texthack.model.QueryRequest;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Controller and View for Module 1: TextHack as a System.
 * Features unified Query Router, Algorithmic Paradigm Mapping Dashboard,
 * parameter controls, metric badges, and step-by-step execution traces.
 */
public class Module1Controller {

    private final VBox root;
    private final QueryRouter queryRouter;

    public Module1Controller() {
        this.queryRouter = new QueryRouter();
        this.root = new VBox(14);
        root.setPadding(new Insets(20));

        Label title = new Label("Module 1: TextHack as an Algorithmic Analytics System");
        title.getStyleClass().add("card-title");
        title.setStyle("-fx-font-size: 20px; -fx-text-fill: #38bdf8;");

        Label subtitle = new Label("Unified text analytics query engine mapping realistic queries to canonical algorithmic paradigms.");
        subtitle.getStyleClass().add("card-subtitle");

        // Algorithmic Paradigm Mapping Dashboard Card
        VBox familyCard = new VBox(8);
        familyCard.getStyleClass().add("card-panel");
        Label familyTitle = new Label("Query-to-Paradigm Theoretical Mapping Dashboard:");
        familyTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #38bdf8; -fx-font-size: 13px;");

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(6);
        addGridRow(grid, 0, "1. Pattern Search", "String Algorithms (Finite Automata / Rolling Hash)");
        addGridRow(grid, 1, "2. Fuzzy Matching", "Dynamic Programming (2D Wagner-Fischer Recurrence)");
        addGridRow(grid, 2, "3. Document Similarity", "Suffix Structures (Generalized SA + Kasai Linear LCP)");
        addGridRow(grid, 3, "4. Citation Flow", "Network Flow (Edmonds-Karp Shortest Augmenting Path)");
        addGridRow(grid, 4, "5. Project Scheduling", "NP-Hard DAG Optimization & Graham Approximation");
        addGridRow(grid, 5, "6. Primality Testing", "Randomized Number-Theoretic Algorithms (Miller-Rabin)");

        familyCard.getChildren().addAll(familyTitle, grid);

        // Query Selector & Form
        VBox formCard = new VBox(10);
        formCard.getStyleClass().add("card-panel");

        HBox topSelectors = new HBox(16);
        VBox qTypeBox = new VBox(4);
        Label qTypeLbl = new Label("Select Query Type:");
        qTypeLbl.setStyle("-fx-font-weight: bold;");
        ComboBox<String> qTypeCombo = new ComboBox<>();
        qTypeCombo.getItems().addAll(
                "1. Pattern Search",
                "2. Fuzzy Matching",
                "3. Document Similarity",
                "4. Citation Flow Analysis",
                "5. Project Scheduling (CPM)",
                "6. Large-Prime Primality Testing"
        );
        qTypeCombo.getSelectionModel().select(0);
        qTypeBox.getChildren().addAll(qTypeLbl, qTypeCombo);
        HBox.setHgrow(qTypeBox, Priority.ALWAYS);

        VBox algoBox = new VBox(4);
        Label algoLbl = new Label("Algorithm Variant:");
        algoLbl.setStyle("-fx-font-weight: bold;");
        ComboBox<String> algoCombo = new ComboBox<>();
        algoCombo.getItems().addAll("KMP", "Naive", "Z-Algorithm", "Rabin-Karp");
        algoCombo.getSelectionModel().select(0);
        algoBox.getChildren().addAll(algoLbl, algoCombo);
        HBox.setHgrow(algoBox, Priority.ALWAYS);

        topSelectors.getChildren().addAll(qTypeBox, algoBox);

        // Inputs
        Label lblInputA = new Label("Primary Text / Corpus Segment:");
        lblInputA.setStyle("-fx-font-weight: bold; -fx-text-fill: #38bdf8;");
        TextArea txtInputA = new TextArea("Computer science leverages algorithms to process textual structures efficiently.");
        txtInputA.setPrefRowCount(3);
        txtInputA.setWrapText(true);

        Label lblInputB = new Label("Pattern / Secondary Input / Number:");
        lblInputB.setStyle("-fx-font-weight: bold; -fx-text-fill: #38bdf8;");
        TextField txtInputB = new TextField("algorithm");

        HBox btnBar = new HBox(10);
        btnBar.setAlignment(Pos.CENTER_LEFT);
        Button executeBtn = new Button("Execute Algorithm Query");
        executeBtn.getStyleClass().add("btn-primary");
        Button sampleBtn = new Button("Load Paradigm Sample");
        sampleBtn.getStyleClass().add("btn-secondary");
        Button resetBtn = new Button("Reset");
        resetBtn.getStyleClass().add("btn-secondary");
        btnBar.getChildren().addAll(executeBtn, sampleBtn, resetBtn);

        formCard.getChildren().addAll(topSelectors, lblInputA, txtInputA, lblInputB, txtInputB, btnBar);

        // Metrics Banner
        HBox metricsBanner = new HBox(16);
        Label timeBadge = createBadge("EXECUTION TIME", "0.00 µs");
        Label opsBadge = createBadge("OPERATIONS / COMPARISONS", "0");
        Label timeCompBadge = createBadge("TIME COMPLEXITY", "O(n + m)");
        Label spaceCompBadge = createBadge("SPACE COMPLEXITY", "O(m)");
        metricsBanner.getChildren().addAll(timeBadge, opsBadge, timeCompBadge, spaceCompBadge);

        // Results Split
        SplitPane resultsSplit = new SplitPane();
        VBox.setVgrow(resultsSplit, Priority.ALWAYS);

        VBox summaryBox = new VBox(6);
        summaryBox.getStyleClass().add("card-panel");
        Label sumTitle = new Label("Algorithm Output Summary:");
        sumTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #38bdf8;");
        TextArea summaryArea = new TextArea();
        summaryArea.setEditable(false);
        summaryArea.setWrapText(true);
        summaryArea.getStyleClass().add("text-area");
        VBox.setVgrow(summaryArea, Priority.ALWAYS);
        summaryBox.getChildren().addAll(sumTitle, summaryArea);

        VBox traceBox = new VBox(6);
        traceBox.getStyleClass().add("card-panel");
        Label traceTitle = new Label("Step-by-Step Visualization / Execution Trace:");
        traceTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #38bdf8;");
        TextArea traceArea = new TextArea();
        traceArea.setEditable(false);
        traceArea.setWrapText(true);
        traceArea.getStyleClass().add("code-area");
        VBox.setVgrow(traceArea, Priority.ALWAYS);
        traceBox.getChildren().addAll(traceTitle, traceArea);

        resultsSplit.getItems().addAll(summaryBox, traceBox);
        resultsSplit.setDividerPositions(0.45);

        // Dynamic Form Updates
        qTypeCombo.setOnAction(e -> {
            int sel = qTypeCombo.getSelectionModel().getSelectedIndex();
            algoCombo.getItems().clear();
            switch (sel) {
                case 0: // Pattern Search
                    algoCombo.getItems().addAll("KMP", "Naive", "Z-Algorithm", "Rabin-Karp");
                    algoCombo.getSelectionModel().select(0);
                    lblInputA.setText("Target Text Body:");
                    lblInputB.setText("Pattern to Find:");
                    txtInputA.setText("Data structures and algorithms form the bedrock of computational analysis.");
                    txtInputB.setText("algorithm");
                    break;
                case 1: // Fuzzy Matching
                    algoCombo.getItems().addAll("Levenshtein Distance", "Damerau-Levenshtein", "Weighted Edit Distance");
                    algoCombo.getSelectionModel().select(0);
                    lblInputA.setText("Source String A:");
                    lblInputB.setText("Target String B:");
                    txtInputA.setText("algorithm");
                    txtInputB.setText("altruism");
                    break;
                case 2: // Document Similarity
                    algoCombo.getItems().addAll("Suffix Array + LCP + Token Jaccard");
                    algoCombo.getSelectionModel().select(0);
                    lblInputA.setText("Document 1 Text:");
                    lblInputB.setText("Document 2 Text:");
                    txtInputA.setText("Knuth-Morris-Pratt and Suffix Arrays are fundamental string indexing algorithms.");
                    txtInputB.setText("Suffix Arrays and Kasai LCP enable linear time substring comparison in algorithms.");
                    break;
                case 3: // Citation Flow
                    algoCombo.getItems().addAll("Edmonds-Karp (Max-Flow)");
                    algoCombo.getSelectionModel().select(0);
                    lblInputA.setText("Citation Network Configuration:");
                    txtInputA.setText("6 Academic Nodes: 0 (Seminal Theory), 1 (NLP), 2 (Systems), 3 (Bioinformatics), 4 (ML), 5 (Target Field)");
                    lblInputB.setText("Source -> Sink Vertices:");
                    txtInputB.setText("0 -> 5");
                    break;
                case 4: // Scheduling
                    algoCombo.getItems().addAll("Critical Path Method (CPM)");
                    algoCombo.getSelectionModel().select(0);
                    lblInputA.setText("Project Activities DAG:");
                    txtInputA.setText("Tasks: Literature Survey (3d), Algorithm Design (4d), Data Collection (2d), Implementation (5d), Benchmarking (3d), Writing (4d)");
                    lblInputB.setText("Available Parallel Machines/Workers:");
                    txtInputB.setText("2");
                    break;
                case 5: // Primality
                    algoCombo.getItems().addAll("Miller-Rabin", "Fermat");
                    algoCombo.getSelectionModel().select(0);
                    lblInputA.setText("Candidate Number Description:");
                    txtInputA.setText("Test large odd integer for probabilistic primality (e.g., Mersenne prime or Carmichael candidate 561):");
                    lblInputB.setText("Candidate Integer (n >= 2):");
                    txtInputB.setText("1000000007");
                    break;
            }
        });

        sampleBtn.setOnAction(e -> {
            int sel = qTypeCombo.getSelectionModel().getSelectedIndex();
            if (sel == 5) {
                txtInputB.setText("2147483647"); // 2^31 - 1 Mersenne prime
            } else if (sel == 1) {
                txtInputA.setText("INTENTION");
                txtInputB.setText("EXECUTION");
            } else if (sel == 0) {
                txtInputA.setText("ABABDABACDABABCABAB");
                txtInputB.setText("ABABCABAB");
            }
        });

        resetBtn.setOnAction(e -> {
            txtInputA.clear();
            txtInputB.clear();
            summaryArea.clear();
            traceArea.clear();
        });

        executeBtn.setOnAction(e -> {
            int sel = qTypeCombo.getSelectionModel().getSelectedIndex();
            String inA = txtInputA.getText();
            String inB = txtInputB.getText();
            String algo = algoCombo.getValue();

            try {
                QueryRequest.QueryType qType;
                long num = 0;
                switch (sel) {
                    case 0: qType = QueryRequest.QueryType.PATTERN_SEARCH; break;
                    case 1: qType = QueryRequest.QueryType.FUZZY_MATCHING; break;
                    case 2: qType = QueryRequest.QueryType.DOCUMENT_SIMILARITY; break;
                    case 3: qType = QueryRequest.QueryType.CITATION_FLOW; break;
                    case 4: qType = QueryRequest.QueryType.PROJECT_SCHEDULING; break;
                    case 5:
                        qType = QueryRequest.QueryType.PRIMALITY_TESTING;
                        num = Long.parseLong(inB.trim());
                        break;
                    default: qType = QueryRequest.QueryType.PATTERN_SEARCH;
                }

                QueryRequest req = new QueryRequest(qType, algo, inA, inB, num, 1, 1, 1);
                AlgorithmResult res = queryRouter.routeQuery(req);

                updateBadge(timeBadge, "EXECUTION TIME", res.getFormattedTime());
                updateBadge(opsBadge, "OPERATIONS / COMPARISONS", String.valueOf(res.getComparisons() + res.getOperations()));
                updateBadge(timeCompBadge, "TIME COMPLEXITY", res.getComplexityInfo().getTimeComplexityWorst());
                updateBadge(spaceCompBadge, "SPACE COMPLEXITY", res.getComplexityInfo().getSpaceComplexity());

                summaryArea.setText("ALGORITHM: " + res.getAlgorithmName() + "\n" +
                        "RESULT: " + res.getResultSummary() + "\n\n" +
                        "DETAILS:\n" + res.getDetailedOutput() + "\n\n" +
                        "THEORETICAL COMPLEXITY:\n" + res.getComplexityInfo().getTheoreticalExplanation());

                StringBuilder traceSb = new StringBuilder();
                for (int i = 0; i < res.getStepTraces().size(); i++) {
                    traceSb.append("[").append(i + 1).append("] ").append(res.getStepTraces().get(i)).append("\n");
                }
                traceArea.setText(traceSb.toString());

            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Execution error: " + ex.getMessage());
                alert.showAndWait();
            }
        });

        root.getChildren().addAll(title, subtitle, familyCard, formCard, metricsBanner, resultsSplit);
    }

    private void addGridRow(GridPane g, int row, String query, String paradigm) {
        Label qLbl = new Label(query);
        qLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #cbd5e1;");
        Label arrow = new Label("→");
        arrow.setStyle("-fx-text-fill: #38bdf8; -fx-font-weight: bold;");
        Label pLbl = new Label(paradigm);
        pLbl.setStyle("-fx-text-fill: #22d3ee;");
        g.addRow(row, qLbl, arrow, pLbl);
    }

    private Label createBadge(String label, String value) {
        Label badge = new Label(label + "\n" + value);
        badge.getStyleClass().add("metric-badge");
        badge.setStyle("-fx-alignment: CENTER; -fx-text-alignment: CENTER; -fx-min-width: 150px;");
        return badge;
    }

    private void updateBadge(Label badge, String label, String value) {
        badge.setText(label + "\n" + value);
    }

    public Parent getView() {
        return root;
    }
}
