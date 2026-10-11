package complainttracker.views;

import complainttracker.data.DatabaseHandler;
import complainttracker.models.Complaint;
import complainttracker.models.Role;
import complainttracker.models.User;
import complainttracker.services.ComplaintService;
import complainttracker.utils.UIUtils;
import javafx.beans.property.SimpleIntegerProperty;
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

public class AdminUserManagementView {
    private TableView<User> table;
    private ObservableList<User> masterUsers;
    private FilteredList<User> filteredUsers;

    public Node getView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(24));

        Label lblTitle = new Label("Institutional User & Citizen Account Management");
        lblTitle.getStyleClass().add("page-title");
        Label lblSub = new Label("System directory of administrators, operational staff, and registered citizen users.");
        lblSub.getStyleClass().add("page-subtitle");
        VBox headerBox = new VBox(2, lblTitle, lblSub);

        // Filter / Controls Card
        VBox filterCard = new VBox(12);
        filterCard.getStyleClass().add("card");

        TextField txtSearch = new TextField();
        txtSearch.setPromptText("🔍 Search by name, username, or email...");
        txtSearch.setPrefWidth(300);

        ComboBox<String> cmbRole = new ComboBox<>();
        cmbRole.getItems().addAll("All Roles", "Administrator", "Customer");
        cmbRole.setValue("All Roles");

        Button btnNewUser = new Button("➕ Register New User");
        btnNewUser.getStyleClass().add("btn-primary");

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        HBox filterRow = new HBox(12, txtSearch, cmbRole, spacer, btnNewUser);
        filterRow.setAlignment(Pos.CENTER_LEFT);
        filterCard.getChildren().add(filterRow);

        // Table
        table = new TableView<>();
        table.setPrefHeight(480);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, javafx.scene.layout.Priority.ALWAYS);

        TableColumn<User, String> colId = new TableColumn<>("User ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setMinWidth(85);
        colId.setMaxWidth(100);
        colId.setStyle("-fx-font-weight: bold; -fx-text-fill: #2563EB;");

        TableColumn<User, String> colUsername = new TableColumn<>("Username");
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colUsername.setMinWidth(120);

        TableColumn<User, String> colName = new TableColumn<>("Full Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colName.setMinWidth(180);

        TableColumn<User, String> colEmail = new TableColumn<>("Email Address");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmail.setMinWidth(190);

        TableColumn<User, String> colPhone = new TableColumn<>("Contact Phone");
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colPhone.setMinWidth(130);

        TableColumn<User, Void> colRole = new TableColumn<>("Role");
        colRole.setMinWidth(130);
        colRole.setMaxWidth(150);
        colRole.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    User u = getTableRow().getItem();
                    Label badge = new Label(u.getRole().getTitle().toUpperCase());
                    badge.getStyleClass().add("badge");
                    if (u.isAdmin()) {
                        badge.setStyle("-fx-background-color: #1E293B; -fx-text-fill: #38BDF8;");
                    } else {
                        badge.setStyle("-fx-background-color: #D1FAE5; -fx-text-fill: #065F46;");
                    }
                    setAlignment(Pos.CENTER);
                    setGraphic(badge);
                }
            }
        });

        TableColumn<User, Number> colComplaints = new TableColumn<>("Complaints Filed");
        colComplaints.setCellValueFactory(cell -> {
            User u = cell.getValue();
            int count = ComplaintService.getInstance().getComplaintsForUser(u).size();
            return new SimpleIntegerProperty(count);
        });
        colComplaints.setMinWidth(120);

        TableColumn<User, Void> colAction = new TableColumn<>("Action");
        colAction.setMinWidth(110);
        colAction.setMaxWidth(130);
        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button btnToggle = new Button("Toggle Role");
            {
                btnToggle.getStyleClass().add("btn-secondary");
                btnToggle.setStyle("-fx-font-size: 11px; -fx-padding: 3px 8px;");
                btnToggle.setOnAction(e -> {
                    User u = getTableRow().getItem();
                    if (u != null) {
                        if ("admin".equalsIgnoreCase(u.getUsername())) {
                            UIUtils.showError("Restricted", "Root Admin Protected", "The primary root administrator role cannot be demoted.");
                            return;
                        }
                        Role newRole = u.isAdmin() ? Role.CUSTOMER : Role.ADMINISTRATOR;
                        u.setRole(newRole);
                        DatabaseHandler.getInstance().saveToFile();
                        refreshUsers();
                        UIUtils.showInfo("Role Updated", "User Role Modified",
                                "User " + u.getUsername() + " is now assigned role: " + newRole.getTitle());
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    setAlignment(Pos.CENTER);
                    setGraphic(btnToggle);
                }
            }
        });

        table.getColumns().addAll(colId, colUsername, colName, colEmail, colPhone, colRole, colComplaints, colAction);

        refreshUsers();

        // Search & Filter
        Runnable applyFilter = () -> {
            String q = txtSearch.getText().toLowerCase().trim();
            String r = cmbRole.getValue();

            filteredUsers.setPredicate(u -> {
                if (!q.isEmpty()) {
                    boolean m = u.getUsername().toLowerCase().contains(q)
                            || u.getFullName().toLowerCase().contains(q)
                            || u.getEmail().toLowerCase().contains(q)
                            || u.getId().toLowerCase().contains(q);
                    if (!m) return false;
                }
                if (r != null && !r.equals("All Roles") && !u.getRole().getTitle().equalsIgnoreCase(r)) {
                    return false;
                }
                return true;
            });
        };

        txtSearch.textProperty().addListener((obs, old, val) -> applyFilter.run());
        cmbRole.setOnAction(e -> applyFilter.run());

        btnNewUser.setOnAction(e -> showCreateUserDialog());

        root.getChildren().addAll(headerBox, filterCard, table);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        return scroll;
    }

    private void refreshUsers() {
        masterUsers = FXCollections.observableArrayList(DatabaseHandler.getInstance().getUsers());
        filteredUsers = new FilteredList<>(masterUsers, p -> true);
        table.setItems(filteredUsers);
    }

    private void showCreateUserDialog() {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Register New System User");
        dialog.setHeaderText("Create a new Citizen or Administrator account:");

        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(10);
        grid.setPadding(new Insets(16));

        TextField txtUname = new TextField();
        TextField txtPass = new TextField("pass123");
        TextField txtName = new TextField();
        TextField txtEmail = new TextField();
        TextField txtPhone = new TextField();
        ComboBox<Role> cmbRole = new ComboBox<>(FXCollections.observableArrayList(Role.values()));
        cmbRole.setValue(Role.CUSTOMER);

        grid.addRow(0, new Label("Username:"), txtUname);
        grid.addRow(1, new Label("Password:"), txtPass);
        grid.addRow(2, new Label("Full Name:"), txtName);
        grid.addRow(3, new Label("Email Address:"), txtEmail);
        grid.addRow(4, new Label("Phone Number:"), txtPhone);
        grid.addRow(5, new Label("Assign Role:"), cmbRole);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                String un = txtUname.getText().trim();
                String pw = txtPass.getText().trim();
                String fn = txtName.getText().trim();
                if (un.isEmpty() || pw.isEmpty() || fn.isEmpty()) {
                    UIUtils.showError("Validation Error", "Missing Required Fields", "Username, password, and name are required.");
                    return null;
                }
                String id = "USR-" + String.format("%02d", DatabaseHandler.getInstance().getUsers().size() + 1);
                return new User(id, un, pw, fn, txtEmail.getText().trim(), txtPhone.getText().trim(), cmbRole.getValue());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(u -> {
            DatabaseHandler.getInstance().getUsers().add(u);
            DatabaseHandler.getInstance().saveToFile();
            refreshUsers();
            UIUtils.showInfo("User Created", "Account Registered", "User " + u.getUsername() + " successfully created.");
        });
    }
}
