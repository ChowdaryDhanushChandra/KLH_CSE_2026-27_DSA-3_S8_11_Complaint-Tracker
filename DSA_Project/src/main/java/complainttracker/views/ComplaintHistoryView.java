package complainttracker.views;

import complainttracker.data.DatabaseHandler;
import complainttracker.models.Complaint;
import complainttracker.models.ComplaintHistory;
import complainttracker.services.AuthService;
import complainttracker.services.ComplaintService;
import complainttracker.utils.UIUtils;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class ComplaintHistoryView {
    private ComboBox<Complaint> cmbComplaint;
    private VBox timelineContainer;
    private VBox summaryCard;
    private Complaint preselectedComplaint;

    public ComplaintHistoryView(Complaint preselectedComplaint) {
        this.preselectedComplaint = preselectedComplaint;
    }

    public Node getView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(24));

        Label lblTitle = new Label("Complaint History & Lifecycle Audit Trail");
        lblTitle.getStyleClass().add("page-title");
        Label lblSub = new Label("Chronological immutable audit log tracking every transition, assignment, and status change.");
        lblSub.getStyleClass().add("page-subtitle");
        VBox headerBox = new VBox(2, lblTitle, lblSub);

        // Selector Card
        HBox selectorCard = new HBox(14);
        selectorCard.getStyleClass().add("card");
        selectorCard.setAlignment(Pos.CENTER_LEFT);

        Label lblSelect = new Label("Select Complaint to Audit:");
        lblSelect.setStyle("-fx-font-weight: bold;");

        complainttracker.models.User curUser = AuthService.getInstance().getCurrentUser();
        List<Complaint> allComplaints = (curUser != null && !curUser.isAdmin())
                ? ComplaintService.getInstance().getComplaintsForUser(curUser)
                : ComplaintService.getInstance().getAllComplaints();
        cmbComplaint = new ComboBox<>(FXCollections.observableArrayList(allComplaints));
        cmbComplaint.setPrefWidth(420);

        Button btnRefresh = new Button("Refresh History");
        btnRefresh.getStyleClass().add("btn-secondary");

        selectorCard.getChildren().addAll(lblSelect, cmbComplaint, btnRefresh);

        // Summary Card
        summaryCard = new VBox(8);
        summaryCard.getStyleClass().add("card");

        // Timeline Container
        Label lblTimelineTitle = new Label("Chronological Audit Timeline");
        lblTimelineTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

        timelineContainer = new VBox(12);

        cmbComplaint.setOnAction(e -> {
            Complaint selected = cmbComplaint.getValue();
            if (selected != null) {
                renderTimeline(selected);
            }
        });

        btnRefresh.setOnAction(e -> {
            Complaint selected = cmbComplaint.getValue();
            if (selected != null) {
                renderTimeline(selected);
            }
        });

        // Set initial selection
        if (preselectedComplaint != null) {
            for (Complaint c : allComplaints) {
                if (c.getId().equals(preselectedComplaint.getId())) {
                    cmbComplaint.setValue(c);
                    break;
                }
            }
        } else if (!allComplaints.isEmpty()) {
            cmbComplaint.setValue(allComplaints.get(0));
        }

        if (cmbComplaint.getValue() != null) {
            renderTimeline(cmbComplaint.getValue());
        }

        root.getChildren().addAll(headerBox, selectorCard, summaryCard, lblTimelineTitle, timelineContainer);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        return scroll;
    }

    private void renderTimeline(Complaint c) {
        summaryCard.getChildren().clear();
        timelineContainer.getChildren().clear();

        // 1. Render Summary
        Label lblHeader = new Label(String.format("[%s] %s", c.getId(), c.getTitle()));
        lblHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        HBox metaRow = new HBox(12,
                new Label("Customer: " + c.getCustomerName()),
                new Label("•"),
                new Label("Category: " + c.getCategory().getDisplayName()),
                new Label("•"),
                new Label("Dept: " + c.getDepartmentName()),
                new Label("•"),
                UIUtils.createPriorityBadge(c.getPriority()),
                UIUtils.createStatusBadge(c.getStatus())
        );
        metaRow.setAlignment(Pos.CENTER_LEFT);

        Label lblDesc = new Label("Details: " + c.getDescription());
        lblDesc.setWrapText(true);
        lblDesc.setStyle("-fx-text-fill: #475569; -fx-font-size: 12px;");

        summaryCard.getChildren().addAll(lblHeader, metaRow, lblDesc);

        // 2. Render Audit Events
        List<ComplaintHistory> history = DatabaseHandler.getInstance().getHistoryForComplaint(c.getId());

        if (history.isEmpty()) {
            Label emptyLbl = new Label("No historical events logged yet for this grievance.");
            emptyLbl.setStyle("-fx-font-style: italic; -fx-text-fill: #94A3B8; -fx-padding: 12px;");
            timelineContainer.getChildren().add(emptyLbl);
            return;
        }

        for (int i = 0; i < history.size(); i++) {
            ComplaintHistory h = history.get(i);

            HBox eventCard = new HBox(14);
            eventCard.getStyleClass().add("card");
            eventCard.setAlignment(Pos.CENTER_LEFT);
            eventCard.setPadding(new Insets(12, 16, 12, 16));

            // Timestamp indicator
            VBox timeBox = new VBox(2);
            timeBox.setMinWidth(140);
            Label lblTime = new Label(h.getFormattedTimestamp());
            lblTime.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #2563EB; -fx-font-family: monospace;");
            Label lblActor = new Label("By: " + h.getPerformedBy());
            lblActor.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");
            timeBox.getChildren().addAll(lblTime, lblActor);

            // Action tag
            Label actionBadge = new Label(h.getAction());
            actionBadge.getStyleClass().add("badge");
            actionBadge.setStyle("-fx-background-color: #EFF6FF; -fx-text-fill: #1D4ED8; -fx-border-color: #BFDBFE;");

            // Notes
            Label lblNotes = new Label(h.getNotes() != null ? h.getNotes() : "");
            lblNotes.setWrapText(true);
            HBox.setHgrow(lblNotes, javafx.scene.layout.Priority.ALWAYS);
            lblNotes.setStyle("-fx-text-fill: #334155;");

            eventCard.getChildren().addAll(timeBox, actionBadge, lblNotes);
            timelineContainer.getChildren().add(eventCard);
        }
    }
}
