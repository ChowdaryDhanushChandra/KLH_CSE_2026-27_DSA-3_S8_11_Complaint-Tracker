package com.texthack.ui.views;

import com.texthack.ui.components.Card;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Theory: Algorithm Canon.
 * Concordance table mapping topics to standard textbooks:
 * CLRS (Part VII), Kleinberg-Tardos (Ch 6-13), Jeff Erickson (Ch 4-12).
 */
public class CanonView extends ScrollPane {

    public static class CanonEntry {
        public final String topic;
        public final String paradigm;
        public final String clrs;
        public final String kt;
        public final String erickson;

        public CanonEntry(String topic, String paradigm, String clrs, String kt, String erickson) {
            this.topic = topic;
            this.paradigm = paradigm;
            this.clrs = clrs;
            this.kt = kt;
            this.erickson = erickson;
        }
    }

    public CanonView() {
        setFitToWidth(true);
        setStyle("-fx-background-color: transparent;");

        VBox content = new VBox(16);
        content.setPadding(new Insets(20));

        Label title = new Label("📜 Algorithm Canon & Textbook Concordance");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #38BDF8;");

        Label sub = new Label("Direct cross-references to CLRS (Part VII), Kleinberg-Tardos (Ch 6–13), and Jeff Erickson (Ch 4–12).");
        sub.setStyle("-fx-font-size: 12px; -fx-text-fill: #94A3B8;");

        TableView<CanonEntry> table = new TableView<>();
        ObservableList<CanonEntry> list = FXCollections.observableArrayList(
                new CanonEntry("Exact String Matching (KMP & Rabin-Karp)", "Finite Automata & Rolling Hashes", "Chapter 32 (pp. 985-1012)", "Chapter 13.5 (Randomized Matching)", "Chapter 4 (Strings & Automata)"),
                new CanonEntry("Multi-Pattern Search (Aho-Corasick)", "Trie & Deterministic Automata", "Chapter 32.3 (Automata)", "Chapter 13.5 (Automata & Hashes)", "Chapter 4.3 (Keyword Trees)"),
                new CanonEntry("Suffix Arrays & Kasai LCP", "Suffix Indexing & Inverted Ranks", "Chapter 32 (Problem 32-1)", "Chapter 6 (Sequence Indexing)", "Chapter 4.5 (Suffix Structures)"),
                new CanonEntry("Sequence Alignment (Wagner-Fischer & Smith-Waterman)", "Dynamic Programming", "Chapter 14/15 (Dynamic Programming)", "Chapter 6.6 (Sequence Alignment)", "Chapter 3 (Edit Distance)"),
                new CanonEntry("Network Flow (Edmonds-Karp & Dinic)", "Augmenting Paths & Blocking Flows", "Chapter 24 (pp. 708-761)", "Chapter 7 (Network Flow)", "Chapter 10-11 (Maximum Flows)"),
                new CanonEntry("NP-Completeness & 3-SAT Reductions", "Polynomial-Time Reductions", "Chapter 34 (NP-Completeness)", "Chapter 8 (NP and Computational Intractability)", "Chapter 12 (NP-Hardness)"),
                new CanonEntry("Approximation Algorithms (Vertex Cover, TSP)", "Approximation Bounds", "Chapter 35 (Approximation)", "Chapter 11 (Approximation)", "Chapter 12 (Approximation)"),
                new CanonEntry("Randomized Algorithms (Miller-Rabin, Quicksort)", "Probabilistic Analysis", "Chapter 7 / 31 (Number Theory)", "Chapter 13 (Randomized Algorithms)", "Chapter 1 (Recursion & Sorting)")
        );
        table.setItems(list);
        table.setPrefHeight(360);

        TableColumn<CanonEntry, String> colTopic = new TableColumn<>("Algorithmic Topic");
        colTopic.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().topic));
        colTopic.setPrefWidth(220);

        TableColumn<CanonEntry, String> colParadigm = new TableColumn<>("Primary Paradigm");
        colParadigm.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().paradigm));
        colParadigm.setPrefWidth(180);

        TableColumn<CanonEntry, String> colClrs = new TableColumn<>("CLRS 4th Ed.");
        colClrs.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().clrs));
        colClrs.setPrefWidth(180);

        TableColumn<CanonEntry, String> colKt = new TableColumn<>("Kleinberg-Tardos");
        colKt.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().kt));
        colKt.setPrefWidth(190);

        TableColumn<CanonEntry, String> colErickson = new TableColumn<>("Jeff Erickson");
        colErickson.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().erickson));
        colErickson.setPrefWidth(190);

        table.getColumns().addAll(colTopic, colParadigm, colClrs, colKt, colErickson);

        Card card = new Card("Standard Curriculum Mapping", "Foundational text mapping", table);
        VBox.setVgrow(card, Priority.ALWAYS);

        content.getChildren().addAll(new VBox(4, title, sub), card);
        setContent(content);
    }
}
