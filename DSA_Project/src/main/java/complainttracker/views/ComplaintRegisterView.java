package complainttracker.views;

import complainttracker.algorithms.EditDistanceDuplicateDetector.DuplicateCheckResult;
import complainttracker.data.DatabaseHandler;
import complainttracker.models.Category;
import complainttracker.models.Complaint;
import complainttracker.models.Department;
import complainttracker.models.Priority;
import complainttracker.models.User;
import complainttracker.services.AuthService;
import complainttracker.services.ComplaintService;
import complainttracker.services.DuplicateDetectionService;
import complainttracker.utils.UIUtils;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Optional;
import java.util.function.Consumer;

public class ComplaintRegisterView {
    private final Consumer<Complaint> onComplaintCreated;
    private final Consumer<Complaint> onViewExisting;

    public ComplaintRegisterView(Consumer<Complaint> onComplaintCreated, Consumer<Complaint> onViewExisting) {
        this.onComplaintCreated = onComplaintCreated;
        this.onViewExisting = onViewExisting;
    }

    public Node getView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(24));
        root.setMaxWidth(800);

        Label lblTitle = new Label("Register New Citizen Grievance");
        lblTitle.getStyleClass().add("page-title");
        Label lblSub = new Label("Intelligent intake with real-time Wagner-Fischer duplicate detection.");
        lblSub.getStyleClass().add("page-subtitle");
        VBox headerBox = new VBox(2, lblTitle, lblSub);

        VBox formCard = new VBox(16);
        formCard.getStyleClass().add("card");

        // Generated ID
        String nextId = DatabaseHandler.getInstance().generateNextComplaintId();
        Label lblIdTag = new Label("Assigned Tracking ID: " + nextId);
        lblIdTag.setStyle("-fx-font-weight: bold; -fx-text-fill: #2563EB; -fx-font-size: 14px;");

        // Customer details
        User currentUser = AuthService.getInstance().getCurrentUser();
        TextField txtName = new TextField(currentUser != null ? currentUser.getFullName() : "");
        txtName.setPromptText("Enter complainant full name");

        TextField txtContact = new TextField(currentUser != null ? (currentUser.getPhone() != null ? currentUser.getPhone() : currentUser.getEmail()) : "");
        txtContact.setPromptText("Enter phone number or email");

        HBox userRow = new HBox(16,
                wrapField("Customer Full Name *", txtName),
                wrapField("Contact Phone / Email *", txtContact)
        );

        // Category & Priority
        ComboBox<Category> cmbCategory = new ComboBox<>(FXCollections.observableArrayList(Category.values()));
        cmbCategory.setValue(Category.WATER);
        cmbCategory.setMaxWidth(Double.MAX_VALUE);

        ComboBox<Priority> cmbPriority = new ComboBox<>(FXCollections.observableArrayList(Priority.values()));
        cmbPriority.setValue(Priority.MEDIUM);
        cmbPriority.setMaxWidth(Double.MAX_VALUE);

        HBox catRow = new HBox(16,
                wrapField("Grievance Category *", cmbCategory),
                wrapField("Initial Priority Level *", cmbPriority)
        );

        // Location
        TextField txtLocation = new TextField();
        txtLocation.setPromptText("e.g. 5th Main Road, Koramangala Ward 142");
        VBox locBox = wrapField("Incident Location / Landmark *", txtLocation);

        // Title
        TextField txtTitle = new TextField();
        txtTitle.setPromptText("Brief summary (e.g. Main water pipeline burst and road flooded)");
        VBox titleBox = wrapField("Complaint Title (Subject) *", txtTitle);

        // Description
        TextArea txtDesc = new TextArea();
        txtDesc.setPromptText("Provide comprehensive details about the issue, affected community members, and immediate risks...");
        txtDesc.setPrefRowCount(4);
        txtDesc.setWrapText(true);
        VBox descBox = wrapField("Detailed Description *", txtDesc);

        // Duplicate Detection Status Label
        Label lblDuplicateStatus = new Label();
        lblDuplicateStatus.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        lblDuplicateStatus.setVisible(false);

        // Action Buttons
        Button btnCheckDup = new Button("🔍 Run Real-Time Duplicate Check (Wagner-Fischer DP)");
        btnCheckDup.getStyleClass().add("btn-secondary");

        Button btnSubmit = new Button("✓ Submit & Register Grievance");
        btnSubmit.getStyleClass().add("btn-primary");
        btnSubmit.setStyle("-fx-font-size: 14px; -fx-padding: 10px 24px;");

        btnCheckDup.setOnAction(e -> {
            String title = txtTitle.getText().trim();
            String desc = txtDesc.getText().trim();
            if (title.isEmpty()) {
                lblDuplicateStatus.setText("⚠ Please enter a complaint title before checking duplicates.");
                lblDuplicateStatus.setStyle("-fx-text-fill: #F59E0B;");
                lblDuplicateStatus.setVisible(true);
                return;
            }

            Complaint probe = new Complaint();
            probe.setTitle(title);
            probe.setDescription(desc);

            DuplicateCheckResult res = DuplicateDetectionService.getInstance().checkComplaint(probe);
            if (res.isDuplicate) {
                lblDuplicateStatus.setText(String.format("⚠ DUPLICATE DETECTED: %.1f%% similarity with [%s] \"%s\"",
                        res.similarityScore, res.matchedComplaint.getId(), res.matchedComplaint.getTitle()));
                lblDuplicateStatus.setStyle("-fx-text-fill: #DC2626;");
                lblDuplicateStatus.setVisible(true);
                showDuplicateDialog(res, () -> {}, () -> onViewExisting.accept(res.matchedComplaint));
            } else {
                lblDuplicateStatus.setText("✓ No duplicates found. Complaint appears unique!");
                lblDuplicateStatus.setStyle("-fx-text-fill: #10B981;");
                lblDuplicateStatus.setVisible(true);
            }
        });

        btnSubmit.setOnAction(e -> {
            String name = txtName.getText().trim();
            String contact = txtContact.getText().trim();
            String title = txtTitle.getText().trim();
            String desc = txtDesc.getText().trim();
            String loc = txtLocation.getText().trim();
            Category cat = cmbCategory.getValue();
            Priority prio = cmbPriority.getValue();

            if (name.isEmpty() || contact.isEmpty() || title.isEmpty() || desc.isEmpty() || loc.isEmpty()) {
                UIUtils.showError("Validation Error", "Missing Required Fields", "Please complete all mandatory fields (*).");
                return;
            }

            // Determine matching department
            Department dept = DatabaseHandler.getInstance().findDepartmentByCategory(cat);
            String deptId = dept != null ? dept.getId() : "DEP-GEN";
            String deptName = dept != null ? dept.getName() : "Public Grievance General Cell";

            Complaint newComp = new Complaint(
                    nextId, name, contact,
                    currentUser != null ? currentUser.getId() : "GUEST",
                    title, desc, cat, prio, deptId, deptName, loc
            );

            // Execute duplicate detection before final registration
            DuplicateCheckResult dupResult = DuplicateDetectionService.getInstance().checkComplaint(newComp);
            if (dupResult.isDuplicate) {
                showDuplicateDialog(dupResult,
                        () -> doSaveComplaint(newComp),
                        () -> onViewExisting.accept(dupResult.matchedComplaint)
                );
            } else {
                doSaveComplaint(newComp);
            }
        });

        HBox btnBox = new HBox(12, btnCheckDup, btnSubmit);
        btnBox.setAlignment(Pos.CENTER_RIGHT);

        formCard.getChildren().addAll(
                lblIdTag, userRow, catRow, locBox, titleBox, descBox,
                lblDuplicateStatus, btnBox
        );

        root.getChildren().addAll(headerBox, formCard);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        return scroll;
    }

    private void doSaveComplaint(Complaint comp) {
        ComplaintService.getInstance().registerComplaint(comp);
        UIUtils.showInfo("Registration Successful", "Grievance Registered Successfully!",
                String.format("Complaint ID [%s] has been logged and assigned to %s.\nSLA Target Resolution: %d hours.",
                        comp.getId(), comp.getDepartmentName(), comp.getSlaHours()));
        onComplaintCreated.accept(comp);
    }

    private void showDuplicateDialog(DuplicateCheckResult dupResult, Runnable onContinue, Runnable onViewExistingAction) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Duplicate Complaint Warning");
        alert.setHeaderText("Potential Duplicate Grievance Detected!");
        alert.setContentText(String.format(
                "Our Dynamic Programming (Wagner-Fischer) algorithm detected a %.1f%% similarity match:\n\n" +
                        "Existing Complaint ID: %s\n" +
                        "Existing Title: %s\n" +
                        "Existing Status: %s\n" +
                        "Existing Department: %s\n\n" +
                        "Would you like to inspect the existing complaint or continue anyway?",
                dupResult.similarityScore,
                dupResult.matchedComplaint.getId(),
                dupResult.matchedComplaint.getTitle(),
                dupResult.matchedComplaint.getStatus(),
                dupResult.matchedComplaint.getDepartmentName()
        ));

        ButtonType btnView = new ButtonType("View Existing Complaint");
        ButtonType btnContinue = new ButtonType("Continue Anyway");
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(btnView, btnContinue, btnCancel);

        Optional<ButtonType> chosen = alert.showAndWait();
        if (chosen.isPresent()) {
            if (chosen.get() == btnView) {
                onViewExistingAction.run();
            } else if (chosen.get() == btnContinue) {
                onContinue.run();
            }
        }
    }

    private VBox wrapField(String labelText, Control input) {
        Label lbl = new Label(labelText);
        lbl.getStyleClass().add("field-label");
        input.setMaxWidth(Double.MAX_VALUE);
        VBox box = new VBox(4, lbl, input);
        HBox.setHgrow(box, javafx.scene.layout.Priority.ALWAYS);
        return box;
    }
}
