package com.texthack.controller;

import com.texthack.datastructures.ManualArrayList;
import com.texthack.engine.module3.*;
import com.texthack.model.AlignmentResult;
import com.texthack.model.ComplexityInfo;
import com.texthack.metrics.ComplexityAnalyzer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Controller and View for Module 3: Advanced Dynamic Programming.
 * Houses 12 dedicated interactive tabs:
 * 1. DP Framework
 * 2. Levenshtein Distance
 * 3. Damerau-Levenshtein
 * 4. Weighted Edit Distance
 * 5. Needleman-Wunsch Global Alignment
 * 6. Smith-Waterman Local Alignment
 * 7. Matrix-Chain Multiplication
 * 8. Optimal Binary Search Tree
 * 9. Bitmask Dynamic Programming (TSP / Hamiltonian / Set Cover)
 * 10. DP on Trees (Diameter / Centroid / Rerooting / Independent Set)
 * 11. Sum Over Subsets (SOS DP)
 * 12. When DP is the Wrong Tool
 */
public class Module3Controller {

    private final VBox root;

    public Module3Controller() {
        root = new VBox(12);
        root.setPadding(new Insets(16));

        Label title = new Label("Module 3: Advanced Dynamic Programming Suite");
        title.getStyleClass().add("card-title");
        title.setStyle("-fx-font-size: 20px; -fx-text-fill: #38bdf8;");

        Label subtitle = new Label("Complete spectrum of classical and advanced DP: edit distance, sequence alignment, interval, bitmask, tree DP, and SOS DP.");
        subtitle.getStyleClass().add("card-subtitle");

        TabPane tabPane = new TabPane();
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        tabPane.getTabs().add(createFrameworkTab());
        tabPane.getTabs().add(createLevenshteinTab());
        tabPane.getTabs().add(createDamerauTab());
        tabPane.getTabs().add(createWeightedTab());
        tabPane.getTabs().add(createNeedlemanTab());
        tabPane.getTabs().add(createSmithWatermanTab());
        tabPane.getTabs().add(createMCMTab());
        tabPane.getTabs().add(createOBSTTab());
        tabPane.getTabs().add(createBitmaskTab());
        tabPane.getTabs().add(createTreeDPTab());
        tabPane.getTabs().add(createSOSTab());
        tabPane.getTabs().add(createWhenWrongTab());

        root.getChildren().addAll(title, subtitle, tabPane);
    }

    private Tab createFrameworkTab() {
        Tab tab = new Tab("1. DP Framework");
        tab.setClosable(false);
        VBox box = new VBox(12);
        box.setPadding(new Insets(14));
        TextArea area = new TextArea(DPFramework.getFrameworkOverview());
        area.setEditable(false);
        area.getStyleClass().add("code-area");
        VBox.setVgrow(area, Priority.ALWAYS);
        box.getChildren().add(area);
        tab.setContent(box);
        return tab;
    }

    private Tab createLevenshteinTab() {
        Tab tab = new Tab("2. Levenshtein");
        tab.setClosable(false);
        tab.setContent(buildAlignmentView("Levenshtein Distance", "kitten", "sitting", (s1, s2) -> LevenshteinDistance.compute(s1, s2)));
        return tab;
    }

    private Tab createDamerauTab() {
        Tab tab = new Tab("3. Damerau-Levenshtein");
        tab.setClosable(false);
        tab.setContent(buildAlignmentView("Damerau-Levenshtein", "CA", "ABC", (s1, s2) -> DamerauLevenshteinDistance.compute(s1, s2)));
        return tab;
    }

