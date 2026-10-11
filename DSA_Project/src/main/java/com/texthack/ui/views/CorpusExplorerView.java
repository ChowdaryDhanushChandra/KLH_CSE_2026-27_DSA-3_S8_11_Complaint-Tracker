package com.texthack.ui.views;

import com.texthack.core.ds.DynamicArray;
import com.texthack.core.model.AlgoResult;
import com.texthack.core.model.Corpus;
import com.texthack.module2_strings.AhoCorasickMatcher;
import com.texthack.module2_strings.KMPMatcher;
import com.texthack.ui.components.Card;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Laboratory: Indian-Language Corpus Explorer.
 * Searches across English, Hindi (Devanagari), and Telugu local corpora
 * using TextHack's hand-built KMP and Aho-Corasick engines.
 */
public class CorpusExplorerView extends ScrollPane {

    private final ComboBox<Corpus.Language> langCombo;
    private final TextField searchField;
    private final RadioButton kmpRadio;
    private final RadioButton ahoRadio;
    private final Label statsLabel;
    private final TextFlow textFlow;

    private String currentCorpusText = "";

    private final KMPMatcher kmp = new KMPMatcher();
    private final AhoCorasickMatcher aho = new AhoCorasickMatcher();

    public CorpusExplorerView() {
        setFitToWidth(true);
        setStyle("-fx-background-color: transparent;");

        VBox content = new VBox(16);
        content.setPadding(new Insets(20));

        Label title = new Label("🌐 Indian-Language Corpus Explorer");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #38BDF8;");

        Label sub = new Label("Offline multilingual text analytics using custom KMP and Aho-Corasick algorithms.");
        sub.setStyle("-fx-font-size: 12px; -fx-text-fill: #94A3B8;");

        // Controls
        langCombo = new ComboBox<>();
        langCombo.getItems().addAll(Corpus.Language.ENGLISH, Corpus.Language.HINDI, Corpus.Language.TELUGU);
        langCombo.getSelectionModel().select(Corpus.Language.ENGLISH);
        langCombo.setOnAction(e -> loadCorpusForSelectedLanguage());

        searchField = new TextField("algorithm");
        searchField.setPromptText("Enter search keyword or pattern...");
        searchField.getStyleClass().add("text-input");
        HBox.setHgrow(searchField, Priority.ALWAYS);

        ToggleGroup algoGroup = new ToggleGroup();
        kmpRadio = new RadioButton("KMP");
        kmpRadio.setToggleGroup(algoGroup);
        kmpRadio.setSelected(true);
        kmpRadio.setStyle("-fx-text-fill: #F8FAFC;");

        ahoRadio = new RadioButton("Aho-Corasick");
        ahoRadio.setToggleGroup(algoGroup);
        ahoRadio.setStyle("-fx-text-fill: #F8FAFC;");

        Button searchBtn = new Button("🔍 Search Corpus");
        searchBtn.getStyleClass().add("primary-btn");
        searchBtn.setOnAction(e -> executeCorpusSearch());

        HBox searchRow = new HBox(12, new Label("Corpus:"), langCombo, searchField, kmpRadio, ahoRadio, searchBtn);
        searchRow.setAlignment(Pos.CENTER_LEFT);

        statsLabel = new Label("Ready to search.");
        statsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #38BDF8; -fx-font-weight: bold;");

        Card searchCard = new Card("Search Parameters & Language Switcher", "", new VBox(10, searchRow, statsLabel));

        // Text Viewer
        textFlow = new TextFlow();
        textFlow.setStyle("-fx-background-color: #0F172A; -fx-padding: 16px; -fx-background-radius: 8px;");

        ScrollPane textScroll = new ScrollPane(textFlow);
        textScroll.setFitToWidth(true);
        textScroll.setPrefHeight(400);
        textScroll.setStyle("-fx-background-color: transparent;");

        Card corpusCard = new Card("Corpus Document Display", "Offline UTF-8 Unicode Text", textScroll);

        content.getChildren().addAll(new VBox(4, title, sub), searchCard, corpusCard);
        setContent(content);

        loadCorpusForSelectedLanguage();
    }

