package complainttracker.views;

import complainttracker.data.DatabaseHandler;
import complainttracker.models.Complaint;
import complainttracker.models.Priority;
import complainttracker.models.Status;
import complainttracker.services.ComplaintService;
import complainttracker.utils.UIUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class AdminSlaManagementView {
    private final Consumer<Complaint> onInspectComplaint;
    private TableView<Complaint> tableBreached;
    private TableView<Complaint> tableAtRisk;

    public AdminSlaManagementView(Consumer<Complaint> onInspectComplaint) {
        this.onInspectComplaint = onInspectComplaint;
    }

    public Node getView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(24));
        root.setFillWidth(true);
        root.setStyle("-fx-background-color: #F8FAFC;");

        // 1. Header
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label lblTitle = new Label("⏱️ SLA Governance & Escalation Center");
        lblTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label lblSub = new Label("Enforce citizen charter resolution deadlines and mitigate SLA breach risks");
        lblSub.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");
        titleBox.getChildren().addAll(lblTitle, lblSub);

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Button btnRefresh = new Button("↻ Refresh SLA Data");
        btnRefresh.getStyleClass().add("btn-secondary");
        btnRefresh.setOnAction(e -> refreshData());

        header.getChildren().addAll(titleBox, spacer, btnRefresh);

        // 2. Metrics Row
        List<Complaint> all = DatabaseHandler.getInstance().getComplaints();
        List<Complaint> active = all.stream().filter(c -> c.getStatus() != Status.RESOLVED).collect(Collectors.toList());
        List<Complaint> breached = active.stream().filter(Complaint::isSlaBreached).collect(Collectors.toList());
        List<Complaint> atRisk = active.stream().filter(c -> {
            if (c.isSlaBreached()) return false;
            long hoursLeft = Duration.between(LocalDateTime.now(), c.getCreatedAt().plusHours(c.getSlaHours())).toHours();
            return hoursLeft <= 12;
        }).collect(Collectors.toList());

        double complianceRate = all.isEmpty() ? 100.0 :
                ((double) (all.size() - breached.size()) / all.size()) * 100.0;

        GridPane metricsGrid = new GridPane();
        metricsGrid.setHgap(16);
        metricsGrid.setVgap(16);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(25);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(25);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(25);
        ColumnConstraints col4 = new ColumnConstraints();
        col4.setPercentWidth(25);
        metricsGrid.getColumnConstraints().addAll(col1, col2, col3, col4);

        VBox cardTotal = createStatCard("ACTIVE TICKETS", String.valueOf(active.size()), "#2563EB", "Under SLA clock");
        VBox cardBreached = createStatCard("SLA BREACHED", String.valueOf(breached.size()), "#EF4444", "Overdue target hours");
        VBox cardAtRisk = createStatCard("AT RISK (<12h)", String.valueOf(atRisk.size()), "#F59E0B", "Immediate attention");
        VBox cardCompliance = createStatCard("COMPLIANCE RATE", String.format("%.1f%%", complianceRate), "#10B981", "Overall charter health");

        metricsGrid.add(cardTotal, 0, 0);
        metricsGrid.add(cardBreached, 1, 0);
        metricsGrid.add(cardAtRisk, 2, 0);
        metricsGrid.add(cardCompliance, 3, 0);

        // 3. SLA Targets Policy Card
        HBox policyCard = new HBox(20);
        policyCard.setAlignment(Pos.CENTER);
        policyCard.setPadding(new Insets(14, 20, 14, 20));
        policyCard.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 10px; -fx-border-color: #E2E8F0; -fx-border-radius: 10px;");

        Label lblPolicyTitle = new Label("📋 Citizen Charter SLA Targets:");
        lblPolicyTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155; -fx-font-size: 13px;");

        Label tagCrit = new Label("🚨 Critical: 12h");
        tagCrit.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #991B1B; -fx-padding: 4px 10px; -fx-background-radius: 12px; -fx-font-weight: bold;");

        Label tagHigh = new Label("🔥 High: 24h");
        tagHigh.setStyle("-fx-background-color: #FFEDD5; -fx-text-fill: #C2410C; -fx-padding: 4px 10px; -fx-background-radius: 12px; -fx-font-weight: bold;");

        Label tagMed = new Label("⚡ Medium: 48h");
        tagMed.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #92400E; -fx-padding: 4px 10px; -fx-background-radius: 12px; -fx-font-weight: bold;");

        Label tagLow = new Label("🟢 Low: 72h");
        tagLow.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #166534; -fx-padding: 4px 10px; -fx-background-radius: 12px; -fx-font-weight: bold;");

        policyCard.getChildren().addAll(lblPolicyTitle, tagCrit, tagHigh, tagMed, tagLow);

        // 4. Section 1: Breached Complaints Table
        VBox breachedBox = new VBox(10);
        Label lblBreachedTitle = new Label("🚨 Breached Grievances (Resolution Target Exceeded)");
        lblBreachedTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #DC2626;");

        tableBreached = buildBreachedTable();
        tableBreached.setItems(FXCollections.observableArrayList(breached));
        tableBreached.setPrefHeight(230);

        breachedBox.getChildren().addAll(lblBreachedTitle, tableBreached);

        // 5. Section 2: At-Risk Complaints Table
        VBox atRiskBox = new VBox(10);
        Label lblAtRiskTitle = new Label("⚠️ At-Risk Complaints (Expiring Within Next 12 Hours)");
        lblAtRiskTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #D97706;");

        tableAtRisk = buildAtRiskTable();
        tableAtRisk.setItems(FXCollections.observableArrayList(atRisk));
        tableAtRisk.setPrefHeight(230);

        atRiskBox.getChildren().addAll(lblAtRiskTitle, tableAtRisk);

        root.getChildren().addAll(header, metricsGrid, policyCard, breachedBox, atRiskBox);

        ScrollPane sp = new ScrollPane(root);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        return sp;
    }

    private TableView<Complaint> buildBreachedTable() {
        TableView<Complaint> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No breached grievances at this time. All cases within SLA."));

        TableColumn<Complaint, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId()));
        colId.setPrefWidth(90);

        TableColumn<Complaint, String> colTitle = new TableColumn<>("Title");
        colTitle.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitle()));
        colTitle.setPrefWidth(220);

        TableColumn<Complaint, String> colDept = new TableColumn<>("Department");
        colDept.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDepartmentName()));
        colDept.setPrefWidth(140);

        TableColumn<Complaint, String> colPriority = new TableColumn<>("Priority");
        colPriority.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPriority().getTitle()));
        colPriority.setPrefWidth(100);

        TableColumn<Complaint, String> colOverdue = new TableColumn<>("Overdue By");
        colOverdue.setCellValueFactory(c -> {
            long hoursOverdue = Duration.between(c.getValue().getCreatedAt().plusHours(c.getValue().getSlaHours()), LocalDateTime.now()).toHours();
            return new SimpleStringProperty(hoursOverdue + " hrs overdue");
        });
        colOverdue.setPrefWidth(130);

        TableColumn<Complaint, Void> colActions = new TableColumn<>("Escalation Actions");
        colActions.setPrefWidth(240);
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnEscalate = new Button("⚡ Escalate");
            private final Button btnInspect = new Button("👁️ Details");
            private final HBox box = new HBox(8, btnEscalate, btnInspect);

            {
                btnEscalate.setStyle("-fx-background-color: #DC2626; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 5px 10px; -fx-background-radius: 6px;");
                btnInspect.getStyleClass().add("btn-secondary");
                btnInspect.setStyle("-fx-font-size: 11px; -fx-padding: 5px 10px;");
                box.setAlignment(Pos.CENTER);

                btnEscalate.setOnAction(e -> {
                    Complaint c = getTableView().getItems().get(getIndex());
                    c.setPriority(Priority.CRITICAL);
                    ComplaintService.getInstance().updateComplaint(
                            c, "ESCALATION", "ESCALATED: Exceeded SLA deadline. Prompt resolution mandated."
                    );
                    UIUtils.showAlert(Alert.AlertType.INFORMATION, "Escalated",
                            "Complaint " + c.getId() + " escalated to CRITICAL priority!");
                    refreshData();
                });

                btnInspect.setOnAction(e -> {
                    Complaint c = getTableView().getItems().get(getIndex());
                    if (onInspectComplaint != null) {
                        onInspectComplaint.accept(c);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        table.getColumns().addAll(colId, colTitle, colDept, colPriority, colOverdue, colActions);
        return table;
    }

    private TableView<Complaint> buildAtRiskTable() {
        TableView<Complaint> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No complaints at immediate risk of SLA breach."));

        TableColumn<Complaint, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId()));
        colId.setPrefWidth(90);

        TableColumn<Complaint, String> colTitle = new TableColumn<>("Title");
        colTitle.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitle()));
        colTitle.setPrefWidth(220);

        TableColumn<Complaint, String> colDept = new TableColumn<>("Department");
        colDept.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDepartmentName()));
        colDept.setPrefWidth(140);

        TableColumn<Complaint, String> colPriority = new TableColumn<>("Priority");
        colPriority.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPriority().getTitle()));
        colPriority.setPrefWidth(100);

        TableColumn<Complaint, String> colRemaining = new TableColumn<>("Time Left");
        colRemaining.setCellValueFactory(c -> {
            long hoursRemaining = Duration.between(LocalDateTime.now(), c.getValue().getCreatedAt().plusHours(c.getValue().getSlaHours())).toHours();
            return new SimpleStringProperty(Math.max(0, hoursRemaining) + " hrs left");
        });
        colRemaining.setPrefWidth(130);

        TableColumn<Complaint, Void> colActions = new TableColumn<>("Actions");
        colActions.setPrefWidth(240);
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnPrioritize = new Button("🔥 Prioritize");
            private final Button btnInspect = new Button("👁️ Details");
            private final HBox box = new HBox(8, btnPrioritize, btnInspect);

            {
                btnPrioritize.setStyle("-fx-background-color: #D97706; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 5px 10px; -fx-background-radius: 6px;");
                btnInspect.getStyleClass().add("btn-secondary");
                btnInspect.setStyle("-fx-font-size: 11px; -fx-padding: 5px 10px;");
                box.setAlignment(Pos.CENTER);

                btnPrioritize.setOnAction(e -> {
                    Complaint c = getTableView().getItems().get(getIndex());
                    if (c.getPriority() == Priority.LOW) c.setPriority(Priority.MEDIUM);
                    else if (c.getPriority() == Priority.MEDIUM) c.setPriority(Priority.HIGH);
                    else if (c.getPriority() == Priority.HIGH) c.setPriority(Priority.CRITICAL);

                    ComplaintService.getInstance().updateComplaint(
                            c, "PRIORITY_ELEVATED", "PRIORITIZED: Approaching SLA breach threshold."
                    );
                    UIUtils.showAlert(Alert.AlertType.INFORMATION, "Priority Elevated",
                            "Complaint " + c.getId() + " priority raised to " + c.getPriority().getTitle());
                    refreshData();
                });

                btnInspect.setOnAction(e -> {
                    Complaint c = getTableView().getItems().get(getIndex());
                    if (onInspectComplaint != null) {
                        onInspectComplaint.accept(c);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        table.getColumns().addAll(colId, colTitle, colDept, colPriority, colRemaining, colActions);
        return table;
    }

    private void refreshData() {
        List<Complaint> all = DatabaseHandler.getInstance().getComplaints();
        List<Complaint> active = all.stream().filter(c -> c.getStatus() != Status.RESOLVED).collect(Collectors.toList());
        List<Complaint> breached = active.stream().filter(Complaint::isSlaBreached).collect(Collectors.toList());
        List<Complaint> atRisk = active.stream().filter(c -> {
            if (c.isSlaBreached()) return false;
            long hoursLeft = Duration.between(LocalDateTime.now(), c.getCreatedAt().plusHours(c.getSlaHours())).toHours();
            return hoursLeft <= 12;
        }).collect(Collectors.toList());

        if (tableBreached != null) tableBreached.setItems(FXCollections.observableArrayList(breached));
        if (tableAtRisk != null) tableAtRisk.setItems(FXCollections.observableArrayList(atRisk));
    }

    private VBox createStatCard(String title, String val, String color, String sub) {
        VBox card = new VBox(4);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 12px; -fx-border-color: #E2E8F0; -fx-border-radius: 12px;");

        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #64748B;");

        Label lblVal = new Label(val);
        lblVal.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        Label lblSub = new Label(sub);
        lblSub.setStyle("-fx-font-size: 11px; -fx-text-fill: #94A3B8;");

        card.getChildren().addAll(lblTitle, lblVal, lblSub);
        return card;
    }
}