    private Tab createWeightedTab() {
        Tab tab = new Tab("4. Weighted Edit Distance");
        tab.setClosable(false);

        VBox content = new VBox(12);
        content.setPadding(new Insets(14));

        HBox costBox = new HBox(12);
        costBox.setAlignment(Pos.CENTER_LEFT);
        Label lblIns = new Label("Insert Cost:");
        TextField txtIns = new TextField("1");
        txtIns.setPrefWidth(60);

        Label lblDel = new Label("Delete Cost:");
        TextField txtDel = new TextField("1");
        txtDel.setPrefWidth(60);

        Label lblRep = new Label("Replace Cost:");
        TextField txtRep = new TextField("3");
        txtRep.setPrefWidth(60);

        costBox.getChildren().addAll(lblIns, txtIns, lblDel, txtDel, lblRep, txtRep);

        VBox form = new VBox(8);
        form.getStyleClass().add("card-panel");
        Label l1 = new Label("String A:");
        TextField t1 = new TextField("sunday");
        Label l2 = new Label("String B:");
        TextField t2 = new TextField("saturday");

        HBox btnBar = new HBox(10);
        Button btnExec = new Button("Compute Weighted Distance");
        btnExec.getStyleClass().add("btn-primary");
        Button btnSample = new Button("Load Sample");
        btnSample.getStyleClass().add("btn-secondary");
        btnBar.getChildren().addAll(btnExec, btnSample);
        form.getChildren().addAll(costBox, l1, t1, l2, t2, btnBar);

        TextArea output = new TextArea();
        output.setEditable(false);
        output.getStyleClass().add("code-area");
        VBox.setVgrow(output, Priority.ALWAYS);

        btnSample.setOnAction(e -> {
            t1.setText("algorithm");
            t2.setText("alligator");
            txtIns.setText("2");
            txtDel.setText("2");
            txtRep.setText("5");
        });

        btnExec.setOnAction(e -> {
            int ci = Integer.parseInt(txtIns.getText().trim());
            int cd = Integer.parseInt(txtDel.getText().trim());
            int cr = Integer.parseInt(txtRep.getText().trim());
            AlignmentResult res = WeightedEditDistance.compute(t1.getText(), t2.getText(), ci, cd, cr);
            output.setText(formatAlignmentResult(res));
        });

        content.getChildren().addAll(form, output);
        tab.setContent(content);
        return tab;
    }

    private Tab createNeedlemanTab() {
        Tab tab = new Tab("5. Needleman-Wunsch");
        tab.setClosable(false);

        VBox content = new VBox(12);
        content.setPadding(new Insets(14));

        HBox params = new HBox(12);
        params.setAlignment(Pos.CENTER_LEFT);
        Label lm = new Label("Match Score:");
        TextField tm = new TextField("1");
        tm.setPrefWidth(60);

        Label lmm = new Label("Mismatch Penalty:");
        TextField tmm = new TextField("-1");
        tmm.setPrefWidth(60);

        Label lg = new Label("Gap Penalty:");
        TextField tg = new TextField("-2");
        tg.setPrefWidth(60);

        params.getChildren().addAll(lm, tm, lmm, tmm, lg, tg);

        VBox form = new VBox(8);
        form.getStyleClass().add("card-panel");
        Label l1 = new Label("Sequence 1 (DNA / Text):");
        TextField t1 = new TextField("GCATGCG");
        Label l2 = new Label("Sequence 2 (DNA / Text):");
        TextField t2 = new TextField("GATTACA");

        HBox btnBar = new HBox(10);
        Button exec = new Button("Compute Global Alignment");
        exec.getStyleClass().add("btn-primary");
        Button sample = new Button("Load Sample");
        sample.getStyleClass().add("btn-secondary");
        btnBar.getChildren().addAll(exec, sample);
        form.getChildren().addAll(params, l1, t1, l2, t2, btnBar);

        TextArea output = new TextArea();
        output.setEditable(false);
        output.getStyleClass().add("code-area");
        VBox.setVgrow(output, Priority.ALWAYS);

        sample.setOnAction(e -> {
            t1.setText("AGGGCT");
            t2.setText("AGGCA");
        });

        exec.setOnAction(e -> {
            int match = Integer.parseInt(tm.getText().trim());
            int mismatch = Integer.parseInt(tmm.getText().trim());
            int gap = Integer.parseInt(tg.getText().trim());
            AlignmentResult res = NeedlemanWunsch.align(t1.getText(), t2.getText(), match, mismatch, gap);
            output.setText(formatAlignmentResult(res));
        });

        content.getChildren().addAll(form, output);
        tab.setContent(content);
        return tab;
    }

    private Tab createSmithWatermanTab() {
        Tab tab = new Tab("6. Smith-Waterman");
        tab.setClosable(false);

        VBox content = new VBox(12);
        content.setPadding(new Insets(14));

        HBox params = new HBox(12);
        params.setAlignment(Pos.CENTER_LEFT);
        Label lm = new Label("Match Score:");
        TextField tm = new TextField("3");
        tm.setPrefWidth(60);

        Label lmm = new Label("Mismatch Penalty:");
        TextField tmm = new TextField("-3");
        tmm.setPrefWidth(60);

        Label lg = new Label("Gap Penalty:");
        TextField tg = new TextField("-2");
        tg.setPrefWidth(60);

        params.getChildren().addAll(lm, tm, lmm, tmm, lg, tg);

        VBox form = new VBox(8);
        form.getStyleClass().add("card-panel");
        Label l1 = new Label("Sequence 1:");
        TextField t1 = new TextField("ACACACTA");
        Label l2 = new Label("Sequence 2:");
        TextField t2 = new TextField("AGCACACA");

        HBox btnBar = new HBox(10);
        Button exec = new Button("Compute Local Alignment");
        exec.getStyleClass().add("btn-primary");
        Button sample = new Button("Load Sample");
        sample.getStyleClass().add("btn-secondary");
        btnBar.getChildren().addAll(exec, sample);
        form.getChildren().addAll(params, l1, t1, l2, t2, btnBar);

        TextArea output = new TextArea();
        output.setEditable(false);
        output.getStyleClass().add("code-area");
        VBox.setVgrow(output, Priority.ALWAYS);

        sample.setOnAction(e -> {
            t1.setText("GGTTGACTA");
            t2.setText("TGTTACGG");
        });

        exec.setOnAction(e -> {
            int match = Integer.parseInt(tm.getText().trim());
            int mismatch = Integer.parseInt(tmm.getText().trim());
            int gap = Integer.parseInt(tg.getText().trim());
            AlignmentResult res = SmithWaterman.align(t1.getText(), t2.getText(), match, mismatch, gap);
            output.setText(formatAlignmentResult(res));
        });

        content.getChildren().addAll(form, output);
        tab.setContent(content);
        return tab;
    }

