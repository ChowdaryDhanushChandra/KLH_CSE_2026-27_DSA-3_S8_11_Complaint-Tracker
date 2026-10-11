package complainttracker.views;

import complainttracker.algorithms.*;
import complainttracker.algorithms.EditDistanceDuplicateDetector.DPResult;
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

public class AlgorithmLabView {

    public Node getView() {
        VBox root = new VBox(16);
        root.setPadding(new Insets(24));

        Label lblTitle = new Label("Interactive Advanced Algorithm Studio");
        lblTitle.getStyleClass().add("page-title");
        Label lblSub = new Label("Deep-dive educational laboratory for the 6 core manual DSA implementations.");
        lblSub.getStyleClass().add("page-subtitle");
        VBox headerBox = new VBox(2, lblTitle, lblSub);

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-background-color: transparent;");

        tabPane.getTabs().addAll(
                createHashMapTab(),
                createPriorityQueueTab(),
                createDPEditDistanceTab(),
                createKMPTab(),
                createNetworkFlowTab(),
                createSchedulerTab()
        );

        root.getChildren().addAll(headerBox, tabPane);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        return scroll;
    }

    // ==========================================
    // TAB 1: CUSTOM HASH MAP
    // ==========================================
    private Tab createHashMapTab() {
        Tab tab = new Tab("1. Custom Hash Table");
        VBox content = new VBox(16);
        content.setPadding(new Insets(16));

        Label lblDesc = new Label("Hand-crafted Hash Table using Separate Chaining (Linked Lists) and Dynamic Rehashing at 75% load factor.");
        lblDesc.setStyle("-fx-font-size: 12px; -fx-text-fill: #475569;");

        CustomHashMap<String, String> demoMap = new CustomHashMap<>(8, 0.75f);
        demoMap.put("CMP-1001", "Water Pipe Leakage");
        demoMap.put("CMP-1002", "Transformer Spark");
        demoMap.put("CMP-1003", "Pothole Crater");
        demoMap.put("CMP-1004", "Garbage Spillage");
        demoMap.put("CMP-1005", "Double Billing");

        TextField txtKey = new TextField();
        txtKey.setPromptText("Enter Key (e.g. CMP-1006)");
        TextField txtVal = new TextField();
        txtVal.setPromptText("Enter Value (e.g. Broken streetlight)");

        Button btnPut = new Button("Put (Insert / Update)");
        btnPut.getStyleClass().add("btn-primary");

        Button btnTrace = new Button("Trace Search Key");
        btnTrace.getStyleClass().add("btn-secondary");

        HBox inputRow = new HBox(10, txtKey, txtVal, btnPut, btnTrace);
        inputRow.setAlignment(Pos.CENTER_LEFT);

        TextArea txtTrace = new TextArea();
        txtTrace.setEditable(false);
        txtTrace.setPrefRowCount(4);
        txtTrace.setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-size: 12px; -fx-control-inner-background: #0F172A; -fx-text-fill: #38BDF8;");

        VBox bucketDisplay = new VBox(6);
        bucketDisplay.getStyleClass().add("card");

        Runnable renderBuckets = () -> {
            bucketDisplay.getChildren().clear();
            Label lblBuckets = new Label(String.format("Bucket Array Representation (Capacity: %d, Elements: %d, Load: %.2f)",
                    demoMap.getCapacity(), demoMap.size(), (double) demoMap.size() / demoMap.getCapacity()));
            lblBuckets.setStyle("-fx-font-weight: bold; -fx-text-fill: #1E293B;");
            bucketDisplay.getChildren().add(lblBuckets);

            CustomHashMap.Entry<String, String>[] buckets = demoMap.getBuckets();
            for (int i = 0; i < demoMap.getCapacity(); i++) {
                HBox bucketRow = new HBox(8);
                bucketRow.setAlignment(Pos.CENTER_LEFT);

                Label lblIdx = new Label(String.format("Bucket [%02d]: ", i));
                lblIdx.setStyle("-fx-font-family: monospace; -fx-font-weight: bold; -fx-text-fill: #2563EB; -fx-min-width: 90px;");
                bucketRow.getChildren().add(lblIdx);

                CustomHashMap.Entry<String, String> curr = buckets[i];
                if (curr == null) {
                    Label lblNull = new Label("null");
                    lblNull.setStyle("-fx-text-fill: #94A3B8; -fx-font-style: italic;");
                    bucketRow.getChildren().add(lblNull);
                } else {
                    while (curr != null) {
                        Label lblNode = new Label(String.format("[%s : %s]", curr.key, curr.value));
                        lblNode.setStyle("-fx-background-color: #EFF6FF; -fx-border-color: #93C5FD; -fx-border-radius: 4px; -fx-padding: 3px 6px; -fx-font-size: 11px;");
                        bucketRow.getChildren().add(lblNode);

                        if (curr.next != null) {
                            Label arrow = new Label("→");
                            arrow.setStyle("-fx-text-fill: #3B82F6; -fx-font-weight: bold;");
                            bucketRow.getChildren().add(arrow);
                        }
                        curr = curr.next;
                    }
                }
                bucketDisplay.getChildren().add(bucketRow);
            }
        };

        btnPut.setOnAction(e -> {
            String k = txtKey.getText().trim();
            String v = txtVal.getText().trim();
            if (!k.isEmpty()) {
                demoMap.put(k, v.isEmpty() ? "Default Value" : v);
                txtTrace.setText(String.format("Inserted [%s => %s]. Size is now %d.", k, v, demoMap.size()));
                renderBuckets.run();
            }
        });

        btnTrace.setOnAction(e -> {
            String k = txtKey.getText().trim();
            if (!k.isEmpty()) {
                CustomHashMap.HashTrace tr = demoMap.traceSearch(k);
                txtTrace.setText(String.format("TRACE SEARCH FOR KEY: '%s'%nHashCode: %d%nBucket Index: %d%nComparisons: %d%nFound: %s%nDetails: %s",
                        tr.keyStr, tr.hashCode, tr.bucketIndex, tr.comparisons, tr.found, tr.pathDescription));
            }
        });

        renderBuckets.run();
        content.getChildren().addAll(lblDesc, inputRow, txtTrace, bucketDisplay);
        tab.setContent(content);
        return tab;
    }

