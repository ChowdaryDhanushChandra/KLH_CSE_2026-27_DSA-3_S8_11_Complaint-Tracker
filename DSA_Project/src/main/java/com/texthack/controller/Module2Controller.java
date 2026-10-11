package com.texthack.controller;

import com.texthack.datastructures.ManualArrayList;
import com.texthack.engine.module2.*;
import com.texthack.model.ComplexityInfo;
import com.texthack.model.SearchResult;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Controller and View for Module 2: String Algorithms.
 * Provides dedicated screens/tabs for:
 * 1. Naive Pattern Matcher
 * 2. Knuth-Morris-Pratt (KMP)
 * 3. Z-Algorithm
 * 4. Rabin-Karp
 * 5. Aho-Corasick
 * 6. Suffix Arrays (O(n log² n))
 * 7. Kasai LCP Array
 * 8. Suffix Automata & Suffix Trees Preview
 */
public class Module2Controller {

    private final VBox root;

    public Module2Controller() {
        root = new VBox(12);
        root.setPadding(new Insets(16));

        Label title = new Label("Module 2: String Algorithms & Suffix Structures");
        title.getStyleClass().add("card-title");
        title.setStyle("-fx-font-size: 20px; -fx-text-fill: #38bdf8;");

        Label subtitle = new Label("Comprehensive analysis, step-by-step table traces, and complexity metrics for exact and dictionary stringology.");
        subtitle.getStyleClass().add("card-subtitle");

        TabPane tabPane = new TabPane();
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        tabPane.getTabs().add(createMatcherTab("1. Naive O(nm)", new NaivePatternMatcher(),
                "AABAACAADAABAABA", "AABA",
                "Compares pattern character-by-character at every offset. Worst case O(nm) on repetitive strings."));

        tabPane.getTabs().add(createMatcherTab("2. Knuth-Morris-Pratt", new KMPMatcher(),
                "ABABDABACDABABCABAB", "ABABCABAB",
                "Precomputes failure function (LPS array) in O(m). Never backtracks text pointer i, ensuring linear O(n+m) time."));

        tabPane.getTabs().add(createMatcherTab("3. Z-Algorithm", new ZAlgorithmMatcher(),
                "AABZAABZCAABZAABZA", "AABZ",
                "Constructs Z-array for P + '$' + T using sliding window [L, R] bounding prefix matches in O(n+m)."));

        tabPane.getTabs().add(createMatcherTab("4. Rabin-Karp", new RabinKarpMatcher(),
                "GEEKS FOR GEEKS", "GEEK",
                "Computes polynomial rolling hash with modular arithmetic. Incurs character verification only upon hash collision."));

        tabPane.getTabs().add(createAhoCorasickTab());
        tabPane.getTabs().add(createSuffixArrayTab());
        tabPane.getTabs().add(createKasaiLCPTab());
        tabPane.getTabs().add(createSuffixPreviewTab());

        root.getChildren().addAll(title, subtitle, tabPane);
    }