    private Tab createMCMTab() {
        Tab tab = new Tab("7. Matrix Chain Multiplication");
        tab.setClosable(false);

        VBox content = new VBox(12);
        content.setPadding(new Insets(14));

        VBox form = new VBox(8);
        form.getStyleClass().add("card-panel");
        Label lblDims = new Label("Matrix Dimension Sequence p (Comma-separated, where A_i is p[i-1] x p[i]):");
        lblDims.setStyle("-fx-font-weight: bold; -fx-text-fill: #38bdf8;");
        TextField txtDims = new TextField("40, 20, 30, 10, 30");

        HBox btnBar = new HBox(10);
        Button exec = new Button("Compute Optimal Parenthesization");
        exec.getStyleClass().add("btn-primary");
        Button sample = new Button("Load Sample (6 Matrices)");
        sample.getStyleClass().add("btn-secondary");
        btnBar.getChildren().addAll(exec, sample);
        form.getChildren().addAll(lblDims, txtDims, btnBar);

        TextArea output = new TextArea();
        output.setEditable(false);
        output.getStyleClass().add("code-area");
        VBox.setVgrow(output, Priority.ALWAYS);

        sample.setOnAction(e -> txtDims.setText("30, 35, 15, 5, 10, 20, 25"));

        exec.setOnAction(e -> {
            try {
                ManualArrayList<String> parts = com.texthack.util.ManualStringUtil.split(txtDims.getText(), ',');
                int[] p = new int[parts.size()];
                for (int i = 0; i < parts.size(); i++) {
                    p[i] = Integer.parseInt(com.texthack.util.ManualStringUtil.trim(parts.get(i)));
                }

                MatrixChainMultiplication.MCMResult res = MatrixChainMultiplication.solve(p);
                StringBuilder sb = new StringBuilder();
                sb.append("MATRIX-CHAIN MULTIPLICATION (INTERVAL DP)\n");
                sb.append("=========================================\n");
                sb.append("Total Matrices: ").append(p.length - 1).append("\n");
                sb.append("Minimum Scalar Multiplications: ").append(res.minCost).append("\n");
                sb.append("Optimal Parenthesization: ").append(res.optimalParenthesization).append("\n");
                sb.append("Execution Time: ").append(res.algorithmResult.getFormattedTime()).append("\n\n");

                sb.append("DP COST TABLE m[i][j]:\n");
                int n = p.length - 1;
                for (int i = 1; i <= n; i++) {
                    for (int j = 1; j <= n; j++) {
                        if (j < i) {
                            sb.append(String.format("%10s", "-"));
                        } else {
                            sb.append(String.format("%10d", res.costTable[i][j]));
                        }
                    }
                    sb.append("\n");
                }

                sb.append("\nSPLIT POINT TABLE s[i][j]:\n");
                for (int i = 1; i <= n; i++) {
                    for (int j = 1; j <= n; j++) {
                        if (j <= i) {
                            sb.append(String.format("%6s", "-"));
                        } else {
                            sb.append(String.format("%6d", res.splitTable[i][j]));
                        }
                    }
                    sb.append("\n");
                }

                sb.append("\nTRACE LOGS:\n");
                for (int i = 0; i < res.algorithmResult.getStepTraces().size(); i++) {
                    sb.append("[").append(i + 1).append("] ").append(res.algorithmResult.getStepTraces().get(i)).append("\n");
                }
                output.setText(sb.toString());

            } catch (Exception ex) {
                output.setText("Error parsing dimensions: " + ex.getMessage());
            }
        });

        content.getChildren().addAll(form, output);
        tab.setContent(content);
        return tab;
    }

