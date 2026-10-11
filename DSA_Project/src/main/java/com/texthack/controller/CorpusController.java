package com.texthack.controller;

import com.texthack.datastructures.ManualArrayList;
import com.texthack.model.Document;
import com.texthack.service.CorpusService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Controller and View for the Local Multilingual Corpus Explorer (English, Hindi, Telugu).
 * Performs offline pattern searching with zero web/network dependencies.
 */
public class CorpusController {

    private final VBox root;
    private final CorpusService corpusService;

    public CorpusController() {
        this.corpusService = new CorpusService();
        this.root = new VBox(14);
        root.setPadding(new Insets(20));

        Label title = new Label("Multilingual Corpus Explorer (Offline Indian-Language Analytics)");
        title.getStyleClass().add("card-title");
        title.setStyle("-fx-font-size: 20px; -fx-text-fill: #38bdf8;");

        Label subtitle = new Label("Explore, inspect, and execute offline algorithmic queries over English, Hindi (Devanagari), and Telugu corpuses.");
        subtitle.getStyleClass().add("card-subtitle");

        // Document selector bar
        HBox docBar = new HBox(12);
        docBar.setAlignment(Pos.CENTER_LEFT);
        Label docLabel = new Label("Select Corpus Document:");
        docLabel.setStyle("-fx-font-weight: bold;");

        ComboBox<Document> docCombo = new ComboBox<>();
        ManualArrayList<Document> docs = corpusService.getDocuments();
        for (int i = 0; i < docs.size(); i++) {
            docCombo.getItems().add(docs.get(i));
        }
        if (docCombo.getItems().size() > 0) {
            docCombo.getSelectionModel().select(0);
        }
        HBox.setHgrow(docCombo, Priority.ALWAYS);

        docBar.getChildren().addAll(docLabel, docCombo);

        // Search Bar
        HBox searchBar = new HBox(10);
        searchBar.setAlignment(Pos.CENTER_LEFT);
        TextField searchInput = new TextField();
        searchInput.setPromptText("Enter pattern to search across corpus (e.g. KMP, एल्गोरिदम, అల్గోరిథం)...");
        HBox.setHgrow(searchInput, Priority.ALWAYS);

        Button searchBtn = new Button("Search Across Corpus");
        searchBtn.getStyleClass().add("btn-primary");
        Button sampleWordBtn = new Button("Load Language Sample Term");
        sampleWordBtn.getStyleClass().add("btn-secondary");

        searchBar.getChildren().addAll(searchInput, searchBtn, sampleWordBtn);

        // Document Viewer and Search Results Split
        SplitPane split = new SplitPane();
        VBox.setVgrow(split, Priority.ALWAYS);

        VBox docViewBox = new VBox(8);
        docViewBox.getStyleClass().add("card-panel");
        Label docTitle = new Label("Corpus Document Text:");
        docTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #38bdf8;");
        Label docMeta = new Label();
        docMeta.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");

        TextArea contentArea = new TextArea();
        contentArea.setEditable(false);
        contentArea.setWrapText(true);
        contentArea.getStyleClass().add("text-area");
        VBox.setVgrow(contentArea, Priority.ALWAYS);
        docViewBox.getChildren().addAll(docTitle, docMeta, contentArea);

        VBox searchResultsBox = new VBox(8);
        searchResultsBox.getStyleClass().add("card-panel");
        Label resultsTitle = new Label("Corpus Search Matches:");
        resultsTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #38bdf8;");

        TextArea resultsArea = new TextArea();
        resultsArea.setEditable(false);
        resultsArea.setWrapText(true);
        resultsArea.getStyleClass().add("code-area");
        VBox.setVgrow(resultsArea, Priority.ALWAYS);
        searchResultsBox.getChildren().addAll(resultsTitle, resultsArea);

        split.getItems().addAll(docViewBox, searchResultsBox);
        split.setDividerPositions(0.6);

        // Listeners & Event Handlers
        Runnable displaySelectedDoc = () -> {
            Document d = docCombo.getValue();
            if (d != null) {
                contentArea.setText(d.getContent());
                docMeta.setText(String.format("Language: %s | Characters: %d | Words: %d",
                        d.getLanguage(), d.getCharacterCount(), d.getWordCount()));
            }
        };

        docCombo.setOnAction(e -> displaySelectedDoc.run());
        displaySelectedDoc.run();

        sampleWordBtn.setOnAction(e -> {
            Document d = docCombo.getValue();
            if (d != null) {
                if ("Hindi".equalsIgnoreCase(d.getLanguage())) {
                    searchInput.setText("एल्गोरिदम");
                } else if ("Telugu".equalsIgnoreCase(d.getLanguage())) {
                    searchInput.setText("అల్గోరిథం");
                } else {
                    searchInput.setText("algorithm");
                }
            }
        });

        searchBtn.setOnAction(e -> {
            String q = searchInput.getText();
            if (q == null || q.trim().isEmpty()) {
                resultsArea.setText("Please enter a pattern to search.");
                return;
            }

            ManualArrayList<CorpusService.CorpusSearchResult> matches = corpusService.searchAll(q);
            StringBuilder sb = new StringBuilder();
            sb.append("OFFLINE MULTILINGUAL CORPUS SEARCH REPORT\n");
            sb.append("Search Term: \"").append(q).append("\"\n");
            sb.append("Total Matching Documents: ").append(matches.size()).append("\n");
            sb.append("====================================================\n\n");

            if (matches.isEmpty()) {
                sb.append("No occurrences found in any corpus document.\n");
            } else {
                for (int i = 0; i < matches.size(); i++) {
                    CorpusService.CorpusSearchResult res = matches.get(i);
                    sb.append("Document: ").append(res.document.getTitle())
                            .append(" [").append(res.document.getLanguage()).append("]\n");
                    sb.append("- Match Count: ").append(res.searchResult.getMatchCount()).append("\n");
                    sb.append("- Offset Indices: ").append(res.searchResult.getIndicesFormatted()).append("\n");
                    sb.append("- Execution Time: ").append(res.searchResult.getFormattedTime()).append("\n");
                    sb.append("- Comparisons: ").append(res.searchResult.getComparisons()).append("\n\n");
                }
            }

            resultsArea.setText(sb.toString());
        });

        root.getChildren().addAll(title, subtitle, docBar, searchBar, split);
    }

    public Parent getView() {
        return root;
    }
}