    private Tab createMatcherTab(String tabName, com.texthack.engine.common.StringAlgorithm matcher,
                                 String defaultText, String defaultPattern, String explanation) {
        Tab tab = new Tab(tabName);
        tab.setClosable(false);

        VBox content = new VBox(12);
        content.setPadding(new Insets(14));

        ComplexityInfo comp = matcher.getComplexity();
        HBox compBanner = new HBox(12);
        compBanner.getChildren().addAll(
                createBadge("TIME COMPLEXITY", comp.getTimeComplexityWorst()),
                createBadge("AVERAGE TIME", comp.getTimeComplexityAverage()),
                createBadge("SPACE COMPLEXITY", comp.getSpaceComplexity())
        );

        Label explLbl = new Label("Theoretical Intuition: " + explanation);
        explLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-style: italic;");

        // Inputs
        VBox form = new VBox(8);
        form.getStyleClass().add("card-panel");

        Label lblText = new Label("Target Text (T):");
        lblText.setStyle("-fx-font-weight: bold; -fx-text-fill: #38bdf8;");
        TextArea txtText = new TextArea(defaultText);
        txtText.setPrefRowCount(3);
        txtText.setWrapText(true);

        Label lblPat = new Label("Pattern (P):");
        lblPat.setStyle("-fx-font-weight: bold; -fx-text-fill: #38bdf8;");
        TextField txtPat = new TextField(defaultPattern);

        HBox btnBar = new HBox(10);
        btnBar.setAlignment(Pos.CENTER_LEFT);
        Button execBtn = new Button("Execute " + matcher.getName());
        execBtn.getStyleClass().add("btn-primary");
        Button sampleBtn = new Button("Load Sample");
        sampleBtn.getStyleClass().add("btn-secondary");
        Button resetBtn = new Button("Reset");
        resetBtn.getStyleClass().add("btn-secondary");
        btnBar.getChildren().addAll(execBtn, sampleBtn, resetBtn);

        form.getChildren().addAll(lblText, txtText, lblPat, txtPat, btnBar);

        // Results
        SplitPane results = new SplitPane();
        VBox.setVgrow(results, Priority.ALWAYS);

        VBox sumBox = new VBox(6);
        sumBox.getStyleClass().add("card-panel");
        Label sumLbl = new Label("Search Summary & Metrics:");
        sumLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #38bdf8;");
        TextArea sumArea = new TextArea();
        sumArea.setEditable(false);
        sumArea.getStyleClass().add("text-area");
        VBox.setVgrow(sumArea, Priority.ALWAYS);
        sumBox.getChildren().addAll(sumLbl, sumArea);

        VBox traceBox = new VBox(6);
        traceBox.getStyleClass().add("card-panel");
        Label traceLbl = new Label("Step-by-Step Visualization Table / Window History:");
        traceLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #38bdf8;");
        TextArea traceArea = new TextArea();
        traceArea.setEditable(false);
        traceArea.getStyleClass().add("code-area");
        VBox.setVgrow(traceArea, Priority.ALWAYS);
        traceBox.getChildren().addAll(traceLbl, traceArea);

        results.getItems().addAll(sumBox, traceBox);
        results.setDividerPositions(0.4);

        sampleBtn.setOnAction(e -> {
            txtText.setText(defaultText);
            txtPat.setText(defaultPattern);
        });

        resetBtn.setOnAction(e -> {
            txtText.clear();
            txtPat.clear();
            sumArea.clear();
            traceArea.clear();
        });

        execBtn.setOnAction(e -> {
            String p = txtPat.getText();
            String t = txtText.getText();
            if (p == null || p.isEmpty() || t == null || t.isEmpty()) {
                Alert a = new Alert(Alert.AlertType.WARNING, "Pattern and text cannot be empty.");
                a.showAndWait();
                return;
            }

            SearchResult sr = matcher.search(p, t);
            sumArea.setText(String.format("ALGORITHM: %s\n" +
                            "Pattern: \"%s\"\n" +
                            "Total Matches Found: %d\n" +
                            "Match Offsets: [%s]\n" +
                            "Execution Time: %s (%d ns)\n" +
                            "Character Comparisons: %d\n\n" +
                            "Theoretical Space: %s\n" +
                            "Theoretical Time: %s",
                    matcher.getName(), sr.getPattern(), sr.getMatchCount(),
                    sr.getIndicesFormatted(), sr.getFormattedTime(), sr.getExecutionTimeNanos(),
                    sr.getComparisons(), comp.getSpaceComplexity(), comp.getTimeComplexityWorst()));

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < sr.getStepTraces().size(); i++) {
                sb.append("[").append(i + 1).append("] ").append(sr.getStepTraces().get(i)).append("\n");
            }
            traceArea.setText(sb.toString());
        });

