package complainttracker.views;

import complainttracker.models.*;
import complainttracker.services.AuthService;
import complainttracker.services.ComplaintService;
import complainttracker.utils.UIUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.List;
import java.util.function.Consumer;

public class CustomerDashboardView {
    private final Consumer<String> onNavigate;
    private final Consumer<Complaint> onSelectComplaint;

    public CustomerDashboardView(Consumer<String> onNavigate, Consumer<Complaint> onSelectComplaint) {
        this.onNavigate = onNavigate;
        this.onSelectComplaint = onSelectComplaint;
    }

    public Node getView() {
        VBox root = new VBox(22);
        root.setPadding(new Insets(24));

        User currentUser = AuthService.getInstance().getCurrentUser();
        String customerName = currentUser != null ? currentUser.getFullName() : "Valued Citizen";

        // 1. Welcome Banner
        VBox welcomeCard = new VBox(8);
        welcomeCard.getStyleClass().add("card");
        welcomeCard.setStyle("-fx-background-color: linear-gradient(to right, #1E293B, #0F172A); -fx-background-radius: 12px; -fx-padding: 24px;");

        Label lblWelcome = new Label("Welcome back, " + customerName + " 👋");
        lblWelcome.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF;");

        Label lblWelcomeSub = new Label("Citizen Grievance Self-Service Portal — Register, track in real-time, and monitor resolution of your civic complaints.");
        lblWelcomeSub.setStyle("-fx-font-size: 13px; -fx-text-fill: #94A3B8;");

        // Quick Action Buttons Row
        HBox quickActions = new HBox(12);
        quickActions.setPadding(new Insets(8, 0, 0, 0));

        Button btnNewComplaint = new Button("➕ Register New Complaint");
        btnNewComplaint.getStyleClass().add("btn-primary");
        btnNewComplaint.setStyle("-fx-font-size: 13px; -fx-padding: 10px 18px;");
        btnNewComplaint.setOnAction(e -> onNavigate.accept("REGISTER"));

        Button btnTrack = new Button("🔍 Track Complaint Status");
        btnTrack.getStyleClass().add("btn-secondary");
        btnTrack.setStyle("-fx-font-size: 13px; -fx-padding: 10px 18px; -fx-background-color: #334155; -fx-text-fill: #FFFFFF; -fx-border-color: #475569;");
        btnTrack.setOnAction(e -> onNavigate.accept("TRACK_COMPLAINT"));

        Button btnMyComplaints = new Button("📄 View My Complaints");
        btnMyComplaints.getStyleClass().add("btn-secondary");
        btnMyComplaints.setStyle("-fx-font-size: 13px; -fx-padding: 10px 18px; -fx-background-color: #334155; -fx-text-fill: #FFFFFF; -fx-border-color: #475569;");
        btnMyComplaints.setOnAction(e -> onNavigate.accept("MY_COMPLAINTS"));

        Button btnHistory = new Button("📜 My Complaint History");
        btnHistory.getStyleClass().add("btn-secondary");
        btnHistory.setStyle("-fx-font-size: 13px; -fx-padding: 10px 18px; -fx-background-color: #334155; -fx-text-fill: #FFFFFF; -fx-border-color: #475569;");
        btnHistory.setOnAction(e -> onNavigate.accept("MY_HISTORY"));

        quickActions.getChildren().addAll(btnNewComplaint, btnTrack, btnMyComplaints, btnHistory);
        welcomeCard.getChildren().addAll(lblWelcome, lblWelcomeSub, quickActions);

        // 2. Fetch Customer Specific Metrics
        List<Complaint> myComplaints = ComplaintService.getInstance().getComplaintsForUser(currentUser);
        int total = myComplaints.size();
        int pending = 0;
        int inProgress = 0;
        int resolved = 0;
        int critical = 0;

        for (Complaint c : myComplaints) {
            if (c.getStatus() == Status.NEW || c.getStatus() == Status.PENDING) pending++;
            else if (c.getStatus() == Status.IN_PROGRESS || c.getStatus() == Status.ESCALATED) inProgress++;
            else if (c.getStatus() == Status.RESOLVED) resolved++;

            if (c.getPriority() == complainttracker.models.Priority.CRITICAL) critical++;
        }

        // 3. Responsive Stat Cards Grid
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(14);
        statsGrid.setVgap(14);

        for (int i = 0; i < 5; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(20.0);
            statsGrid.getColumnConstraints().add(cc);
        }

        VBox c1 = UIUtils.createStatCard("MY TOTAL COMPLAINTS", String.valueOf(total), "All tickets submitted", "#2563EB");
        VBox c2 = UIUtils.createStatCard("PENDING REVIEW", String.valueOf(pending), "Under initial intake", "#F59E0B");
        VBox c3 = UIUtils.createStatCard("IN PROGRESS", String.valueOf(inProgress), "Field technician assigned", "#8B5CF6");
        VBox c4 = UIUtils.createStatCard("RESOLVED & CLOSED", String.valueOf(resolved), "Satisfactorily resolved", "#10B981");
        VBox c5 = UIUtils.createStatCard("CRITICAL / URGENT", String.valueOf(critical), "High priority alerts", "#EF4444");

        statsGrid.add(c1, 0, 0);
        statsGrid.add(c2, 1, 0);
        statsGrid.add(c3, 2, 0);
        statsGrid.add(c4, 3, 0);
        statsGrid.add(c5, 4, 0);

        // 4. "My Recent Complaints" Table Card
        VBox tableCard = new VBox(12);
        tableCard.getStyleClass().add("card");

        HBox tableHeader = new HBox(12);
        tableHeader.setAlignment(Pos.CENTER_LEFT);

        Label lblTableTitle = new Label("My Recent Grievances");
        lblTableTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Button btnViewAll = new Button("View All My Complaints →");
        btnViewAll.getStyleClass().add("btn-secondary");
        btnViewAll.setOnAction(e -> onNavigate.accept("MY_COMPLAINTS"));

        tableHeader.getChildren().addAll(lblTableTitle, spacer, btnViewAll);

        TableView<Complaint> table = new TableView<>();
        table.setPrefHeight(280);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Complaint, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setMinWidth(85);
        colId.setMaxWidth(100);
        colId.setStyle("-fx-font-weight: bold; -fx-text-fill: #2563EB;");

        TableColumn<Complaint, String> colTitle = new TableColumn<>("Complaint Title");
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colTitle.setMinWidth(240);

        TableColumn<Complaint, String> colCat = new TableColumn<>("Category");
        colCat.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategory().getDisplayName()));
        colCat.setMinWidth(140);

        TableColumn<Complaint, Void> colPrio = new TableColumn<>("Priority");
        colPrio.setMinWidth(95);
        colPrio.setMaxWidth(110);
        colPrio.setCellFactory(col -> new TableCell<>() {
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

        TableColumn<Complaint, Void> colStatus = new TableColumn<>("Status");
        colStatus.setMinWidth(110);
        colStatus.setMaxWidth(130);
        colStatus.setCellFactory(col -> new TableCell<>() {
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

        TableColumn<Complaint, String> colSubmitted = new TableColumn<>("Submitted Date");
        colSubmitted.setCellValueFactory(new PropertyValueFactory<>("formattedCreatedAt"));
        colSubmitted.setMinWidth(140);

        TableColumn<Complaint, String> colSLA = new TableColumn<>("SLA Window");
        colSLA.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSlaRemainingDisplay()));
        colSLA.setMinWidth(130);

        TableColumn<Complaint, Void> colAction = new TableColumn<>("Action");
        colAction.setMinWidth(110);
        colAction.setMaxWidth(130);
        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button btnView = new Button("🔍 Details");
            {
                btnView.getStyleClass().add("btn-secondary");
                btnView.setStyle("-fx-font-size: 11px; -fx-padding: 4px 10px;");
                btnView.setOnAction(e -> {
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
                    setGraphic(btnView);
                }
            }
        });

        table.getColumns().addAll(colId, colTitle, colCat, colPrio, colStatus, colSubmitted, colSLA, colAction);

        if (myComplaints.isEmpty()) {
            table.setPlaceholder(new Label("You haven't filed any complaints yet. Click '+ Register New Complaint' above."));
        } else {
            table.setItems(FXCollections.observableArrayList(myComplaints));
        }

        table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && table.getSelectionModel().getSelectedItem() != null) {
                onSelectComplaint.accept(table.getSelectionModel().getSelectedItem());
            }
        });

        tableCard.getChildren().addAll(tableHeader, table);

        root.getChildren().addAll(welcomeCard, statsGrid, tableCard);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        return scroll;
    }
}
