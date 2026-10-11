package complainttracker.models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Complaint implements Comparable<Complaint> {
    private String id;
    private String customerName;
    private String customerContact;
    private String customerUserId;
    private String title;
    private String description;
    private Category category;
    private Priority priority;
    private Status status;
    private String departmentId;
    private String departmentName;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private int slaHours;
    private int escalationLevel;
    private int rating; // 0 = unrated, 1-5
    private String feedback;
    private String assignedStaff;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Complaint() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = Status.NEW;
        this.priority = Priority.MEDIUM;
        this.category = Category.OTHER;
        this.slaHours = this.priority.getDefaultSlaHours();
        this.escalationLevel = 0;
        this.rating = 0;
    }

    public Complaint(String id, String customerName, String customerContact, String customerUserId,
                     String title, String description, Category category, Priority priority,
                     String departmentId, String departmentName, String location) {
        this.id = id;
        this.customerName = customerName;
        this.customerContact = customerContact;
        this.customerUserId = customerUserId;
        this.title = title;
        this.description = description;
        this.category = category != null ? category : Category.OTHER;
        this.priority = priority != null ? priority : Priority.MEDIUM;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.location = location;
        this.status = Status.NEW;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.slaHours = this.priority.getDefaultSlaHours();
        this.escalationLevel = 0;
        this.rating = 0;
    }

    // Full constructor for storage loading
    public Complaint(String id, String customerName, String customerContact, String customerUserId,
                     String title, String description, Category category, Priority priority,
                     Status status, String departmentId, String departmentName, String location,
                     LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime resolvedAt,
                     int slaHours, int escalationLevel, int rating, String feedback, String assignedStaff) {
        this.id = id;
        this.customerName = customerName;
        this.customerContact = customerContact;
        this.customerUserId = customerUserId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.status = status;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.location = location;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.resolvedAt = resolvedAt;
        this.slaHours = slaHours;
        this.escalationLevel = escalationLevel;
        this.rating = rating;
        this.feedback = feedback;
        this.assignedStaff = assignedStaff;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerContact() {
        return customerContact;
    }

    public void setCustomerContact(String customerContact) {
        this.customerContact = customerContact;
    }

    public String getCustomerUserId() {
        return customerUserId;
    }

    public void setCustomerUserId(String customerUserId) {
        this.customerUserId = customerUserId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
        if (priority != null && this.slaHours == 0) {
            this.slaHours = priority.getDefaultSlaHours();
        }
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
        if (status == Status.RESOLVED && this.resolvedAt == null) {
            this.resolvedAt = LocalDateTime.now();
        }
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public int getSlaHours() {
        return slaHours;
    }

    public void setSlaHours(int slaHours) {
        this.slaHours = slaHours;
    }

    public int getEscalationLevel() {
        return escalationLevel;
    }

    public void setEscalationLevel(int escalationLevel) {
        this.escalationLevel = escalationLevel;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getAssignedStaff() {
        return assignedStaff;
    }

    public void setAssignedStaff(String assignedStaff) {
        this.assignedStaff = assignedStaff;
    }

    public String getFormattedCreatedAt() {
        return createdAt != null ? createdAt.format(FORMATTER) : "";
    }

    public String getFormattedUpdatedAt() {
        return updatedAt != null ? updatedAt.format(FORMATTER) : "";
    }

    public String getFormattedResolvedAt() {
        return resolvedAt != null ? resolvedAt.format(FORMATTER) : "N/A";
    }

    public LocalDateTime getSlaDeadline() {
        if (createdAt == null) return LocalDateTime.now();
        return createdAt.plusHours(slaHours);
    }

    public String getFormattedDeadline() {
        return getSlaDeadline().format(FORMATTER);
    }

    public boolean isSlaBreached() {
        if (createdAt == null) return false;
        LocalDateTime deadline = getSlaDeadline();
        if (status == Status.RESOLVED) {
            return resolvedAt != null && resolvedAt.isAfter(deadline);
        }
        return LocalDateTime.now().isAfter(deadline);
    }

    public long getAgeHours() {
        if (createdAt == null) return 0;
        LocalDateTime end = (status == Status.RESOLVED && resolvedAt != null) ? resolvedAt : LocalDateTime.now();
        return Math.max(0, Duration.between(createdAt, end).toHours());
    }

    public double getSlaPercentage() {
        if (createdAt == null || slaHours <= 0) return 0.0;
        long elapsedMinutes;
        if (status == Status.RESOLVED && resolvedAt != null) {
            elapsedMinutes = Duration.between(createdAt, resolvedAt).toMinutes();
        } else {
            elapsedMinutes = Duration.between(createdAt, LocalDateTime.now()).toMinutes();
        }
        double totalMinutes = slaHours * 60.0;
        return Math.min(1.0, Math.max(0.0, elapsedMinutes / totalMinutes));
    }

    public String getSlaRemainingDisplay() {
        if (status == Status.RESOLVED) {
            return isSlaBreached() ? "Resolved (Breached SLA)" : "Resolved (Within SLA)";
        }
        long remainingMinutes = Duration.between(LocalDateTime.now(), getSlaDeadline()).toMinutes();
        if (remainingMinutes <= 0) {
            long overdueHours = Math.abs(remainingMinutes) / 60;
            return "Breached by " + overdueHours + "h";
        }
        long hours = remainingMinutes / 60;
        long mins = remainingMinutes % 60;
        return hours + "h " + mins + "m left";
    }

    /**
     * Natural order for PriorityQueue:
     * 1. Priority level: CRITICAL (1) before HIGH (2) before MEDIUM (3) before LOW (4)
     * 2. Tie-break: older complaints (earlier createdAt) come first!
     */
    @Override
    public int compareTo(Complaint other) {
        if (other == null) return -1;
        int p1 = this.priority != null ? this.priority.getLevel() : 3;
        int p2 = other.priority != null ? other.priority.getLevel() : 3;
        if (p1 != p2) {
            return Integer.compare(p1, p2); // 1 comes before 2 (Min-heap for level number means highest urgency first)
        }
        if (this.createdAt != null && other.createdAt != null) {
            return this.createdAt.compareTo(other.createdAt); // Earlier date comes first
        }
        return this.id != null ? this.id.compareTo(other.id) : 0;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + priority + " - " + title + " (" + status + ")";
    }
}