    // ==========================================
    // TAB 2: PRIORITY QUEUE (BINARY HEAP)
    // ==========================================
    private Tab createPriorityQueueTab() {
        Tab tab = new Tab("2. Priority Queue (Binary Heap)");
        VBox content = new VBox(16);
        content.setPadding(new Insets(16));

        Label lblDesc = new Label("Hand-built Binary Min-Heap Priority Queue with O(log N) Heapify-Up (insert) and Heapify-Down (poll).");
        lblDesc.setStyle("-fx-font-size: 12px; -fx-text-fill: #475569;");

        CustomPriorityQueue<Complaint> demoHeap = new CustomPriorityQueue<>();
        // Seed with sample complaints
        demoHeap.insert(new Complaint("T-01", "User A", "", "", "Water leak", "", null, Priority.LOW, null, null, null));
        demoHeap.insert(new Complaint("T-02", "User B", "", "", "Transformer explosion", "", null, Priority.CRITICAL, null, null, null));
        demoHeap.insert(new Complaint("T-03", "User C", "", "", "Road pothole", "", null, Priority.HIGH, null, null, null));
        demoHeap.insert(new Complaint("T-04", "User D", "", "", "Garbage bin", "", null, Priority.MEDIUM, null, null, null));

        TextField txtLabel = new TextField();
        txtLabel.setPromptText("Enter complaint description");

        ComboBox<Priority> cmbPrio = new ComboBox<>();
        cmbPrio.getItems().addAll(Priority.values());
        cmbPrio.setValue(Priority.HIGH);

        Button btnInsert = new Button("Insert into Heap");
        btnInsert.getStyleClass().add("btn-primary");

        Button btnPoll = new Button("Extract Root (Poll Min)");
        btnPoll.getStyleClass().add("btn-danger");

        HBox inputRow = new HBox(10, txtLabel, cmbPrio, btnInsert, btnPoll);
        inputRow.setAlignment(Pos.CENTER_LEFT);

        TextArea txtTrace = new TextArea();
        txtTrace.setEditable(false);
        txtTrace.setPrefRowCount(5);
        txtTrace.setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-size: 12px; -fx-control-inner-background: #0F172A; -fx-text-fill: #38BDF8;");

        FlowPane heapArrayPane = new FlowPane();
        heapArrayPane.setHgap(10);
        heapArrayPane.setVgap(10);
        heapArrayPane.getStyleClass().add("card");

        Runnable renderHeap = () -> {
            heapArrayPane.getChildren().clear();
            List<Complaint> items = demoHeap.toList();
            for (int i = 0; i < items.size(); i++) {
                Complaint c = items.get(i);
                VBox nodeBox = new VBox(4);
                nodeBox.setAlignment(Pos.CENTER);
                nodeBox.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #CBD5E1; -fx-border-radius: 8px; -fx-padding: 8px 12px;");

                Label lblIdx = new Label("Index [" + i + "]" + (i == 0 ? " (ROOT)" : ""));
                lblIdx.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #2563EB;");

                Label lblItem = new Label(c.getTitle());
                lblItem.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

                Label badge = UIUtils.createPriorityBadge(c.getPriority());

                nodeBox.getChildren().addAll(lblIdx, lblItem, badge);
                heapArrayPane.getChildren().add(nodeBox);
            }
        };

        btnInsert.setOnAction(e -> {
            String title = txtLabel.getText().trim();
            if (title.isEmpty()) title = "Grievance Task " + (demoHeap.size() + 1);
            Complaint c = new Complaint("T-NEW", "Complainant", "", "", title, "", null, cmbPrio.getValue(), null, null, null);
            List<CustomPriorityQueue.HeapStep> trace = new ArrayList<>();
            demoHeap.insertWithTrace(c, trace);

            StringBuilder sb = new StringBuilder("=== INSERTION & HEAPIFY-UP TRACE ===\n");
            for (CustomPriorityQueue.HeapStep st : trace) sb.append(st).append("\n");
            txtTrace.setText(sb.toString());
            renderHeap.run();
        });

        btnPoll.setOnAction(e -> {
            if (demoHeap.isEmpty()) {
                txtTrace.setText("Heap is currently empty.");
                return;
            }
            List<CustomPriorityQueue.HeapStep> trace = new ArrayList<>();
            Complaint rootComp = demoHeap.pollWithTrace(trace);

            StringBuilder sb = new StringBuilder("=== EXTRACTION & HEAPIFY-DOWN TRACE ===\n");
            sb.append("Extracted Min Element: ").append(rootComp).append("\n");
            for (CustomPriorityQueue.HeapStep st : trace) sb.append(st).append("\n");
            txtTrace.setText(sb.toString());
            renderHeap.run();
        });

        renderHeap.run();
        content.getChildren().addAll(lblDesc, inputRow, txtTrace, new Label("Heap Array Memory Representation:"), heapArrayPane);
        tab.setContent(content);
        return tab;
    }

