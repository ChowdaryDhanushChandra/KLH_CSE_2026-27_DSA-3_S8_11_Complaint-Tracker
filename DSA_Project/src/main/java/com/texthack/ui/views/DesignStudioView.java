package com.texthack.ui.views;

import com.texthack.ui.components.Card;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Theory: Algorithm Design Studio.
 * Interactive scenario quiz evaluating algorithmic selection, paradigm trade-offs, and asymptotic bounds.
 */
public class DesignStudioView extends ScrollPane {

    private static class Question {
        String scenario;
        String question;
        String[] options;
        int correctIdx;
        String explanation;

        Question(String s, String q, String[] opt, int c, String exp) {
            this.scenario = s;
            this.question = q;
            this.options = opt;
            this.correctIdx = c;
            this.explanation = exp;
        }
    }

    private final Question[] questions = {
        new Question(
            "You are building a live intrusion detection system scanning millions of streaming network packets against 1,000 malware signatures simultaneously in real-time.",
            "Which algorithmic paradigm and data structure should you select for single-pass stream processing?",
            new String[]{
                "Aho-Corasick Automaton with BFS failure and dictionary links",
                "KMP repeated sequentially 1,000 times per packet",
                "Held-Karp Bitmask Dynamic Programming",
                "Wagner-Fischer Matrix Distance"
            },
            0,
            "Aho-Corasick constructs a Trie with failure transitions in O(m · Σ), enabling linear single-pass streaming across text in O(n + z) time regardless of the number of patterns."
        ),
        new Question(
            "A computational genomics pipeline requires detecting conserved local motifs between two divergent DNA sequences, discarding unrelated flanking sequences.",
            "Which algorithm is mathematically designed for optimal local sequence alignment?",
            new String[]{
                "Smith-Waterman Algorithm with recurrence clamped at zero",
                "Needleman-Wunsch Global Alignment",
                "Rabin-Karp Rolling Hash",
                "Graham's List Scheduling Heuristic"
            },
            0,
            "Smith-Waterman clamps negative cell values to 0 (max(0, ...)), allowing alignment traceback to begin from the highest scoring cell anywhere in the matrix and terminate when 0 is reached."
        ),
        new Question(
            "You need to index all suffixes of a 100,000-character genome text to enable fast O(m log n) substring lookups without incurring high memory overhead.",
            "Which algorithm constructs the suffix array in O(n log² n) without building heavy pointer trees?",
            new String[]{
                "Prefix-Doubling Suffix Array (Manber-Myers)",
                "Dijkstra's Shortest Path",
                "Edmonds-Karp Max Flow",
                "Fermat Probabilistic Primality Test"
            },
            0,
            "Prefix-doubling sorts suffixes by 2^k length prefixes iteratively in O(n log² n), consuming only O(n) memory without the 20x memory overhead of pointer-heavy suffix trees."
        )
    };

    private int currentQuestionIndex = 0;
    private int score = 0;

    private final Label scoreLabel;
    private final Label scenarioLabel;
    private final Label questionLabel;
    private final VBox optionsBox;
    private final Label feedbackLabel;
    private final Button nextBtn;
    private final ToggleGroup group;

    public DesignStudioView() {
        setFitToWidth(true);
        setStyle("-fx-background-color: transparent;");

        VBox content = new VBox(16);
        content.setPadding(new Insets(20));

        Label title = new Label("🎓 Algorithm Design Studio & Scenario Quiz");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #38BDF8;");

        scoreLabel = new Label("Score: 0 / " + questions.length);
        scoreLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #22C55E;");

        HBox headerRow = new HBox(title, scoreLabel);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(title, Priority.ALWAYS);

        scenarioLabel = new Label();
        scenarioLabel.setWrapText(true);
        scenarioLabel.setStyle("-fx-font-size: 13px; -fx-font-style: italic; -fx-text-fill: #94A3B8;");

        questionLabel = new Label();
        questionLabel.setWrapText(true);
        questionLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #F8FAFC;");

        optionsBox = new VBox(8);
        group = new ToggleGroup();

        feedbackLabel = new Label();
        feedbackLabel.setWrapText(true);
        feedbackLabel.setStyle("-fx-font-size: 12px;");

        Button submitBtn = new Button("Submit Answer");
        submitBtn.getStyleClass().add("primary-btn");
        submitBtn.setOnAction(e -> evaluateAnswer());

        nextBtn = new Button("Next Scenario →");
        nextBtn.getStyleClass().add("secondary-btn");
        nextBtn.setDisable(true);
        nextBtn.setOnAction(e -> loadNextQuestion());

        HBox actionRow = new HBox(12, submitBtn, nextBtn);
        actionRow.setAlignment(Pos.CENTER_LEFT);

        Card quizCard = new Card("Design Challenge", "",
                new VBox(12, scenarioLabel, questionLabel, optionsBox, actionRow, feedbackLabel));

        content.getChildren().addAll(headerRow, quizCard);
        setContent(content);

        loadQuestion(0);
    }

    private void loadQuestion(int index) {
        if (index < 0 || index >= questions.length) return;
        currentQuestionIndex = index;
        Question q = questions[index];

        scenarioLabel.setText("Scenario: " + q.scenario);
        questionLabel.setText(q.question);
        feedbackLabel.setText("");
        nextBtn.setDisable(true);

        optionsBox.getChildren().clear();
        group.getToggles().clear();

        for (int i = 0; i < q.options.length; i++) {
            RadioButton rb = new RadioButton(q.options[i]);
            rb.setToggleGroup(group);
            rb.setUserData(i);
            rb.setStyle("-fx-text-fill: #F8FAFC; -fx-font-size: 13px;");
            optionsBox.getChildren().add(rb);
        }
    }

    private void evaluateAnswer() {
        Toggle selected = group.getSelectedToggle();
        if (selected == null) {
            feedbackLabel.setText("⚠ Please select an option first.");
            feedbackLabel.setStyle("-fx-text-fill: #EF4444;");
            return;
        }

        int choice = (int) selected.getUserData();
        Question q = questions[currentQuestionIndex];

        if (choice == q.correctIdx) {
            score++;
            scoreLabel.setText("Score: " + score + " / " + questions.length);
            feedbackLabel.setText("✓ Correct! " + q.explanation);
            feedbackLabel.setStyle("-fx-text-fill: #22C55E; -fx-font-weight: bold;");
        } else {
            feedbackLabel.setText("✗ Incorrect. " + q.explanation);
            feedbackLabel.setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold;");
        }

        nextBtn.setDisable(false);
    }

    private void loadNextQuestion() {
        if (currentQuestionIndex < questions.length - 1) {
            loadQuestion(currentQuestionIndex + 1);
        } else {
            scenarioLabel.setText("Quiz Complete!");
            questionLabel.setText("Your Final Score: " + score + " out of " + questions.length);
            optionsBox.getChildren().clear();
            feedbackLabel.setText("Great work exploring advanced algorithmic design principles!");
            feedbackLabel.setStyle("-fx-text-fill: #38BDF8; -fx-font-weight: bold;");
            nextBtn.setText("↺ Restart Quiz");
            nextBtn.setOnAction(e -> {
                score = 0;
                scoreLabel.setText("Score: 0 / " + questions.length);
                nextBtn.setText("Next Scenario →");
                nextBtn.setOnAction(ev -> loadNextQuestion());
                loadQuestion(0);
            });
        }
    }
}
