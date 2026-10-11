package com.texthack.ui.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Visualizes text and pattern character sequences with individual tiles,
 * indices, and dynamic state colors (match, mismatch, active window).
 */
public class StringVisualizer extends VBox {

    public enum CellState {
        DEFAULT,
        MATCH,
        MISMATCH,
        ACTIVE,
        WINDOW
    }

    private static class CharCell extends VBox {
        private final Label charLabel;
        private final Label indexLabel;

        public CharCell(char c, int index) {
            super(2);
            setAlignment(Pos.CENTER);
            getStyleClass().add("char-cell");

            charLabel = new Label(String.valueOf(c));
            charLabel.getStyleClass().add("char-text");

            indexLabel = new Label(String.valueOf(index));
            indexLabel.getStyleClass().add("char-index");

            getChildren().addAll(charLabel, indexLabel);
        }

        public void setState(CellState state) {
            getStyleClass().removeAll("char-cell-match", "char-cell-mismatch", "char-cell-active", "char-cell-window");
            switch (state) {
                case MATCH: getStyleClass().add("char-cell-match"); break;
                case MISMATCH: getStyleClass().add("char-cell-mismatch"); break;
                case ACTIVE: getStyleClass().add("char-cell-active"); break;
                case WINDOW: getStyleClass().add("char-cell-window"); break;
                default: break;
            }
        }
    }

    private String text = "";
    private String pattern = "";
    private final HBox textBox;
    private final HBox patternBox;
    private CharCell[] textCells = new CharCell[0];
    private CharCell[] patternCells = new CharCell[0];

    public StringVisualizer() {
        super(10);

        textBox = new HBox(4);
        textBox.setAlignment(Pos.CENTER_LEFT);

        patternBox = new HBox(4);
        patternBox.setAlignment(Pos.CENTER_LEFT);

        Label textLbl = new Label("Text Array (T):");
        textLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #94A3B8;");

        Label patLbl = new Label("Pattern Alignment (P):");
        patLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #94A3B8;");

        VBox content = new VBox(6, textLbl, textBox, patLbl, patternBox);

        ScrollPane sp = new ScrollPane(content);
        sp.setFitToHeight(true);
        sp.setPrefHeight(150);
        sp.setStyle("-fx-background-color: transparent;");

        getChildren().add(sp);
    }

    public void setStrings(String text, String pattern) {
        this.text = (text != null) ? text : "";
        this.pattern = (pattern != null) ? pattern : "";

        textBox.getChildren().clear();
        patternBox.getChildren().clear();

        textCells = new CharCell[this.text.length()];
        for (int i = 0; i < this.text.length(); i++) {
            CharCell cell = new CharCell(this.text.charAt(i), i);
            textCells[i] = cell;
            textBox.getChildren().add(cell);
        }

        patternCells = new CharCell[this.pattern.length()];
        for (int j = 0; j < this.pattern.length(); j++) {
            CharCell cell = new CharCell(this.pattern.charAt(j), j);
            patternCells[j] = cell;
            patternBox.getChildren().add(cell);
        }
    }

    public void updateState(int textIdx, int patternIdx, int rangeStart, int rangeEnd, boolean isMatch, boolean isMismatch) {
        // Reset all states
        for (CharCell cell : textCells) cell.setState(CellState.DEFAULT);
        for (CharCell cell : patternCells) cell.setState(CellState.DEFAULT);

        // Highlight window range
        if (rangeStart >= 0 && rangeEnd < textCells.length && rangeStart <= rangeEnd) {
            for (int k = rangeStart; k <= rangeEnd; k++) {
                textCells[k].setState(CellState.WINDOW);
            }
        }

        // Highlight active comparisons
        if (textIdx >= 0 && textIdx < textCells.length) {
            if (isMatch) textCells[textIdx].setState(CellState.MATCH);
            else if (isMismatch) textCells[textIdx].setState(CellState.MISMATCH);
            else textCells[textIdx].setState(CellState.ACTIVE);
        }

        if (patternIdx >= 0 && patternIdx < patternCells.length) {
            if (isMatch) patternCells[patternIdx].setState(CellState.MATCH);
            else if (isMismatch) patternCells[patternIdx].setState(CellState.MISMATCH);
            else patternCells[patternIdx].setState(CellState.ACTIVE);
        }
    }
}
