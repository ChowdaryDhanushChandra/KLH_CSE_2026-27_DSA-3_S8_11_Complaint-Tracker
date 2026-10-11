package complainttracker.views;

import complainttracker.algorithms.NetworkFlowAssignment.AssignmentResult;
import complainttracker.models.Department;
import complainttracker.services.DepartmentService;
import complainttracker.utils.UIUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class DepartmentView {
    private FlowPane deptsContainer;
    private TextArea txtFlowTrace;

    public Node getView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(24));

        Label lblTitle = new Label("Department Capacity & Workload Balancing");
        lblTitle.getStyleClass().add("page-title");
        Label lblSub = new Label("Resource allocation powered by Edmonds-Karp Bipartite Maximum Flow Algorithm.");
        lblSub.getStyleClass().add("page-subtitle");
        VBox headerBox = new VBox(2, lblTitle, lblSub);

        // Action Card
        VBox actionCard = new VBox(12);
        actionCard.getStyleClass().add("card");
        actionCard.setStyle("-fx-background-color: linear-gradient(to right, #0F172A, #1E293B);");

        Label lblBanner = new Label("Automated Algorithmic Load Balancing");
        lblBanner.setStyle("-fx-text-fill: #38BDF8; -fx-font-weight: bold; -fx-font-size: 15px;");

        Label lblBannerDesc = new Label("Uses Edmonds-Karp Network Flow to model unassigned complaints as demand nodes and department capacities as residual sink edges, finding maximum feasible assignment.");
        lblBannerDesc.setStyle("-fx-text-fill: #E2E8F0; -fx-font-size: 12px;");

        Button btnRunFlow = new Button("⚡ RUN NETWORK FLOW AUTO-ALLOCATION (Edmonds-Karp Max-Flow)");
        btnRunFlow.getStyleClass().add("btn-primary");
        btnRunFlow.setStyle("-fx-font-size: 13px; -fx-padding: 10px 20px;");

        actionCard.getChildren().addAll(lblBanner, lblBannerDesc, btnRunFlow);

        // Departments FlowPane
        deptsContainer = new FlowPane();
        deptsContainer.setHgap(16);
        deptsContainer.setVgap(16);
        deptsContainer.setPrefWrapLength(900);

        // Execution Step Log Area
        VBox logBox = new VBox(8);
        logBox.getStyleClass().add("card");
        Label lblLogTitle = new Label("Edmonds-Karp Residual Network & Augmenting Path Log");
        lblLogTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155;");

        txtFlowTrace = new TextArea();
        txtFlowTrace.setEditable(false);
        txtFlowTrace.setPrefRowCount(6);
        txtFlowTrace.setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-size: 12px; -fx-control-inner-background: #0F172A; -fx-text-fill: #38BDF8;");
        txtFlowTrace.setText("Click 'RUN NETWORK FLOW AUTO-ALLOCATION' above to execute Edmonds-Karp algorithm.");

        logBox.getChildren().addAll(lblLogTitle, txtFlowTrace);

        btnRunFlow.setOnAction(e -> {
            AssignmentResult result = DepartmentService.getInstance().runNetworkFlowAllocation();
            StringBuilder sb = new StringBuilder();
            sb.append("=== EDMONDS-KARP MAXIMUM FLOW ALLOCATION RESULT ===\n");
            sb.append(String.format("Total Augmenting Flow Pushed: %d | Assignments Made: %d%n%n",
                    result.maxFlowValue, result.totalAssigned));
            for (String step : result.executionSteps) {
                sb.append("• ").append(step).append("\n");
            }
            txtFlowTrace.setText(sb.toString());

            UIUtils.showInfo("Max-Flow Complete", "Network Flow Allocation Finished",
                    String.format("Successfully allocated %d complaints across municipal departments with capacity constraints.",
                            result.totalAssigned));

            refreshDepartmentCards();
        });

        refreshDepartmentCards();

        root.getChildren().addAll(headerBox, actionCard, deptsContainer, logBox);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        return scroll;
    }

    private void refreshDepartmentCards() {
        deptsContainer.getChildren().clear();
        List<Department> list = DepartmentService.getInstance().getAllDepartments();

        for (Department d : list) {
            VBox card = new VBox(10);
            card.getStyleClass().add("card");
            card.setPrefWidth(280);

            // Dept Name & ID
            Label lblId = new Label(d.getId());
            lblId.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #2563EB;");

            Label lblName = new Label(d.getName());
            lblName.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
            lblName.setWrapText(true);

            Label lblCat = new Label("Jurisdiction: " + (d.getCategoryServed() != null ? d.getCategoryServed().getDisplayName() : "General"));
            lblCat.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");

            // Load & Capacity
            double pct = d.getLoadPercentage();
            Label lblLoad = new Label(String.format("Active Load: %d / %d slots (%.0f%%)", d.getCurrentLoad(), d.getCapacity(), pct));
            lblLoad.setStyle("-fx-font-weight: 600; -fx-font-size: 12px;");

            ProgressBar bar = new ProgressBar(pct / 100.0);
            bar.setMaxWidth(Double.MAX_VALUE);
            bar.setPrefHeight(10);

            String barColor = "#10B981";
            if (pct >= 85.0) barColor = "#EF4444";
            else if (pct >= 65.0) barColor = "#F59E0B";
            bar.setStyle("-fx-accent: " + barColor + ";");

            // Staff & Resolution time
            Label lblStaff = new Label(String.format("Staff: %d officers | Avg Resolution: %.1fh",
                    d.getStaffCount(), d.getAvgResolutionHours()));
            lblStaff.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");

            card.getChildren().addAll(lblId, lblName, lblCat, new Separator(), lblLoad, bar, lblStaff);
            deptsContainer.getChildren().add(card);
        }
    }
}
