package complainttracker.models;

public enum Status {
    NEW("New", "#3B82F6"),
    PENDING("Pending", "#F59E0B"),
    IN_PROGRESS("In Progress", "#8B5CF6"),
    RESOLVED("Resolved", "#10B981"),
    ESCALATED("Escalated", "#DC2626"),
    REJECTED("Rejected", "#6B7280");

    private final String displayName;
    private final String colorHex;

    Status(String displayName, String colorHex) {
        this.displayName = displayName;
        this.colorHex = colorHex;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColorHex() {
        return colorHex;
    }

    public static Status fromString(String text) {
        if (text == null) return NEW;
        for (Status s : Status.values()) {
            if (s.name().equalsIgnoreCase(text.trim()) || s.displayName.equalsIgnoreCase(text.trim())) {
                return s;
            }
        }
        return NEW;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
