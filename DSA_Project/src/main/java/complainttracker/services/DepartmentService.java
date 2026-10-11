package complainttracker.services;

import complainttracker.algorithms.NetworkFlowAssignment;
import complainttracker.algorithms.NetworkFlowAssignment.AssignmentResult;
import complainttracker.data.DatabaseHandler;
import complainttracker.models.Complaint;
import complainttracker.models.Department;
import complainttracker.models.Status;

import java.util.ArrayList;
import java.util.List;

public class DepartmentService {
    private static DepartmentService instance;

    private DepartmentService() {}

    public static synchronized DepartmentService getInstance() {
        if (instance == null) {
            instance = new DepartmentService();
        }
        return instance;
    }

    public List<Department> getAllDepartments() {
        return DatabaseHandler.getInstance().getDepartments();
    }

    public Department getDepartmentById(String id) {
        return DatabaseHandler.getInstance().findDepartmentById(id);
    }

    public AssignmentResult runNetworkFlowAllocation() {
        List<Complaint> unassigned = new ArrayList<>();
        for (Complaint c : DatabaseHandler.getInstance().getComplaints()) {
            if (c.getStatus() != Status.RESOLVED && c.getStatus() != Status.REJECTED) {
                // If unassigned or pending assignment
                if (c.getDepartmentId() == null || c.getDepartmentId().isEmpty() || "DEP-GEN".equalsIgnoreCase(c.getDepartmentId())) {
                    unassigned.add(c);
                }
            }
        }

        List<Department> departments = DatabaseHandler.getInstance().getDepartments();
        AssignmentResult result = NetworkFlowAssignment.allocateComplaints(unassigned, departments);

        // Apply allocations
        for (NetworkFlowAssignment.ComplaintAssignment ca : result.assignments) {
            ca.complaint.setDepartmentId(ca.department.getId());
            ca.complaint.setDepartmentName(ca.department.getName());
            if (ca.complaint.getStatus() == Status.NEW) {
                ca.complaint.setStatus(Status.PENDING);
            }
            ComplaintService.getInstance().updateComplaint(ca.complaint, "Auto-Assigned via Max-Flow",
                    "Network Flow Engine", "Assigned to department: " + ca.department.getName());
        }

        DatabaseHandler.getInstance().recalculateDepartmentLoads();
        return result;
    }
}
