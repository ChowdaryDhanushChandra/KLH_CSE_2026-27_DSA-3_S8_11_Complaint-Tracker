package complainttracker.services;

import complainttracker.data.DatabaseHandler;
import complainttracker.models.*;

import java.time.Duration;
import java.util.*;

public class AnalyticsService {
    private static AnalyticsService instance;

    private AnalyticsService() {}

    public static synchronized AnalyticsService getInstance() {
        if (instance == null) {
            instance = new AnalyticsService();
        }
        return instance;
    }

    public static class SystemStats {
        public int totalComplaints;
        public int newCount;
        public int pendingCount;
        public int inProgressCount;
        public int resolvedCount;
        public int escalatedCount;
        public int rejectedCount;
        public int criticalCount;
        public int highCount;
        public int mediumCount;
        public int lowCount;
        public int slaBreachedCount;
        public double slaComplianceRate; // %
        public double avgResolutionHours;
        public double avgCustomerRating;
        public int[] ratingCounts = new int[6]; // index 1..5
        public Map<Category, Integer> categoryDistribution = new LinkedHashMap<>();
        public Map<String, Integer> departmentDistribution = new LinkedHashMap<>();
    }

    public SystemStats calculateStats() {
        SystemStats s = new SystemStats();
        List<Complaint> list = DatabaseHandler.getInstance().getComplaints();
        s.totalComplaints = list.size();

        for (Category c : Category.values()) {
            s.categoryDistribution.put(c, 0);
        }

        int resolvedOnTime = 0;
        double totalResHours = 0;
        int resolvedWithHours = 0;
        int totalRatingsSum = 0;
        int ratingSubmissions = 0;

        for (Complaint c : list) {
            // Status counts
            if (c.getStatus() == Status.NEW) s.newCount++;
            else if (c.getStatus() == Status.PENDING) s.pendingCount++;
            else if (c.getStatus() == Status.IN_PROGRESS) s.inProgressCount++;
            else if (c.getStatus() == Status.RESOLVED) s.resolvedCount++;
            else if (c.getStatus() == Status.ESCALATED) s.escalatedCount++;
            else if (c.getStatus() == Status.REJECTED) s.rejectedCount++;

            // Priority counts
            if (c.getPriority() == Priority.CRITICAL) s.criticalCount++;
            else if (c.getPriority() == Priority.HIGH) s.highCount++;
            else if (c.getPriority() == Priority.MEDIUM) s.mediumCount++;
            else if (c.getPriority() == Priority.LOW) s.lowCount++;

            // SLA Breaches
            if (c.isSlaBreached()) {
                s.slaBreachedCount++;
            }

            // Categories
            s.categoryDistribution.put(c.getCategory(), s.categoryDistribution.getOrDefault(c.getCategory(), 0) + 1);

            // Departments
            String dept = c.getDepartmentName() != null ? c.getDepartmentName() : "Unassigned";
            s.departmentDistribution.put(dept, s.departmentDistribution.getOrDefault(dept, 0) + 1);

            // Resolution Time & SLA compliance for resolved complaints
            if (c.getStatus() == Status.RESOLVED) {
                if (!c.isSlaBreached()) {
                    resolvedOnTime++;
                }
                if (c.getCreatedAt() != null && c.getResolvedAt() != null) {
                    long hours = Duration.between(c.getCreatedAt(), c.getResolvedAt()).toHours();
                    totalResHours += hours;
                    resolvedWithHours++;
                }
                if (c.getRating() >= 1 && c.getRating() <= 5) {
                    s.ratingCounts[c.getRating()]++;
                    totalRatingsSum += c.getRating();
                    ratingSubmissions++;
                }
            }
        }

        s.slaComplianceRate = s.resolvedCount > 0 ? ((double) resolvedOnTime / s.resolvedCount) * 100.0 : 100.0;
        s.avgResolutionHours = resolvedWithHours > 0 ? (totalResHours / resolvedWithHours) : 0.0;
        s.avgCustomerRating = ratingSubmissions > 0 ? ((double) totalRatingsSum / ratingSubmissions) : 0.0;

        return s;
    }
}
