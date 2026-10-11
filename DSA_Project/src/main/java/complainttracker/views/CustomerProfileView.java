package complainttracker.views;

import complainttracker.models.Complaint;
import complainttracker.models.Status;
import complainttracker.models.User;
import complainttracker.services.AuthService;
import complainttracker.services.ComplaintService;
import complainttracker.utils.UIUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class CustomerProfileView {

    public Node getView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(24));

        Label lblTitle = new Label("Citizen Profile & Account Settings");
        lblTitle.getStyleClass().add("page-title");
        Label lblSub = new Label("Manage your personal contact details, review complaint activity, and citizen charter rights.");
        lblSub.getStyleClass().add("page-subtitle");
        VBox headerBox = new VBox(2, lblTitle, lblSub);

        User currentUser = AuthService.getInstance().getCurrentUser();
        List<Complaint> myComplaints = ComplaintService.getInstance().getComplaintsForUser(currentUser);

        int total = myComplaints.size();
        int resolved = 0;
        int active = 0;
        double sumRating = 0;
        int ratedCount = 0;

        for (Complaint c : myComplaints) {
            if (c.getStatus() == Status.RESOLVED) resolved++;
            else if (c.getStatus() != Status.REJECTED) active++;

            if (c.getRating() > 0) {
                sumRating += c.getRating();
                ratedCount++;
            }
        }
        double avgRating = ratedCount > 0 ? (sumRating / ratedCount) : 5.0;

        // Profile Details Card
        VBox profileCard = new VBox(16);
        profileCard.getStyleClass().add("card");

        HBox userHeader = new HBox(16);
        userHeader.setAlignment(Pos.CENTER_LEFT);

        Label avatar = new Label("👤");
        avatar.setStyle("-fx-font-size: 36px; -fx-background-color: #EFF6FF; -fx-padding: 12px; -fx-background-radius: 40px;");

        VBox userTitleBox = new VBox(4);
        Label lblName = new Label(currentUser != null ? currentUser.getFullName() : "Priya Venkatesh");
        lblName.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label lblRoleTag = new Label("ROLE: CITIZEN / CUSTOMER");
        lblRoleTag.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #10B981; -fx-background-color: #D1FAE5; -fx-padding: 2px 8px; -fx-background-radius: 10px;");
        userTitleBox.getChildren().addAll(lblName, lblRoleTag);

        userHeader.getChildren().addAll(avatar, userTitleBox);

        GridPane fieldsGrid = new GridPane();
        fieldsGrid.setHgap(20);
        fieldsGrid.setVgap(12);

        TextField txtName = new TextField(currentUser != null ? currentUser.getFullName() : "");
        TextField txtEmail = new TextField(currentUser != null ? currentUser.getEmail() : "");
        TextField txtPhone = new TextField(currentUser != null ? currentUser.getPhone() : "");
        TextField txtId = new TextField(currentUser != null ? currentUser.getId() : "USR-02");
        txtId.setEditable(false);
        txtId.setStyle("-fx-background-color: #F1F5F9;");

        fieldsGrid.addRow(0, new Label("Account ID:"), txtId);
        fieldsGrid.addRow(1, new Label("Full Name:"), txtName);
        fieldsGrid.addRow(2, new Label("Email Address:"), txtEmail);
        fieldsGrid.addRow(3, new Label("Contact Phone:"), txtPhone);

        Button btnSave = new Button("💾 Save Profile Updates");
        btnSave.getStyleClass().add("btn-primary");
        btnSave.setOnAction(e -> {
            if (currentUser != null) {
                currentUser.setFullName(txtName.getText().trim());
                currentUser.setEmail(txtEmail.getText().trim());
                currentUser.setPhone(txtPhone.getText().trim());
                UIUtils.showInfo("Profile Updated", "Changes Saved", "Your contact information was successfully updated.");
            }
        });

        profileCard.getChildren().addAll(userHeader, new Separator(), fieldsGrid, btnSave);

        // Activity Summary Card
        HBox statsRow = new HBox(14);
        statsRow.getChildren().addAll(
                UIUtils.createStatCard("Total Filed", String.valueOf(total), "Lifetime tickets", "#2563EB"),
                UIUtils.createStatCard("Active Cases", String.valueOf(active), "Under resolution", "#F59E0B"),
                UIUtils.createStatCard("Resolved Cases", String.valueOf(resolved), "Successfully closed", "#10B981"),
                UIUtils.createStatCard("Feedback CSAT", String.format("%.1f ★", avgRating), "Average score given", "#8B5CF6")
        );

        // Citizen Charter Card
        VBox charterCard = new VBox(10);
        charterCard.getStyleClass().add("card");
        charterCard.setStyle("-fx-background-color: #F8FAFC;");

        Label lblCharter = new Label("🏛️ Citizen Grievance Charter & Rights");
        lblCharter.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #1E293B;");

        Label lblCharterText = new Label("Under our digital governance framework, every citizen complaint is protected by guaranteed SLA timelines:\n" +
                "• Critical Emergencies (Gas leaks, live wire hazards, water contamination): Max 12 hours\n" +
                "• High Priority (Major potholes, transformer breakdown, drainage backflow): Max 24 hours\n" +
                "• Medium Priority (Streetlight cluster, garbage dump, water billing dispute): Max 48 hours\n" +
                "• Low Priority (General maintenance, signage, bus route frequency): Max 72 hours\n\n" +
                "If a grievance exceeds the designated SLA window, it is automatically escalated to the Department Chief.");
        lblCharterText.setStyle("-fx-font-size: 12px; -fx-text-fill: #475569;");
        lblCharterText.setWrapText(true);

        charterCard.getChildren().addAll(lblCharter, lblCharterText);

        root.getChildren().addAll(headerBox, profileCard, statsRow, charterCard);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        return scroll;
    }
}
