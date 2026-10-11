package complainttracker.views;

import complainttracker.data.DatabaseHandler;
import complainttracker.models.Category;
import complainttracker.models.Complaint;
import complainttracker.models.Department;
import complainttracker.models.Priority;
import complainttracker.models.Status;
import complainttracker.services.AuthService;
import complainttracker.services.ComplaintService;
import complainttracker.utils.UIUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ComplaintListView {
    private final Consumer<Complaint> onNavigateToHistory;
    private TableView<Complaint> table;
    private ObservableList<Complaint> masterData;
    private FilteredList<Complaint> filteredData;

    public ComplaintListView(Consumer<Complaint> onNavigateToHistory) {
        this.onNavigateToHistory = onNavigateToHistory;
    }

    public Node getView() {
        VBox root = new VBox(16);
        root.setPadding(new Insets(24));

        Label lblTitle = new Label("Grievance Repository & Management");
        lblTitle.getStyleClass().add("page-title");
        Label lblSub = new Label("Filter, search, audit, update status, and manage the complete complaint lifecycle.");
        lblSub.getStyleClass().add("page-subtitle");
        VBox headerBox = new VBox(2, lblTitle, lblSub);

        // Filter Controls Card
        VBox filterCard = new VBox(12);
        filterCard.getStyleClass().add("card");

        TextField txtSearch = new TextField();
        txtSearch.setPromptText("🔍 Fast search by ID, Title, Customer, or Location...");
        txtSearch.setPrefWidth(300);

        ComboBox<String> cmbCat = new ComboBox<>();
        cmbCat.getItems().add("All Categories");
        for (Category c : Category.values()) cmbCat.getItems().add(c.getDisplayName());
        cmbCat.setValue("All Categories");

        ComboBox<String> cmbPrio = new ComboBox<>();
        cmbPrio.getItems().add("All Priorities");
        for (Priority p : Priority.values()) cmbPrio.getItems().add(p.getDisplayName());
        cmbPrio.setValue("All Priorities");

        ComboBox<String> cmbStatus = new ComboBox<>();
        cmbStatus.getItems().add("All Statuses");
        for (Status s : Status.values()) cmbStatus.getItems().add(s.getDisplayName());
        cmbStatus.setValue("All Statuses");

        CheckBox chkSlaOnly = new CheckBox("SLA Breached Only");

        Button btnReset = new Button("Reset Filters");
        btnReset.getStyleClass().add("btn-secondary");

        HBox filterRow = new HBox(12, txtSearch, cmbCat, cmbPrio, cmbStatus, chkSlaOnly, btnReset);
        filterRow.setAlignment(Pos.CENTER_LEFT);
        filterCard.getChildren().add(filterRow);

        // Table Setup
        table = new TableView<>();
        table.setPrefHeight(520);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, javafx.scene.layout.Priority.ALWAYS);

        TableColumn<Complaint, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(85);
        colId.setStyle("-fx-font-weight: bold; -fx-text-fill: #2563EB;");

        TableColumn<Complaint, String> colTitle = new TableColumn<>("Title");
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colTitle.setPrefWidth(240);

        TableColumn<Complaint, String> colCustomer = new TableColumn<>("Complainant");
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colCustomer.setPrefWidth(140);

        TableColumn<Complaint, String> colCategory = new TableColumn<>("Category");
        colCategory.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategory().getDisplayName()));
        colCategory.setPrefWidth(150);

        TableColumn<Complaint, Void> colPriority = new TableColumn<>("Priority");
        colPriority.setPrefWidth(100);
        colPriority.setCellFactory(col -> new TableCell<>() {
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
        colStatus.setPrefWidth(110);
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

        TableColumn<Complaint, String> colSLA = new TableColumn<>("SLA Window");
        colSLA.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSlaRemainingDisplay()));
        colSLA.setPrefWidth(130);

        TableColumn<Complaint, String> colDept = new TableColumn<>("Department");
        colDept.setCellValueFactory(new PropertyValueFactory<>("departmentName"));
        colDept.setPrefWidth(180);

        TableColumn<Complaint, Void> colActions = new TableColumn<>("Actions");
        colActions.setPrefWidth(240);
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnView = new Button("View");
            private final Button btnEdit = new Button("Update");
            private final Button btnResolve = new Button("Resolve");
            private final Button btnDel = new Button("Del");
            private final HBox pane = new HBox(6, btnView, btnEdit, btnResolve, btnDel);

            {
                btnView.getStyleClass().add("btn-secondary");
                btnEdit.getStyleClass().add("btn-secondary");
                btnResolve.getStyleClass().add("btn-success");
                btnDel.getStyleClass().add("btn-danger");
                pane.setAlignment(Pos.CENTER_LEFT);

                btnView.setStyle("-fx-font-size: 11px; -fx-padding: 3px 8px;");
                btnEdit.setStyle("-fx-font-size: 11px; -fx-padding: 3px 8px;");
                btnResolve.setStyle("-fx-font-size: 11px; -fx-padding: 3px 8px;");
                btnDel.setStyle("-fx-font-size: 11px; -fx-padding: 3px 8px;");

                btnView.setOnAction(e -> {
                    Complaint c = getTableRow().getItem();
                    if (c != null) {
                        if (AuthService.getInstance().isAdmin()) {
                            AdminComplaintDetailDialog.show(c, () -> refreshTable(), onNavigateToHistory);
                        } else {
                            showDetailDialog(c);
                        }
                    }
                });

                btnEdit.setOnAction(e -> {
                    Complaint c = getTableRow().getItem();
                    if (c != null) showUpdateDialog(c);
                });

                btnResolve.setOnAction(e -> {
                    Complaint c = getTableRow().getItem();
                    if (c != null) showResolveDialog(c);
                });

                btnDel.setOnAction(e -> {
                    Complaint c = getTableRow().getItem();
                    if (c != null) {
                        if (UIUtils.showConfirmation("Delete Complaint", "Confirm Deletion",
                                "Are you sure you want to permanently delete complaint " + c.getId() + "?")) {
                            ComplaintService.getInstance().deleteComplaint(c.getId());
                            refreshTable();
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Complaint c = getTableRow().getItem();
                    btnResolve.setDisable(c.getStatus() == Status.RESOLVED);
                    setGraphic(pane);
                }
            }
        });

        table.getColumns().addAll(colId, colTitle, colCustomer, colCategory, colPriority, colStatus, colSLA, colDept, colActions);

        // Load data
        refreshTable();

        // Filter Logic
        Runnable applyFilters = () -> {
            String query = txtSearch.getText().toLowerCase().trim();
            String cat = cmbCat.getValue();
            String prio = cmbPrio.getValue();
            String stat = cmbStatus.getValue();
            boolean slaOnly = chkSlaOnly.isSelected();

            filteredData.setPredicate(c -> {
                if (!query.isEmpty()) {
                    boolean matchesQuery = c.getId().toLowerCase().contains(query)
                            || c.getTitle().toLowerCase().contains(query)
                            || c.getCustomerName().toLowerCase().contains(query)
                            || c.getLocation().toLowerCase().contains(query);
                    if (!matchesQuery) return false;
                }

                if (cat != null && !cat.equals("All Categories") && !c.getCategory().getDisplayName().equals(cat)) {
                    return false;
                }

                if (prio != null && !prio.equals("All Priorities") && !c.getPriority().getDisplayName().equals(prio)) {
                    return false;
                }

                if (stat != null && !stat.equals("All Statuses") && !c.getStatus().getDisplayName().equals(stat)) {
                    return false;
                }

                if (slaOnly && !c.isSlaBreached()) {
                    return false;
                }

                return true;
            });
        };

        txtSearch.textProperty().addListener((obs, old, val) -> applyFilters.run());
        cmbCat.setOnAction(e -> applyFilters.run());
        cmbPrio.setOnAction(e -> applyFilters.run());
        cmbStatus.setOnAction(e -> applyFilters.run());
        chkSlaOnly.setOnAction(e -> applyFilters.run());

        btnReset.setOnAction(e -> {
            txtSearch.clear();
            cmbCat.setValue("All Categories");
            cmbPrio.setValue("All Priorities");
            cmbStatus.setValue("All Statuses");
            chkSlaOnly.setSelected(false);
            applyFilters.run();
        });

        table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && table.getSelectionModel().getSelectedItem() != null) {
                Complaint c = table.getSelectionModel().getSelectedItem();
                if (AuthService.getInstance().isAdmin()) {
                    AdminComplaintDetailDialog.show(c, () -> refreshTable(), onNavigateToHistory);
                } else {
                    showDetailDialog(c);
                }
            }
        });

        root.getChildren().addAll(headerBox, filterCard, table);
        return root;
    }

    public void refreshTable() {
        masterData = FXCollections.observableArrayList(ComplaintService.getInstance().getAllComplaints());
        filteredData = new FilteredList<>(masterData, p -> true);
        table.setItems(filteredData);
    }

    public void showDetailDialog(Complaint c) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Grievance File: " + c.getId());
        dialog.setHeaderText(c.getTitle());

        VBox content = new VBox(14);
        content.setPrefWidth(550);
        content.setPadding(new Insets(16));

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(8);

        grid.addRow(0, new Label("ID:"), new Label(c.getId()));
        grid.addRow(1, new Label("Complainant:"), new Label(c.getCustomerName() + " (" + c.getCustomerContact() + ")"));
        grid.addRow(2, new Label("Category:"), new Label(c.getCategory().getDisplayName()));
        grid.addRow(3, new Label("Priority:"), UIUtils.createPriorityBadge(c.getPriority()));
        grid.addRow(4, new Label("Status:"), UIUtils.createStatusBadge(c.getStatus()));
        grid.addRow(5, new Label("Department:"), new Label(c.getDepartmentName()));
        grid.addRow(6, new Label("Location:"), new Label(c.getLocation()));
        grid.addRow(7, new Label("Filed Date:"), new Label(c.getFormattedCreatedAt()));
        grid.addRow(8, new Label("SLA Deadline:"), new Label(c.getFormattedDeadline() + " (" + c.getSlaRemainingDisplay() + ")"));
        grid.addRow(9, new Label("Assigned Staff:"), new Label(c.getAssignedStaff() != null ? c.getAssignedStaff() : "Unassigned"));

        if (c.getStatus() == Status.RESOLVED) {
            grid.addRow(10, new Label("Resolved At:"), new Label(c.getFormattedResolvedAt()));
            grid.addRow(11, new Label("Citizen Rating:"), new Label(c.getRating() > 0 ? c.getRating() + "/5 ★" : "Unrated"));
            grid.addRow(12, new Label("Feedback:"), new Label(c.getFeedback() != null ? c.getFeedback() : "None"));
        }

        Label lblDescTitle = new Label("Description:");
        lblDescTitle.setStyle("-fx-font-weight: bold;");
        Label lblDesc = new Label(c.getDescription());
        lblDesc.setWrapText(true);

        Button btnViewAudit = new Button("📜 View Audit Trail & Full History");
        btnViewAudit.getStyleClass().add("btn-secondary");
        btnViewAudit.setOnAction(e -> {
            dialog.close();
            onNavigateToHistory.accept(c);
        });

        content.getChildren().addAll(grid, lblDescTitle, lblDesc, btnViewAudit);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private void showUpdateDialog(Complaint c) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Update Status / Assignment: " + c.getId());

        VBox box = new VBox(12);
        box.setPadding(new Insets(16));
        box.setPrefWidth(420);

        ComboBox<Status> cmbStat = new ComboBox<>(FXCollections.observableArrayList(Status.values()));
        cmbStat.setValue(c.getStatus());
        cmbStat.setMaxWidth(Double.MAX_VALUE);

        ComboBox<Priority> cmbPrio = new ComboBox<>(FXCollections.observableArrayList(Priority.values()));
        cmbPrio.setValue(c.getPriority());
        cmbPrio.setMaxWidth(Double.MAX_VALUE);

        List<Department> depts = DatabaseHandler.getInstance().getDepartments();
        ComboBox<Department> cmbDept = new ComboBox<>(FXCollections.observableArrayList(depts));
        for (Department d : depts) {
            if (d.getId().equalsIgnoreCase(c.getDepartmentId())) {
                cmbDept.setValue(d);
                break;
            }
        }
        cmbDept.setMaxWidth(Double.MAX_VALUE);

        TextField txtStaff = new TextField(c.getAssignedStaff() != null ? c.getAssignedStaff() : "");
        TextField txtNotes = new TextField();
        txtNotes.setPromptText("Enter audit trail notes for this update...");

        box.getChildren().addAll(
                new Label("Status:"), cmbStat,
                new Label("Priority:"), cmbPrio,
                new Label("Department:"), cmbDept,
                new Label("Assigned Staff:"), txtStaff,
                new Label("Update Reason / Notes:"), txtNotes
        );

        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                c.setStatus(cmbStat.getValue());
                c.setPriority(cmbPrio.getValue());
                if (cmbDept.getValue() != null) {
                    c.setDepartmentId(cmbDept.getValue().getId());
                    c.setDepartmentName(cmbDept.getValue().getName());
                }
                c.setAssignedStaff(txtStaff.getText().trim());
                ComplaintService.getInstance().updateComplaint(c, "Manual Update", txtNotes.getText().trim());
                return true;
            }
            return false;
        });

        dialog.showAndWait().ifPresent(updated -> {
            if (updated) refreshTable();
        });
    }

    private void showResolveDialog(Complaint c) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Resolve Grievance: " + c.getId());

        VBox box = new VBox(12);
        box.setPadding(new Insets(16));
        box.setPrefWidth(420);

        Label lblPrompt = new Label("Record resolution details and citizen satisfaction rating:");

        ComboBox<Integer> cmbRating = new ComboBox<>(FXCollections.observableArrayList(5, 4, 3, 2, 1));
        cmbRating.setValue(5);

        TextArea txtFeedback = new TextArea();
        txtFeedback.setPromptText("Customer remarks / feedback (optional)...");
        txtFeedback.setPrefRowCount(2);

        TextArea txtResolutionNotes = new TextArea();
        txtResolutionNotes.setPromptText("Technical actions taken by field technicians to resolve the issue...");
        txtResolutionNotes.setPrefRowCount(3);

        box.getChildren().addAll(
                lblPrompt,
                new Label("Citizen Rating (1-5 Stars):"), cmbRating,
                new Label("Citizen Feedback Comments:"), txtFeedback,
                new Label("Technical Resolution Report:"), txtResolutionNotes
        );

        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                int rating = cmbRating.getValue();
                String fb = txtFeedback.getText().trim();
                String notes = txtResolutionNotes.getText().trim();
                ComplaintService.getInstance().resolveComplaint(c.getId(), rating, fb, notes);
                return true;
            }
            return false;
        });

        dialog.showAndWait().ifPresent(res -> {
            if (res) refreshTable();
        });
    }
}
