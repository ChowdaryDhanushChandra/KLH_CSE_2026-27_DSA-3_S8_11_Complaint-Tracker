package complainttracker.views;

import complainttracker.models.Department;
import complainttracker.services.AnalyticsService;
import complainttracker.services.AnalyticsService.SystemStats;
import complainttracker.services.DepartmentService;
import complainttracker.services.ReportService;
import complainttracker.utils.UIUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;

public class AnalyticsView {

    public Node getView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(24));

        Label lblTitle = new Label("Performance Reports & Analytical Intelligence");
        lblTitle.getStyleClass().add("page-title");
        Label lblSub = new Label("Institutional SLA compliance, citizen satisfaction ratings, and CSV report export.");
        lblSub.getStyleClass().add("page-subtitle");
        VBox headerBox = new VBox(2, lblTitle, lblSub);

        SystemStats stats = AnalyticsService.getInstance().calculateStats();

        // 1. KPI Cards Row
        HBox kpiRow = new HBox(16);
        kpiRow.getChildren().addAll(
                UIUtils.createStatCard("SLA Compliance", String.format("%.1f%%", stats.slaComplianceRate), "Target >85%", "#10B981"),
                UIUtils.createStatCard("Avg Resolution Time", String.format("%.1f hrs", stats.avgResolutionHours), "Across departments", "#2563EB"),
                UIUtils.createStatCard("Citizen CSAT Rating", String.format("%.2f / 5 ★", stats.avgCustomerRating), "Based on feedback", "#F59E0B"),
                UIUtils.createStatCard("Resolved Cases", String.valueOf(stats.resolvedCount), "Completed files", "#8B5CF6")
        );

        // 2. Export Button Card
        HBox exportCard = new HBox(16);
        exportCard.getStyleClass().add("card");
        exportCard.setAlignment(Pos.CENTER_LEFT);

        VBox exportInfo = new VBox(4);
        HBox.setHgrow(exportInfo, javafx.scene.layout.Priority.ALWAYS);
        Label lblExpTitle = new Label("Generate Institutional Audit CSV Report");
        lblExpTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #0F172A;");
        Label lblExpSub = new Label("Download comprehensive spreadsheet containing all complaints, SLA statuses, ratings, and resolution notes.");
        lblExpSub.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");
        exportInfo.getChildren().addAll(lblExpTitle, lblExpSub);

        Button btnExport = new Button("📥 EXPORT COMPLAINTS TO CSV");
        btnExport.getStyleClass().add("btn-primary");
        btnExport.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Save Complaints CSV Report");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv"));
            chooser.setInitialFileName("ComplaintTracker_Report.csv");

            File target = chooser.showSaveDialog(root.getScene().getWindow());
            if (target != null) {
                boolean ok = ReportService.getInstance().exportToCSV(target, null);
                if (ok) {
                    UIUtils.showInfo("Export Successful", "Report Generated Successfully",
                            "Complaints database has been written to: " + target.getAbsolutePath());
                } else {
                    UIUtils.showError("Export Error", "Export Failed", "Could not write to file: " + target.getAbsolutePath());
                }
            }
        });

        exportCard.getChildren().addAll(exportInfo, btnExport);

        // 3. Rating & Department Charts
        HBox chartsRow = new HBox(16);

        // Rating BarChart
        CategoryAxis rxAxis = new CategoryAxis();
        NumberAxis ryAxis = new NumberAxis();
        rxAxis.setLabel("Satisfaction Stars");
        ryAxis.setLabel("Number of Citizens");

        BarChart<String, Number> ratingChart = new BarChart<>(rxAxis, ryAxis);
        ratingChart.setTitle("Citizen Rating Distribution (CSAT)");
        ratingChart.setLegendVisible(false);
        ratingChart.setPrefHeight(280);

        XYChart.Series<String, Number> rSeries = new XYChart.Series<>();
        for (int stars = 1; stars <= 5; stars++) {
            rSeries.getData().add(new XYChart.Data<>(stars + " Stars ★", stats.ratingCounts[stars]));
        }
        ratingChart.getData().add(rSeries);

        VBox ratingBox = new VBox(8, ratingChart);
        ratingBox.getStyleClass().add("card");
        HBox.setHgrow(ratingBox, javafx.scene.layout.Priority.ALWAYS);

        // Department Utilization BarChart
        CategoryAxis dxAxis = new CategoryAxis();
        NumberAxis dyAxis = new NumberAxis();
        dxAxis.setLabel("Department");
        dyAxis.setLabel("Active Cases");

        BarChart<String, Number> deptChart = new BarChart<>(dxAxis, dyAxis);
        deptChart.setTitle("Department Workload");
        deptChart.setLegendVisible(false);
        deptChart.setPrefHeight(280);

        XYChart.Series<String, Number> dSeries = new XYChart.Series<>();
        for (Department d : DepartmentService.getInstance().getAllDepartments()) {
            dSeries.getData().add(new XYChart.Data<>(d.getId(), d.getCurrentLoad()));
        }
        deptChart.getData().add(dSeries);

        VBox deptBox = new VBox(8, deptChart);
        deptBox.getStyleClass().add("card");
        HBox.setHgrow(deptBox, javafx.scene.layout.Priority.ALWAYS);

        chartsRow.getChildren().addAll(ratingBox, deptBox);

        root.getChildren().addAll(headerBox, kpiRow, exportCard, chartsRow);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        return scroll;
    }
}
