package complainttracker.models;

public enum Category {
    WATER("Water Supply", "DEP-WAT"),
    ELECTRICITY("Electricity & Power", "DEP-ELE"),
    ROADS("Roads & Infrastructure", "DEP-ROA"),
    SANITATION("Sanitation & Waste", "DEP-SAN"),
    TRANSPORT("Public Transport", "DEP-TRA"),
    BILLING("Billing & Finance", "DEP-BIL"),
    IT_SERVICES("IT & Digital Services", "DEP-ITS"),
    OTHER("Other / General", "DEP-GEN");

    private final String displayName;
    private final String defaultDeptId;

    Category(String displayName, String defaultDeptId) {
        this.displayName = displayName;
        this.defaultDeptId = defaultDeptId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDefaultDeptId() {
        return defaultDeptId;
    }

    public static Category fromString(String text) {
        if (text == null) return OTHER;
        for (Category c : Category.values()) {
            if (c.name().equalsIgnoreCase(text.trim()) || c.displayName.equalsIgnoreCase(text.trim())) {
                return c;
            }
        }
        return OTHER;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
