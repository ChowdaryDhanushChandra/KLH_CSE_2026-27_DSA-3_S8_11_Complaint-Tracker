package com.texthack.ui.views;

import com.texthack.core.ds.DynamicArray;
import com.texthack.core.model.AlgoResult;
import com.texthack.core.model.StepEvent;
import com.texthack.core.util.ManualStringUtil;
import com.texthack.module2_strings.*;
import com.texthack.ui.components.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Module 2: String Algorithms View.
 * Implements the unified 3-panel layout (Input, Animated Visualization, Info & Pseudocode)
 * with the bottom Step Player across all required algorithms.
 */
public class Module2View extends BorderPane {

    public enum StringAlgo {
        NAIVE("Naive O(nm)", "O(n · m)", "O(1)"),
        KMP("Knuth-Morris-Pratt (KMP)", "O(n + m)", "O(m)"),
        Z_FUNCTION("Z-Algorithm", "O(n + m)", "O(n + m)"),
        RABIN_KARP("Rabin-Karp (Double Hash)", "O(n + m)", "O(1)"),
        AHO_CORASICK("Aho-Corasick", "O(n + m + z)", "O(m · Σ)"),
        SUFFIX_ARRAY_LCP("Suffix Array & Kasai LCP", "O(n log² n)", "O(n)"),
        SUFFIX_AUTOMATON("Suffix Automaton Preview", "O(n · Σ)", "O(n · Σ)"),
        SUFFIX_TREE("Suffix Tree Preview", "O(n · Σ)", "O(n · Σ)");

        private final String displayName;
        private final String timeComp;
        private final String spaceComp;

        StringAlgo(String displayName, String timeComp, String spaceComp) {
            this.displayName = displayName;
            this.timeComp = timeComp;
            this.spaceComp = spaceComp;
        }

        public String getDisplayName() { return displayName; }
        public String getTimeComp() { return timeComp; }
        public String getSpaceComp() { return spaceComp; }
    }

    private StringAlgo currentAlgo = StringAlgo.NAIVE;

    // Center Panels & Components
    private final StringVisualizer stringVisualizer;
    private final MatrixGrid matrixGrid;
    private final GraphCanvas graphCanvas;
    private final StackPane visualStack;

    // Right Info Components
    private final Label algoTitleLabel;
    private final Label algoDescLabel;
    private final ComplexityBadge timeBadge;
    private final ComplexityBadge spaceBadge;
    private final CodeViewer codeViewer;
    private final Label statElapsedLabel;
    private final Label statOpsLabel;
    private final Label statMemoryLabel;
    private final Label statMatchesLabel;

    // Left Input Components
    private final TextField textInput;
    private final TextField patternInput;
    private final ComboBox<String> presetCombo;
    private final Label validationLabel;

    // Bottom Step Player
    private final StepPlayer stepPlayer;

    // Algorithm Engines
    private final NaiveMatcher naiveMatcher = new NaiveMatcher();
    private final KMPMatcher kmpMatcher = new KMPMatcher();
    private final ZFunctionMatcher zMatcher = new ZFunctionMatcher();
    private final RabinKarpMatcher rabinKarpMatcher = new RabinKarpMatcher();
    private final AhoCorasickMatcher ahoCorasickMatcher = new AhoCorasickMatcher();
    private final SuffixArrayBuilder saBuilder = new SuffixArrayBuilder();
    private final KasaiLCP kasaiLCP = new KasaiLCP();
    private final SuffixAutomatonPreview samPreview = new SuffixAutomatonPreview();
    private final SuffixTreePreview treePreview = new SuffixTreePreview();

