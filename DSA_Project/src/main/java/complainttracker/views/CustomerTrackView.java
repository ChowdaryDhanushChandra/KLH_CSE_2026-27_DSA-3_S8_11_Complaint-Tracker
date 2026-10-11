package complainttracker.views;

import complainttracker.data.DatabaseHandler;
import complainttracker.models.*;
import complainttracker.services.AuthService;
import complainttracker.services.ComplaintService;
import complainttracker.utils.UIUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDateTime;
import java.util.List;

public class CustomerTrackView {
    private final Complaint initialComplaint;
    private TextField txtSearchId;
    private VBox resultContainer;

    public CustomerTrackView(Complaint initialComplaint) {
        this.initialComplaint = initialComplaint;
    }

    public Node getView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(24));

        Label lblTitle = new Label("Live Grievance Tracking");
        lblTitle.getStyleClass().add("page-title");
        Label lblSub = new Label("Track real-time progress, department assignment, and field resolution timeline.");
        lblSub.getStyleClass().add("page-subtitle");
        VBox headerBox = new VBox(2, lblTitle, lblSub);

        // Search Input Card
        VBox searchCard = new VBox(12);
        searchCard.getStyleClass().add("card");

        Label lblPrompt = new Label("Enter Tracking ID:");
        lblPrompt.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155;");

        txtSearchId = new TextField();
        txtSearchId.setPromptText("e.g. CMP-1001, CMP-1004...");
        txtSearchId.setPrefWidth(260);

        Button btnTrack = new Button("🔍 Track Grievance");
        btnTrack.getStyleClass().add("btn-primary");

        HBox inputRow = new HBox(12, lblPrompt, txtSearchId, btnTrack);
        inputRow.setAlignment(Pos.CENTER_LEFT);

        // Quick Pick Chips for logged-in user
        User currentUser = AuthService.getInstance().getCurrentUser();
        List<Complaint> myTickets = ComplaintService.getInstance().getComplaintsForUser(currentUser);
        HBox chipsRow = new HBox(8);
        chipsRow.setAlignment(Pos.CENTER_LEFT);
        Label lblQuick = new Label("My Recent Tickets:");
        lblQuick.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");
        chipsRow.getChildren().add(lblQuick);

        for (int i = 0; i < Math.min(4, myTickets.size()); i++) {
            Complaint tc = myTickets.get(i);
            Button chip = new Button(tc.getId());
            chip.getStyleClass().add("btn-secondary");
            chip.setStyle("-fx-font-size: 11px; -fx-padding: 3px 8px;");
            chip.setOnAction(e -> {
                txtSearchId.setText(tc.getId());
                performTrack(tc.getId());
            });
            chipsRow.getChildren().add(chip);
        }

        searchCard.getChildren().addAll(inputRow, chipsRow);

        // Result Container
        resultContainer = new VBox(16);

        btnTrack.setOnAction(e -> performTrack(txtSearchId.getText().trim()));

        // Preload initial complaint if provided, else first user complaint
        if (initialComplaint != null) {
            txtSearchId.setText(initialComplaint.getId());
            performTrack(initialComplaint.getId());
        } else if (!myTickets.isEmpty()) {
            txtSearchId.setText(myTickets.get(0).getId());
            performTrack(myTickets.get(0).getId());
        }

        root.getChildren().addAll(headerBox, searchCard, resultContainer);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        return scroll;
    }

    private void performTrack(String complaintId) {
        resultContainer.getChildren().clear();

        if (complaintId == null || complaintId.isEmpty()) {
            Label lblEmpty = new Label("Please enter a valid Complaint ID.");
            lblEmpty.setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold;");
            resultContainer.getChildren().add(lblEmpty);
            return;
        }

        Complaint complaint = ComplaintService.getInstance().getComplaintById(complaintId);

        if (complaint == null) {
            VBox notFoundCard = new VBox(10);
            notFoundCard.getStyleClass().add("card");
            notFoundCard.setAlignment(Pos.CENTER);
            notFoundCard.setStyle("-fx-padding: 30px;");

            Label icon = new Label("⚠️");
            icon.setStyle("-fx-font-size: 32px;");

            Label lblMsg = new Label("Complaint ID '" + complaintId + "' Not Found");
            lblMsg.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

            Label lblSubMsg = new Label("Please verify the tracking number or view your active complaints under 'My Complaints'.");
            lblSubMsg.setStyle("-fx-text-fill: #64748B;");

            notFoundCard.getChildren().addAll(icon, lblMsg, lblSubMsg);
            resultContainer.getChildren().add(notFoundCard);
            return;
        }

        // Render Complaint Found Card
        VBox trackingCard = new VBox(16);
        trackingCard.getStyleClass().add("card");

        // Header
        HBox cardHeader = new HBox(12);
        cardHeader.setAlignment(Pos.CENTER_LEFT);

        Label lblVerified = new Label("Complaint Found ✓");
        lblVerified.setStyle("-fx-text-fill: #10B981; -fx-font-weight: bold; -fx-font-size: 13px;");

        Label lblId = new Label("[" + complaint.getId() + "] " + complaint.getTitle());
        lblId.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        HBox badges = new HBox(8,
                UIUtils.createPriorityBadge(complaint.getPriority()),
                UIUtils.createStatusBadge(complaint.getStatus())
        );

        cardHeader.getChildren().addAll(lblId, lblVerified, spacer, badges);

        // Key Info Grid
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(16);
        infoGrid.setVgap(8);
        infoGrid.setStyle("-fx-background-color: #F8FAFC; -fx-padding: 14px; -fx-background-radius: 8px;");

        infoGrid.addRow(0, boldLabel("Category:"), new Label(complaint.getCategory().getDisplayName()));
        infoGrid.addRow(1, boldLabel("Assigned Dept:"), new Label(complaint.getDepartmentName()));
        infoGrid.addRow(2, boldLabel("Field Officer:"), new Label(complaint.getAssignedStaff() != null ? complaint.getAssignedStaff() : "Under Assignment"));
        infoGrid.addRow(3, boldLabel("Filed On:"), new Label(complaint.getFormattedCreatedAt()));
        infoGrid.addRow(4, boldLabel("SLA Target Window:"), new Label(complaint.getSlaHours() + " hours (" + complaint.getSlaRemainingDisplay() + ")"));
        infoGrid.addRow(5, boldLabel("Incident Location:"), new Label(complaint.getLocation()));

        // Visual Progress Timeline
        VBox timelineBox = buildVisualTimeline(complaint);

        // Complainant Actions (Add Note, Rate if Resolved)
        HBox actionRow = new HBox(12);
        actionRow.setAlignment(Pos.CENTER_RIGHT);

        Button btnAddNote = new Button("💬 Add Additional Information / Follow-Up Note");
        btnAddNote.getStyleClass().add("btn-secondary");
        btnAddNote.setOnAction(e -> showAddNoteDialog(complaint));

        actionRow.getChildren().add(btnAddNote);

        if (complaint.getStatus() == Status.RESOLVED) {
            Button btnFeedback = new Button("⭐ " + (complaint.getRating() > 0 ? "Update Citizen Rating" : "Rate Service & Provide Feedback"));
            btnFeedback.getStyleClass().add("btn-success");
            btnFeedback.setOnAction(e -> showFeedbackDialog(complaint));
            actionRow.getChildren().add(btnFeedback);
        }

        // Recent Audit Updates
        VBox updatesBox = new VBox(8);
        Label lblHist = new Label("Recent Status Logs & Timeline Events:");
        lblHist.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #1E293B;");
        updatesBox.getChildren().add(lblHist);

        List<ComplaintHistory> logs = DatabaseHandler.getInstance().getHistoryForComplaint(complaint.getId());
        for (int i = 0; i < Math.min(5, logs.size()); i++) {
            ComplaintHistory h = logs.get(i);
            HBox logRow = new HBox(10);
            logRow.setStyle("-fx-padding: 6px 0; -fx-border-color: #F1F5F9; -fx-border-width: 0 0 1px 0;");
            Label lblT = new Label(h.getFormattedTimestamp());
            lblT.setStyle("-fx-font-size: 11px; -fx-font-family: monospace; -fx-text-fill: #64748B; -fx-min-width: 130px;");
            Label lblA = new Label(h.getAction());
            lblA.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #2563EB; -fx-min-width: 120px;");
            Label lblN = new Label(h.getNotes() != null ? h.getNotes() : "");
            lblN.setStyle("-fx-font-size: 12px; -fx-text-fill: #334155;");
            lblN.setWrapText(true);
            logRow.getChildren().addAll(lblT, lblA, lblN);
            updatesBox.getChildren().add(logRow);
        }

        trackingCard.getChildren().addAll(cardHeader, infoGrid, timelineBox, actionRow, new Separator(), updatesBox);
        resultContainer.getChildren().add(trackingCard);
    }

    private VBox buildVisualTimeline(Complaint c) {
        VBox box = new VBox(10);
        box.setStyle("-fx-padding: 12px 0;");

        Label lblTimelineTitle = new Label("Grievance Resolution Progress:");
        lblTimelineTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #1E293B;");

        int currentStep = 1;
        if (c.getStatus() == Status.NEW) currentStep = 1;
        else if (c.getStatus() == Status.PENDING) currentStep = 2;
        else if (c.getStatus() == Status.IN_PROGRESS && (c.getAssignedStaff() == null || c.getAssignedStaff().isEmpty())) currentStep = 3;
        else if (c.getStatus() == Status.IN_PROGRESS || c.getStatus() == Status.ESCALATED) currentStep = 4;
        else if (c.getStatus() == Status.RESOLVED && c.getRating() == 0) currentStep = 5;
        else if (c.getStatus() == Status.RESOLVED && c.getRating() > 0) currentStep = 6;

        String[] stepLabels = {
                "1. Submitted",
                "2. Reviewed",
                "3. Assigned Dept",
                "4. In Investigation",
                "5. Resolution Done",
                "6. Closed"
        };

        HBox timelineRow = new HBox(0);
        timelineRow.setAlignment(Pos.CENTER);

        for (int i = 0; i < stepLabels.length; i++) {
            int stepNum = i + 1;
            boolean isCompleted = stepNum < currentStep;
            boolean isCurrent = stepNum == currentStep;

            VBox nodeBox = new VBox(6);
            nodeBox.setAlignment(Pos.CENTER);
            nodeBox.setMinWidth(110);

            Label circle = new Label(isCompleted ? "✓" : String.valueOf(stepNum));
            circle.setAlignment(Pos.CENTER);
            circle.setPrefSize(28, 28);
            circle.setStyle("-fx-background-radius: 14px; -fx-font-weight: bold; -fx-font-size: 12px; " +
                    (isCompleted ? "-fx-background-color: #10B981; -fx-text-fill: white;" :
                            isCurrent ? "-fx-background-color: #2563EB; -fx-text-fill: white;" :
                                    "-fx-background-color: #E2E8F0; -fx-text-fill: #64748B;"));

            Label lblStep = new Label(stepLabels[i]);
            lblStep.setStyle("-fx-font-size: 11px; -fx-font-weight: " + (isCurrent ? "bold" : "normal") + "; " +
                    "-fx-text-fill: " + (isCurrent ? "#2563EB" : isCompleted ? "#10B981" : "#64748B") + ";");

            nodeBox.getChildren().addAll(circle, lblStep);
            timelineRow.getChildren().add(nodeBox);

            if (i < stepLabels.length - 1) {
                Region line = new Region();
                line.setPrefHeight(3);
                line.setPrefWidth(40);
                line.setStyle("-fx-background-color: " + (stepNum < currentStep ? "#10B981" : "#E2E8F0") + ";");
                VBox lineWrapper = new VBox(line);
                lineWrapper.setAlignment(Pos.CENTER);
                lineWrapper.setPadding(new Insets(0, 0, 16, 0));
                timelineRow.getChildren().add(lineWrapper);
            }
        }

        box.getChildren().addAll(lblTimelineTitle, timelineRow);
        return box;
    }

    private void showAddNoteDialog(Complaint c) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Add Follow-Up Note: " + c.getId());
        dialog.setHeaderText("Add additional information or remarks for this complaint:");

        VBox content = new VBox(10);
        content.setPadding(new Insets(16));
        content.setPrefWidth(420);

        TextArea txtNote = new TextArea();
        txtNote.setPromptText("Enter any new details, landmark directions, or urgency updates...");
        txtNote.setPrefRowCount(4);

        content.getChildren().add(txtNote);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> btn == ButtonType.OK ? txtNote.getText().trim() : null);

        dialog.showAndWait().ifPresent(note -> {
            if (!note.isEmpty()) {
                ComplaintService.getInstance().addCitizenComment(c.getId(), note);
                UIUtils.showInfo("Note Added", "Your remark was appended to the grievance file.",
                        "Field officers will review your note during the next inspection.");
                performTrack(c.getId());
            }
        });
    }

    private void showFeedbackDialog(Complaint c) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Rate Resolution: " + c.getId());
        dialog.setHeaderText("Citizen Satisfaction & Service Feedback");

        VBox content = new VBox(12);
        content.setPadding(new Insets(16));
        content.setPrefWidth(420);

        Label lblRating = new Label("Satisfaction Rating (1 to 5 Stars):");
        ComboBox<Integer> cmbRating = new ComboBox<>();
        cmbRating.getItems().addAll(5, 4, 3, 2, 1);
        cmbRating.setValue(c.getRating() > 0 ? c.getRating() : 5);

        Label lblFeedback = new Label("Comments / Feedback:");
        TextArea txtFb = new TextArea(c.getFeedback() != null ? c.getFeedback() : "");
        txtFb.setPromptText("Please share your feedback regarding timeliness and quality of work...");
        txtFb.setPrefRowCount(3);

        content.getChildren().addAll(lblRating, cmbRating, lblFeedback, txtFb);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                int r = cmbRating.getValue();
                String fb = txtFb.getText().trim();
                c.setRating(r);
                c.setFeedback(fb);
                ComplaintService.getInstance().updateComplaint(c, "Citizen Rated Service (" + r + "★)",
                        AuthService.getInstance().getCurrentUser().getFullName(), fb);
                return true;
            }
            return false;
        });

        dialog.showAndWait().ifPresent(ok -> {
            if (ok) {
                UIUtils.showInfo("Thank You", "Feedback Recorded", "Thank you for helping us improve public services!");
                performTrack(c.getId());
            }
        });
    }

    private Label boldLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #475569;");
        return lbl;
    }
}
