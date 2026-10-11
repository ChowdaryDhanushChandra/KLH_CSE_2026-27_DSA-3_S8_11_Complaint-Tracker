package complainttracker.views;

import complainttracker.algorithms.CustomPriorityQueue;
import complainttracker.models.Complaint;
import complainttracker.models.Priority;
import complainttracker.services.ComplaintService;
import complainttracker.utils.UIUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PriorityQueueView {
    private final Consumer<Complaint> onSelectComplaint;
    private VBox queueCardsContainer;
    private TextArea txtHeapTrace;
    private Label lblQueueStats;

    public PriorityQueueView(Consumer<Complaint> onSelectComplaint) {
        this.onSelectComplaint = onSelectComplaint;
    }

    public Node getView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(24));

        Label lblTitle = new Label("Smart Priority Dispatch Queue (Binary Min-Heap)");
        lblTitle.getStyleClass().add("page-title");
        Label lblSub = new Label("Autonomous priority scheduling backed by Custom Binary Heap with O(log N) operations.");
        lblSub.getStyleClass().add("page-subtitle");
        VBox headerBox = new VBox(2, lblTitle, lblSub);

        // Control Banner Card
        VBox bannerCard = new VBox(14);
        bannerCard.getStyleClass().add("card");
        bannerCard.setStyle("-fx-background-color: linear-gradient(to right, #1E293B, #0F172A); -fx-background-radius: 12px;");

        lblQueueStats = new Label("Active Queue Size: Loading...");
        lblQueueStats.setStyle("-fx-text-fill: #38BDF8; -fx-font-weight: bold; -fx-font-size: 15px;");

        Label lblExpl = new Label("The binary heap prioritizes Critical over High, Medium, and Low. When priorities match, earlier submission timestamps take precedence (anti-starvation guarantee).");
        lblExpl.setStyle("-fx-text-fill: #E2E8F0; -fx-font-size: 12px;");

        Button btnProcessNext = new Button("⚡ DISPATCH / PROCESS NEXT HIGHEST PRIORITY COMPLAINT");
        btnProcessNext.getStyleClass().add("btn-primary");
        btnProcessNext.setStyle("-fx-font-size: 14px; -fx-padding: 12px 24px; -fx-background-color: #2563EB;");

        bannerCard.getChildren().addAll(lblQueueStats, lblExpl, btnProcessNext);

        // Heap Step Trace Box
        VBox traceBox = new VBox(6);
        traceBox.getStyleClass().add("card");
        Label lblTraceTitle = new Label("Binary Heap Dynamic Trace Log (Bubble-Up / Sift-Down Steps)");
        lblTraceTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155;");

        txtHeapTrace = new TextArea();
        txtHeapTrace.setEditable(false);
        txtHeapTrace.setPrefRowCount(4);
        txtHeapTrace.setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-size: 12px; -fx-control-inner-background: #0F172A; -fx-text-fill: #38BDF8;");
        txtHeapTrace.setText("Heap initialized. Click 'DISPATCH NEXT' to trigger root extraction and heapify-down trace.");

        traceBox.getChildren().addAll(lblTraceTitle, txtHeapTrace);

        // Queue Cards Section
        Label lblCardsTitle = new Label("Current Heap Order (Top Elements in Priority Order)");
        lblCardsTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

        queueCardsContainer = new VBox(10);

        btnProcessNext.setOnAction(e -> {
            List<CustomPriorityQueue.HeapStep> trace = new ArrayList<>();
            Complaint next = ComplaintService.getInstance().processNextHighestPriority(trace);

            if (next != null) {
                StringBuilder sb = new StringBuilder();
                sb.append(String.format(">>> DISPATCHED ROOT ITEM: [%s] %s (Priority: %s, Filed: %s)%n",
                        next.getId(), next.getTitle(), next.getPriority(), next.getFormattedCreatedAt()));
                sb.append("-------------------------------------------------------------------------\n");
                for (CustomPriorityQueue.HeapStep step : trace) {
                    sb.append(String.format("[%s] %s%n", step.action, step.description));
                }
                txtHeapTrace.setText(sb.toString());

                UIUtils.showInfo("Complaint Dispatched", "Highest Priority Complaint Dispatched",
                        String.format("Complaint [%s] '%s' has been marked IN PROGRESS and dispatched for field resolution.",
                                next.getId(), next.getTitle()));

                refreshQueueView();
            } else {
                txtHeapTrace.setText("Priority Queue is currently empty. No active complaints to process.");
                UIUtils.showInfo("Queue Empty", "No Complaints in Queue", "All registered complaints have been processed or resolved!");
            }
        });

        refreshQueueView();

        root.getChildren().addAll(headerBox, bannerCard, traceBox, lblCardsTitle, queueCardsContainer);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        return scroll;
    }

    private void refreshQueueView() {
        queueCardsContainer.getChildren().clear();
        CustomPriorityQueue<Complaint> heap = ComplaintService.getInstance().getPriorityHeap();
        int size = heap.size();
        lblQueueStats.setText("Active Queue Size: " + size + " Complaints in Binary Heap");

        if (size == 0) {
            Label lblEmpty = new Label("🎉 No pending complaints! All items have been dispatched.");
            lblEmpty.setStyle("-fx-font-size: 14px; -fx-text-fill: #10B981; -fx-padding: 20px;");
            queueCardsContainer.getChildren().add(lblEmpty);
            return;
        }

        List<Complaint> items = heap.toList();
        // Sort items by priority for display
        items.sort(Complaint::compareTo);

        for (int i = 0; i < items.size(); i++) {
            Complaint c = items.get(i);
            int rank = i + 1;

            HBox card = new HBox(16);
            card.getStyleClass().add("card");
            card.setAlignment(Pos.CENTER_LEFT);
            card.setPadding(new Insets(12, 16, 12, 16));

            // Rank indicator
            Label lblRank = new Label("#" + rank);
            lblRank.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " +
                    (rank == 1 ? "#EF4444" : "#64748B") + "; -fx-min-width: 40px;");

            // Content
            VBox infoBox = new VBox(4);
            HBox.setHgrow(infoBox, javafx.scene.layout.Priority.ALWAYS);

            Label lblHeading = new Label("[" + c.getId() + "] " + c.getTitle());
            lblHeading.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #0F172A;");

            Label lblMeta = new Label(String.format("Complainant: %s | Location: %s | Filed: %s | SLA: %s",
                    c.getCustomerName(), c.getLocation(), c.getFormattedCreatedAt(), c.getSlaRemainingDisplay()));
            lblMeta.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");

            infoBox.getChildren().addAll(lblHeading, lblMeta);

            // Badges
            HBox badges = new HBox(8,
                    UIUtils.createPriorityBadge(c.getPriority()),
                    UIUtils.createStatusBadge(c.getStatus())
            );
            badges.setAlignment(Pos.CENTER_RIGHT);

            Button btnInspect = new Button("Inspect");
            btnInspect.getStyleClass().add("btn-secondary");
            btnInspect.setOnAction(e -> onSelectComplaint.accept(c));

            card.getChildren().addAll(lblRank, infoBox, badges, btnInspect);
            queueCardsContainer.getChildren().add(card);
        }
    }
}