        content.getChildren().addAll(compBanner, explLbl, form, results);
        tab.setContent(content);
        return tab;
    }

    private Tab createAhoCorasickTab() {
        Tab tab = new Tab("5. Aho-Corasick");
        tab.setClosable(false);

        VBox content = new VBox(12);
        content.setPadding(new Insets(14));

        AhoCorasickMatcher ac = new AhoCorasickMatcher();
        ComplexityInfo comp = ac.getComplexity();

        HBox compBanner = new HBox(12);
        compBanner.getChildren().addAll(
                createBadge("TIME COMPLEXITY", comp.getTimeComplexityWorst()),
                createBadge("SPACE COMPLEXITY", comp.getSpaceComplexity())
        );

        Label expl = new Label("Preprocesses dictionary into a trie with BFS failure and output links. Scans text in single streaming linear pass O(n + z).");
        expl.setStyle("-fx-text-fill: #94a3b8;");

        VBox form = new VBox(8);
        form.getStyleClass().add("card-panel");

        Label lblPats = new Label("Patterns (Comma-separated dictionary):");
        lblPats.setStyle("-fx-font-weight: bold; -fx-text-fill: #38bdf8;");
        TextField txtPats = new TextField("he, she, his, hers");

        Label lblText = new Label("Target Text Body:");
        lblText.setStyle("-fx-font-weight: bold; -fx-text-fill: #38bdf8;");
        TextArea txtText = new TextArea("ushers said that she saw his brother and he greeted hers with joy.");
        txtText.setPrefRowCount(3);
        txtText.setWrapText(true);

        HBox btnBar = new HBox(10);
        btnBar.setAlignment(Pos.CENTER_LEFT);
        Button execBtn = new Button("Execute Aho-Corasick");
        execBtn.getStyleClass().add("btn-primary");
        Button sampleBtn = new Button("Load Sample");
        sampleBtn.getStyleClass().add("btn-secondary");
        Button resetBtn = new Button("Reset");
        resetBtn.getStyleClass().add("btn-secondary");
        btnBar.getChildren().addAll(execBtn, sampleBtn, resetBtn);

        form.getChildren().addAll(lblPats, txtPats, lblText, txtText, btnBar);

        TextArea output = new TextArea();
        output.setEditable(false);
        output.getStyleClass().add("code-area");
        VBox.setVgrow(output, Priority.ALWAYS);

        sampleBtn.setOnAction(e -> {
            txtPats.setText("apple, banana, app, le");
            txtText.setText("The applicant ate an apple and a sweet banana under the tree.");
        });

        resetBtn.setOnAction(e -> {
            txtPats.clear();
            txtText.clear();
            output.clear();
        });

        execBtn.setOnAction(e -> {
            String rawPats = txtPats.getText();
            String text = txtText.getText();
            if (rawPats == null || text == null || text.isEmpty()) return;

            ManualArrayList<String> patList = com.texthack.util.ManualStringUtil.split(rawPats, ',');
            ManualArrayList<String> cleanPats = new ManualArrayList<>();
            for (int i = 0; i < patList.size(); i++) {
                String p = com.texthack.util.ManualStringUtil.trim(patList.get(i));
                if (!p.isEmpty()) cleanPats.add(p);
            }

            AhoCorasickMatcher.ACResult res = ac.search(cleanPats, text);
            StringBuilder sb = new StringBuilder();
            sb.append("AHO-CORASICK MULTI-PATTERN SEARCH EXECUTION REPORT\n");
            sb.append("===================================================\n");
            sb.append("Patterns Count: ").append(cleanPats.size()).append(" | Text Length: ").append(text.length()).append("\n");
            sb.append("Total Occurrences Found: ").append(res.matches.size()).append("\n");
            sb.append("Execution Time: ").append(res.formattedTime).append(" (").append(res.executionTimeNanos).append(" ns)\n\n");

            sb.append("MATCHED PATTERNS:\n");
            for (int i = 0; i < res.matches.size(); i++) {
                sb.append("• ").append(res.matches.get(i).toString()).append("\n");
            }
            sb.append("\nAUTOMATON CONSTRUCTION & BFS TRACE LOGS:\n");
            for (int i = 0; i < res.traceLogs.size(); i++) {
                sb.append("[").append(i + 1).append("] ").append(res.traceLogs.get(i)).append("\n");
            }
            output.setText(sb.toString());
        });

        content.getChildren().addAll(compBanner, expl, form, output);
        tab.setContent(content);
        return tab;
    }

    private Tab createSuffixArrayTab() {
        Tab tab = new Tab("6. Suffix Arrays");
        tab.setClosable(false);

        VBox content = new VBox(12);
        content.setPadding(new Insets(14));

        HBox compBanner = new HBox(12);
        compBanner.getChildren().addAll(
                createBadge("CONSTRUCTION TIME", "O(n log² n)"),
                createBadge("SPACE COMPLEXITY", "O(n)"),
                createBadge("LINEAR SA-IS BOUND", "O(n)")
        );

        Label expl = new Label("Constructs lexicographically sorted array of all suffixes via prefix-doubling. Includes full SA-IS theoretical walkthrough.");
        expl.setStyle("-fx-text-fill: #94a3b8;");

        VBox form = new VBox(8);
        form.getStyleClass().add("card-panel");

        Label lblText = new Label("Input String for Suffix Array:");
        lblText.setStyle("-fx-font-weight: bold; -fx-text-fill: #38bdf8;");
        TextField txtText = new TextField("banana");

        HBox btnBar = new HBox(10);
        btnBar.setAlignment(Pos.CENTER_LEFT);
        Button execBtn = new Button("Construct Suffix Array");
        execBtn.getStyleClass().add("btn-primary");
        Button saisBtn = new Button("View Linear O(n) SA-IS Walkthrough");
        saisBtn.getStyleClass().add("btn-accent");
        Button sampleBtn = new Button("Load Sample");
        sampleBtn.getStyleClass().add("btn-secondary");
        btnBar.getChildren().addAll(execBtn, saisBtn, sampleBtn);

        form.getChildren().addAll(lblText, txtText, btnBar);

        TextArea output = new TextArea();
        output.setEditable(false);
        output.getStyleClass().add("code-area");
        VBox.setVgrow(output, Priority.ALWAYS);

        sampleBtn.setOnAction(e -> txtText.setText("mississippi"));

        saisBtn.setOnAction(e -> output.setText(SuffixArrayBuilder.getSaisConceptualExplanation()));

        execBtn.setOnAction(e -> {
            String str = txtText.getText();
            if (str == null || str.isEmpty()) return;

            SuffixArray sa = SuffixArrayBuilder.build(str);
            StringBuilder sb = new StringBuilder();
            sb.append("SUFFIX ARRAY (SA) COMPUTED FOR: \"").append(str).append("\"\n");
            sb.append("Length n = ").append(sa.length()).append(" | Time: ").append(sa.getFormattedTime()).append("\n");
            sb.append("========================================================================\n");
            sb.append(String.format("%-6s | %-12s | %s\n", "Rank", "SA[i] (Offset)", "Sorted Suffix"));
            sb.append("------------------------------------------------------------------------\n");

            int[] arr = sa.getArray();
            for (int i = 0; i < arr.length; i++) {
                sb.append(String.format("%-6d | %-12d | %s\n", i, arr[i], sa.getSuffix(i)));
            }

            sb.append("\nCONSTRUCTION TRACE LOGS:\n");
            for (int i = 0; i < sa.getTraceLogs().size(); i++) {
                sb.append("[").append(i + 1).append("] ").append(sa.getTraceLogs().get(i)).append("\n");
            }
            output.setText(sb.toString());
        });

        content.getChildren().addAll(compBanner, expl, form, output);
        tab.setContent(content);
        return tab;
    }

    private Tab createKasaiLCPTab() {
        Tab tab = new Tab("7. Kasai LCP");
        tab.setClosable(false);

        VBox content = new VBox(12);
        content.setPadding(new Insets(14));

        HBox compBanner = new HBox(12);
        compBanner.getChildren().addAll(
                createBadge("TIME COMPLEXITY", "O(n) Linear"),
                createBadge("SPACE COMPLEXITY", "O(n)"),
                createBadge("THEOREM", "LCP[rank[i]] >= LCP[rank[i-1]] - 1")
        );

        Label expl = new Label("Kasai's algorithm constructs the Longest Common Prefix (LCP) array in linear O(n) time using suffix ranks.");
        expl.setStyle("-fx-text-fill: #94a3b8;");

        VBox form = new VBox(8);
        form.getStyleClass().add("card-panel");

        Label lblText = new Label("Input String for SA + LCP Construction:");
        lblText.setStyle("-fx-font-weight: bold; -fx-text-fill: #38bdf8;");
        TextField txtText = new TextField("banana");

        HBox btnBar = new HBox(10);
        btnBar.setAlignment(Pos.CENTER_LEFT);
        Button execBtn = new Button("Compute Suffix Array & Kasai LCP");
        execBtn.getStyleClass().add("btn-primary");
        Button sampleBtn = new Button("Load Sample");
        sampleBtn.getStyleClass().add("btn-secondary");
        btnBar.getChildren().addAll(execBtn, sampleBtn);

        form.getChildren().addAll(lblText, txtText, btnBar);

        TextArea output = new TextArea();
        output.setEditable(false);
        output.getStyleClass().add("code-area");
        VBox.setVgrow(output, Priority.ALWAYS);

        sampleBtn.setOnAction(e -> txtText.setText("abracadabra"));

        execBtn.setOnAction(e -> {
            String str = txtText.getText();
            if (str == null || str.isEmpty()) return;

            SuffixArray sa = SuffixArrayBuilder.build(str);
            KasaiLCP.LCPResult lcpRes = KasaiLCP.compute(sa);

            StringBuilder sb = new StringBuilder();
            sb.append("COMBINED SUFFIX ARRAY & KASAI LCP TABLE FOR: \"").append(str).append("\"\n");
            sb.append("Total Suffixes: ").append(sa.length()).append(" | Kasai Runtime: ").append(lcpRes.formattedTime).append("\n");
            sb.append("================================================================================\n");
            sb.append(String.format("%-6s | %-8s | %-8s | %s\n", "Rank i", "SA[i]", "LCP[i]", "Lexicographically Sorted Suffix"));
            sb.append("--------------------------------------------------------------------------------\n");

            int[] arr = sa.getArray();
            for (int i = 0; i < arr.length; i++) {
                sb.append(String.format("%-6d | %-8d | %-8d | %s\n", i, arr[i], lcpRes.lcp[i], sa.getSuffix(i)));
            }

            sb.append("\nKASAI STEP-BY-STEP LINEAR VERIFICATION LOGS:\n");
            for (int i = 0; i < lcpRes.traceLogs.size(); i++) {
                sb.append("[").append(i + 1).append("] ").append(lcpRes.traceLogs.get(i)).append("\n");
            }
            output.setText(sb.toString());
        });

        content.getChildren().addAll(compBanner, expl, form, output);
        tab.setContent(content);
        return tab;
    }

    private Tab createSuffixPreviewTab() {
        Tab tab = new Tab("8. Suffix Automata & Trees Preview");
        tab.setClosable(false);

        VBox content = new VBox(12);
        content.setPadding(new Insets(14));

        Label head = new Label("Suffix Automata & Suffix Trees – Advanced Elective Preview");
        head.setStyle("-fx-font-weight: bold; -fx-text-fill: #38bdf8; -fx-font-size: 16px;");

        TextArea area = new TextArea();
        area.setEditable(false);
        area.getStyleClass().add("code-area");
        VBox.setVgrow(area, Priority.ALWAYS);

        StringBuilder sb = new StringBuilder();
        sb.append(SuffixAutomaton.getOverview()).append("\n\n");
        sb.append("========================================================================================\n\n");
        sb.append(SuffixTreePreview.getOverview());

        area.setText(sb.toString());

        content.getChildren().addAll(head, area);
        tab.setContent(content);
        return tab;
    }

    private Label createBadge(String label, String value) {
        Label badge = new Label(label + "\n" + value);
        badge.getStyleClass().add("metric-badge");
        badge.setStyle("-fx-alignment: CENTER; -fx-text-alignment: CENTER; -fx-min-width: 150px;");
        return badge;
    }

    public Parent getView() {
        return root;
    }
}
