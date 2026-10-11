package complainttracker.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ComplaintHistory {
    private String id;
    private String complaintId;
    private LocalDateTime timestamp;
    private String action;
    private String performedBy;
    private String notes;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ComplaintHistory() {
        this.timestamp = LocalDateTime.now();
    }

    public ComplaintHistory(String id, String complaintId, LocalDateTime timestamp, String action, String performedBy, String notes) {
        this.id = id;
        this.complaintId = complaintId;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
        this.action = action;
        this.performedBy = performedBy;
        this.notes = notes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getFormattedTimestamp() {
        return timestamp != null ? timestamp.format(FORMATTER) : "";
    }

    @Override
    public String toString() {
        return "[" + getFormattedTimestamp() + "] " + action + " by " + performedBy + (notes != null && !notes.isEmpty() ? " - " + notes : "");
    }
}