    private Tab createOBSTTab() {
        Tab tab = new Tab("8. Optimal BST");
        tab.setClosable(false);

        VBox content = new VBox(12);
        content.setPadding(new Insets(14));

        VBox form = new VBox(8);
        form.getStyleClass().add("card-panel");
        Label lKeys = new Label("Search Keys (Comma-separated, sorted):");
        lKeys.setStyle("-fx-font-weight: bold; -fx-text-fill: #38bdf8;");
        TextField tKeys = new TextField("k1, k2, k3, k4, k5");

        Label lP = new Label("Successful Search Probabilities p[1..n]:");
        TextField tP = new TextField("0.15, 0.10, 0.05, 0.10, 0.20");

        Label lQ = new Label("Unsuccessful Dummy Probabilities q[0..n]:");
        TextField tQ = new TextField("0.05, 0.10, 0.05, 0.05, 0.05, 0.10");

        HBox btnBar = new HBox(10);
        Button exec = new Button("Construct Optimal BST");
        exec.getStyleClass().add("btn-primary");
        Button sample = new Button("Load Sample");
        sample.getStyleClass().add("btn-secondary");
        btnBar.getChildren().addAll(exec, sample);
        form.getChildren().addAll(lKeys, tKeys, lP, tP, lQ, tQ, btnBar);

        TextArea output = new TextArea();
        output.setEditable(false);
        output.getStyleClass().add("code-area");
        VBox.setVgrow(output, Priority.ALWAYS);

        sample.setOnAction(e -> {
            tKeys.setText("k1, k2, k3");
            tP.setText("0.4, 0.1, 0.2");
            tQ.setText("0.1, 0.1, 0.05, 0.05");
        });

        exec.setOnAction(e -> {
            try {
                ManualArrayList<String> rawK = com.texthack.util.ManualStringUtil.split(tKeys.getText(), ',');
                ManualArrayList<String> rawP = com.texthack.util.ManualStringUtil.split(tP.getText(), ',');
                ManualArrayList<String> rawQ = com.texthack.util.ManualStringUtil.split(tQ.getText(), ',');

                String[] keys = new String[rawK.size()];
                for (int i = 0; i < rawK.size(); i++) keys[i] = com.texthack.util.ManualStringUtil.trim(rawK.get(i));

                double[] p = new double[rawP.size() + 1];
                for (int i = 0; i < rawP.size(); i++) p[i + 1] = Double.parseDouble(com.texthack.util.ManualStringUtil.trim(rawP.get(i)));

                double[] q = new double[rawQ.size()];
                for (int i = 0; i < rawQ.size(); i++) q[i] = Double.parseDouble(com.texthack.util.ManualStringUtil.trim(rawQ.get(i)));

                OptimalBinarySearchTree.OBSTResult res = OptimalBinarySearchTree.compute(keys, p, q);
                StringBuilder sb = new StringBuilder();
                sb.append("OPTIMAL BINARY SEARCH TREE (INTERVAL DP)\n");
                sb.append("========================================\n");
                sb.append(String.format("Minimum Expected Search Cost: %.4f\n", res.minCost));
                sb.append("Runtime: ").append(res.algorithmResult.getFormattedTime()).append("\n\n");

                sb.append("ROOT TABLE (Optimal Root Node for Subtrees):\n");
                int n = keys.length;
                for (int i = 1; i <= n; i++) {
                    for (int j = 1; j <= n; j++) {
                        if (j < i) {
                            sb.append(String.format("%8s", "-"));
                        } else {
                            sb.append(String.format("%8s", keys[res.rootTable[i][j] - 1]));
                        }
                    }
                    sb.append("\n");
                }

                sb.append("\nSTEP-BY-STEP TRACE:\n");
                for (int i = 0; i < res.algorithmResult.getStepTraces().size(); i++) {
                    sb.append("[").append(i + 1).append("] ").append(res.algorithmResult.getStepTraces().get(i)).append("\n");
                }
                output.setText(sb.toString());

            } catch (Exception ex) {
                output.setText("Error constructing OBST: " + ex.getMessage());
            }
        });

        content.getChildren().addAll(form, output);
        tab.setContent(content);
        return tab;
    }

