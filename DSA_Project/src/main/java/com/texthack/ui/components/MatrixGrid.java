package com.texthack.ui.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * Grid component for rendering 1D and 2D arrays (LPS table, Z-array,
 * Suffix Array table, LCP array, DP matrices).
 */
public class MatrixGrid extends VBox {
    private final GridPane grid;
    private Label[][] cellLabels;

    public MatrixGrid() {
        super(8);
        grid = new GridPane();
        grid.setHgap(4);
        grid.setVgap(4);
        grid.setAlignment(Pos.CENTER_LEFT);

        ScrollPane sp = new ScrollPane(grid);
        sp.setFitToHeight(true);
        sp.setPrefHeight(130);
        sp.setStyle("-fx-background-color: transparent;");

        getChildren().add(sp);
    }

    public void setArray1D(String rowHeader, String[] headers, int[] values) {
        grid.getChildren().clear();
        if (headers == null || values == null) return;

        int cols = headers.length;
        cellLabels = new Label[2][cols];

        Label rowTitle = new Label(rowHeader);
        rowTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #38BDF8; -fx-min-width: 90px;");
        grid.add(rowTitle, 0, 1);

        Label headerTitle = new Label("Index / Char");
        headerTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #94A3B8; -fx-min-width: 90px;");
        grid.add(headerTitle, 0, 0);

        for (int c = 0; c < cols; c++) {
            Label hLbl = createCell(headers[c], false);
            cellLabels[0][c] = hLbl;
            grid.add(hLbl, c + 1, 0);

            Label vLbl = createCell(String.valueOf(values[c]), true);
            cellLabels[1][c] = vLbl;
            grid.add(vLbl, c + 1, 1);
        }
    }

    public void updateActiveCell(int colIndex) {
        if (cellLabels == null || cellLabels.length < 2) return;
        for (int c = 0; c < cellLabels[1].length; c++) {
            if (cellLabels[1][c] != null) {
                cellLabels[1][c].setStyle("-fx-background-color: #1E293B; -fx-text-fill: #F8FAFC; -fx-border-color: #334155;");
            }
        }
        if (colIndex >= 0 && colIndex < cellLabels[1].length && cellLabels[1][colIndex] != null) {
            cellLabels[1][colIndex].setStyle("-fx-background-color: rgba(56, 189, 248, 0.35); -fx-text-fill: #38BDF8; -fx-border-color: #38BDF8; -fx-font-weight: bold;");
        }
    }

    private Label createCell(String text, boolean isValue) {
        Label lbl = new Label(text);
        lbl.setAlignment(Pos.CENTER);
        lbl.setPrefWidth(38);
        lbl.setPrefHeight(32);
        if (isValue) {
            lbl.setStyle("-fx-background-color: #1E293B; -fx-text-fill: #F8FAFC; -fx-border-color: #334155; -fx-border-radius: 4px; -fx-background-radius: 4px; -fx-font-family: 'JetBrains Mono', monospace;");
        } else {
            lbl.setStyle("-fx-background-color: transparent; -fx-text-fill: #94A3B8; -fx-font-size: 11px;");
        }
        return lbl;
    }
}