    // ==========================================
    // TAB 3: DYNAMIC PROGRAMMING EDIT DISTANCE
    // ==========================================
    private Tab createDPEditDistanceTab() {
        Tab tab = new Tab("3. DP Edit Distance (Levenshtein)");
        VBox content = new VBox(16);
        content.setPadding(new Insets(16));

        Label lblDesc = new Label("Wagner-Fischer 2D Dynamic Programming matrix computation for duplicate detection and text similarity.");
        lblDesc.setStyle("-fx-font-size: 12px; -fx-text-fill: #475569;");

        TextField txtA = new TextField("Water pipe burst in 4th Block");
        txtA.setPrefWidth(260);
        TextField txtB = new TextField("Water pipe broken in 4th Block");
        txtB.setPrefWidth(260);

        Button btnCompute = new Button("Compute DP Matrix & Similarity");
        btnCompute.getStyleClass().add("btn-primary");

        HBox inputRow = new HBox(10, new Label("Text 1:"), txtA, new Label("Text 2:"), txtB, btnCompute);
        inputRow.setAlignment(Pos.CENTER_LEFT);

        Label lblResult = new Label();
        lblResult.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2563EB;");

        TextArea txtAlignment = new TextArea();
        txtAlignment.setEditable(false);
        txtAlignment.setPrefRowCount(4);
        txtAlignment.setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-size: 12px; -fx-control-inner-background: #0F172A; -fx-text-fill: #38BDF8;");

        GridPane matrixGrid = new GridPane();
        matrixGrid.setHgap(3);
        matrixGrid.setVgap(3);
        matrixGrid.getStyleClass().add("card");

        btnCompute.setOnAction(e -> {
            String s1 = txtA.getText().trim();
            String s2 = txtB.getText().trim();

            DPResult dp = EditDistanceDuplicateDetector.computeMatrix(s1, s2);
            lblResult.setText(String.format("Levenshtein Distance: %d edits | Similarity Score: %.1f%% (%s)",
                    dp.distance, dp.similarityScore, dp.similarityScore >= 70.0 ? "DUPLICATE MATCH" : "DISTINCT"));

            StringBuilder sb = new StringBuilder("Optimal Backtracked Edit Operations:\n");
            for (String op : dp.operations) sb.append("• ").append(op).append("\n");
            txtAlignment.setText(sb.toString());

            // Render sample 2D grid (limit size to first 12 chars to avoid UI explosion)
            matrixGrid.getChildren().clear();
            int rows = Math.min(12, dp.matrix.length);
            int cols = Math.min(12, dp.matrix[0].length);

            // Column headers
            matrixGrid.add(createGridHeader("ε"), 1, 0);
            for (int j = 1; j < cols; j++) {
                matrixGrid.add(createGridHeader(String.valueOf(s2.charAt(j - 1))), j + 1, 0);
            }

            for (int i = 0; i < rows; i++) {
                // Row header
                String rowHeader = (i == 0) ? "ε" : String.valueOf(s1.charAt(i - 1));
                matrixGrid.add(createGridHeader(rowHeader), 0, i + 1);

                for (int j = 0; j < cols; j++) {
                    Label cell = new Label(String.valueOf(dp.matrix[i][j]));
                    cell.setPrefSize(32, 28);
                    cell.setAlignment(Pos.CENTER);
                    cell.setStyle("-fx-border-color: #E2E8F0; -fx-background-color: #FFFFFF; -fx-font-family: monospace; -fx-font-size: 11px;");
                    matrixGrid.add(cell, j + 1, i + 1);
                }
            }
        });

        // Trigger initial calculation
        btnCompute.fire();

        content.getChildren().addAll(lblDesc, inputRow, lblResult, txtAlignment, new Label("DP Matrix Table Preview:"), matrixGrid);
        tab.setContent(content);
        return tab;
    }