    private Tab createBitmaskTab() {
        Tab tab = new Tab("9. Bitmask DP");
        tab.setClosable(false);

        VBox content = new VBox(12);
        content.setPadding(new Insets(14));

        Label warning = new Label("Safety Guard: Max allowed vertices/elements is 16 to guarantee polynomial memory bounds (2^16 = 65,536 states).");
        warning.setStyle("-fx-text-fill: #38bdf8; -fx-font-weight: bold;");

        ComboBox<String> bitmaskType = new ComboBox<>();
        bitmaskType.getItems().addAll("Travelling Salesman Problem (TSP DP)", "Hamiltonian Path Detection", "Set Cover DP");
        bitmaskType.getSelectionModel().select(0);

        VBox form = new VBox(8);
        form.getStyleClass().add("card-panel");
        Label desc = new Label("Configuration:");
        desc.setStyle("-fx-font-weight: bold;");

        TextArea inputData = new TextArea(
                "4 Vertices Distance Matrix:\n" +
                "0, 10, 15, 20\n" +
                "10, 0, 35, 25\n" +
                "15, 35, 0, 30\n" +
                "20, 25, 30, 0"
        );
        inputData.setPrefRowCount(5);

        HBox btnBar = new HBox(10);
        Button exec = new Button("Execute Bitmask DP");
        exec.getStyleClass().add("btn-primary");
        Button sample = new Button("Load 5-City TSP Sample");
        sample.getStyleClass().add("btn-secondary");
        btnBar.getChildren().addAll(exec, sample);
        form.getChildren().addAll(new Label("Select Problem:"), bitmaskType, desc, inputData, btnBar);

        TextArea output = new TextArea();
        output.setEditable(false);
        output.getStyleClass().add("code-area");
        VBox.setVgrow(output, Priority.ALWAYS);

        sample.setOnAction(e -> {
            inputData.setText(
                    "0, 12, 10, 19, 8\n" +
                    "12, 0, 3, 7, 2\n" +
                    "10, 3, 0, 6, 20\n" +
                    "19, 7, 6, 0, 4\n" +
                    "8, 2, 20, 4, 0"
            );
        });

        exec.setOnAction(e -> {
            try {
                int sel = bitmaskType.getSelectionModel().getSelectedIndex();
                if (sel == 0) {
                    // TSP
                    int[][] dist = parseMatrix(inputData.getText());
                    TravellingSalesmanDP.TSPResult res = TravellingSalesmanDP.solve(dist);
                    output.setText("TRAVELLING SALESMAN PROBLEM (HELD-KARP BITMASK DP)\n" +
                            "===================================================\n" +
                            "Cities (n): " + dist.length + " (States: 2^" + dist.length + " · " + dist.length + ")\n" +
                            "Minimum Tour Distance: " + res.minCost + "\n" +
                            "Optimal Hamiltonian Tour: " + res.tourString + "\n" +
                            "Execution Time: " + res.algorithmResult.getFormattedTime() + "\n\n" +
                            "TRACE LOGS:\n" + formatTraces(res.algorithmResult.getStepTraces()));
                } else if (sel == 1) {
                    // Hamiltonian Path
                    int[][] dist = parseMatrix(inputData.getText());
                    boolean[][] adj = new boolean[dist.length][dist.length];
                    for (int i = 0; i < dist.length; i++) {
                        for (int j = 0; j < dist.length; j++) {
                            adj[i][j] = (dist[i][j] > 0 && dist[i][j] < 1000);
                        }
                    }
                    HamiltonianPathDP.HamResult res = HamiltonianPathDP.findPath(adj);
                    output.setText("HAMILTONIAN PATH DETECTION (BITMASK DP)\n" +
                            "========================================\n" +
                            "Hamiltonian Path Exists: " + res.exists + "\n" +
                            "Path: " + res.path + "\n\n" +
                            "TRACE LOGS:\n" + formatTraces(res.algorithmResult.getStepTraces()));
                } else {
                    // Set Cover
                    int universeSize = 4;
                    int[] masks = new int[]{0b0011, 0b0101, 0b1010, 0b1100};
                    String[] names = new String[]{"S1 {0,1}", "S2 {0,2}", "S3 {1,3}", "S4 {2,3}"};
                    SetCoverDP.SetCoverResult res = SetCoverDP.solve(universeSize, masks, names);
                    output.setText("SET COVER PROBLEM (BITMASK DP)\n" +
                            "==============================\n" +
                            "Universe Size: " + universeSize + " elements\n" +
                            res.algorithmResult.getResultSummary() + "\n" +
                            res.algorithmResult.getDetailedOutput() + "\n\n" +
                            "TRACE LOGS:\n" + formatTraces(res.algorithmResult.getStepTraces()));
                }
            } catch (Exception ex) {
                output.setText("Error executing bitmask DP: " + ex.getMessage());
            }
        });

        content.getChildren().addAll(warning, form, output);
        tab.setContent(content);
        return tab;
    }

