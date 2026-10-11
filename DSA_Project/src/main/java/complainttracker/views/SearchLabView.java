package complainttracker.views;

import complainttracker.algorithms.*;
import complainttracker.models.Complaint;
import complainttracker.services.ComplaintService;
import complainttracker.utils.UIUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SearchLabView {
    private final Consumer<Complaint> onSelectComplaint;
    private TableView<Complaint> resultsTable;
    private TextArea txtAlgorithmSteps;
    private HBox metricsCardsBox;

    public SearchLabView(Consumer<Complaint> onSelectComplaint) {
        this.onSelectComplaint = onSelectComplaint;
    }

    public Node getView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(24));

        Label lblTitle = new Label("Search & Pattern Matching Laboratory");
        lblTitle.getStyleClass().add("page-title");
        Label lblSub = new Label("Empirical performance comparison of Hash Lookups, KMP, Rabin-Karp, and Naive String Search.");
        lblSub.getStyleClass().add("page-subtitle");
        VBox headerBox = new VBox(2, lblTitle, lblSub);

        // Search Control Card
        VBox searchCard = new VBox(12);
        searchCard.getStyleClass().add("card");

        TextField txtQuery = new TextField("Water");
        txtQuery.setPromptText("Enter pattern to search across complaints corpus (e.g. 'burst', 'transformer', 'pothole', 'CMP-1001')...");
        txtQuery.setPrefWidth(400);

        Button btnCompareAll = new Button("⚡ Benchmark & Run All 4 Search Algorithms");
        btnCompareAll.getStyleClass().add("btn-primary");

        HBox queryRow = new HBox(12, txtQuery, btnCompareAll);
        queryRow.setAlignment(Pos.CENTER_LEFT);
        searchCard.getChildren().add(queryRow);

        // Metrics comparison cards row
        metricsCardsBox = new HBox(14);

        // Results Table
        Label lblTableHeading = new Label("Matched Complaints in Corpus");
        lblTableHeading.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

        resultsTable = new TableView<>();
        resultsTable.setPrefHeight(220);

        TableColumn<Complaint, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(85);
        colId.setStyle("-fx-font-weight: bold; -fx-text-fill: #2563EB;");

        TableColumn<Complaint, String> colTitle = new TableColumn<>("Title");
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colTitle.setPrefWidth(280);

        TableColumn<Complaint, String> colDesc = new TableColumn<>("Description Preview");
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        colDesc.setPrefWidth(350);

        TableColumn<Complaint, String> colCat = new TableColumn<>("Category");
        colCat.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategory().getDisplayName()));
        colCat.setPrefWidth(140);

        TableColumn<Complaint, Void> colPrio = new TableColumn<>("Priority");
        colPrio.setPrefWidth(95);
        colPrio.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Complaint c = getTableRow().getItem();
                    setGraphic(UIUtils.createPriorityBadge(c.getPriority()));
                    setAlignment(Pos.CENTER);
                }
            }
        });

        resultsTable.getColumns().addAll(colId, colTitle, colDesc, colCat, colPrio);
        resultsTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && resultsTable.getSelectionModel().getSelectedItem() != null) {
                onSelectComplaint.accept(resultsTable.getSelectionModel().getSelectedItem());
            }
        });

        VBox tableCard = new VBox(8, lblTableHeading, resultsTable);
        tableCard.getStyleClass().add("card");

        // Step Trace Output
        VBox traceBox = new VBox(6);
        traceBox.getStyleClass().add("card");
        Label lblTrace = new Label("Algorithmic Execution Trace & Comparison Metrics");
        lblTrace.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155;");

        txtAlgorithmSteps = new TextArea();
        txtAlgorithmSteps.setEditable(false);
        txtAlgorithmSteps.setPrefRowCount(7);
        txtAlgorithmSteps.setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-size: 12px; -fx-control-inner-background: #0F172A; -fx-text-fill: #38BDF8;");
        txtAlgorithmSteps.setText("Click 'Benchmark & Run All 4 Search Algorithms' to evaluate search metrics.");

        traceBox.getChildren().addAll(lblTrace, txtAlgorithmSteps);

        btnCompareAll.setOnAction(e -> runBenchmark(txtQuery.getText().trim()));

        // Run initial default search
        runBenchmark(txtQuery.getText().trim());

        root.getChildren().addAll(headerBox, searchCard, metricsCardsBox, tableCard, traceBox);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        return scroll;
    }

    private void runBenchmark(String pattern) {
        if (pattern.isEmpty()) {
            UIUtils.showError("Validation Error", "Query is Empty", "Please enter a search query.");
            return;
        }

        List<Complaint> allComplaints = ComplaintService.getInstance().getAllComplaints();
        CustomHashMap<String, Complaint> hashMap = ComplaintService.getInstance().getIdIndex();

        // 1. Hash Map Direct ID Lookup
        long hashStart = System.nanoTime();
        Complaint hashFound = hashMap.get(pattern.toUpperCase());
        long hashElapsedNanos = System.nanoTime() - hashStart;
        CustomHashMap.HashTrace hashTrace = hashMap.traceSearch(pattern.toUpperCase());

        // Concatenate corpus text for string search
        StringBuilder corpusBuilder = new StringBuilder();
        for (Complaint c : allComplaints) {
            corpusBuilder.append(c.getId()).append(" ").append(c.getTitle()).append(" ").append(c.getDescription()).append("\n");
        }
        String corpus = corpusBuilder.toString();

        // 2. Knuth-Morris-Pratt Search
        StringMatchResult kmpResult = KMPStringMatcher.search(corpus, pattern);

        // 3. Rabin-Karp Search
        StringMatchResult rkResult = RabinKarpMatcher.search(corpus, pattern);

        // 4. Naive Search
        StringMatchResult naiveResult = NaiveMatcher.search(corpus, pattern);

        // Find matching complaints for table
        List<Complaint> matchedComplaints = new ArrayList<>();
        String lowerPat = pattern.toLowerCase();
        for (Complaint c : allComplaints) {
            if (c.getId().toLowerCase().contains(lowerPat)
                    || c.getTitle().toLowerCase().contains(lowerPat)
                    || c.getDescription().toLowerCase().contains(lowerPat)
                    || c.getLocation().toLowerCase().contains(lowerPat)) {
                matchedComplaints.add(c);
            }
        }
        resultsTable.setItems(FXCollections.observableArrayList(matchedComplaints));

        // Update Metric Cards
        metricsCardsBox.getChildren().clear();
        metricsCardsBox.getChildren().addAll(
                UIUtils.createStatCard("1. Hash Index", String.format("%.2f µs", hashElapsedNanos / 1000.0),
                        hashFound != null ? "Found (O(1))" : "Key Miss", "#2563EB"),
                UIUtils.createStatCard("2. KMP Matcher", String.format("%.2f µs", kmpResult.getExecutionTimeMicros()),
                        kmpResult.getTotalComparisons() + " comps (O(N+M))", "#10B981"),
                UIUtils.createStatCard("3. Rabin-Karp", String.format("%.2f µs", rkResult.getExecutionTimeMicros()),
                        rkResult.getTotalComparisons() + " comps (Rolling)", "#F59E0B"),
                UIUtils.createStatCard("4. Naive Search", String.format("%.2f µs", naiveResult.getExecutionTimeMicros()),
                        naiveResult.getTotalComparisons() + " comps (O(N*M))", "#EF4444")
        );

        // Update Trace Log
        StringBuilder log = new StringBuilder();
        log.append(String.format("=== PATTERN SEARCH BENCHMARK FOR: \"%s\" (Corpus Length: %d chars) ===%n%n", pattern, corpus.length()));

        log.append("[1. HASH SEARCH] ").append(hashTrace.pathDescription).append("\n\n");

        log.append(String.format("[2. KMP MATCHER] Matches: %d | Time: %.2f µs | Comparisons: %d%n",
                kmpResult.getMatchCount(), kmpResult.getExecutionTimeMicros(), kmpResult.getTotalComparisons()));
        if (!kmpResult.getTraceSteps().isEmpty()) {
            log.append("  • ").append(kmpResult.getTraceSteps().get(0)).append("\n");
            for (int i = 1; i < Math.min(4, kmpResult.getTraceSteps().size()); i++) {
                log.append("  • ").append(kmpResult.getTraceSteps().get(i)).append("\n");
            }
        }
        log.append("\n");

        log.append(String.format("[3. RABIN-KARP] Matches: %d | Time: %.2f µs | Total Comparisons: %d%n",
                rkResult.getMatchCount(), rkResult.getExecutionTimeMicros(), rkResult.getTotalComparisons()));
        for (int i = 0; i < Math.min(3, rkResult.getTraceSteps().size()); i++) {
            log.append("  • ").append(rkResult.getTraceSteps().get(i)).append("\n");
        }
        log.append("\n");

        log.append(String.format("[4. NAIVE MATCHER] Matches: %d | Time: %.2f µs | Comparisons: %d (Baseline Brute-Force)%n",
                naiveResult.getMatchCount(), naiveResult.getExecutionTimeMicros(), naiveResult.getTotalComparisons()));

        txtAlgorithmSteps.setText(log.toString());
    }
}
