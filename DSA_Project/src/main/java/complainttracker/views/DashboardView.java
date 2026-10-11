package complainttracker.views;

import complainttracker.models.Category;
import complainttracker.models.Complaint;
import complainttracker.models.Priority;
import complainttracker.services.AnalyticsService;
import complainttracker.services.AnalyticsService.SystemStats;
import complainttracker.services.ComplaintService;
import complainttracker.utils.UIUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class DashboardView {
    private final Consumer<Complaint> onSelectComplaint;

    public DashboardView(Consumer<Complaint> onSelectComplaint) {
        this.onSelectComplaint = onSelectComplaint;
    }

    public Node getView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(24));
        root.getStyleClass().add("root");

        // 1. Header
        Label lblTitle = new Label("System Dashboard & Real-Time Analytics");
        lblTitle.getStyleClass().add("page-title");
        Label lblSub = new Label("Live metrics, SLA compliance, and algorithmic workload distribution.");
        lblSub.getStyleClass().add("page-subtitle");
        VBox headerBox = new VBox(2, lblTitle, lblSub);

        // 2. Fetch fresh stats
        SystemStats stats = AnalyticsService.getInstance().calculateStats();

        // 3. Stat Cards Row
        HBox statsRow = new HBox(14);
        statsRow.getChildren().addAll(
                UIUtils.createStatCard("Total Complaints", String.valueOf(stats.totalComplaints), "All time registered", "#2563EB"),
                UIUtils.createStatCard("Pending / New", String.valueOf(stats.newCount + stats.pendingCount), "Awaiting action", "#F59E0B"),
                UIUtils.createStatCard("In Progress", String.valueOf(stats.inProgressCount), "Active resolution", "#8B5CF6"),
                UIUtils.createStatCard("Resolved", String.valueOf(stats.resolvedCount), String.format("%.1f%% SLA On-Time", stats.slaComplianceRate), "#10B981"),
                UIUtils.createStatCard("Critical Urgent", String.valueOf(stats.criticalCount), "Priority 1 emergencies", "#EF4444"),
                UIUtils.createStatCard("SLA Breached", String.valueOf(stats.slaBreachedCount), "Overdue compliance", "#DC2626")
        );

        // 4. Charts Section
        HBox chartsRow = new HBox(16);

        // Category Pie Chart
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for (Map.Entry<Category, Integer> entry : stats.categoryDistribution.entrySet()) {
            if (entry.getValue() > 0) {
                pieData.add(new PieChart.Data(entry.getKey().getDisplayName() + " (" + entry.getValue() + ")", entry.getValue()));
            }
        }
        PieChart categoryChart = new PieChart(pieData);
        categoryChart.setTitle("Complaints by Category");
        categoryChart.setLegendVisible(false);
        categoryChart.setPrefHeight(280);

        VBox pieBox = new VBox(8, categoryChart);
        pieBox.getStyleClass().add("card");
        HBox.setHgrow(pieBox, javafx.scene.layout.Priority.ALWAYS);

        // Priority Bar Chart
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Priority");
        yAxis.setLabel("Complaints Count");

        BarChart<String, Number> priorityChart = new BarChart<>(xAxis, yAxis);
        priorityChart.setTitle("Complaints by Priority Level");
        priorityChart.setLegendVisible(false);
        priorityChart.setPrefHeight(280);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Critical", stats.criticalCount));
        series.getData().add(new XYChart.Data<>("High", stats.highCount));
        series.getData().add(new XYChart.Data<>("Medium", stats.mediumCount));
        series.getData().add(new XYChart.Data<>("Low", stats.lowCount));
        priorityChart.getData().add(series);

        VBox barBox = new VBox(8, priorityChart);
        barBox.getStyleClass().add("card");
        HBox.setHgrow(barBox, javafx.scene.layout.Priority.ALWAYS);

        chartsRow.getChildren().addAll(pieBox, barBox);

        // 5. Recent Complaints Quick Table
        Label lblRecent = new Label("Recently Filed Complaints (Click to View Details)");
        lblRecent.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

        TableView<Complaint> table = new TableView<>();
        table.setPrefHeight(240);

        TableColumn<Complaint, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(90);

        TableColumn<Complaint, String> colTitle = new TableColumn<>("Title");
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colTitle.setPrefWidth(300);

        TableColumn<Complaint, String> colCat = new TableColumn<>("Category");
        colCat.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getCategory().getDisplayName()));
        colCat.setPrefWidth(160);

        TableColumn<Complaint, String> colPrio = new TableColumn<>("Priority");
        colPrio.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getPriority().getDisplayName()));
        colPrio.setPrefWidth(100);

        TableColumn<Complaint, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getStatus().getDisplayName()));
        colStatus.setPrefWidth(110);

        TableColumn<Complaint, String> colDate = new TableColumn<>("Created At");
        colDate.setCellValueFactory(new PropertyValueFactory<>("formattedCreatedAt"));
        colDate.setPrefWidth(150);

        table.getColumns().addAll(colId, colTitle, colCat, colPrio, colStatus, colDate);

        // Populate recent complaints (last 8)
        List<Complaint> all = ComplaintService.getInstance().getAllComplaints();
        ObservableList<Complaint> recentList = FXCollections.observableArrayList();
        for (int i = all.size() - 1; i >= Math.max(0, all.size() - 8); i--) {
            recentList.add(all.get(i));
        }
        table.setItems(recentList);

        table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && table.getSelectionModel().getSelectedItem() != null) {
                onSelectComplaint.accept(table.getSelectionModel().getSelectedItem());
            }
        });

        VBox tableCard = new VBox(10, lblRecent, table);
        tableCard.getStyleClass().add("card");

        root.getChildren().addAll(headerBox, statsRow, chartsRow, tableCard);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        return scroll;
    }
}