    @SuppressWarnings("unchecked")
    private Tab createTreeDPTab() {
        Tab tab = new Tab("10. DP on Trees");
        tab.setClosable(false);

        VBox content = new VBox(12);
        content.setPadding(new Insets(14));

        HBox top = new HBox(12);
        top.setAlignment(Pos.CENTER_LEFT);
        Label lblT = new Label("Tree DP Technique:");
        lblT.setStyle("-fx-font-weight: bold;");
        ComboBox<String> treeOps = new ComboBox<>();
        treeOps.getItems().addAll("Tree Diameter DP", "Tree Centroid DP", "Rerooting DP (All Distances)", "Independent Set on Tree");
        treeOps.getSelectionModel().select(0);
        top.getChildren().addAll(lblT, treeOps);

        VBox form = new VBox(8);
        form.getStyleClass().add("card-panel");
        Label lblDesc = new Label("Tree Edges (Format: u - v per line, 0-indexed):");
        lblDesc.setStyle("-fx-font-weight: bold; -fx-text-fill: #38bdf8;");
        TextArea txtEdges = new TextArea(
                "0 - 1\n" +
                "0 - 2\n" +
                "1 - 3\n" +
                "1 - 4\n" +
                "2 - 5\n" +
                "2 - 6"
        );
        txtEdges.setPrefRowCount(6);

        HBox btnBar = new HBox(10);
        Button exec = new Button("Execute Tree DP");
        exec.getStyleClass().add("btn-primary");
        Button sample = new Button("Load 10-Node Tree Sample");
        sample.getStyleClass().add("btn-secondary");
        btnBar.getChildren().addAll(exec, sample);
        form.getChildren().addAll(top, lblDesc, txtEdges, btnBar);

        TextArea output = new TextArea();
        output.setEditable(false);
        output.getStyleClass().add("code-area");
        VBox.setVgrow(output, Priority.ALWAYS);

        sample.setOnAction(e -> {
            txtEdges.setText(
                    "0 - 1\n" +
                    "1 - 2\n" +
                    "2 - 3\n" +
                    "2 - 4\n" +
                    "1 - 5\n" +
                    "5 - 6\n" +
                    "6 - 7\n" +
                    "6 - 8\n" +
                    "0 - 9"
            );
        });

        exec.setOnAction(e -> {
            try {
                // Parse tree adjacency
                ManualArrayList<String> lines = com.texthack.util.ManualStringUtil.split(txtEdges.getText(), '\n');
                int maxV = 0;
                ManualArrayList<int[]> edgeList = new ManualArrayList<>();
                for (int i = 0; i < lines.size(); i++) {
                    String line = com.texthack.util.ManualStringUtil.trim(lines.get(i));
                    if (line.isEmpty()) continue;
                    ManualArrayList<String> tokens = com.texthack.util.ManualStringUtil.split(line, '-');
                    if (tokens.size() >= 2) {
                        int u = Integer.parseInt(com.texthack.util.ManualStringUtil.trim(tokens.get(0)));
                        int v = Integer.parseInt(com.texthack.util.ManualStringUtil.trim(tokens.get(1)));
                        edgeList.add(new int[]{u, v});
                        if (u > maxV) maxV = u;
                        if (v > maxV) maxV = v;
                    }
                }

                int n = maxV + 1;
                ManualArrayList<Integer>[] tree = new ManualArrayList[n];
                for (int i = 0; i < n; i++) tree[i] = new ManualArrayList<>();

                for (int i = 0; i < edgeList.size(); i++) {
                    int[] edge = edgeList.get(i);
                    tree[edge[0]].add(edge[1]);
                    tree[edge[1]].add(edge[0]);
                }

                int sel = treeOps.getSelectionModel().getSelectedIndex();
                if (sel == 0) {
                    TreeDiameterDP.DiameterResult res = TreeDiameterDP.compute(n, tree);
                    output.setText(res.algorithmResult.getResultSummary() + "\n" +
                            res.algorithmResult.getDetailedOutput() + "\n\n" +
                            "TRACE LOGS:\n" + formatTraces(res.algorithmResult.getStepTraces()));
                } else if (sel == 1) {
                    TreeCentroidDP.CentroidResult res = TreeCentroidDP.findCentroids(n, tree);
                    output.setText(res.algorithmResult.getResultSummary() + "\n" +
                            res.algorithmResult.getDetailedOutput() + "\n\n" +
                            "TRACE LOGS:\n" + formatTraces(res.algorithmResult.getStepTraces()));
                } else if (sel == 2) {
                    RerootingDP.RerootResult res = RerootingDP.computeAllDistances(n, tree);
                    StringBuilder sb = new StringBuilder();
                    sb.append("TREE REROOTING TECHNIQUE (O(V) LINEAR PASSES)\n");
                    sb.append("=============================================\n");
                    sb.append(res.algorithmResult.getResultSummary()).append("\n\n");
                    sb.append("Distance sum for every vertex u:\n");
                    for (int i = 0; i < res.allDistancesSum.length; i++) {
                        sb.append("• Vertex ").append(i).append(": Total distance = ").append(res.allDistancesSum[i]).append("\n");
                    }
                    sb.append("\nTRACE LOGS:\n").append(formatTraces(res.algorithmResult.getStepTraces()));
                    output.setText(sb.toString());
                } else {
                    int[] weights = new int[n];
                    for (int i = 0; i < n; i++) weights[i] = 1;
                    TreeDP.TreeDPResult res = TreeDP.solveIndependentSet(n, tree, weights);
                    output.setText(res.algorithmResult.getResultSummary() + "\n" +
                            res.algorithmResult.getDetailedOutput() + "\n\n" +
                            "TRACE LOGS:\n" + formatTraces(res.algorithmResult.getStepTraces()));
                }

            } catch (Exception ex) {
                output.setText("Error executing tree DP: " + ex.getMessage());
            }
        });

        content.getChildren().addAll(form, output);
        tab.setContent(content);
        return tab;
    }

