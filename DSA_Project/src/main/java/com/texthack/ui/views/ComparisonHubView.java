package com.texthack.ui.views;

import com.texthack.core.ds.DynamicArray;
import com.texthack.core.model.AlgoResult;
import com.texthack.module2_strings.KMPMatcher;
import com.texthack.module2_strings.NaiveMatcher;
import com.texthack.module2_strings.RabinKarpMatcher;
import com.texthack.module2_strings.ZFunctionMatcher;
import com.texthack.ui.components.Card;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Laboratory: Comparison Hub.
 * Executes multiple algorithms side-by-side on identical input,
 * rendering a results TableView, telemetry comparison, and CSV export.
 */
public class ComparisonHubView extends ScrollPane {

    public static class RowData {
        public final String algo;
        public final String matches;
        public final String timeMicros;
        public final String comparisons;
        public final String memory;
        public final String complexity;

        public RowData(String algo, String matches, String timeMicros, String comparisons, String memory, String complexity) {
            this.algo = algo;
            this.matches = matches;
            this.timeMicros = timeMicros;
            this.comparisons = comparisons;
            this.memory = memory;
            this.complexity = complexity;
        }
    }

    private final TextField textInput;
    private final TextField patternInput;
    private final CheckBox naiveCheck;
    private final CheckBox kmpCheck;
    private final CheckBox zCheck;
    private final CheckBox rkCheck;
    private final TableView<RowData> table;
    private final ObservableList<RowData> tableData;

    private final NaiveMatcher naive = new NaiveMatcher();
    private final KMPMatcher kmp = new KMPMatcher();
    private final ZFunctionMatcher zFunc = new ZFunctionMatcher();
    private final RabinKarpMatcher rk = new RabinKarpMatcher();

