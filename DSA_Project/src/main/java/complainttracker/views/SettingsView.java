package complainttracker.views;

import complainttracker.data.DatabaseHandler;
import complainttracker.services.ComplaintService;
import complainttracker.services.DuplicateDetectionService;
import complainttracker.utils.UIUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;

public class SettingsView {
    private final Runnable onDataReset;

    public SettingsView(Runnable onDataReset) {
        this.onDataReset = onDataReset;
    }

    public Node getView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(24));

        Label lblTitle = new Label("System Settings & Algorithmic Parameters");
        lblTitle.getStyleClass().add("page-title");
        Label lblSub = new Label("Configure duplicate detection thresholds, restore seed data, and review engine specs.");
        lblSub.getStyleClass().add("page-subtitle");
        VBox headerBox = new VBox(2, lblTitle, lblSub);

        // Algorithm Parameters Card
        VBox algoCard = new VBox(14);
        algoCard.getStyleClass().add("card");

        Label lblAlgoHead = new Label("Duplicate Detection Engine (Wagner-Fischer DP)");
        lblAlgoHead.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #0F172A;");

        double currThresh = DuplicateDetectionService.getInstance().getThresholdPercentage();
        Label lblSliderVal = new Label(String.format("Current Similarity Warning Threshold: %.0f%%", currThresh));
        lblSliderVal.setStyle("-fx-font-weight: bold; -fx-text-fill: #2563EB;");

        Slider sldThresh = new Slider(30, 95, currThresh);
        sldThresh.setShowTickLabels(true);
        sldThresh.setShowTickMarks(true);
        sldThresh.setMajorTickUnit(10);
        sldThresh.setBlockIncrement(5);

        sldThresh.valueProperty().addListener((obs, old, val) -> {
            DuplicateDetectionService.getInstance().setThresholdPercentage(val.doubleValue());
            lblSliderVal.setText(String.format("Current Similarity Warning Threshold: %.0f%%", val.doubleValue()));
        });

        Label lblNote = new Label("If a citizen registers a complaint with title/description similarity above this threshold, the system displays an interactive warning modal dialog.");
        lblNote.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");

        algoCard.getChildren().addAll(lblAlgoHead, lblSliderVal, sldThresh, lblNote);

        // Database Maintenance Card
        VBox dbCard = new VBox(14);
        dbCard.getStyleClass().add("card");

        Label lblDbHead = new Label("Database Maintenance & Offline Seeder");
        lblDbHead.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #0F172A;");

        Label lblDbDesc = new Label("All complaint records, audit logs, and users are stored 100% offline in 'data/complaints_db.json'. You can reset to fresh demo data at any time.");
        lblDbDesc.setStyle("-fx-font-size: 12px; -fx-text-fill: #475569;");

        Button btnResetDemo = new Button("🔄 Restore Fresh Demo Data (35+ Realistic Complaints)");
        btnResetDemo.getStyleClass().add("btn-warning");
        btnResetDemo.setOnAction(e -> {
            if (UIUtils.showConfirmation("Reset Database", "Confirm Database Reset",
                    "This will restore the 35+ realistic complaints, historical audit trails, and departments.")) {
                DatabaseHandler.getInstance().seedDemoData();
                DatabaseHandler.getInstance().saveToFile();
                ComplaintService.getInstance().refreshIndexes();
                UIUtils.showInfo("Database Reset", "Demo Data Restored", "Sample records successfully seeded!");
                onDataReset.run();
            }
        });

        dbCard.getChildren().addAll(lblDbHead, lblDbDesc, btnResetDemo);

        // Project Info Card
        VBox infoCard = new VBox(12);
        infoCard.getStyleClass().add("card");
        infoCard.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: #CBD5E1;");

        Label lblAboutHead = new Label("About Complaint Tracker – DSA-3 Project");
        lblAboutHead.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #0F172A;");

        Label lblDsaList = new Label("Active DSA Components:\n" +
                "• Custom Separate-Chaining Hash Table (O(1) search with collision chains)\n" +
                "• Binary Min-Heap Priority Queue (O(log N) insert/extract, tie-breaking by arrival time)\n" +
                "• Circular Array FIFO Queue (O(1) enqueue/dequeue)\n" +
                "• Wagner-Fischer 2D Dynamic Programming (Levenshtein edit distance)\n" +
                "• Knuth-Morris-Pratt (KMP) with LPS array preprocessing\n" +
                "• Rabin-Karp Polynomial Rolling Hash\n" +
                "• Edmonds-Karp Network Flow (Augmenting paths for department capacity balancing)\n" +
                "• Multi-Criteria Scheduling Engine & QuickSort (Median-of-3)");
        lblDsaList.setStyle("-fx-font-family: monospace; -fx-font-size: 11px; -fx-text-fill: #334155;");

        infoCard.getChildren().addAll(lblAboutHead, lblDsaList);

        root.getChildren().addAll(headerBox, algoCard, dbCard, infoCard);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        return scroll;
    }
}