    private Tab createSOSTab() {
        Tab tab = new Tab("11. SOS DP");
        tab.setClosable(false);

        VBox content = new VBox(12);
        content.setPadding(new Insets(14));

        VBox form = new VBox(8);
        form.getStyleClass().add("card-panel");
        Label lBits = new Label("Bit Count n (Masks = 2^n):");
        lBits.setStyle("-fx-font-weight: bold; -fx-text-fill: #38bdf8;");
        TextField tBits = new TextField("3");

        Label lArr = new Label("Array Elements A[0..2^n - 1] (Comma-separated):");
        TextField tArr = new TextField("1, 2, 3, 4, 5, 6, 7, 8");

        HBox btnBar = new HBox(10);
        Button exec = new Button("Execute SOS DP O(n · 2^n)");
        exec.getStyleClass().add("btn-primary");
        Button sample = new Button("Load 4-Bit Sample");
        sample.getStyleClass().add("btn-secondary");
        btnBar.getChildren().addAll(exec, sample);
        form.getChildren().addAll(lBits, tBits, lArr, tArr, btnBar);

        TextArea output = new TextArea();
        output.setEditable(false);
        output.getStyleClass().add("code-area");
        VBox.setVgrow(output, Priority.ALWAYS);

        sample.setOnAction(e -> {
            tBits.setText("4");
            tArr.setText("1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4");
        });

        exec.setOnAction(e -> {
            try {
                int n = Integer.parseInt(tBits.getText().trim());
                ManualArrayList<String> parts = com.texthack.util.ManualStringUtil.split(tArr.getText(), ',');
                int[] a = new int[parts.size()];
                for (int i = 0; i < parts.size(); i++) {
                    a[i] = Integer.parseInt(com.texthack.util.ManualStringUtil.trim(parts.get(i)));
                }

                SOSDP.SOSResult res = SOSDP.compute(n, a);
                StringBuilder sb = new StringBuilder();
                sb.append("SUM OVER SUBSETS (SOS DP) RESULTS\n");
                sb.append("==================================\n");
                sb.append("Number of Bits: ").append(n).append(" | Total Submasks: ").append(1 << n).append("\n");
                sb.append("Time Complexity: O(n · 2^n) = ").append(n * (1 << n)).append(" ops\n");
                sb.append("Execution Time: ").append(res.algorithmResult.getFormattedTime()).append("\n\n");

                sb.append(String.format("%-8s | %-8s | %-8s | %s\n", "Mask", "Binary", "A[mask]", "F[mask] (Sum over submasks)"));
                sb.append("----------------------------------------------------------\n");

                for (int m = 0; m < (1 << n); m++) {
                    String bin = Integer.toBinaryString(m);
                    while (bin.length() < n) bin = "0" + bin;
                    sb.append(String.format("%-8d | %-8s | %-8d | %-8d\n",
                            m, bin, (m < a.length ? a[m] : 0), res.sosValues[m]));
                }

                sb.append("\nTRACE LOGS:\n").append(formatTraces(res.algorithmResult.getStepTraces()));
                output.setText(sb.toString());

            } catch (Exception ex) {
                output.setText("Error executing SOS DP: " + ex.getMessage());
            }
        });

        content.getChildren().addAll(form, output);
        tab.setContent(content);
        return tab;
    }

    private Tab createWhenWrongTab() {
        Tab tab = new Tab("12. When DP is Wrong");
        tab.setClosable(false);
        VBox box = new VBox(12);
        box.setPadding(new Insets(14));
        TextArea area = new TextArea(WhenDPIsWrong.getGuide());
        area.setEditable(false);
        area.getStyleClass().add("code-area");
        VBox.setVgrow(area, Priority.ALWAYS);
        box.getChildren().add(area);
        tab.setContent(box);
        return tab;
    }

    @FunctionalInterface
    private interface AlignmentFunction {
        AlignmentResult compute(String s, String t);
    }