    private Label createGridHeader(String text) {
        Label lbl = new Label(text);
        lbl.setPrefSize(32, 28);
        lbl.setAlignment(Pos.CENTER);
        lbl.setStyle("-fx-font-weight: bold; -fx-background-color: #F1F5F9; -fx-border-color: #CBD5E1; -fx-font-family: monospace;");
        return lbl;
    }

    // ==========================================
    // TAB 4: KNUTH-MORRIS-PRATT (KMP)
    // ==========================================
    private Tab createKMPTab() {
        Tab tab = new Tab("4. KMP String Matcher");
        VBox content = new VBox(16);
        content.setPadding(new Insets(16));

        Label lblDesc = new Label("Knuth-Morris-Pratt Algorithm featuring the Longest Proper Prefix which is also Suffix (LPS) Preprocessing Array.");
        lblDesc.setStyle("-fx-font-size: 12px; -fx-text-fill: #475569;");

        TextField txtText = new TextField("SEWERAGE_OVERFLOW_NEAR_SEWERAGE_PUMP_STATION");
        txtText.setPrefWidth(350);
        TextField txtPat = new TextField("SEWERAGE");
        txtPat.setPrefWidth(150);

        Button btnRun = new Button("Run KMP Algorithm");
        btnRun.getStyleClass().add("btn-primary");

        HBox inputRow = new HBox(10, new Label("Text:"), txtText, new Label("Pattern:"), txtPat, btnRun);
        inputRow.setAlignment(Pos.CENTER_LEFT);

        TextArea txtTrace = new TextArea();
        txtTrace.setEditable(false);
        txtTrace.setPrefRowCount(6);
        txtTrace.setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-size: 12px; -fx-control-inner-background: #0F172A; -fx-text-fill: #38BDF8;");

        HBox lpsPane = new HBox(8);
        lpsPane.getStyleClass().add("card");

        btnRun.setOnAction(e -> {
            String text = txtText.getText().trim();
            String pat = txtPat.getText().trim();

            int[] lps = KMPStringMatcher.computeLPSArray(pat);
            StringMatchResult res = KMPStringMatcher.search(text, pat);

            // Render LPS
            lpsPane.getChildren().clear();
            lpsPane.getChildren().add(new Label("LPS Array: "));
            for (int i = 0; i < pat.length(); i++) {
                VBox box = new VBox(2);
                box.setAlignment(Pos.CENTER);
                box.setStyle("-fx-border-color: #93C5FD; -fx-background-color: #EFF6FF; -fx-padding: 4px 8px; -fx-border-radius: 4px;");
                Label charLbl = new Label(String.valueOf(pat.charAt(i)));
                charLbl.setStyle("-fx-font-weight: bold; -fx-font-family: monospace;");
                Label valLbl = new Label(String.valueOf(lps[i]));
                valLbl.setStyle("-fx-text-fill: #2563EB; -fx-font-size: 11px;");
                box.getChildren().addAll(charLbl, valLbl);
                lpsPane.getChildren().add(box);
            }

            StringBuilder sb = new StringBuilder();
            sb.append(String.format("=== KMP SEARCH RESULTS ===%nMatches Found: %d | Time: %.2f µs | Comparisons: %d%n%n",
                    res.getMatchCount(), res.getExecutionTimeMicros(), res.getTotalComparisons()));
            for (String step : res.getTraceSteps()) {
                sb.append("• ").append(step).append("\n");
            }
            txtTrace.setText(sb.toString());
        });

        btnRun.fire();
        content.getChildren().addAll(lblDesc, inputRow, lpsPane, txtTrace);
        tab.setContent(content);
        return tab;
    }

