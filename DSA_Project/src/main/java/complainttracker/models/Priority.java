package complainttracker.models;

public enum Priority {
    CRITICAL(1, "Critical", "#EF4444", 12),
    HIGH(2, "High", "#F97316", 24),
    MEDIUM(3, "Medium", "#EAB308", 48),
    LOW(4, "Low", "#10B981", 72);

    private final int level;
    private final String displayName;
    private final String colorHex;
    private final int defaultSlaHours;

    Priority(int level, String displayName, String colorHex, int defaultSlaHours) {
        this.level = level;
        this.displayName = displayName;
        this.colorHex = colorHex;
        this.defaultSlaHours = defaultSlaHours;
    }

    public int getLevel() {
        return level;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getTitle() {
        return displayName;
    }

    public String getColorHex() {
        return colorHex;
    }

    public int getDefaultSlaHours() {
        return defaultSlaHours;
    }

    public static Priority fromString(String text) {
        if (text == null) return MEDIUM;
        for (Priority p : Priority.values()) {
            if (p.name().equalsIgnoreCase(text.trim()) || p.displayName.equalsIgnoreCase(text.trim())) {
                return p;
            }
        }
        return MEDIUM;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
