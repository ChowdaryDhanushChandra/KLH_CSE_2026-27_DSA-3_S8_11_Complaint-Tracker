package com.texthack.controller;

import com.texthack.datastructures.ManualArrayList;
import com.texthack.service.BenchmarkService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Controller and View for the Algorithm Comparison Hub.
 * Executes multiple algorithms simultaneously on identical inputs,
 * populating a JavaFX TableView comparing execution times, operations, and correctness.
 */
public class ComparisonController {

    private final VBox root;
    private final BenchmarkService benchmarkService;

    public ComparisonController() {
        this.benchmarkService = new BenchmarkService();
        this.root = new VBox(14);
        root.setPadding(new Insets(20));

        Label title = new Label("Algorithm Comparison & Benchmarking Hub");
        title.getStyleClass().add("card-title");
        title.setStyle("-fx-font-size: 20px; -fx-text-fill: #38bdf8;");

        Label subtitle = new Label("Benchmark multiple algorithmic paradigms on identical inputs with precision timing and operation counts.");
        subtitle.getStyleClass().add("card-subtitle");

        // Benchmark mode selector
        HBox modeBox = new HBox(12);
        modeBox.setAlignment(Pos.CENTER_LEFT);
        Label modeLabel = new Label("Benchmark Category:");
        modeLabel.setStyle("-fx-font-weight: bold;");

        ComboBox<String> modeCombo = new ComboBox<>();
        modeCombo.getItems().addAll("String Pattern Matching (Naive vs KMP vs Z vs Rabin-Karp)",
                                   "Edit Distance & Fuzzy Matching (Levenshtein vs Damerau vs Weighted)");
        modeCombo.getSelectionModel().select(0);
        HBox.setHgrow(modeCombo, Priority.ALWAYS);
        modeBox.getChildren().addAll(modeLabel, modeCombo);

        // Input Fields
        VBox inputBox = new VBox(8);
        inputBox.getStyleClass().add("card-panel");

        Label lblInputA = new Label("Pattern / Word A:");
        lblInputA.setStyle("-fx-font-weight: bold; -fx-text-fill: #38bdf8;");
        TextField txtInputA = new TextField("algorithm");

        Label lblInputB = new Label("Target Text / Word B:");
        lblInputB.setStyle("-fx-font-weight: bold; -fx-text-fill: #38bdf8;");
        TextArea txtInputB = new TextArea("An algorithm is an unambiguous specification of how to solve a class of problems. Algorithms can perform calculation, data processing, and automated reasoning tasks.");
        txtInputB.setPrefRowCount(3);
        txtInputB.setWrapText(true);

        HBox btnBar = new HBox(10);
        btnBar.setAlignment(Pos.CENTER_LEFT);
        Button runBtn = new Button("Run Simultaneous Benchmark");
        runBtn.getStyleClass().add("btn-primary");
        Button loadSampleBtn = new Button("Load Stress Sample");
        loadSampleBtn.getStyleClass().add("btn-secondary");
        Button resetBtn = new Button("Reset Inputs");
        resetBtn.getStyleClass().add("btn-secondary");
        btnBar.getChildren().addAll(runBtn, loadSampleBtn, resetBtn);

        inputBox.getChildren().addAll(lblInputA, txtInputA, lblInputB, txtInputB, btnBar);

        // TableView for results
        TableView<BenchmarkService.ComparisonRow> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<BenchmarkService.ComparisonRow, String> colAlgo = new TableColumn<>("Algorithm");
        colAlgo.setCellValueFactory(new PropertyValueFactory<>("algorithmName"));
        colAlgo.setPrefWidth(180);

        TableColumn<BenchmarkService.ComparisonRow, String> colTime = new TableColumn<>("Execution Time");
        colTime.setCellValueFactory(new PropertyValueFactory<>("executionTime"));
        colTime.setPrefWidth(110);

        TableColumn<BenchmarkService.ComparisonRow, Long> colOps = new TableColumn<>("Operations / Comparisons");
        colOps.setCellValueFactory(new PropertyValueFactory<>("comparisonsOrOps"));
        colOps.setPrefWidth(160);

        TableColumn<BenchmarkService.ComparisonRow, String> colRes = new TableColumn<>("Output Summary");
        colRes.setCellValueFactory(new PropertyValueFactory<>("resultSummary"));
        colRes.setPrefWidth(160);

        TableColumn<BenchmarkService.ComparisonRow, String> colTimeComp = new TableColumn<>("Time Complexity");
        colTimeComp.setCellValueFactory(new PropertyValueFactory<>("timeComplexity"));
        colTimeComp.setPrefWidth(120);

        TableColumn<BenchmarkService.ComparisonRow, String> colSpaceComp = new TableColumn<>("Space Complexity");
        colSpaceComp.setCellValueFactory(new PropertyValueFactory<>("spaceComplexity"));
        colSpaceComp.setPrefWidth(110);

        table.getColumns().addAll(colAlgo, colTime, colOps, colRes, colTimeComp, colSpaceComp);

        // Mode change listener
        modeCombo.setOnAction(e -> {
            if (modeCombo.getSelectionModel().getSelectedIndex() == 0) {
                lblInputA.setText("Search Pattern:");
                lblInputB.setText("Target Text Body:");
                txtInputA.setText("algorithm");
                txtInputB.setText("An algorithm is an unambiguous specification of how to solve a class of problems. Algorithms can perform calculation, data processing, and automated reasoning tasks.");
            } else {
                lblInputA.setText("Source String A:");
                lblInputB.setText("Target String B:");
                txtInputA.setText("algorithm");
                txtInputB.setText("altruistic");
            }
            table.getItems().clear();
        });

        loadSampleBtn.setOnAction(e -> {
            if (modeCombo.getSelectionModel().getSelectedIndex() == 0) {
                txtInputA.setText("ABABAC");
                txtInputB.setText("ABABABACABABACABABACABABACABABACABABACABABACABABACABABACABABACABABACABABACABABACABABACABABAC");
            } else {
                txtInputA.setText("COMMUNICATION");
                txtInputB.setText("COMPUTATION");
            }
        });

        resetBtn.setOnAction(e -> {
            txtInputA.clear();
            txtInputB.clear();
            table.getItems().clear();
        });

        runBtn.setOnAction(e -> {
            String inA = txtInputA.getText();
            String inB = txtInputB.getText();
            if (inA == null || inA.trim().isEmpty() || inB == null || inB.trim().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter both input fields before benchmarking.");
                alert.showAndWait();
                return;
            }

            ObservableList<BenchmarkService.ComparisonRow> items = FXCollections.observableArrayList();
            if (modeCombo.getSelectionModel().getSelectedIndex() == 0) {
                ManualArrayList<BenchmarkService.ComparisonRow> rows = benchmarkService.compareStringMatchers(inA, inB);
                for (int i = 0; i < rows.size(); i++) {
                    items.add(rows.get(i));
                }
            } else {
                ManualArrayList<BenchmarkService.ComparisonRow> rows = benchmarkService.compareEditDistances(inA, inB);
                for (int i = 0; i < rows.size(); i++) {
                    items.add(rows.get(i));
                }
            }
            table.setItems(items);
        });

        root.getChildren().addAll(title, subtitle, modeBox, inputBox, table);
    }

    public Parent getView() {
        return root;
    }
}
