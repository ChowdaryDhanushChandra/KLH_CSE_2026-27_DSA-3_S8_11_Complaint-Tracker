package complainttracker.services;

import complainttracker.algorithms.CustomHashMap;
import complainttracker.algorithms.CustomPriorityQueue;
import complainttracker.data.DatabaseHandler;
import complainttracker.models.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ComplaintService {
    private static ComplaintService instance;

    // Custom DSA structures maintained for real-time operations
    private final CustomHashMap<String, Complaint> idIndex = new CustomHashMap<>();
    private final CustomPriorityQueue<Complaint> priorityHeap = new CustomPriorityQueue<>();

    private ComplaintService() {
        refreshIndexes();
    }

    public static synchronized ComplaintService getInstance() {
        if (instance == null) {
            instance = new ComplaintService();
        }
        return instance;
    }

    public synchronized void refreshIndexes() {
        idIndex.clear();
        priorityHeap.clear();

        List<Complaint> all = DatabaseHandler.getInstance().getComplaints();
        for (Complaint c : all) {
            idIndex.put(c.getId(), c);
            if (c.getStatus() != Status.RESOLVED && c.getStatus() != Status.REJECTED) {
                priorityHeap.insert(c);
            }
        }
    }

    public CustomHashMap<String, Complaint> getIdIndex() {
        return idIndex;
    }

    public CustomPriorityQueue<Complaint> getPriorityHeap() {
        return priorityHeap;
    }

    public List<Complaint> getAllComplaints() {
        return DatabaseHandler.getInstance().getComplaints();
    }

    public Complaint getComplaintById(String id) {
        if (id == null) return null;
        // O(1) custom hash map lookup!
        Complaint c = idIndex.get(id);
        if (c != null) return c;
        return DatabaseHandler.getInstance().findComplaintById(id);
    }

    public void registerComplaint(Complaint complaint) {
        String actor = AuthService.getInstance().isLoggedIn() ?
                AuthService.getInstance().getCurrentUser().getFullName() : "Anonymous Citizen";

        DatabaseHandler.getInstance().addComplaint(complaint, actor);
        refreshIndexes();
    }

    public void updateComplaint(Complaint complaint, String action, String notes) {
        String actor = AuthService.getInstance().isLoggedIn() ?
                AuthService.getInstance().getCurrentUser().getFullName() : "System Admin";
        updateComplaint(complaint, action, actor, notes);
    }

    public void updateComplaint(Complaint complaint, String action, String userActor, String notes) {
        DatabaseHandler.getInstance().updateComplaint(complaint, action, userActor, notes);
        refreshIndexes();
    }

    public void resolveComplaint(String complaintId, int rating, String feedbackComment, String resolutionNotes) {
        Complaint c = getComplaintById(complaintId);
        if (c != null) {
            c.setStatus(Status.RESOLVED);
            c.setResolvedAt(LocalDateTime.now());
            c.setRating(rating);
            c.setFeedback(feedbackComment);

            String actor = AuthService.getInstance().isLoggedIn() ?
                    AuthService.getInstance().getCurrentUser().getFullName() : "Support Agent";

            DatabaseHandler.getInstance().updateComplaint(c, "Resolved", actor,
                    "Complaint resolved. Citizen rating: " + rating + "★. " + resolutionNotes);
            refreshIndexes();
        }
    }

    public void escalateComplaint(String complaintId, String reason) {
        Complaint c = getComplaintById(complaintId);
        if (c != null) {
            c.setStatus(Status.ESCALATED);
            c.setPriority(Priority.CRITICAL);
            c.setEscalationLevel(c.getEscalationLevel() + 1);

            String actor = AuthService.getInstance().isLoggedIn() ?
                    AuthService.getInstance().getCurrentUser().getFullName() : "Supervisor";

            DatabaseHandler.getInstance().updateComplaint(c, "Escalated", actor,
                    "Escalated to Level " + c.getEscalationLevel() + ". Reason: " + reason);
            refreshIndexes();
        }
    }

    public void deleteComplaint(String complaintId) {
        String actor = AuthService.getInstance().isLoggedIn() ?
                AuthService.getInstance().getCurrentUser().getFullName() : "Administrator";

        DatabaseHandler.getInstance().deleteComplaint(complaintId, actor);
        refreshIndexes();
    }

    /**
     * Dequeues the highest priority complaint from CustomPriorityQueue for immediate processing.
     */
    public Complaint processNextHighestPriority(List<CustomPriorityQueue.HeapStep> traceList) {
        Complaint next = priorityHeap.pollWithTrace(traceList);
        if (next != null) {
            if (next.getStatus() == Status.NEW || next.getStatus() == Status.PENDING) {
                next.setStatus(Status.IN_PROGRESS);
                updateComplaint(next, "Started Processing", "Priority Dispatcher",
                        "Dequeued from Priority Queue as highest priority item.");
            }
        }
        return next;
    }

    public List<Complaint> filterComplaints(Category category, Priority priority, Status status, Boolean slaBreachedOnly) {
        List<Complaint> result = new ArrayList<>();
        for (Complaint c : getAllComplaints()) {
            if (category != null && c.getCategory() != category) continue;
            if (priority != null && c.getPriority() != priority) continue;
            if (status != null && c.getStatus() != status) continue;
            if (slaBreachedOnly != null && slaBreachedOnly && !c.isSlaBreached()) continue;
            result.add(c);
        }
        return result;
    }

    public List<Complaint> getComplaintsForUser(User user) {
        if (user == null || user.isAdmin()) {
            return getAllComplaints();
        }
        List<Complaint> list = new ArrayList<>();
        String uId = user.getId();
        String uName = user.getFullName();
        String uPhone = user.getPhone();
        String uEmail = user.getEmail();

        for (Complaint c : getAllComplaints()) {
            boolean match = (c.getCustomerUserId() != null && c.getCustomerUserId().equalsIgnoreCase(uId))
                    || (c.getCustomerName() != null && c.getCustomerName().equalsIgnoreCase(uName))
                    || (c.getCustomerContact() != null && (c.getCustomerContact().equalsIgnoreCase(uPhone) || c.getCustomerContact().equalsIgnoreCase(uEmail)));
            if (match) {
                list.add(c);
            }
        }
        return list;
    }

    public String getQueuePosition(Complaint complaint) {
        if (complaint == null || complaint.getStatus() == Status.RESOLVED || complaint.getStatus() == Status.REJECTED) {
            return "N/A (Resolved/Closed)";
        }
        List<Complaint> items = priorityHeap.toList();
        items.sort(Complaint::compareTo);
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId().equalsIgnoreCase(complaint.getId())) {
                return "#" + (i + 1) + " in Active Queue";
            }
        }
        return "Dispatched / In Progress";
    }

    public String calculateSlaRisk(Complaint complaint) {
        if (complaint == null) return "UNKNOWN";
        if (complaint.isSlaBreached()) return "BREACHED";
        if (complaint.getStatus() == Status.RESOLVED) return "RESOLVED (ON TIME)";

        long remainingMinutes = java.time.Duration.between(LocalDateTime.now(), complaint.getSlaDeadline()).toMinutes();
        if (remainingMinutes <= 360) {
            return "CRITICAL RISK (< 6h)";
        } else if (remainingMinutes <= 720) {
            return "HIGH RISK (< 12h)";
        } else if (remainingMinutes <= 1440) {
            return "MODERATE RISK";
        }
        return "LOW (ON TRACK)";
    }

    public void addCitizenComment(String complaintId, String comment) {
        Complaint c = getComplaintById(complaintId);
        if (c != null) {
            User u = AuthService.getInstance().getCurrentUser();
            String actor = u != null ? u.getFullName() : "Citizen";
            DatabaseHandler.getInstance().addHistory(complaintId, "Citizen Follow-Up / Additional Info",
                    actor, comment);
            c.setUpdatedAt(LocalDateTime.now());
            DatabaseHandler.getInstance().saveToFile();
            NotificationService.getInstance().refreshNotifications();
        }
    }

    public void addAdminInternalNote(String complaintId, String internalNote) {
        Complaint c = getComplaintById(complaintId);
        if (c != null) {
            User u = AuthService.getInstance().getCurrentUser();
            String actor = u != null ? u.getFullName() : "Administrator";
            DatabaseHandler.getInstance().addHistory(complaintId, "Internal Operational Note",
                    actor, "[INTERNAL ONLY] " + internalNote);
            c.setUpdatedAt(LocalDateTime.now());
            DatabaseHandler.getInstance().saveToFile();
            NotificationService.getInstance().refreshNotifications();
        }
    }
}