    public Module2View() {
        setPadding(new Insets(14));

        // 1. Top Sub-Navigation Tabs
        setTop(buildSubNavbar());

        // 2. Center Visualization Stack
        stringVisualizer = new StringVisualizer();
        matrixGrid = new MatrixGrid();
        graphCanvas = new GraphCanvas();

        visualStack = new StackPane();
        visualStack.setStyle("-fx-background-color: #0F172A;");
        updateVisualStack();

        // 3. Right Info & Pseudocode Panel
        algoTitleLabel = new Label();
        algoTitleLabel.getStyleClass().add("card-title");

        algoDescLabel = new Label();
        algoDescLabel.setWrapText(true);
        algoDescLabel.getStyleClass().add("card-subtitle");

        timeBadge = new ComplexityBadge("O(n · m)");
        spaceBadge = new ComplexityBadge("O(1)", ComplexityBadge.BadgeType.ACCENT);

        HBox badgeBox = new HBox(8, timeBadge, spaceBadge);
        badgeBox.setAlignment(Pos.CENTER_LEFT);

        codeViewer = new CodeViewer(NaiveMatcher.PSEUDOCODE);

        statElapsedLabel = new Label("0 µs");
        statElapsedLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #38BDF8;");

        statOpsLabel = new Label("0");
        statOpsLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #22C55E;");

        statMemoryLabel = new Label("0 B");
        statMemoryLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #F59E0B;");

        statMatchesLabel = new Label("0");
        statMatchesLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #F8FAFC;");

        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(12);
        statsGrid.setVgap(6);
        statsGrid.add(new Label("Elapsed Time:"), 0, 0);
        statsGrid.add(statElapsedLabel, 1, 0);
        statsGrid.add(new Label("Comparisons:"), 0, 1);
        statsGrid.add(statOpsLabel, 1, 1);
        statsGrid.add(new Label("Memory Est:"), 0, 2);
        statsGrid.add(statMemoryLabel, 1, 2);
        statsGrid.add(new Label("Matches Found:"), 0, 3);
        statsGrid.add(statMatchesLabel, 1, 3);

        VBox rightBox = new VBox(12,
                new Card("Algorithm Canon", "", new VBox(6, algoTitleLabel, algoDescLabel, badgeBox)),
                new Card("Pseudocode & Active Line", "", codeViewer),
                new Card("Execution Telemetry", "", statsGrid)
        );
        rightBox.setPrefWidth(320);
        rightBox.setMinWidth(280);

        // 4. Left Input Panel
        textInput = new TextField("ABABDABACDABABCABAB");
        textInput.getStyleClass().add("text-input");

        patternInput = new TextField("ABABCABAB");
        patternInput.getStyleClass().add("text-input");

        presetCombo = new ComboBox<>();
        presetCombo.getStyleClass().add("combo-box");
        presetCombo.setMaxWidth(Double.MAX_VALUE);
        loadPresetsForCurrentAlgo();

        Button runBtn = new Button("▶ Execute Algorithm");
        runBtn.getStyleClass().add("primary-btn");
        runBtn.setMaxWidth(Double.MAX_VALUE);
        runBtn.setOnAction(e -> runCurrentAlgorithm());

        Button randomBtn = new Button("🎲 Randomize");
        randomBtn.getStyleClass().add("secondary-btn");
        randomBtn.setOnAction(e -> randomizeInputs());

        Button resetBtn = new Button("↺ Reset");
        resetBtn.getStyleClass().add("secondary-btn");
        resetBtn.setOnAction(e -> resetInputs());

        HBox btnRow = new HBox(8, randomBtn, resetBtn);
        btnRow.setAlignment(Pos.CENTER);

        validationLabel = new Label();
        validationLabel.setWrapText(true);
        validationLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #22C55E;");

        VBox leftBox = new VBox(12,
                new Card("Input Configuration", "Parameters & Corpus Source",
                        new VBox(10,
                                new Label("Source Text (T):"), textInput,
                                new Label("Target Pattern / Dictionary (P):"), patternInput,
                                new Label("Sample Presets:"), presetCombo,
                                runBtn, btnRow, validationLabel
                        ))
        );
        leftBox.setPrefWidth(280);
        leftBox.setMinWidth(250);

        // 5. Center Split/Layout
        VBox centerBox = new VBox(10,
                new Card("Interactive Visualization Canvas", "Step-by-step state representation", visualStack)
        );
        VBox.setVgrow(centerBox, Priority.ALWAYS);

        BorderPane mainContent = new BorderPane();
        mainContent.setLeft(leftBox);
        mainContent.setCenter(centerBox);
        mainContent.setRight(rightBox);
        BorderPane.setMargin(leftBox, new Insets(8, 8, 8, 0));
        BorderPane.setMargin(centerBox, new Insets(8, 8, 8, 8));
        BorderPane.setMargin(rightBox, new Insets(8, 0, 8, 8));

        setCenter(mainContent);

        // 6. Bottom Step Player
        stepPlayer = new StepPlayer();
        stepPlayer.setStepListener(this::handleAnimationStep);
        setBottom(stepPlayer);

        // Initial setup
        switchAlgorithm(StringAlgo.KMP);
    }

