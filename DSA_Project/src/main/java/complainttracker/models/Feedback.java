package complainttracker.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Feedback {
    private String complaintId;
    private int rating; // 1 to 5
    private String comments;
    private LocalDateTime submittedAt;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Feedback() {
        this.submittedAt = LocalDateTime.now();
    }

    public Feedback(String complaintId, int rating, String comments) {
        this.complaintId = complaintId;
        this.rating = Math.max(1, Math.min(5, rating));
        this.comments = comments;
        this.submittedAt = LocalDateTime.now();
    }

    public String getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = Math.max(1, Math.min(5, rating));
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getFormattedSubmittedAt() {
        return submittedAt != null ? submittedAt.format(FORMATTER) : "";
    }

    public String getStarDisplay() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rating; i++) sb.append("★");
        for (int i = rating; i < 5; i++) sb.append("☆");
        return sb.toString();
    }

    @Override
    public String toString() {
        return getStarDisplay() + " (" + rating + "/5): " + comments;
    }
}
