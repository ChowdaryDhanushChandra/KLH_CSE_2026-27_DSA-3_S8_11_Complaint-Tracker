package complainttracker.views;

import complainttracker.data.DatabaseHandler;
import complainttracker.models.User;
import complainttracker.services.AuthService;
import complainttracker.utils.UIUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class AdminProfileView {

    public Node getView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(24));
        root.setFillWidth(true);
        root.setStyle("-fx-background-color: #F8FAFC;");

        User admin = AuthService.getInstance().getCurrentUser();
        String adminName = admin != null ? admin.getFullName() : "Dr. Rajesh Sharma";
        String adminEmail = admin != null ? admin.getEmail() : "admin.ops@smartgrievance.gov";
        String adminPhone = admin != null ? admin.getPhone() : "+91 98450 11223";

        // 1. Header Banner
        HBox banner = new HBox(20);
        banner.setAlignment(Pos.CENTER_LEFT);
        banner.setPadding(new Insets(24));
        banner.setStyle("-fx-background-color: linear-gradient(to right, #0F172A, #1E293B); -fx-background-radius: 12px;");

        Label lblAvatar = new Label("🛡️");
        lblAvatar.setStyle("-fx-font-size: 48px; -fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 50%; -fx-padding: 12px;");

        VBox bannerInfo = new VBox(6);
        Label lblName = new Label(adminName);
        lblName.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF;");

        Label lblRole = new Label("Chief Operations Commissioner & System Administrator");
        lblRole.setStyle("-fx-font-size: 13px; -fx-text-fill: #38BDF8; -fx-font-weight: bold;");

        Label lblDept = new Label("Municipal Grievance Redressal Authority • System Node: CENTRAL-01");
        lblDept.setStyle("-fx-font-size: 12px; -fx-text-fill: #94A3B8;");

        bannerInfo.getChildren().addAll(lblName, lblRole, lblDept);

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        VBox badgeBox = new VBox(6);
        badgeBox.setAlignment(Pos.CENTER_RIGHT);
        Label lblStatus = new Label("● SUPERUSER PRIVILEGES");
        lblStatus.setStyle("-fx-background-color: #065F46; -fx-text-fill: #6EE7B7; -fx-padding: 6px 12px; -fx-background-radius: 20px; -fx-font-weight: bold; -fx-font-size: 11px;");
        Label lblClearance = new Label("Security Level: CLEARANCE TIER-1");
        lblClearance.setStyle("-fx-text-fill: #CBD5E1; -fx-font-size: 11px;");
        badgeBox.getChildren().addAll(lblStatus, lblClearance);

        banner.getChildren().addAll(lblAvatar, bannerInfo, spacer, badgeBox);

        // 2. Metrics Strip
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(16);
        statsGrid.setVgap(16);

        ColumnConstraints c1 = new ColumnConstraints(); c1.setPercentWidth(25);
        ColumnConstraints c2 = new ColumnConstraints(); c2.setPercentWidth(25);
        ColumnConstraints c3 = new ColumnConstraints(); c3.setPercentWidth(25);
        ColumnConstraints c4 = new ColumnConstraints(); c4.setPercentWidth(25);
        statsGrid.getColumnConstraints().addAll(c1, c2, c3, c4);

        int totalComplaints = DatabaseHandler.getInstance().getComplaints().size();
        int totalUsers = DatabaseHandler.getInstance().getUsers().size();
        int totalDepts = DatabaseHandler.getInstance().getDepartments().size();

        statsGrid.add(createMiniCard("MANAGED TICKETS", String.valueOf(totalComplaints), "#2563EB"), 0, 0);
        statsGrid.add(createMiniCard("REGISTERED USERS", String.valueOf(totalUsers), "#8B5CF6"), 1, 0);
        statsGrid.add(createMiniCard("MUNICIPAL DEPTS", String.valueOf(totalDepts), "#059669"), 2, 0);
        statsGrid.add(createMiniCard("ENGINE ENGINE", "DSA-3 v3.2.0", "#D97706"), 3, 0);

        // 3. Two Column Split: Profile Information & Administrative Authorities
        HBox bodyRow = new HBox(20);

        // Left Card: Operational Identity
        VBox profileCard = new VBox(16);
        profileCard.setPadding(new Insets(20));
        profileCard.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 12px; -fx-border-color: #E2E8F0; -fx-border-radius: 12px;");
        HBox.setHgrow(profileCard, javafx.scene.layout.Priority.ALWAYS);

        Label lblSec1 = new Label("👤 Administrator Identity Details");
        lblSec1.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        GridPane formGrid = new GridPane();
        formGrid.setHgap(16);
        formGrid.setVgap(12);

        formGrid.add(createFieldLabel("Full Name:"), 0, 0);
        TextField txtName = new TextField(adminName);
        txtName.setPrefWidth(260);
        formGrid.add(txtName, 1, 0);

        formGrid.add(createFieldLabel("Official Email:"), 0, 1);
        TextField txtEmail = new TextField(adminEmail);
        formGrid.add(txtEmail, 1, 1);

        formGrid.add(createFieldLabel("Contact Phone:"), 0, 2);
        TextField txtPhone = new TextField(adminPhone);
        formGrid.add(txtPhone, 1, 2);

        formGrid.add(createFieldLabel("Jurisdiction:"), 0, 3);
        Label lblJuris = new Label("Metropolitan District (Zones 1-8)");
        lblJuris.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155;");
        formGrid.add(lblJuris, 1, 3);

        formGrid.add(createFieldLabel("Auth Role:"), 0, 4);
        Label lblAuthRole = new Label("ADMINISTRATOR (Full Access)");
        lblAuthRole.setStyle("-fx-text-fill: #2563EB; -fx-font-weight: bold;");
        formGrid.add(lblAuthRole, 1, 4);

        Button btnSave = new Button("💾 Update Contact Details");
        btnSave.getStyleClass().add("btn-primary");
        btnSave.setOnAction(e -> {
            if (admin != null) {
                admin.setFullName(txtName.getText().trim());
                admin.setEmail(txtEmail.getText().trim());
                admin.setPhone(txtPhone.getText().trim());
                DatabaseHandler.getInstance().saveUsers();
            }
            UIUtils.showAlert(Alert.AlertType.INFORMATION, "Profile Updated", "Administrator contact credentials updated successfully.");
        });

        profileCard.getChildren().addAll(lblSec1, formGrid, btnSave);

        // Right Card: Administrative Authority Checklist
        VBox rightsCard = new VBox(14);
        rightsCard.setPadding(new Insets(20));
        rightsCard.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 12px; -fx-border-color: #E2E8F0; -fx-border-radius: 12px;");
        HBox.setHgrow(rightsCard, javafx.scene.layout.Priority.ALWAYS);

        Label lblSec2 = new Label("🔐 System Delegations & Privileges");
        lblSec2.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        rightsCard.getChildren().addAll(
                lblSec2,
                createRightItem("✅ Grievance Lifecycle Authority", "Assign departments, alter priority, resolve, and close tickets."),
                createRightItem("✅ Network Flow & DSA Execution", "Run Dinic / Edmonds-Karp capacity assignment and pattern matchers."),
                createRightItem("✅ SLA Policy Overrides", "Override deadline timers and mandate executive escalations."),
                createRightItem("✅ User RBAC Management", "Provision officers, adjust role authorizations, and audit login trails."),
                createRightItem("✅ Master Audit Trail Access", "View immutable timestamped transaction and operation histories.")
        );

        bodyRow.getChildren().addAll(profileCard, rightsCard);

        root.getChildren().addAll(banner, statsGrid, bodyRow);

        ScrollPane sp = new ScrollPane(root);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        return sp;
    }

    private Label createFieldLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #64748B;");
        return lbl;
    }

    private VBox createMiniCard(String title, String val, String color) {
        VBox card = new VBox(4);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 10px; -fx-border-color: #E2E8F0; -fx-border-radius: 10px;");

        Label lblT = new Label(title);
        lblT.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #64748B;");

        Label lblV = new Label(val);
        lblV.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        card.getChildren().addAll(lblT, lblV);
        return card;
    }

    private VBox createRightItem(String title, String desc) {
        VBox box = new VBox(2);
        box.setPadding(new Insets(8, 12, 8, 12));
        box.setStyle("-fx-background-color: #F1F5F9; -fx-background-radius: 8px;");

        Label lblT = new Label(title);
        lblT.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        Label lblD = new Label(desc);
        lblD.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");

        box.getChildren().addAll(lblT, lblD);
        return box;
    }
}