    private Node buildSubNavbar() {
        HBox bar = new HBox(6);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: #1E293B; -fx-padding: 8px 12px; -fx-background-radius: 8px;");

        ToggleGroup group = new ToggleGroup();
        for (StringAlgo algo : StringAlgo.values()) {
            ToggleButton tb = new ToggleButton(algo.getDisplayName());
            tb.setToggleGroup(group);
            tb.getStyleClass().add("secondary-btn");
            if (algo == StringAlgo.KMP) tb.setSelected(true);

            tb.setOnAction(e -> {
                if (tb.isSelected()) {
                    switchAlgorithm(algo);
                }
            });
            bar.getChildren().add(tb);
        }

        ScrollPane sp = new ScrollPane(bar);
        sp.setFitToHeight(true);
        sp.setStyle("-fx-background-color: transparent;");
        return sp;
    }

    private void switchAlgorithm(StringAlgo algo) {
        this.currentAlgo = algo;
        algoTitleLabel.setText(algo.getDisplayName());
        timeBadge.setText(algo.getTimeComp());
        spaceBadge.setText(algo.getSpaceComp());

        loadPresetsForCurrentAlgo();
        updateVisualStack();

        switch (algo) {
            case NAIVE:
                algoDescLabel.setText("Sliding window exact character comparisons. Shifts window by 1 on mismatch without memory of past matches.");
                replaceCodeViewer(NaiveMatcher.PSEUDOCODE);
                break;
            case KMP:
                algoDescLabel.setText("Knuth-Morris-Pratt preprocesses pattern into LPS (π) array. Falls back via failure function without backtracking text pointer.");
                replaceCodeViewer(KMPMatcher.PSEUDOCODE);
                break;
            case Z_FUNCTION:
                algoDescLabel.setText("Computes longest prefix match intervals [L, R] over P + '$' + T. Reuses prior substring matches within the current Z-box.");
                replaceCodeViewer(ZFunctionMatcher.PSEUDOCODE);
                break;
            case RABIN_KARP:
                algoDescLabel.setText("Double hashing polynomial rolling hashes (Base 256/313, Modulo 10⁹+7 / 10⁹+9) with spurious collision inspection.");
                replaceCodeViewer(RabinKarpMatcher.PSEUDOCODE);
                break;
            case AHO_CORASICK:
                algoDescLabel.setText("Trie-based deterministic automaton with BFS failure transitions and output links for streaming multi-pattern search.");
                replaceCodeViewer(AhoCorasickMatcher.PSEUDOCODE);
                break;
            case SUFFIX_ARRAY_LCP:
                algoDescLabel.setText("Manber-Myers prefix-doubling O(n log² n) suffix array construction combined with Kasai's linear-time O(n) LCP array.");
                replaceCodeViewer(SuffixArrayBuilder.PSEUDOCODE);
                break;
            case SUFFIX_AUTOMATON:
                algoDescLabel.setText("Directed Acyclic Word Graph (DAWG) compact automaton representing all substrings in <= 2n-1 states and 3n-4 transitions.");
                replaceCodeViewer(SuffixAutomatonPreview.PSEUDOCODE);
                break;
            case SUFFIX_TREE:
                algoDescLabel.setText("Ukkonen's linear-time suffix tree with implicit active points, edge labels, and suffix link transitions.");
                replaceCodeViewer(SuffixTreePreview.PSEUDOCODE);
                break;
        }

        runCurrentAlgorithm();
    }

    private void replaceCodeViewer(String[] code) {
        VBox parent = (VBox) codeViewer.getParent();
        if (parent != null) {
            int idx = parent.getChildren().indexOf(codeViewer);
            if (idx >= 0) {
                CodeViewer newCv = new CodeViewer(code);
                parent.getChildren().set(idx, newCv);
            }
        }
    }

    private void updateVisualStack() {
        visualStack.getChildren().clear();
        if (currentAlgo == StringAlgo.AHO_CORASICK || currentAlgo == StringAlgo.SUFFIX_AUTOMATON || currentAlgo == StringAlgo.SUFFIX_TREE) {
            visualStack.getChildren().add(graphCanvas);
        } else if (currentAlgo == StringAlgo.SUFFIX_ARRAY_LCP) {
            visualStack.getChildren().add(matrixGrid);
        } else {
            VBox combinedBox = new VBox(12, stringVisualizer, matrixGrid);
            visualStack.getChildren().add(combinedBox);
        }
    }

