package complainttracker.views;

import complainttracker.algorithms.EditDistanceDuplicateDetector;
import complainttracker.algorithms.EditDistanceDuplicateDetector.DuplicateCheckResult;
import complainttracker.data.DatabaseHandler;
import complainttracker.models.*;
import complainttracker.services.AuthService;
import complainttracker.services.ComplaintService;
import complainttracker.utils.UIUtils;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class AdminComplaintDetailDialog {

    public static void show(Complaint complaint, Runnable onUpdated) {
        show(complaint, onUpdated, c -> {});
    }

    public static void show(Complaint complaint, Runnable onUpdated, java.util.function.Consumer<Complaint> onNavigateToHistory) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Operational Case File: " + complaint.getId());
        dialog.setHeaderText("Grievance Control & Algorithmic Surveillance: " + complaint.getId());

        VBox content = new VBox(16);
        content.setPrefWidth(680);
        content.setPadding(new Insets(16));

        // 1. Complaint Basic Info Grid
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(16);
        infoGrid.setVgap(8);
        infoGrid.setStyle("-fx-background-color: #F8FAFC; -fx-padding: 14px; -fx-border-color: #E2E8F0; -fx-border-radius: 8px;");

        infoGrid.addRow(0, boldLabel("Complainant:"), new Label(complaint.getCustomerName() + " (" + complaint.getCustomerContact() + ")"));
        infoGrid.addRow(1, boldLabel("Title:"), new Label(complaint.getTitle()));
        infoGrid.addRow(2, boldLabel("Category:"), new Label(complaint.getCategory().getDisplayName()));
        infoGrid.addRow(3, boldLabel("Priority:"), UIUtils.createPriorityBadge(complaint.getPriority()));
        infoGrid.addRow(4, boldLabel("Current Status:"), UIUtils.createStatusBadge(complaint.getStatus()));
        infoGrid.addRow(5, boldLabel("Department:"), new Label(complaint.getDepartmentName()));
        infoGrid.addRow(6, boldLabel("Assigned Staff:"), new Label(complaint.getAssignedStaff() != null ? complaint.getAssignedStaff() : "Unassigned"));
        infoGrid.addRow(7, boldLabel("Filed Date:"), new Label(complaint.getFormattedCreatedAt()));
        infoGrid.addRow(8, boldLabel("SLA Deadline:"), new Label(complaint.getFormattedDeadline() + " (" + complaint.getSlaRemainingDisplay() + ")"));
        infoGrid.addRow(9, boldLabel("Incident Location:"), new Label(complaint.getLocation()));

        // Description
        Label lblDescTitle = new Label("Full Description:");
        lblDescTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #1E293B;");
        Label lblDesc = new Label(complaint.getDescription());
        lblDesc.setWrapText(true);
        lblDesc.setStyle("-fx-text-fill: #334155; -fx-font-size: 12px;");

        // 2. Algorithmic Information Box (DSA Core Features Connected!)
        VBox algoBox = new VBox(8);
        algoBox.setStyle("-fx-background-color: #0F172A; -fx-padding: 14px; -fx-background-radius: 8px;");

        Label lblAlgoTitle = new Label("🧠 DSA Algorithmic Intelligence & Risk Assessment");
        lblAlgoTitle.setStyle("-fx-text-fill: #38BDF8; -fx-font-weight: bold; -fx-font-size: 13px;");

        // Compute real-time algorithmic properties:
        String queuePos = ComplaintService.getInstance().getQueuePosition(complaint);
        String slaRisk = ComplaintService.getInstance().calculateSlaRisk(complaint);

        // Run Wagner-Fischer duplicate search against existing database
        DuplicateCheckResult dupCheck = EditDistanceDuplicateDetector.findDuplicate(
                complaint, DatabaseHandler.getInstance().getComplaints(), 65.0);

        String dupText = dupCheck.isDuplicate ?
                String.format("HIGH (%.1f%% match with [%s] \"%s\")",
                        dupCheck.similarityScore, dupCheck.matchedComplaint.getId(), dupCheck.matchedComplaint.getTitle()) :
                "Low (<65% similarity - Unique issue)";

        GridPane algoGrid = new GridPane();
        algoGrid.setHgap(14);
        algoGrid.setVgap(6);

        algoGrid.addRow(0, algoLabel("Binary Heap Queue Position:"), algoValue(queuePos));
        algoGrid.addRow(1, algoLabel("Wagner-Fischer Duplicate Risk:"), algoValue(dupText));
        algoGrid.addRow(2, algoLabel("Algorithmic Priority Score:"), algoValue(complaint.getPriority() == complainttracker.models.Priority.CRITICAL ? "100.0 / 100" : complaint.getPriority() == complainttracker.models.Priority.HIGH ? "75.0 / 100" : complaint.getPriority() == complainttracker.models.Priority.MEDIUM ? "50.0 / 100" : "25.0 / 100"));
        algoGrid.addRow(3, algoLabel("SLA Compliance Risk Level:"), algoValue(slaRisk));
        algoGrid.addRow(4, algoLabel("Department Load Balance:"), algoValue(complaint.getDepartmentName() + " (Capacity monitored via Edmonds-Karp)"));

        algoBox.getChildren().addAll(lblAlgoTitle, algoGrid);

        // 3. Admin Operational Action Buttons
        VBox actionsCard = new VBox(8);
        actionsCard.setStyle("-fx-background-color: #F1F5F9; -fx-padding: 12px; -fx-background-radius: 8px;");

        Label lblActHeader = new Label("Administrative Controls:");
        lblActHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #334155;");

        FlowPane actionsFlow = new FlowPane();
        actionsFlow.setHgap(8);
        actionsFlow.setVgap(8);

        Button btnAssignDept = new Button("Assign Department");
        btnAssignDept.getStyleClass().add("btn-secondary");

        Button btnAssignOfficer = new Button("Assign Field Officer");
        btnAssignOfficer.getStyleClass().add("btn-secondary");

        Button btnChangePrio = new Button("Change Priority");
        btnChangePrio.getStyleClass().add("btn-secondary");

        Button btnUpdateStatus = new Button("Update Status");
        btnUpdateStatus.getStyleClass().add("btn-secondary");

        Button btnEscalate = new Button("⚡ Escalate");
        btnEscalate.getStyleClass().add("btn-warning");

        Button btnAddNote = new Button("📝 Add Internal Note");
        btnAddNote.getStyleClass().add("btn-secondary");

        Button btnResolve = new Button("✓ Resolve Complaint");
        btnResolve.getStyleClass().add("btn-success");
        btnResolve.setDisable(complaint.getStatus() == Status.RESOLVED);

        actionsFlow.getChildren().addAll(
                btnAssignDept, btnAssignOfficer, btnChangePrio,
                btnUpdateStatus, btnEscalate, btnAddNote, btnResolve
        );
        actionsCard.getChildren().addAll(lblActHeader, actionsFlow);

        // Action Handlers
        btnAssignDept.setOnAction(e -> {
            List<Department> depts = DatabaseHandler.getInstance().getDepartments();
            ChoiceDialog<Department> d = new ChoiceDialog<>(depts.get(0), depts);
            d.setTitle("Assign Department: " + complaint.getId());
            d.setHeaderText("Route grievance to departmental jurisdiction:");
            d.showAndWait().ifPresent(dept -> {
                String oldDept = complaint.getDepartmentName();
                complaint.setDepartmentId(dept.getId());
                complaint.setDepartmentName(dept.getName());
                ComplaintService.getInstance().updateComplaint(complaint, "Department Reassigned",
                        AuthService.getInstance().getCurrentUser().getFullName(),
                        "Reassigned from [" + oldDept + "] to [" + dept.getName() + "]");
                dialog.close();
                onUpdated.run();
                show(complaint, onUpdated, onNavigateToHistory);
            });
        });

        btnAssignOfficer.setOnAction(e -> {
            TextInputDialog d = new TextInputDialog(complaint.getAssignedStaff() != null ? complaint.getAssignedStaff() : "");
            d.setTitle("Assign Field Officer: " + complaint.getId());
            d.setHeaderText("Enter field officer or engineer name:");
            d.showAndWait().ifPresent(staff -> {
                String oldStaff = complaint.getAssignedStaff();
                complaint.setAssignedStaff(staff.trim());
                ComplaintService.getInstance().updateComplaint(complaint, "Field Officer Assigned",
                        AuthService.getInstance().getCurrentUser().getFullName(),
                        "Staff assigned: " + staff.trim() + " (Previous: " + oldStaff + ")");
                dialog.close();
                onUpdated.run();
                show(complaint, onUpdated, onNavigateToHistory);
            });
        });

        btnChangePrio.setOnAction(e -> {
            ChoiceDialog<complainttracker.models.Priority> d = new ChoiceDialog<>(complaint.getPriority(), complainttracker.models.Priority.values());
            d.setTitle("Change Priority: " + complaint.getId());
            d.setHeaderText("Adjust urgency level:");
            d.showAndWait().ifPresent(p -> {
                complainttracker.models.Priority oldPrio = complaint.getPriority();
                complaint.setPriority(p);
                ComplaintService.getInstance().updateComplaint(complaint, "Priority Adjusted",
                        AuthService.getInstance().getCurrentUser().getFullName(),
                        "Priority changed from " + oldPrio + " to " + p);
                dialog.close();
                onUpdated.run();
                show(complaint, onUpdated, onNavigateToHistory);
            });
        });

        btnUpdateStatus.setOnAction(e -> {
            ChoiceDialog<Status> d = new ChoiceDialog<>(complaint.getStatus(), Status.values());
            d.setTitle("Update Status: " + complaint.getId());
            d.setHeaderText("Change lifecycle status:");
            d.showAndWait().ifPresent(s -> {
                Status oldStatus = complaint.getStatus();
                complaint.setStatus(s);
                ComplaintService.getInstance().updateComplaint(complaint, "Status Updated",
                        AuthService.getInstance().getCurrentUser().getFullName(),
                        "Status changed from " + oldStatus + " to " + s);
                dialog.close();
                onUpdated.run();
                show(complaint, onUpdated, onNavigateToHistory);
            });
        });

        btnEscalate.setOnAction(e -> {
            TextInputDialog d = new TextInputDialog("SLA breach risk / emergency hazard");
            d.setTitle("Escalate Grievance: " + complaint.getId());
            d.setHeaderText("Enter reason for administrative escalation:");
            d.showAndWait().ifPresent(reason -> {
                ComplaintService.getInstance().escalateComplaint(complaint.getId(), reason);
                UIUtils.showInfo("Escalation Completed", "Grievance Escalated",
                        "Priority elevated to CRITICAL and escalated to supervisor.");
                dialog.close();
                onUpdated.run();
                show(complaint, onUpdated, onNavigateToHistory);
            });
        });

        btnAddNote.setOnAction(e -> {
            TextInputDialog d = new TextInputDialog();
            d.setTitle("Internal Admin Note: " + complaint.getId());
            d.setHeaderText("Add internal operational note (hidden from citizen):");
            d.showAndWait().ifPresent(note -> {
                if (!note.trim().isEmpty()) {
                    ComplaintService.getInstance().addAdminInternalNote(complaint.getId(), note.trim());
                    UIUtils.showInfo("Note Saved", "Internal Note Added",
                        "The note has been logged to the operational audit trail.");
                    dialog.close();
                    onUpdated.run();
                    show(complaint, onUpdated, onNavigateToHistory);
                }
            });
        });

        btnResolve.setOnAction(e -> {
            Dialog<Boolean> resDialog = new Dialog<>();
            resDialog.setTitle("Resolve Case: " + complaint.getId());
            resDialog.setHeaderText("Record technical resolution details:");

            VBox resBox = new VBox(10);
            resBox.setPadding(new Insets(14));
            resBox.setPrefWidth(400);

            TextArea txtResNotes = new TextArea();
            txtResNotes.setPromptText("Technical actions taken by field technicians to fix the issue...");
            txtResNotes.setPrefRowCount(4);

            resBox.getChildren().addAll(new Label("Resolution Report:"), txtResNotes);
            resDialog.getDialogPane().setContent(resBox);
            resDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            resDialog.setResultConverter(b -> b == ButtonType.OK);
            resDialog.showAndWait().ifPresent(ok -> {
                if (ok) {
                    ComplaintService.getInstance().resolveComplaint(complaint.getId(), 5,
                            "Resolved by field operations", txtResNotes.getText().trim());
                    UIUtils.showInfo("Case Resolved", "Grievance Resolved", "Status changed to RESOLVED.");
                    dialog.close();
                    onUpdated.run();
                    show(complaint, onUpdated, onNavigateToHistory);
                }
            });
        });

        Button btnViewAudit = new Button("📜 View Complete Audit Trail History");
        btnViewAudit.getStyleClass().add("btn-secondary");
        btnViewAudit.setOnAction(e -> {
            dialog.close();
            onNavigateToHistory.accept(complaint);
        });

        content.getChildren().addAll(infoGrid, lblDescTitle, lblDesc, algoBox, actionsCard, btnViewAudit);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        dialog.getDialogPane().setContent(scroll);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private static Label boldLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #475569; -fx-min-width: 120px;");
        return lbl;
    }

    private static Label algoLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-family: monospace; -fx-font-weight: bold; -fx-text-fill: #94A3B8;");
        return lbl;
    }

    private static Label algoValue(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-family: monospace; -fx-text-fill: #38BDF8; -fx-font-weight: bold;");
        lbl.setWrapText(true);
        return lbl;
    }
}
