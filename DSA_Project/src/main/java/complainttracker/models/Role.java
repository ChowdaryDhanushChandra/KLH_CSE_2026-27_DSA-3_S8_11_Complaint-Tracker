package complainttracker.models;

public enum Role {
    ADMINISTRATOR("Administrator"),
    CUSTOMER("Customer");

    private final String title;

    Role(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static Role fromString(String roleStr) {
        if (roleStr == null) return CUSTOMER;
        for (Role r : Role.values()) {
            if (r.name().equalsIgnoreCase(roleStr.trim()) || r.title.equalsIgnoreCase(roleStr.trim())) {
                return r;
            }
        }
        return CUSTOMER;
    }

    @Override
    public String toString() {
        return title;
    }
}