    // ==========================================
    // TAB 5: NETWORK FLOW (EDMONDS-KARP)
    // ==========================================
    private Tab createNetworkFlowTab() {
        Tab tab = new Tab("5. Network Flow (Edmonds-Karp)");
        VBox content = new VBox(16);
        content.setPadding(new Insets(16));

        Label lblDesc = new Label("Edmonds-Karp BFS Maximum Flow Algorithm to balance complaint assignment against municipal department capacities.");
        lblDesc.setStyle("-fx-font-size: 12px; -fx-text-fill: #475569;");

        Button btnDemoFlow = new Button("Execute Flow Network Simulation");
        btnDemoFlow.getStyleClass().add("btn-primary");

        TextArea txtLog = new TextArea();
        txtLog.setEditable(false);
        txtLog.setPrefRowCount(10);
        txtLog.setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-size: 12px; -fx-control-inner-background: #0F172A; -fx-text-fill: #38BDF8;");

        btnDemoFlow.setOnAction(e -> {
            List<Complaint> dummyComplaints = new ArrayList<>();
            dummyComplaints.add(new Complaint("C1", "Citizen 1", "", "", "Water pipe leak", "", null, Priority.HIGH, null, null, null));
            dummyComplaints.add(new Complaint("C2", "Citizen 2", "", "", "Water contaminated", "", null, Priority.CRITICAL, null, null, null));
            dummyComplaints.add(new Complaint("C3", "Citizen 3", "", "", "Power blackout", "", null, Priority.HIGH, null, null, null));
            dummyComplaints.add(new Complaint("C4", "Citizen 4", "", "", "Pothole crater", "", null, Priority.MEDIUM, null, null, null));

            List<complainttracker.models.Department> dummyDepts = new ArrayList<>();
            dummyDepts.add(new complainttracker.models.Department("DEP-WAT", "Water Dept", complainttracker.models.Category.WATER, 2, 5, 12.0));
            dummyDepts.add(new complainttracker.models.Department("DEP-ELE", "Power Dept", complainttracker.models.Category.ELECTRICITY, 2, 5, 8.0));
            dummyDepts.add(new complainttracker.models.Department("DEP-ROA", "Roads Dept", complainttracker.models.Category.ROADS, 1, 4, 24.0));

            NetworkFlowAssignment.AssignmentResult res = NetworkFlowAssignment.allocateComplaints(dummyComplaints, dummyDepts);

            StringBuilder sb = new StringBuilder();
            sb.append("=== EDMONDS-KARP BFS AUGMENTING PATH TRACE ===\n");
            sb.append("Total Capacity Flow Pushed: ").append(res.maxFlowValue).append("\n\n");
            for (String step : res.executionSteps) {
                sb.append("• ").append(step).append("\n");
            }
            txtLog.setText(sb.toString());
        });

        btnDemoFlow.fire();
        content.getChildren().addAll(lblDesc, btnDemoFlow, txtLog);
        tab.setContent(content);
        return tab;
    }

