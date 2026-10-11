package complainttracker.models;

public class Department {
    private String id;
    private String name;
    private Category categoryServed;
    private int capacity;
    private int currentLoad;
    private int staffCount;
    private double avgResolutionHours;

    public Department() {
    }

    public Department(String id, String name, Category categoryServed, int capacity, int staffCount, double avgResolutionHours) {
        this.id = id;
        this.name = name;
        this.categoryServed = categoryServed;
        this.capacity = capacity;
        this.currentLoad = 0;
        this.staffCount = staffCount;
        this.avgResolutionHours = avgResolutionHours;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategoryServed() {
        return categoryServed;
    }

    public void setCategoryServed(Category categoryServed) {
        this.categoryServed = categoryServed;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCurrentLoad() {
        return currentLoad;
    }

    public void setCurrentLoad(int currentLoad) {
        this.currentLoad = currentLoad;
    }

    public int getStaffCount() {
        return staffCount;
    }

    public void setStaffCount(int staffCount) {
        this.staffCount = staffCount;
    }

    public double getAvgResolutionHours() {
        return avgResolutionHours;
    }

    public void setAvgResolutionHours(double avgResolutionHours) {
        this.avgResolutionHours = avgResolutionHours;
    }

    public double getLoadPercentage() {
        if (capacity <= 0) return 0.0;
        return Math.min(100.0, (double) currentLoad / capacity * 100.0);
    }

    public boolean hasAvailableCapacity() {
        return currentLoad < capacity;
    }

    @Override
    public String toString() {
        return name + " (" + currentLoad + "/" + capacity + ")";
    }
}