    private VBox buildAlignmentView(String title, String defA, String defB, AlignmentFunction func) {
        VBox content = new VBox(12);
        content.setPadding(new Insets(14));

        ComplexityInfo comp = ComplexityAnalyzer.getComplexity(title);
        HBox compBanner = new HBox(12);
        compBanner.getChildren().addAll(
                createBadge("TIME COMPLEXITY", comp.getTimeComplexityWorst()),
                createBadge("SPACE COMPLEXITY", comp.getSpaceComplexity())
        );

        VBox form = new VBox(8);
        form.getStyleClass().add("card-panel");
        Label l1 = new Label("String A:");
        TextField t1 = new TextField(defA);
        Label l2 = new Label("String B:");
        TextField t2 = new TextField(defB);

        HBox btnBar = new HBox(10);
        Button exec = new Button("Compute " + title);
        exec.getStyleClass().add("btn-primary");
        Button sample = new Button("Load Sample");
        sample.getStyleClass().add("btn-secondary");
        btnBar.getChildren().addAll(exec, sample);
        form.getChildren().addAll(l1, t1, l2, t2, btnBar);

        TextArea output = new TextArea();
        output.setEditable(false);
        output.getStyleClass().add("code-area");
        VBox.setVgrow(output, Priority.ALWAYS);

        sample.setOnAction(e -> {
            t1.setText(defA);
            t2.setText(defB);
        });

        exec.setOnAction(e -> {
            AlignmentResult res = func.compute(t1.getText(), t2.getText());
            output.setText(formatAlignmentResult(res));
        });

        content.getChildren().addAll(compBanner, form, output);
        return content;
    }

    private String formatAlignmentResult(AlignmentResult res) {
        StringBuilder sb = new StringBuilder();
        sb.append(res.getAlgorithmName().toUpperCase()).append("\n");
        sb.append("=========================================================================\n");
        sb.append("Distance / Optimal Score: ").append(res.getDistanceOrScore()).append("\n");
        sb.append("Execution Time: ").append(res.getFormattedTime()).append(" (").append(res.getExecutionTimeNanos()).append(" ns)\n");
        if (res.getAlignedSeq1() != null && !res.getAlignedSeq1().isEmpty()) {
            sb.append("Aligned String 1: ").append(res.getAlignedSeq1()).append("\n");
            sb.append("Aligned String 2: ").append(res.getAlignedSeq2()).append("\n");
        }
        sb.append("\nOPERATION SEQUENCE / TRACEBACK:\n");
        for (int i = 0; i < res.getOperations().size(); i++) {
            sb.append("• ").append(res.getOperations().get(i)).append("\n");
        }

        sb.append("\n2D DYNAMIC PROGRAMMING RECURRENCE TABLE:\n");
        int[][] dp = res.getDpMatrix();
        String[] row = res.getRowLabels();
        String[] col = res.getColLabels();

        if (dp != null && dp.length > 0 && dp[0].length > 0) {
            sb.append(String.format("%6s", ""));
            for (int j = 0; j < col.length; j++) {
                sb.append(String.format("%6s", col[j]));
            }
            sb.append("\n");

            for (int i = 0; i < row.length; i++) {
                sb.append(String.format("%6s", row[i]));
                for (int j = 0; j < col.length; j++) {
                    sb.append(String.format("%6d", dp[i][j]));
                }
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    private int[][] parseMatrix(String text) {
        ManualArrayList<String> lines = com.texthack.util.ManualStringUtil.split(text, '\n');
        ManualArrayList<int[]> rows = new ManualArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = com.texthack.util.ManualStringUtil.trim(lines.get(i));
            if (line.isEmpty() || line.startsWith("#") || line.contains("Vertices")) continue;
            ManualArrayList<String> tokens = com.texthack.util.ManualStringUtil.split(line, ',');
            int[] row = new int[tokens.size()];
            for (int j = 0; j < tokens.size(); j++) {
                row[j] = Integer.parseInt(com.texthack.util.ManualStringUtil.trim(tokens.get(j)));
            }
            rows.add(row);
        }
        int[][] matrix = new int[rows.size()][rows.size()];
        for (int i = 0; i < rows.size(); i++) {
            matrix[i] = rows.get(i);
        }
        return matrix;
    }

    private String formatTraces(ManualArrayList<String> traces) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < traces.size(); i++) {
            sb.append("[").append(i + 1).append("] ").append(traces.get(i)).append("\n");
        }
        return sb.toString();
    }

    private Label createBadge(String label, String value) {
        Label badge = new Label(label + "\n" + value);
        badge.getStyleClass().add("metric-badge");
        badge.setStyle("-fx-alignment: CENTER; -fx-text-alignment: CENTER; -fx-min-width: 140px;");
        return badge;
    }

    public Parent getView() {
        return root;
    }
}
