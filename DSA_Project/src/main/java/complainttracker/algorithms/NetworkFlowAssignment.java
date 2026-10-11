package complainttracker.algorithms;

import complainttracker.models.Complaint;
import complainttracker.models.Department;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Network Flow (Edmonds-Karp BFS Augmenting Path)
 * Solves the Department Workload Assignment & Capacity Balancing Problem.
 *
 * Source (S) -> Complaints (cap 1) -> Compatible Departments (cap 1) -> Sink (T) (cap = remaining capacity)
 */
public class NetworkFlowAssignment {

    public static class AssignmentResult {
        public final int totalAssigned;
        public final List<ComplaintAssignment> assignments;
        public final List<String> executionSteps;
        public final int maxFlowValue;

        public AssignmentResult(int totalAssigned, List<ComplaintAssignment> assignments, List<String> executionSteps, int maxFlowValue) {
            this.totalAssigned = totalAssigned;
            this.assignments = assignments;
            this.executionSteps = executionSteps;
            this.maxFlowValue = maxFlowValue;
        }
    }

    public static class ComplaintAssignment {
        public final Complaint complaint;
        public final Department department;

        public ComplaintAssignment(Complaint complaint, Department department) {
            this.complaint = complaint;
            this.department = department;
        }
    }

    public static AssignmentResult allocateComplaints(List<Complaint> unassignedComplaints, List<Department> departments) {
        List<String> steps = new ArrayList<>();
        List<ComplaintAssignment> assignments = new ArrayList<>();

        if (unassignedComplaints == null || unassignedComplaints.isEmpty() || departments == null || departments.isEmpty()) {
            steps.add("No unassigned complaints or departments available.");
            return new AssignmentResult(0, assignments, steps, 0);
        }

        int numComplaints = unassignedComplaints.size();
        int numDepts = departments.size();

        // Node indexing:
        // 0: Source (S)
        // 1 .. numComplaints: Complaint nodes
        // numComplaints + 1 .. numComplaints + numDepts: Department nodes
        // Total nodes - 1: Sink (T)
        int totalNodes = 2 + numComplaints + numDepts;
        int source = 0;
        int sink = totalNodes - 1;

        int[][] capacity = new int[totalNodes][totalNodes];

        steps.add(String.format("Constructing Flow Network: Source(0), %d Complaints(1..%d), %d Departments(%d..%d), Sink(%d)",
                numComplaints, numComplaints, numDepts, numComplaints + 1, numComplaints + numDepts, sink));

        // 1. Source -> Complaints (capacity = 1 each)
        for (int i = 0; i < numComplaints; i++) {
            int compNode = 1 + i;
            capacity[source][compNode] = 1;
        }

        // 2. Complaints -> Compatible Departments
        for (int i = 0; i < numComplaints; i++) {
            int compNode = 1 + i;
            Complaint c = unassignedComplaints.get(i);

            for (int j = 0; j < numDepts; j++) {
                Department d = departments.get(j);
                int deptNode = 1 + numComplaints + j;

                // Compatible if category matches, or if department is general/backup
                boolean isCompatible = (d.getCategoryServed() != null && c.getCategory() == d.getCategoryServed())
                        || (d.getId() != null && d.getId().equals(c.getDepartmentId()))
                        || "DEP-GEN".equalsIgnoreCase(d.getId());

                if (isCompatible) {
                    capacity[compNode][deptNode] = 1;
                }
            }
        }

        // 3. Departments -> Sink (capacity = available capacity)
        for (int j = 0; j < numDepts; j++) {
            Department d = departments.get(j);
            int deptNode = 1 + numComplaints + j;
            int availableCap = Math.max(0, d.getCapacity() - d.getCurrentLoad());
            capacity[deptNode][sink] = availableCap;
            steps.add(String.format("Department [%s] (%s): Capacity = %d, Current Load = %d, Residual Available = %d",
                    d.getId(), d.getName(), d.getCapacity(), d.getCurrentLoad(), availableCap));
        }

        // Residual capacity graph
        int[][] residual = new int[totalNodes][totalNodes];
        for (int i = 0; i < totalNodes; i++) {
            System.arraycopy(capacity[i], 0, residual[i], 0, totalNodes);
        }

        // Edmonds-Karp Max Flow
        int maxFlow = 0;
        int[] parent = new int[totalNodes];
        int iteration = 0;

        while (bfs(residual, source, sink, parent)) {
            iteration++;
            // Find bottleneck capacity along augmenting path
            int pathFlow = Integer.MAX_VALUE;
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                pathFlow = Math.min(pathFlow, residual[u][v]);
            }

            // Update residual capacities
            StringBuilder pathStr = new StringBuilder();
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                residual[u][v] -= pathFlow;
                residual[v][u] += pathFlow;
                pathStr.insert(0, " -> " + getNodeLabel(v, numComplaints, numDepts));
            }
            pathStr.insert(0, "Source");

            maxFlow += pathFlow;
            steps.add(String.format("Iteration %d: Found Augmenting Path [%s] with Flow = %d. Total Max Flow = %d",
                    iteration, pathStr.toString(), pathFlow, maxFlow));
        }

        // Extract assignments from flow on edges: compNode -> deptNode
        for (int i = 0; i < numComplaints; i++) {
            int compNode = 1 + i;
            for (int j = 0; j < numDepts; j++) {
                int deptNode = 1 + numComplaints + j;
                // If capacity was 1 and residual is 0, flow was pushed!
                if (capacity[compNode][deptNode] > 0 && residual[compNode][deptNode] == 0) {
                    Complaint assignedComp = unassignedComplaints.get(i);
                    Department assignedDept = departments.get(j);
                    assignments.add(new ComplaintAssignment(assignedComp, assignedDept));
                    steps.add(String.format("Allocated Complaint [%s - %s] -> Department [%s - %s]",
                            assignedComp.getId(), assignedComp.getTitle(), assignedDept.getId(), assignedDept.getName()));
                    break;
                }
            }
        }

        steps.add(String.format("Max-Flow Allocation Completed: %d/%d complaints successfully assigned.",
                assignments.size(), numComplaints));

        return new AssignmentResult(assignments.size(), assignments, steps, maxFlow);
    }

    private static boolean bfs(int[][] residual, int source, int sink, int[] parent) {
        Arrays.fill(parent, -1);
        parent[source] = source;

        CustomQueue<Integer> q = new CustomQueue<>(residual.length);
        q.enqueue(source);

        while (!q.isEmpty()) {
            int u = q.dequeue();

            for (int v = 0; v < residual.length; v++) {
                if (parent[v] == -1 && residual[u][v] > 0) {
                    parent[v] = u;
                    if (v == sink) return true;
                    q.enqueue(v);
                }
            }
        }
        return false;
    }

    private static String getNodeLabel(int node, int numComplaints, int numDepts) {
        if (node == 0) return "S";
        if (node == 1 + numComplaints + numDepts) return "T";
        if (node <= numComplaints) return "C" + node;
        return "D" + (node - numComplaints);
    }
}
