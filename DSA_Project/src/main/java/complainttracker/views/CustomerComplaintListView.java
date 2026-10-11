package complainttracker.views;

import complainttracker.models.Category;
import complainttracker.models.Complaint;
import complainttracker.models.Status;
import complainttracker.models.User;
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
import java.util.function.Consumer;

public class CustomerComplaintListView {
    private final Consumer<Complaint> onTrackComplaint;
    private final Runnable onNewComplaint;
    private TableView<Complaint> table;
    private ObservableList<Complaint> masterData;
    private FilteredList<Complaint> filteredData;

    public CustomerComplaintListView(Consumer<Complaint> onTrackComplaint) {
        this(onTrackComplaint, null);
    }

    public CustomerComplaintListView(Consumer<Complaint> onTrackComplaint, Runnable onNewComplaint) {
        this.onTrackComplaint = onTrackComplaint;
        this.onNewComplaint = onNewComplaint;
    }

    public Node getView() {
        VBox root = new VBox(16);
        root.setPadding(new Insets(24));

        Label lblTitle = new Label("My Filed Grievances");
        lblTitle.getStyleClass().add("page-title");
        Label lblSub = new Label("Review all complaints filed under your account, check status, and add follow-up notes.");
        lblSub.getStyleClass().add("page-subtitle");
        VBox headerBox = new VBox(2, lblTitle, lblSub);

        // Filter Card
        VBox filterCard = new VBox(12);
        filterCard.getStyleClass().add("card");

        TextField txtSearch = new TextField();
        txtSearch.setPromptText("🔍 Search your complaints by ID, title, or location...");
        txtSearch.setPrefWidth(320);

        ComboBox<String> cmbCat = new ComboBox<>();
        cmbCat.getItems().add("All Categories");
        for (Category c : Category.values()) cmbCat.getItems().add(c.getDisplayName());
        cmbCat.setValue("All Categories");

        ComboBox<String> cmbStatus = new ComboBox<>();
        cmbStatus.getItems().add("All Statuses");
        for (Status s : Status.values()) cmbStatus.getItems().add(s.getDisplayName());
        cmbStatus.setValue("All Statuses");

        Button btnReset = new Button("Clear Filter");
        btnReset.getStyleClass().add("btn-secondary");

        HBox filterRow = new HBox(12, txtSearch, cmbCat, cmbStatus, btnReset);
        filterRow.setAlignment(Pos.CENTER_LEFT);
        filterCard.getChildren().add(filterRow);

        // Table
        table = new TableView<>();
        table.setPrefHeight(520);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, javafx.scene.layout.Priority.ALWAYS);

        TableColumn<Complaint, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setMinWidth(85);
        colId.setMaxWidth(100);
        colId.setStyle("-fx-font-weight: bold; -fx-text-fill: #2563EB;");

        TableColumn<Complaint, String> colTitle = new TableColumn<>("Title");
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

        TableColumn<Complaint, String> colDept = new TableColumn<>("Department");
        colDept.setCellValueFactory(new PropertyValueFactory<>("departmentName"));
        colDept.setMinWidth(160);

        TableColumn<Complaint, String> colCreated = new TableColumn<>("Filed Date");
        colCreated.setCellValueFactory(new PropertyValueFactory<>("formattedCreatedAt"));
        colCreated.setMinWidth(130);

        TableColumn<Complaint, String> colSLA = new TableColumn<>("SLA Window");
        colSLA.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSlaRemainingDisplay()));
        colSLA.setMinWidth(130);

        TableColumn<Complaint, Void> colAction = new TableColumn<>("Action");
        colAction.setMinWidth(150);
        colAction.setMaxWidth(170);
        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button btnTrack = new Button("🔍 Track");
            private final Button btnNote = new Button("💬 Note");
            private final HBox box = new HBox(6, btnTrack, btnNote);

            {
                btnTrack.getStyleClass().add("btn-primary");
                btnNote.getStyleClass().add("btn-secondary");
                btnTrack.setStyle("-fx-font-size: 11px; -fx-padding: 3px 8px;");
                btnNote.setStyle("-fx-font-size: 11px; -fx-padding: 3px 8px;");
                box.setAlignment(Pos.CENTER_LEFT);

                btnTrack.setOnAction(e -> {
                    Complaint c = getTableRow().getItem();
                    if (c != null) onTrackComplaint.accept(c);
                });

                btnNote.setOnAction(e -> {
                    Complaint c = getTableRow().getItem();
                    if (c != null) showQuickNoteDialog(c);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    setGraphic(box);
                }
            }
        });

        table.getColumns().addAll(colId, colTitle, colCat, colPrio, colStatus, colDept, colCreated, colSLA, colAction);

        refreshData();

        // Filters
        Runnable applyFilters = () -> {
            String query = txtSearch.getText().toLowerCase().trim();
            String cat = cmbCat.getValue();
            String stat = cmbStatus.getValue();

            filteredData.setPredicate(c -> {
                if (!query.isEmpty()) {
                    boolean m = c.getId().toLowerCase().contains(query)
                            || c.getTitle().toLowerCase().contains(query)
                            || c.getLocation().toLowerCase().contains(query);
                    if (!m) return false;
                }

                if (cat != null && !cat.equals("All Categories") && !c.getCategory().getDisplayName().equals(cat)) {
                    return false;
                }

                if (stat != null && !stat.equals("All Statuses") && !c.getStatus().getDisplayName().equals(stat)) {
                    return false;
                }

                return true;
            });
        };

        txtSearch.textProperty().addListener((obs, old, val) -> applyFilters.run());
        cmbCat.setOnAction(e -> applyFilters.run());
        cmbStatus.setOnAction(e -> applyFilters.run());

        btnReset.setOnAction(e -> {
            txtSearch.clear();
            cmbCat.setValue("All Categories");
            cmbStatus.setValue("All Statuses");
            applyFilters.run();
        });

        table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && table.getSelectionModel().getSelectedItem() != null) {
                onTrackComplaint.accept(table.getSelectionModel().getSelectedItem());
            }
        });

        root.getChildren().addAll(headerBox, filterCard, table);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        return scroll;
    }

    private void refreshData() {
        User u = AuthService.getInstance().getCurrentUser();
        List<Complaint> list = ComplaintService.getInstance().getComplaintsForUser(u);
        masterData = FXCollections.observableArrayList(list);
        filteredData = new FilteredList<>(masterData, p -> true);
        table.setItems(filteredData);
    }

    private void showQuickNoteDialog(Complaint c) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Follow-Up Note: " + c.getId());
        dialog.setHeaderText("Add additional information or clarification for " + c.getId() + ":");
        dialog.setContentText("Your Note:");

        dialog.showAndWait().ifPresent(note -> {
            if (!note.trim().isEmpty()) {
                ComplaintService.getInstance().addCitizenComment(c.getId(), note.trim());
                UIUtils.showInfo("Note Saved", "Remark Appended",
                        "Your note has been recorded and will be seen by the field officer.");
                refreshData();
            }
        });
    }
}