    // ==========================================
    // TAB 6: MULTI-CRITERIA SCHEDULER
    // ==========================================
    private Tab createSchedulerTab() {
        Tab tab = new Tab("6. Multi-Factor Scheduler");
        VBox content = new VBox(16);
        content.setPadding(new Insets(16));

        Label lblDesc = new Label("Evaluates pending grievances with weighted multi-criteria scoring and sorts dynamically via custom QuickSort (Median-of-3).");
        lblDesc.setStyle("-fx-font-size: 12px; -fx-text-fill: #475569;");

        Slider sldPriority = new Slider(0.1, 1.0, 0.45);
        sldPriority.setShowTickLabels(true);
        sldPriority.setShowTickMarks(true);

        Slider sldSLA = new Slider(0.1, 1.0, 0.35);
        sldSLA.setShowTickLabels(true);
        sldSLA.setShowTickMarks(true);

        Slider sldAging = new Slider(0.1, 1.0, 0.20);
        sldAging.setShowTickLabels(true);
        sldAging.setShowTickMarks(true);

        Button btnSchedule = new Button("Run Scheduler & QuickSort");
        btnSchedule.getStyleClass().add("btn-primary");

        HBox slidersRow = new HBox(16,
                new VBox(4, new Label("Priority Weight (w1):"), sldPriority),
                new VBox(4, new Label("SLA Urgency Weight (w2):"), sldSLA),
                new VBox(4, new Label("Aging / Wait Weight (w3):"), sldAging),
                btnSchedule
        );
        slidersRow.setAlignment(Pos.CENTER_LEFT);

        Label lblFormula = new Label();
        lblFormula.setStyle("-fx-font-weight: bold; -fx-text-fill: #2563EB;");

        TableView<ComplaintScheduler.ScheduledItem> table = new TableView<>();
        table.setPrefHeight(260);

        TableColumn<ComplaintScheduler.ScheduledItem, Number> colRank = new TableColumn<>("Rank");
        colRank.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getRank()));
        colRank.setPrefWidth(60);

        TableColumn<ComplaintScheduler.ScheduledItem, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getComplaint().getId()));
        colId.setPrefWidth(85);

        TableColumn<ComplaintScheduler.ScheduledItem, String> colTitle = new TableColumn<>("Title");
        colTitle.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getComplaint().getTitle()));
        colTitle.setPrefWidth(220);

        TableColumn<ComplaintScheduler.ScheduledItem, String> colScore = new TableColumn<>("Total Score");
        colScore.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.format("%.1f", c.getValue().getTotalScore())));
        colScore.setPrefWidth(90);

        TableColumn<ComplaintScheduler.ScheduledItem, String> colBreakdown = new TableColumn<>("Formula Breakdown");
        colBreakdown.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getScoreBreakdown()));
        colBreakdown.setPrefWidth(320);

        table.getColumns().addAll(colRank, colId, colTitle, colScore, colBreakdown);

        btnSchedule.setOnAction(e -> {
            double w1 = sldPriority.getValue();
            double w2 = sldSLA.getValue();
            double w3 = sldAging.getValue();

            ComplaintScheduler.SchedulePlan plan = ComplaintScheduler.schedule(
                    ComplaintService.getInstance().getAllComplaints(), w1, w2, w3);

            lblFormula.setText(plan.formulaExplanation + " | QuickSort: " + plan.sortMetrics.comparisonCount + " comparisons, " + plan.sortMetrics.swapCount + " swaps (" + (plan.sortMetrics.executionNanos / 1000.0) + " µs)");
            table.setItems(javafx.collections.FXCollections.observableArrayList(plan.items));
        });

        btnSchedule.fire();
        content.getChildren().addAll(lblDesc, slidersRow, lblFormula, table);
        tab.setContent(content);
        return tab;
    }
}
