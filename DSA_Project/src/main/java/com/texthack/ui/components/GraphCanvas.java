package com.texthack.ui.components;

import com.texthack.core.ds.DynamicArray;
import com.texthack.core.ds.Trie;
import com.texthack.core.ds.Trie.TrieNode;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;

/**
 * Interactive canvas for rendering Trie graphs, Aho-Corasick failure links (red dashed lines),
 * dictionary output links (green dashed lines), and suffix automaton/tree states.
 */
public class GraphCanvas extends ScrollPane {
    private final Canvas canvas;
    private final GraphicsContext gc;

    public GraphCanvas() {
        this.canvas = new Canvas(800, 360);
        this.gc = canvas.getGraphicsContext2D();

        setContent(canvas);
        setFitToWidth(true);
        setFitToHeight(true);
        setPrefHeight(260);
        setStyle("-fx-background-color: transparent;");

        drawEmpty("Trie / Automaton Graph will appear here upon execution.");
    }

    public void drawEmpty(String message) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.web("#1E293B"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.web("#64748B"));
        gc.fillText(message, 30, 40);
    }

    public void drawTrie(Trie trie, int activeNodeId) {
        if (trie == null) return;
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.web("#0F172A"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Simple hierarchical layout
        TrieNode root = trie.getRoot();
        double startX = canvas.getWidth() / 2;
        double startY = 40;
        drawTrieNodeRecursive(root, startX, startY, canvas.getWidth() / 3, activeNodeId);
    }

    private void drawTrieNodeRecursive(TrieNode node, double x, double y, double xSpread, int activeNodeId) {
        if (node == null) return;

        DynamicArray<TrieNode> children = node.children.values();
        int childCount = children.size();
        double childSpacing = (childCount > 1) ? (xSpread * 2) / (childCount - 1) : 0;
        double childStartX = x - xSpread;

        for (int i = 0; i < childCount; i++) {
            TrieNode child = children.get(i);
            double cx = (childCount == 1) ? x : childStartX + i * childSpacing;
            double cy = y + 70;

            // Draw tree edge
            gc.setStroke(Color.web("#334155"));
            gc.setLineWidth(2);
            gc.strokeLine(x, y, cx, cy);

            // Draw edge character
            gc.setFill(Color.web("#38BDF8"));
            gc.fillText(String.valueOf(child.ch), (x + cx) / 2 + 5, (y + cy) / 2);

            // Draw failure link if present (curved red dashed)
            if (child.failLink != null && child.failLink != node && child.failLink.id != 0) {
                gc.setStroke(Color.web("#EF4444"));
                gc.setLineWidth(1.2);
                gc.strokeLine(cx, cy, x, y);
            }

            drawTrieNodeRecursive(child, cx, cy, xSpread * 0.55, activeNodeId);
        }

        // Draw node circle
        boolean isActive = (node.id == activeNodeId);
        double radius = 16;
        if (isActive) {
            gc.setFill(Color.web("#38BDF8"));
            gc.fillOval(x - radius - 3, y - radius - 3, (radius + 3) * 2, (radius + 3) * 2);
        }

        gc.setFill(node.isEndOfWord ? Color.web("#22C55E") : Color.web("#1E293B"));
        gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);

        gc.setStroke(Color.web("#64748B"));
        gc.setLineWidth(1.5);
        gc.strokeOval(x - radius, y - radius, radius * 2, radius * 2);

        // Node ID and char label
        gc.setFill(Color.web("#F8FAFC"));
        String label = (node.id == 0) ? "R" : String.valueOf(node.ch);
        gc.fillText(label, x - 4, y + 4);
    }
}
