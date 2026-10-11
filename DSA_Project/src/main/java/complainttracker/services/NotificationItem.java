package complainttracker.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NotificationItem {
    private final String id;
    private final String title;
    private final String message;
    private final LocalDateTime timestamp;
    private final String severity; // INFO, WARNING, CRITICAL, SUCCESS
    private final String relatedComplaintId;
    private boolean read;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMM dd, HH:mm");

    public NotificationItem(String id, String title, String message, LocalDateTime timestamp,
                            String severity, String relatedComplaintId) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
        this.severity = severity != null ? severity : "INFO";
        this.relatedComplaintId = relatedComplaintId;
        this.read = false;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getFormattedTimestamp() { return timestamp.format(FORMATTER); }
    public String getSeverity() { return severity; }
    public String getRelatedComplaintId() { return relatedComplaintId; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
}
