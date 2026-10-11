package com.texthack.graph;

import com.texthack.datastructures.ManualArrayList;
import com.texthack.metrics.ComplexityAnalyzer;
import com.texthack.metrics.ExecutionTimer;
import com.texthack.metrics.OperationCounter;
import com.texthack.model.AlgorithmResult;
import com.texthack.model.GraphEdge;
import com.texthack.model.ScheduleTask;

/**
 * Critical Path Method (CPM) and scheduling approximation calculator without java.util.*.
 */
public class CriticalPathCalculator {

    public static class ScheduleResult {
        public final int projectDuration;
        public final ManualArrayList<ScheduleTask> tasks;
        public final ManualArrayList<ScheduleTask> criticalPath;
        public final String approximationSummary;
        public final AlgorithmResult algorithmResult;

        public ScheduleResult(int projectDuration, ManualArrayList<ScheduleTask> tasks,
                              ManualArrayList<ScheduleTask> criticalPath,
                              String approximationSummary, AlgorithmResult algorithmResult) {
            this.projectDuration = projectDuration;
            this.tasks = tasks;
            this.criticalPath = criticalPath;
            this.approximationSummary = approximationSummary;
            this.algorithmResult = algorithmResult;
        }
    }

    public static ScheduleResult calculate(ManualArrayList<ScheduleTask> tasks, int numWorkers) {
        ExecutionTimer timer = new ExecutionTimer();
        OperationCounter counter = new OperationCounter();
        timer.start();

        int n = tasks.size();
        ManualArrayList<String> traces = new ManualArrayList<>();

        if (n == 0) {
            timer.stop();
            AlgorithmResult emptyRes = new AlgorithmResult(
                    "Critical Path Method (CPM)", "Duration: 0 days", "No tasks provided.",
                    0, "0 ns", 0, 0,
                    ComplexityAnalyzer.getComplexity("Critical Path Method (CPM)"), traces);
            return new ScheduleResult(0, tasks, new ManualArrayList<>(), "No tasks.", emptyRes);
        }

        // Build adjacency list for forward and backward passes
        // Directed edge: dependency -> task
        DirectedGraph graph = new DirectedGraph(n);
        for (int i = 0; i < n; i++) {
            ScheduleTask task = tasks.get(i);
            ManualArrayList<Integer> deps = task.getDependencies();
            for (int d = 0; d < deps.size(); d++) {
                int depId = deps.get(d);
                if (depId >= 0 && depId < n) {
                    graph.addEdge(depId, i, task.getDuration());
                }
            }
        }

        // 1. Topological Sort
        ManualArrayList<Integer> topo = graph.topologicalSort();
        if (topo.size() != n) {
            timer.stop();
            traces.add("Error: Cycle detected in task dependency graph! Scheduling is impossible.");
            AlgorithmResult errorRes = new AlgorithmResult(
                    "Critical Path Method (CPM)", "Cycle Detected", "Dependency graph contains circular constraints.",
                    timer.getElapsedNanos(), timer.getFormattedTime(), 0, 0,
                    ComplexityAnalyzer.getComplexity("Critical Path Method (CPM)"), traces);
            return new ScheduleResult(-1, tasks, new ManualArrayList<>(), "Cycle detected", errorRes);
        }

        traces.add("Topological order computed: " + topo.toString());

        // 2. Forward Pass: Earliest Start (ES) and Earliest Finish (EF)
        for (int i = 0; i < n; i++) {
            tasks.get(i).setEarlyStart(0);
            tasks.get(i).setEarlyFinish(tasks.get(i).getDuration());
        }

        int projectDuration = 0;
        for (int i = 0; i < topo.size(); i++) {
            int u = topo.get(i);
            ScheduleTask taskU = tasks.get(u);
            taskU.setEarlyFinish(taskU.getEarlyStart() + taskU.getDuration());
            if (taskU.getEarlyFinish() > projectDuration) {
                projectDuration = taskU.getEarlyFinish();
            }

            ManualArrayList<GraphEdge> edges = graph.getEdges(u);
            for (int e = 0; e < edges.size(); e++) {
                counter.incrementOperations();
                int v = edges.get(e).getTo();
                ScheduleTask taskV = tasks.get(v);
                if (taskU.getEarlyFinish() > taskV.getEarlyStart()) {
                    taskV.setEarlyStart(taskU.getEarlyFinish());
                }
            }
        }
        traces.add("Forward pass complete. Total Project Duration = " + projectDuration + " time units.");

        // 3. Backward Pass: Latest Start (LS) and Latest Finish (LF)
        for (int i = 0; i < n; i++) {
            tasks.get(i).setLateFinish(projectDuration);
            tasks.get(i).setLateStart(projectDuration - tasks.get(i).getDuration());
        }

        for (int i = topo.size() - 1; i >= 0; i--) {
            int u = topo.get(i);
            ScheduleTask taskU = tasks.get(u);

            ManualArrayList<GraphEdge> edges = graph.getEdges(u);
            int minSuccLS = projectDuration;
            for (int e = 0; e < edges.size(); e++) {
                counter.incrementOperations();
                int v = edges.get(e).getTo();
                ScheduleTask taskV = tasks.get(v);
                if (taskV.getLateStart() < minSuccLS) {
                    minSuccLS = taskV.getLateStart();
                }
            }
            if (edges.size() > 0) {
                taskU.setLateFinish(minSuccLS);
            }
            taskU.setLateStart(taskU.getLateFinish() - taskU.getDuration());
            int slack = taskU.getLateStart() - taskU.getEarlyStart();
            taskU.setSlack(slack);
            taskU.setCritical(slack == 0);
        }

        // 4. Identify Critical Path tasks
        ManualArrayList<ScheduleTask> criticalPath = new ManualArrayList<>();
        StringBuilder critPathStr = new StringBuilder();
        for (int i = 0; i < n; i++) {
            ScheduleTask t = tasks.get(i);
            if (t.isCritical()) {
                criticalPath.add(t);
                critPathStr.append(t.getName()).append(" (Id: ").append(t.getId()).append(") -> ");
            }
        }
        if (critPathStr.length() > 4) {
            critPathStr.setLength(critPathStr.length() - 4);
        }
        traces.add("Critical Path: " + critPathStr.toString());

        // 5. NP-Hard Scheduling Approximation (Graham's List Scheduling Heuristic)
        // Graham (1966) proves List Scheduling achieves <= (2 - 1/m) * OPT makespan
        int workers = Math.max(numWorkers, 1);
        int[] machineBusyUntil = new int[workers];
        int totalWork = 0;
        for (int i = 0; i < n; i++) {
            totalWork += tasks.get(i).getDuration();
        }

        // Greedily assign tasks in topological order to earliest available machine
        for (int i = 0; i < topo.size(); i++) {
            int u = topo.get(i);
            int duration = tasks.get(u).getDuration();
            int earliestMachine = 0;
            for (int m = 1; m < workers; m++) {
                if (machineBusyUntil[m] < machineBusyUntil[earliestMachine]) {
                    earliestMachine = m;
                }
            }
            // Earliest start time must also satisfy task's precedence constraints
            int startAfter = Math.max(machineBusyUntil[earliestMachine], tasks.get(u).getEarlyStart());
            machineBusyUntil[earliestMachine] = startAfter + duration;
        }

        int approxMakespan = 0;
        for (int m = 0; m < workers; m++) {
            if (machineBusyUntil[m] > approxMakespan) {
                approxMakespan = machineBusyUntil[m];
            }
        }

        double approxBound = (2.0 - (1.0 / workers));
        String approxSummary = String.format(
                "Graham's List Scheduling on %d parallel machines:\n" +
                "- Unconstrained CPM Duration (infinite machines): %d time units\n" +
                "- Heuristic Makespan with %d machines: %d time units\n" +
                "- Theoretical Approximation Ratio: <= (2 - 1/m) = %.2fx OPT\n" +
                "- Total Workload: %d units across tasks.",
                workers, projectDuration, workers, approxMakespan, approxBound, totalWork
        );
        traces.add(approxSummary);

        timer.stop();

        AlgorithmResult algoRes = new AlgorithmResult(
                "Critical Path Method (CPM)",
                "Duration: " + projectDuration + " units | Critical Tasks: " + criticalPath.size(),
                "Project scheduling evaluated via DAG topological traversal with Graham List approximation.",
                timer.getElapsedNanos(),
                timer.getFormattedTime(),
                counter.getComparisons(),
                counter.getOperations(),
                ComplexityAnalyzer.getComplexity("Critical Path Method (CPM)"),
                traces
        );

        return new ScheduleResult(projectDuration, tasks, criticalPath, approxSummary, algoRes);
    }
}