    private void runCurrentAlgorithm() {
        String text = textInput.getText();
        String pattern = patternInput.getText();

        if (text == null || text.trim().isEmpty()) {
            validationLabel.setText("⚠ Please provide non-empty source text.");
            validationLabel.setStyle("-fx-text-fill: #EF4444;");
            return;
        }

        validationLabel.setText("✓ Valid input. Execution complete.");
        validationLabel.setStyle("-fx-text-fill: #22C55E;");

        switch (currentAlgo) {
            case NAIVE: {
                stringVisualizer.setStrings(text, pattern);
                AlgoResult<DynamicArray<Integer>> res = naiveMatcher.search(text, pattern);
                stepPlayer.setSteps(res.getSteps());
                updateTelemetry(res.getElapsedMicros(), res.getComparisons(), res.getMemoryBytes(), res.getOutput().size());
                break;
            }
            case KMP: {
                stringVisualizer.setStrings(text, pattern);
                AlgoResult<KMPMatcher.KMPResult> res = kmpMatcher.search(text, pattern);
                stepPlayer.setSteps(res.getSteps());
                int[] lps = res.getOutput().getLpsTable();
                String[] headers = new String[pattern.length()];
                for (int i = 0; i < pattern.length(); i++) headers[i] = pattern.charAt(i) + " (" + i + ")";
                matrixGrid.setArray1D("LPS [π] Table:", headers, lps);
                updateTelemetry(res.getElapsedMicros(), res.getComparisons(), res.getMemoryBytes(), res.getOutput().getMatches().size());
                break;
            }
            case Z_FUNCTION: {
                stringVisualizer.setStrings(text, pattern);
                AlgoResult<ZFunctionMatcher.ZResult> res = zMatcher.search(text, pattern);
                stepPlayer.setSteps(res.getSteps());
                int[] z = res.getOutput().getZArray();
                String comb = res.getOutput().getCombinedString();
                String[] headers = new String[Math.min(z.length, 25)];
                int[] vals = new int[headers.length];
                for (int i = 0; i < headers.length; i++) {
                    headers[i] = comb.charAt(i) + " (" + i + ")";
                    vals[i] = z[i];
                }
                matrixGrid.setArray1D("Z-Array Values:", headers, vals);
                updateTelemetry(res.getElapsedMicros(), res.getComparisons(), res.getMemoryBytes(), res.getOutput().getMatches().size());
                break;
            }
            case RABIN_KARP: {
                stringVisualizer.setStrings(text, pattern);
                AlgoResult<RabinKarpMatcher.RabinKarpResult> res = rabinKarpMatcher.search(text, pattern);
                stepPlayer.setSteps(res.getSteps());
                updateTelemetry(res.getElapsedMicros(), res.getComparisons(), res.getMemoryBytes(), res.getOutput().getMatches().size());
                break;
            }
            case AHO_CORASICK: {
                DynamicArray<String> patterns = ManualStringUtil.split(pattern, ',');
                for (int p = 0; p < patterns.size(); p++) {
                    patterns.set(p, ManualStringUtil.trim(patterns.get(p)));
                }
                AlgoResult<AhoCorasickMatcher.AhoCorasickResult> res = ahoCorasickMatcher.search(text, patterns);
                stepPlayer.setSteps(res.getSteps());
                graphCanvas.drawTrie(res.getOutput().getTrie(), 0);
                updateTelemetry(res.getElapsedMicros(), res.getComparisons(), res.getMemoryBytes(), res.getOutput().getMatches().size());
                break;
            }
            case SUFFIX_ARRAY_LCP: {
                AlgoResult<int[]> saRes = saBuilder.buildSuffixArray(text);
                int[] sa = saRes.getOutput();
                AlgoResult<int[]> lcpRes = kasaiLCP.computeLCP(text, sa);
                int[] lcp = lcpRes.getOutput();
                stepPlayer.setSteps(saRes.getSteps());

                String[] headers = new String[sa.length];
                for (int i = 0; i < sa.length; i++) headers[i] = "Rank " + i;
                matrixGrid.setArray1D("Suffix Array (SA):", headers, sa);
                updateTelemetry(saRes.getElapsedMicros() + lcpRes.getElapsedMicros(),
                        saRes.getComparisons() + lcpRes.getComparisons(),
                        saRes.getMemoryBytes() + lcpRes.getMemoryBytes(), sa.length);
                break;
            }
            case SUFFIX_AUTOMATON: {
                AlgoResult<SuffixAutomatonPreview.SAMResult> res = samPreview.buildAutomaton(text);
                stepPlayer.setSteps(res.getSteps());
                graphCanvas.drawEmpty("Suffix Automaton built with " + res.getOutput().getStateCount() + " states for: \"" + text + "\"");
                updateTelemetry(res.getElapsedMicros(), res.getComparisons(), res.getMemoryBytes(), res.getOutput().getStateCount());
                break;
            }
            case SUFFIX_TREE: {
                AlgoResult<SuffixTreePreview.TreeResult> res = treePreview.buildTree(text);
                stepPlayer.setSteps(res.getSteps());
                graphCanvas.drawEmpty("Suffix Tree created with " + res.getOutput().getNodes().size() + " nodes & " + res.getOutput().getEdges().size() + " edges.");
                updateTelemetry(res.getElapsedMicros(), res.getComparisons(), res.getMemoryBytes(), res.getOutput().getNodes().size());
                break;
            }
        }
    }