    private void loadCorpusForSelectedLanguage() {
        Corpus.Language lang = langCombo.getValue();
        String path = "/corpus/" + lang.getCode() + ".txt";
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is != null) {
                currentCorpusText = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            } else {
                currentCorpusText = "Corpus file not found at " + path;
            }
        } catch (Exception ex) {
            currentCorpusText = "Error reading corpus: " + ex.getMessage();
        }

        if (lang == Corpus.Language.HINDI) {
            searchField.setText("एल्गोरिदम");
        } else if (lang == Corpus.Language.TELUGU) {
            searchField.setText("అల్గోరిథం");
        } else {
            searchField.setText("algorithm");
        }

        renderPlainText(currentCorpusText);
    }

    private void executeCorpusSearch() {
        String query = searchField.getText();
        if (query == null || query.isEmpty()) {
            renderPlainText(currentCorpusText);
            statsLabel.setText("Please enter a valid search pattern.");
            return;
        }

        long elapsedMicros;
        int matchCount;

        if (kmpRadio.isSelected()) {
            AlgoResult<KMPMatcher.KMPResult> res = kmp.search(currentCorpusText, query);
            elapsedMicros = res.getElapsedMicros();
            matchCount = res.getOutput().getMatches().size();
            renderHighlightedText(currentCorpusText, res.getOutput().getMatches(), query.length());
        } else {
            DynamicArray<String> patterns = new DynamicArray<>();
            patterns.add(query);
            AlgoResult<AhoCorasickMatcher.AhoCorasickResult> res = aho.search(currentCorpusText, patterns);
            elapsedMicros = res.getElapsedMicros();
            matchCount = res.getOutput().getMatches().size();

            DynamicArray<Integer> positions = new DynamicArray<>();
            for (int i = 0; i < res.getOutput().getMatches().size(); i++) {
                positions.add(res.getOutput().getMatches().get(i).getStartIndex());
            }
            renderHighlightedText(currentCorpusText, positions, query.length());
        }

        statsLabel.setText("Found " + matchCount + " occurrences of \"" + query + "\" in " +
                elapsedMicros + " µs using " + (kmpRadio.isSelected() ? "KMP" : "Aho-Corasick") + ".");
    }

    private void renderPlainText(String content) {
        textFlow.getChildren().clear();
        Text text = new Text(content);
        text.setStyle("-fx-fill: #F8FAFC; -fx-font-size: 13px; -fx-font-family: 'Segoe UI', 'Inter';");
        textFlow.getChildren().add(text);
    }

    private void renderHighlightedText(String content, DynamicArray<Integer> matchPositions, int matchLen) {
        textFlow.getChildren().clear();
        if (matchPositions.isEmpty()) {
            renderPlainText(content);
            return;
        }

        int lastIdx = 0;
        for (int i = 0; i < matchPositions.size(); i++) {
            int start = matchPositions.get(i);
            if (start > lastIdx && lastIdx < content.length()) {
                Text normal = new Text(content.substring(lastIdx, Math.min(start, content.length())));
                normal.setStyle("-fx-fill: #F8FAFC; -fx-font-size: 13px;");
                textFlow.getChildren().add(normal);
            }

            int end = Math.min(start + matchLen, content.length());
            if (start < content.length()) {
                Text highlight = new Text(content.substring(start, end));
                highlight.setStyle("-fx-fill: #0F172A; -fx-font-weight: bold; -fx-background-color: #38BDF8; -fx-font-size: 13px;");
                textFlow.getChildren().add(highlight);
            }
            lastIdx = end;
        }

        if (lastIdx < content.length()) {
            Text trailing = new Text(content.substring(lastIdx));
            trailing.setStyle("-fx-fill: #F8FAFC; -fx-font-size: 13px;");
            textFlow.getChildren().add(trailing);
        }
    }
}
