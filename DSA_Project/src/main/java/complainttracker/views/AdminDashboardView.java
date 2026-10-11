package complainttracker.views;

import complainttracker.models.Category;
import complainttracker.models.Complaint;
import complainttracker.models.Department;
import complainttracker.models.Priority;
import complainttracker.models.Status;
import complainttracker.services.AnalyticsService;
import complainttracker.services.AnalyticsService.SystemStats;
import complainttracker.services.ComplaintService;
import complainttracker.services.DepartmentService;
import complainttracker.utils.UIUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class AdminDashboardView {
    private final Consumer<String> onNavigate;
    private final Consumer<Complaint> onSelectComplaint;

    public AdminDashboardView(Consumer<String> onNavigate, Consumer<Complaint> onSelectComplaint) {
        this.onNavigate = onNavigate;
        this.onSelectComplaint = onSelectComplaint;
    }

    public Node getView() {
        VBox root = new VBox(22);
        root.setPadding(new Insets(24));

        // 1. Header
        HBox headerBox = new HBox(16);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(2);
        Label lblTitle = new Label("Complaint Management Portal — Operations Center");
        lblTitle.getStyleClass().add("page-title");
        Label lblSub = new Label("Centralized administrative oversight, real-time algorithmic workload balancing, and SLA surveillance.");
        lblSub.getStyleClass().add("page-subtitle");
        titleBox.getChildren().addAll(lblTitle, lblSub);

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Label lblAdminBadge = new Label("🛡️ ADMINISTRATOR OPERATIONS");
        lblAdminBadge.setStyle("-fx-background-color: #1E293B; -fx-text-fill: #38BDF8; -fx-font-weight: bold; -fx-padding: 6px 14px; -fx-background-radius: 20px; -fx-font-size: 11px;");

        headerBox.getChildren().addAll(titleBox, spacer, lblAdminBadge);

        // 2. Fetch Metrics
        SystemStats stats = AnalyticsService.getInstance().calculateStats();

        // 3. Responsive 4x2 Stat Cards Grid
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(14);
        statsGrid.setVgap(14);

        for (int i = 0; i < 4; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(25.0);
            statsGrid.getColumnConstraints().add(cc);
        }

        // Row 1
        statsGrid.add(UIUtils.createStatCard("TOTAL COMPLAINTS", String.valueOf(stats.totalComplaints), "System wide registered", "#2563EB"), 0, 0);
        statsGrid.add(UIUtils.createStatCard("NEW / PENDING", String.valueOf(stats.newCount + stats.pendingCount), "Awaiting resolution", "#F59E0B"), 1, 0);
        statsGrid.add(UIUtils.createStatCard("IN PROGRESS", String.valueOf(stats.inProgressCount), "Active field work", "#8B5CF6"), 2, 0);
        statsGrid.add(UIUtils.createStatCard("RESOLVED CASES", String.valueOf(stats.resolvedCount), String.format("%.1f%% on-time SLA", stats.slaComplianceRate), "#10B981"), 3, 0);

        // Row 2
        statsGrid.add(UIUtils.createStatCard("CRITICAL URGENT", String.valueOf(stats.criticalCount), "Priority 1 emergencies", "#EF4444"), 0, 1);
        statsGrid.add(UIUtils.createStatCard("SLA BREACHED", String.valueOf(stats.slaBreachedCount), "Overdue compliance", "#DC2626"), 1, 1);
        statsGrid.add(UIUtils.createStatCard("AVG RESOLUTION TIME", String.format("%.1f hrs", stats.avgResolutionHours), "Mean turnaround time", "#0284C7"), 2, 1);
        statsGrid.add(UIUtils.createStatCard("CITIZEN SATISFACTION", String.format("%.2f / 5 ★", stats.avgCustomerRating), "Post-resolution ratings", "#D97706"), 3, 1);

        // 4. Operations Command Bar
        HBox commandBar = new HBox(12);
        commandBar.getStyleClass().add("card");
        commandBar.setAlignment(Pos.CENTER_LEFT);
        commandBar.setStyle("-fx-background-color: #0F172A; -fx-padding: 14px 20px;");

        Label lblCommandTitle = new Label("OPERATIONAL ACTIONS:");
        lblCommandTitle.setStyle("-fx-text-fill: #94A3B8; -fx-font-weight: bold; -fx-font-size: 11px;");

        Button btnFlow = new Button("⚡ Run Network Flow Allocation");
        btnFlow.getStyleClass().add("btn-primary");
        btnFlow.setOnAction(e -> onNavigate.accept("DEPARTMENT_FLOW"));

        Button btnQueue = new Button("⚡ Process Priority Queue");
        btnQueue.getStyleClass().add("btn-secondary");
        btnQueue.setStyle("-fx-background-color: #1E293B; -fx-text-fill: #FFFFFF; -fx-border-color: #334155;");
        btnQueue.setOnAction(e -> onNavigate.accept("PRIORITY_QUEUE"));

        Button btnSearchLab = new Button("🔍 Search & Pattern Lab");
        btnSearchLab.getStyleClass().add("btn-secondary");
        btnSearchLab.setStyle("-fx-background-color: #1E293B; -fx-text-fill: #FFFFFF; -fx-border-color: #334155;");
        btnSearchLab.setOnAction(e -> onNavigate.accept("SEARCH_LAB"));

        Button btnAlgoStudio = new Button("🧪 Algorithm Studio");
        btnAlgoStudio.getStyleClass().add("btn-secondary");
        btnAlgoStudio.setStyle("-fx-background-color: #1E293B; -fx-text-fill: #FFFFFF; -fx-border-color: #334155;");
        btnAlgoStudio.setOnAction(e -> onNavigate.accept("ALGO_LAB"));

        commandBar.getChildren().addAll(lblCommandTitle, btnFlow, btnQueue, btnSearchLab, btnAlgoStudio);

        // 5. Charts Row (Category Pie + Priority Bar)
        HBox chartsRow = new HBox(16);

        // Category Pie Chart
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for (Map.Entry<Category, Integer> entry : stats.categoryDistribution.entrySet()) {
            if (entry.getValue() > 0) {
                pieData.add(new PieChart.Data(entry.getKey().getDisplayName() + " (" + entry.getValue() + ")", entry.getValue()));
            }
        }
        PieChart categoryChart = new PieChart(pieData);
        categoryChart.setTitle("Grievance Volume by Category");
        categoryChart.setLegendVisible(false);
        categoryChart.setPrefHeight(270);

        VBox pieBox = new VBox(8, categoryChart);
        pieBox.getStyleClass().add("card");
        HBox.setHgrow(pieBox, javafx.scene.layout.Priority.ALWAYS);

        // Department Utilization Chart
        CategoryAxis dxAxis = new CategoryAxis();
        NumberAxis dyAxis = new NumberAxis();
        dxAxis.setLabel("Department");
        dyAxis.setLabel("Active Grievances");

        BarChart<String, Number> deptChart = new BarChart<>(dxAxis, dyAxis);
        deptChart.setTitle("Department Workload Distribution");
        deptChart.setLegendVisible(false);
        deptChart.setPrefHeight(270);

        XYChart.Series<String, Number> dSeries = new XYChart.Series<>();
        for (Department d : DepartmentService.getInstance().getAllDepartments()) {
            dSeries.getData().add(new XYChart.Data<>(d.getId(), d.getCurrentLoad()));
        }
        deptChart.getData().add(dSeries);

        VBox deptBox = new VBox(8, deptChart);
        deptBox.getStyleClass().add("card");
        HBox.setHgrow(deptBox, javafx.scene.layout.Priority.ALWAYS);

        chartsRow.getChildren().addAll(pieBox, deptBox);

        // 6. Urgent Attention Table (Critical & SLA Breached complaints)
        VBox urgentCard = new VBox(12);
        urgentCard.getStyleClass().add("card");

        HBox urgentHeader = new HBox(12);
        urgentHeader.setAlignment(Pos.CENTER_LEFT);

        Label lblUrgentTitle = new Label("⚠️ Grievances Requiring Urgent Operational Attention (Critical & SLA Breached)");
        lblUrgentTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #991B1B;");

        Region uSpacer = new Region();
        HBox.setHgrow(uSpacer, javafx.scene.layout.Priority.ALWAYS);

        Button btnViewAll = new Button("View All Complaints →");
        btnViewAll.getStyleClass().add("btn-secondary");
        btnViewAll.setOnAction(e -> onNavigate.accept("COMPLAINTS"));

        urgentHeader.getChildren().addAll(lblUrgentTitle, uSpacer, btnViewAll);

        TableView<Complaint> table = new TableView<>();
        table.setPrefHeight(240);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Complaint, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setMinWidth(85);
        colId.setMaxWidth(100);
        colId.setStyle("-fx-font-weight: bold; -fx-text-fill: #2563EB;");

        TableColumn<Complaint, String> colTitleCol = new TableColumn<>("Title");
        colTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        colTitleCol.setMinWidth(260);

        TableColumn<Complaint, String> colComplainant = new TableColumn<>("Complainant");
        colComplainant.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colComplainant.setMinWidth(140);

        TableColumn<Complaint, String> colDeptCol = new TableColumn<>("Department");
        colDeptCol.setCellValueFactory(new PropertyValueFactory<>("departmentName"));
        colDeptCol.setMinWidth(150);

        TableColumn<Complaint, Void> colPrioCol = new TableColumn<>("Priority");
        colPrioCol.setMinWidth(95);
        colPrioCol.setMaxWidth(110);
        colPrioCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Complaint c = getTableRow().getItem();
                    setGraphic(UIUtils.createPriorityBadge(c.getPriority()));
                    setAlignment(Pos.CENTER);
                }
            }
        });

        TableColumn<Complaint, Void> colStatusCol = new TableColumn<>("Status");
        colStatusCol.setMinWidth(110);
        colStatusCol.setMaxWidth(125);
        colStatusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Complaint c = getTableRow().getItem();
                    setGraphic(UIUtils.createStatusBadge(c.getStatus()));
                    setAlignment(Pos.CENTER);
                }
            }
        });

        TableColumn<Complaint, String> colSlaCol = new TableColumn<>("SLA Status");
        colSlaCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSlaRemainingDisplay()));
        colSlaCol.setMinWidth(130);

        TableColumn<Complaint, Void> colAction = new TableColumn<>("Action");
        colAction.setMinWidth(100);
        colAction.setMaxWidth(120);
        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button btnInspect = new Button("Inspect");
            {
                btnInspect.getStyleClass().add("btn-primary");
                btnInspect.setStyle("-fx-font-size: 11px; -fx-padding: 3px 10px;");
                btnInspect.setOnAction(e -> {
                    Complaint c = getTableRow().getItem();
                    if (c != null) onSelectComplaint.accept(c);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    setAlignment(Pos.CENTER);
                    setGraphic(btnInspect);
                }
            }
        });

        table.getColumns().addAll(colId, colTitleCol, colComplainant, colDeptCol, colPrioCol, colStatusCol, colSlaCol, colAction);

        // Filter urgent complaints: critical priority or SLA breached
        ObservableList<Complaint> urgentList = FXCollections.observableArrayList();
        for (Complaint c : ComplaintService.getInstance().getAllComplaints()) {
            if ((c.getPriority() == Priority.CRITICAL || c.isSlaBreached()) && c.getStatus() != Status.RESOLVED) {
                urgentList.add(c);
            }
        }
        table.setItems(urgentList);

        table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && table.getSelectionModel().getSelectedItem() != null) {
                onSelectComplaint.accept(table.getSelectionModel().getSelectedItem());
            }
        });

        urgentCard.getChildren().addAll(urgentHeader, table);

        root.getChildren().addAll(headerBox, statsGrid, commandBar, chartsRow, urgentCard);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        return scroll;
    }
}