    private void handleAnimationStep(int stepIndex, StepEvent step) {
        if (step == null) return;
        codeViewer.setActiveLine(step.getCodeLine());

        boolean isMatch = (step.getType() == StepEvent.StepType.COMPARE_MATCH || step.getType() == StepEvent.StepType.PATTERN_FOUND);
        boolean isMismatch = (step.getType() == StepEvent.StepType.COMPARE_MISMATCH || step.getType() == StepEvent.StepType.COLLISION_DETECTED);

        stringVisualizer.updateState(step.getPrimaryIndex(), step.getSecondaryIndex(),
                step.getRangeStart(), step.getRangeEnd(), isMatch, isMismatch);

        if (step.getType() == StepEvent.StepType.TABLE_UPDATE && step.getSecondaryIndex() >= 0) {
            matrixGrid.updateActiveCell(step.getPrimaryIndex());
        }
    }

    private void updateTelemetry(long micros, long ops, long memory, int matches) {
        statElapsedLabel.setText(micros + " µs");
        statOpsLabel.setText(String.valueOf(ops));
        statMemoryLabel.setText(memory + " B");
        statMatchesLabel.setText(String.valueOf(matches));
    }

    private void loadPresetsForCurrentAlgo() {
        presetCombo.getItems().clear();
        if (currentAlgo == StringAlgo.AHO_CORASICK) {
            presetCombo.getItems().addAll("ushers [he, she, his, hers]", "technology [tech, log, no, on]", "coronavirus [corona, virus, nav]");
            presetCombo.getSelectionModel().select(0);
        } else if (currentAlgo == StringAlgo.SUFFIX_ARRAY_LCP || currentAlgo == StringAlgo.SUFFIX_AUTOMATON || currentAlgo == StringAlgo.SUFFIX_TREE) {
            presetCombo.getItems().addAll("banana$", "mississippi$", "abracadabra$");
            presetCombo.getSelectionModel().select(0);
        } else {
            presetCombo.getItems().addAll("ABABDABACDABABCABAB -> ABABCABAB", "AABAACAADAABAABA -> AABA", "ABC ABCDAB ABCDABCDABDE -> ABCDABD", "Double Hash Collision Test");
            presetCombo.getSelectionModel().select(0);
        }

        presetCombo.setOnAction(e -> applySelectedPreset());
    }

    private void applySelectedPreset() {
        String sel = presetCombo.getValue();
        if (sel == null) return;

        if (sel.contains("->")) {
            String[] parts = sel.split("->");
            textInput.setText(parts[0].trim());
            patternInput.setText(parts[1].trim());
        } else if (sel.contains("[") && sel.contains("]")) {
            int b1 = sel.indexOf('[');
            int b2 = sel.indexOf(']');
            textInput.setText(sel.substring(0, b1).trim());
            patternInput.setText(sel.substring(b1 + 1, b2).trim());
        } else {
            textInput.setText(sel);
            patternInput.setText("na");
        }
        runCurrentAlgorithm();
    }

    private void randomizeInputs() {
        String chars = "ABCD";
        StringBuilder sbT = new StringBuilder();
        StringBuilder sbP = new StringBuilder();
        for (int i = 0; i < 20; i++) sbT.append(chars.charAt((int) (Math.random() * chars.length())));
        for (int i = 0; i < 4; i++) sbP.append(chars.charAt((int) (Math.random() * chars.length())));
        textInput.setText(sbT.toString());
        patternInput.setText(sbP.toString());
        runCurrentAlgorithm();
    }

    private void resetInputs() {
        textInput.setText("ABABDABACDABABCABAB");
        patternInput.setText("ABABCABAB");
        runCurrentAlgorithm();
    }
}