    public ComparisonHubView() {
        setFitToWidth(true);
        setStyle("-fx-background-color: transparent;");

        VBox content = new VBox(16);
        content.setPadding(new Insets(20));

        Label title = new Label("🔬 Algorithm Comparison Hub");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #38BDF8;");

        Label sub = new Label("Benchmarking multiple algorithms side-by-side on identical input corpora.");
        sub.setStyle("-fx-font-size: 12px; -fx-text-fill: #94A3B8;");

        // Input Card
        textInput = new TextField("ABABDABACDABABCABABABABDABACDABABCABABABABDABACDABABCABAB");
        textInput.getStyleClass().add("text-input");

        patternInput = new TextField("ABABCABAB");
        patternInput.getStyleClass().add("text-input");

        naiveCheck = new CheckBox("Naive O(nm)"); naiveCheck.setSelected(true); naiveCheck.setStyle("-fx-text-fill: #F8FAFC;");
        kmpCheck = new CheckBox("KMP O(n+m)"); kmpCheck.setSelected(true); kmpCheck.setStyle("-fx-text-fill: #F8FAFC;");
        zCheck = new CheckBox("Z-Algorithm O(n+m)"); zCheck.setSelected(true); zCheck.setStyle("-fx-text-fill: #F8FAFC;");
        rkCheck = new CheckBox("Rabin-Karp O(n+m)"); rkCheck.setSelected(true); rkCheck.setStyle("-fx-text-fill: #F8FAFC;");

        HBox checkRow = new HBox(16, naiveCheck, kmpCheck, zCheck, rkCheck);

        Button runBtn = new Button("▶ Execute Benchmark Hub");
        runBtn.getStyleClass().add("primary-btn");
        runBtn.setOnAction(e -> runBenchmark());

        Button exportBtn = new Button("📥 Export CSV");
        exportBtn.getStyleClass().add("secondary-btn");
        exportBtn.setOnAction(e -> exportCsv());

        HBox btnRow = new HBox(12, runBtn, exportBtn);

        Card inputCard = new Card("Benchmark Parameters", "Set input text, pattern, and select algorithms",
                new VBox(10,
                        new Label("Source Text:"), textInput,
                        new Label("Search Pattern:"), patternInput,
                        new Label("Select Competing Algorithms:"), checkRow,
                        btnRow
                ));

        // Results Table
        table = new TableView<>();
        tableData = FXCollections.observableArrayList();
        table.setItems(tableData);
        table.setPrefHeight(260);

        TableColumn<RowData, String> colAlgo = new TableColumn<>("Algorithm");
        colAlgo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().algo));
        colAlgo.setPrefWidth(180);

        TableColumn<RowData, String> colMatches = new TableColumn<>("Matches");
        colMatches.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().matches));
        colMatches.setPrefWidth(90);

        TableColumn<RowData, String> colTime = new TableColumn<>("Elapsed Time (µs)");
        colTime.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().timeMicros));
        colTime.setPrefWidth(140);

        TableColumn<RowData, String> colComp = new TableColumn<>("Comparisons");
        colComp.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().comparisons));
        colComp.setPrefWidth(130);

        TableColumn<RowData, String> colMem = new TableColumn<>("Est. Memory (Bytes)");
        colMem.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().memory));
        colMem.setPrefWidth(160);

        TableColumn<RowData, String> colComplexity = new TableColumn<>("Worst Case");
        colComplexity.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().complexity));
        colComplexity.setPrefWidth(120);

        table.getColumns().addAll(colAlgo, colMatches, colTime, colComp, colMem, colComplexity);

        Card tableCard = new Card("Comparative Telemetry Matrix", "Execution statistics across algorithms", table);

        content.getChildren().addAll(new VBox(4, title, sub), inputCard, tableCard);
        setContent(content);

        runBenchmark();
    }

    private void runBenchmark() {
        tableData.clear();
        String text = textInput.getText();
        String pat = patternInput.getText();

        if (text == null || pat == null || text.isEmpty() || pat.isEmpty()) return;

        if (naiveCheck.isSelected()) {
            AlgoResult<DynamicArray<Integer>> r = naive.search(text, pat);
            tableData.add(new RowData("Naive Pattern Matcher", String.valueOf(r.getOutput().size()),
                    String.valueOf(r.getElapsedMicros()), String.valueOf(r.getComparisons()),
                    String.valueOf(r.getMemoryBytes()), r.getTimeComplexity()));
        }

        if (kmpCheck.isSelected()) {
            AlgoResult<KMPMatcher.KMPResult> r = kmp.search(text, pat);
            tableData.add(new RowData("Knuth-Morris-Pratt (KMP)", String.valueOf(r.getOutput().getMatches().size()),
                    String.valueOf(r.getElapsedMicros()), String.valueOf(r.getComparisons()),
                    String.valueOf(r.getMemoryBytes()), r.getTimeComplexity()));
        }

        if (zCheck.isSelected()) {
            AlgoResult<ZFunctionMatcher.ZResult> r = zFunc.search(text, pat);
            tableData.add(new RowData("Z-Algorithm", String.valueOf(r.getOutput().getMatches().size()),
                    String.valueOf(r.getElapsedMicros()), String.valueOf(r.getComparisons()),
                    String.valueOf(r.getMemoryBytes()), r.getTimeComplexity()));
        }

        if (rkCheck.isSelected()) {
            AlgoResult<RabinKarpMatcher.RabinKarpResult> r = rk.search(text, pat);
            tableData.add(new RowData("Rabin-Karp (Double Hash)", String.valueOf(r.getOutput().getMatches().size()),
                    String.valueOf(r.getElapsedMicros()), String.valueOf(r.getComparisons()),
                    String.valueOf(r.getMemoryBytes()), r.getTimeComplexity()));
        }
    }

    private void exportCsv() {
        StringBuilder sb = new StringBuilder();
        sb.append("Algorithm,Matches,Elapsed_Micros,Comparisons,Memory_Bytes,Complexity\n");
        for (RowData row : tableData) {
            sb.append("\"").append(row.algo).append("\",")
              .append(row.matches).append(",")
              .append(row.timeMicros).append(",")
              .append(row.comparisons).append(",")
              .append(row.memory).append(",")
              .append("\"").append(row.complexity).append("\"\n");
        }

        FileChooser fc = new FileChooser();
        fc.setTitle("Export Benchmark Results to CSV");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv"));
        fc.setInitialFileName("texthack_benchmark.csv");

        File file = fc.showSaveDialog(getScene().getWindow());
        if (file != null) {
            try (FileWriter fw = new FileWriter(file)) {
                fw.write(sb.toString());
            } catch (IOException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to write CSV: " + ex.getMessage());
                alert.showAndWait();
            }
        }
    }
}
