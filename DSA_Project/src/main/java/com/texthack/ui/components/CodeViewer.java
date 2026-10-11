package com.texthack.ui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Displays algorithm pseudocode with line numbers and dynamic active line highlighting.
 */
public class CodeViewer extends VBox {
    private final String[] lines;
    private final HBox[] lineBoxes;
    private int activeLine = -1;

    public CodeViewer(String[] lines) {
        super(2);
        this.lines = lines != null ? lines : new String[0];
        this.lineBoxes = new HBox[this.lines.length];

        getStyleClass().add("code-viewer");
        setPadding(new Insets(8));

        VBox contentBox = new VBox(2);
        for (int i = 0; i < this.lines.length; i++) {
            HBox lineBox = new HBox(8);
            lineBox.setAlignment(Pos.CENTER_LEFT);
            lineBox.getStyleClass().add("code-line");

            Label numLabel = new Label(String.format("%2d", i + 1));
            numLabel.getStyleClass().add("line-num");

            Label textLabel = new Label(this.lines[i]);
            textLabel.getStyleClass().add("code-line");
            HBox.setHgrow(textLabel, Priority.ALWAYS);

            lineBox.getChildren().addAll(numLabel, textLabel);
            lineBoxes[i] = lineBox;
            contentBox.getChildren().add(lineBox);
        }

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(180);
        scrollPane.setMaxHeight(260);
        getChildren().add(scrollPane);
    }

    public void setActiveLine(int lineNumber) {
        int index = lineNumber - 1; // 1-indexed to 0-indexed
        if (activeLine >= 0 && activeLine < lineBoxes.length) {
            lineBoxes[activeLine].getStyleClass().remove("code-line-active");
        }
        if (index >= 0 && index < lineBoxes.length) {
            lineBoxes[index].getStyleClass().add("code-line-active");
            activeLine = index;
        } else {
            activeLine = -1;
        }
    }

    public void clearHighlight() {
        if (activeLine >= 0 && activeLine < lineBoxes.length) {
            lineBoxes[activeLine].getStyleClass().remove("code-line-active");
        }
        activeLine = -1;
    }
}
