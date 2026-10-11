package complainttracker.data;

import complainttracker.models.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Offline Data Storage & Seeder.
 * Manages local persistence of Complaints, Users, Departments, and History.
 * Stores data safely in `data/complaints_db.json`.
 */
public class DatabaseHandler {
    private static final String DATA_DIR = "data";
    private static final String DB_FILE = "data/complaints_db.json";
    private static final DateTimeFormatter ISO_FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private static DatabaseHandler instance;

    private final List<Complaint> complaints = new ArrayList<>();
    private final List<User> users = new ArrayList<>();
    private final List<Department> departments = new ArrayList<>();
    private final List<ComplaintHistory> historyLogs = new ArrayList<>();

    private DatabaseHandler() {
        ensureDataDirectory();
        if (!loadFromFile()) {
            seedDemoData();
            saveToFile();
        }
    }

    public static synchronized DatabaseHandler getInstance() {
        if (instance == null) {
            instance = new DatabaseHandler();
        }
        return instance;
    }

    private void ensureDataDirectory() {
        try {
            Path dir = Paths.get(DATA_DIR);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not create data directory: " + e.getMessage());
        }
    }

    // ==========================================
    // GETTERS & DATA ACCESS
    // ==========================================

    public List<Complaint> getComplaints() {
        return complaints;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public List<ComplaintHistory> getHistoryLogs() {
        return historyLogs;
    }

    public List<ComplaintHistory> getHistoryForComplaint(String complaintId) {
        List<ComplaintHistory> list = new ArrayList<>();
        if (complaintId == null) return list;
        for (ComplaintHistory h : historyLogs) {
            if (complaintId.equalsIgnoreCase(h.getComplaintId())) {
                list.add(h);
            }
        }
        // Sort newest first
        list.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        return list;
    }

    public Complaint findComplaintById(String id) {
        if (id == null) return null;
        for (Complaint c : complaints) {
            if (id.equalsIgnoreCase(c.getId())) {
                return c;
            }
        }
        return null;
    }

    public Department findDepartmentById(String id) {
        if (id == null) return null;
        for (Department d : departments) {
            if (id.equalsIgnoreCase(d.getId())) {
                return d;
            }
        }
        return null;
    }

    public Department findDepartmentByCategory(Category category) {
        if (category == null) return null;
        for (Department d : departments) {
            if (d.getCategoryServed() == category) {
                return d;
            }
        }
        return null;
    }

    public User authenticate(String username, String password) {
        if (username == null || password == null) return null;
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username.trim()) && u.getPassword().equals(password.trim())) {
                return u;
            }
        }
        return null;
    }

    public void addComplaint(Complaint complaint, String userActor) {
        complaints.add(complaint);
        addHistory(complaint.getId(), "Complaint Registered", userActor,
                "Complaint filed under category " + complaint.getCategory() + " with priority " + complaint.getPriority());
        recalculateDepartmentLoads();
        saveToFile();
    }

    public void updateComplaint(Complaint complaint, String action, String userActor, String notes) {
        complaint.setUpdatedAt(LocalDateTime.now());
        addHistory(complaint.getId(), action, userActor, notes);
        recalculateDepartmentLoads();
        saveToFile();
    }

    public void deleteComplaint(String id, String userActor) {
        Complaint c = findComplaintById(id);
        if (c != null) {
            complaints.remove(c);
            addHistory(id, "Complaint Deleted", userActor, "Complaint removed from system.");
            recalculateDepartmentLoads();
            saveToFile();
        }
    }

    public void addHistory(String complaintId, String action, String performedBy, String notes) {
        String histId = "HIST-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        historyLogs.add(new ComplaintHistory(histId, complaintId, LocalDateTime.now(), action, performedBy, notes));
    }

    public void recalculateDepartmentLoads() {
        for (Department d : departments) {
            int active = 0;
            for (Complaint c : complaints) {
                if (c.getStatus() != Status.RESOLVED && c.getStatus() != Status.REJECTED) {
                    if (d.getId().equalsIgnoreCase(c.getDepartmentId())) {
                        active++;
                    }
                }
            }
            d.setCurrentLoad(active);
        }
    }

    public String generateNextComplaintId() {
        int max = 1000;
        for (Complaint c : complaints) {
            if (c.getId() != null && c.getId().startsWith("CMP-")) {
                try {
                    int num = Integer.parseInt(c.getId().substring(4));
                    if (num > max) max = num;
                } catch (NumberFormatException ignored) {}
            }
        }
        return "CMP-" + (max + 1);
    }

    // ==========================================
    // DEMO DATA GENERATOR (35+ realistic records)
    // ==========================================

    public void seedDemoData() {
        complaints.clear();
        users.clear();
        departments.clear();
        historyLogs.clear();

        // 1. Users
        users.add(new User("USR-01", "admin", "admin123", "Chief Commissioner Sharma", "admin@complaints.gov", "9876543210", Role.ADMINISTRATOR));
        users.add(new User("USR-02", "customer", "cust123", "Priya Venkatesh", "priya.v@gmail.com", "9845012345", Role.CUSTOMER));
        users.add(new User("USR-03", "rajesh", "raj123", "Rajesh Kumar", "rajesh.k@outlook.com", "9812345678", Role.CUSTOMER));
        users.add(new User("USR-04", "staff", "staff123", "Officer Anil Verma", "anil.verma@civic.org", "9823456789", Role.ADMINISTRATOR));

        // 2. Departments with capacities
        Department depWat = new Department("DEP-WAT", "Water Supply & Sewerage Board", Category.WATER, 12, 18, 14.5);
        Department depEle = new Department("DEP-ELE", "State Power & Electricity Board", Category.ELECTRICITY, 10, 15, 8.2);
        Department depRoa = new Department("DEP-ROA", "Public Works & Road Infrastructure", Category.ROADS, 8, 12, 36.0);
        Department depSan = new Department("DEP-SAN", "Solid Waste & Sanitation Wing", Category.SANITATION, 15, 25, 6.0);
        Department depTra = new Department("DEP-TRA", "Metropolitan Transit Department", Category.TRANSPORT, 8, 10, 18.5);
        Department depBil = new Department("DEP-BIL", "Property Tax & Revenue Department", Category.BILLING, 6, 8, 24.0);
        Department depIts = new Department("DEP-ITS", "Digital Governance & IT Operations", Category.IT_SERVICES, 5, 6, 4.5);
        Department depGen = new Department("DEP-GEN", "Public Grievance General Cell", Category.OTHER, 10, 12, 12.0);

        departments.addAll(Arrays.asList(depWat, depEle, depRoa, depSan, depTra, depBil, depIts, depGen));

        LocalDateTime now = LocalDateTime.now();

        // 3. Seeding Realistic Complaints
        addSeed(new Complaint("CMP-1001", "Priya Venkatesh", "9845012345", "USR-02",
                "Severe Main Water Pipe Burst in 4th Block",
                "Drinking water main pipeline burst near park entrance. Huge water wastage and flooding onto main road.",
                Category.WATER, Priority.CRITICAL, Status.IN_PROGRESS, "DEP-WAT", depWat.getName(),
                "4th Block, Koramangala, Bengaluru",
                now.minusHours(5), now.minusHours(2), null, 12, 1, 0, null, "Suresh (Technician)"));

        addSeed(new Complaint("CMP-1002", "Rajesh Kumar", "9812345678", "USR-03",
                "Transformer Sparking and Frequent Blackout",
                "Distribution transformer on 2nd cross emitting sparks and smoke. High risk of electrical fire.",
                Category.ELECTRICITY, Priority.CRITICAL, Status.NEW, "DEP-ELE", depEle.getName(),
                "2nd Cross, Indiranagar, Bengaluru",
                now.minusHours(2), now.minusHours(2), null, 12, 2, 0, null, "Unassigned"));

        addSeed(new Complaint("CMP-1003", "Ananya Hegde", "9741234567", "USR-02",
                "Deep Pothole Hazard near Metro Pillar 142",
                "A 2-foot wide dangerous pothole causing severe two-wheeler skidding during rainy evening peak traffic.",
                Category.ROADS, Priority.HIGH, Status.PENDING, "DEP-ROA", depRoa.getName(),
                "Old Airport Road near Pillar 142",
                now.minusHours(20), now.minusHours(10), null, 24, 0, 0, null, "Ramesh Rao (JE)"));

        addSeed(new Complaint("CMP-1004", "Karthik Sundaram", "9880123456", "USR-03",
                "Overflowing Garbage Bin Spilling on Pedestrian Walkway",
                "Community garbage dumpster has not been cleared for 4 days. Strong foul stench and stray animal menace.",
                Category.SANITATION, Priority.MEDIUM, Status.RESOLVED, "DEP-SAN", depSan.getName(),
                "Opposite City Central Library, Malleshwaram",
                now.minusDays(3), now.minusDays(1), now.minusHours(12), 48, 0, 5,
                "Sanitation team arrived with compactor truck and sanitized the spot. Great work!", "Kavita (Supervisor)"));

        addSeed(new Complaint("CMP-1005", "Sunil Deshmukh", "9900112233", "USR-02",
                "Double Deduction in Water Supply Monthly Bill",
                "Net banking auto-debit charged twice for consumer water account #W-88291 during September billing cycle.",
                Category.BILLING, Priority.MEDIUM, Status.IN_PROGRESS, "DEP-BIL", depBil.getName(),
                "Consumer Account: W-88291, Jayanagar",
                now.minusDays(1), now.minusHours(6), null, 48, 0, 0, null, "Accounts Desk 2"));

        addSeed(new Complaint("CMP-1006", "Deepa Nambiar", "9988776655", "USR-03",
                "Street Lights Not Working for Whole Stretch of 100ft Road",
                "Over 15 street lights non-functional creating a pitch dark accident hazard zone for women commuters at night.",
                Category.ELECTRICITY, Priority.HIGH, Status.PENDING, "DEP-ELE", depEle.getName(),
                "100 Feet Road, 7th Sector, HSR Layout",
                now.minusHours(18), now.minusHours(15), null, 24, 0, 0, null, "Lineman Team A"));

        // SLA Breached Case
        addSeed(new Complaint("CMP-1007", "Manoj Tiwari", "9123456780", "USR-02",
                "Broken Manhole Cover Threatening Vehicles and Pedestrians",
                "Heavy iron manhole lid shattered. Open drainage chamber poses fatal hazard to school children.",
                Category.ROADS, Priority.CRITICAL, Status.ESCALATED, "DEP-ROA", depRoa.getName(),
                "Beside St. Joseph School, Richmond Road",
                now.minusHours(36), now.minusHours(1), null, 12, 2, 0, null, "Chief Road Engineer"));

        // Duplicate Case 1 (High similarity to CMP-1001 for duplicate test)
        addSeed(new Complaint("CMP-1008", "Vikram Rathore", "9870011223", "USR-03",
                "Severe Main Water Pipe Burst in 4th Block Park",
                "Water pipeline is broken and leaking heavily near 4th block entrance park. Road is submerged.",
                Category.WATER, Priority.HIGH, Status.NEW, "DEP-WAT", depWat.getName(),
                "4th Block Park, Koramangala",
                now.minusHours(1), now.minusHours(1), null, 24, 0, 0, null, "Unassigned"));

        // Duplicate Case 2 (High similarity to CMP-1003)
        addSeed(new Complaint("CMP-1009", "Meera Sen", "9765432109", "USR-02",
                "Dangerous Pothole near Metro Pillar 142 on Airport Road",
                "Large crater pothole close to pillar 142. Two-wheelers falling down.",
                Category.ROADS, Priority.HIGH, Status.NEW, "DEP-ROA", depRoa.getName(),
                "Metro Pillar 142, Old Airport Road",
                now.minusMinutes(40), now.minusMinutes(40), null, 24, 0, 0, null, "Unassigned"));

        addSeed(new Complaint("CMP-1010", "Girish Babu", "9844001122", "USR-03",
                "Bus Route #201 Frequency Severely Reduced During Morning Peak",
                "Commuters stranded at bus stop as 3 consecutive buses failed to arrive on schedule.",
                Category.TRANSPORT, Priority.LOW, Status.RESOLVED, "DEP-TRA", depTra.getName(),
                "Silk Board Bus Terminal",
                now.minusDays(4), now.minusDays(2), now.minusDays(1), 72, 0, 4,
                "BMTC added 2 supplementary feeder trips starting 8:30 AM.", "Divisional Traffic Inspector"));

        addSeed(new Complaint("CMP-1011", "Arun Mathew", "9448123456", "USR-02",
                "Sewage Backflow in Residential Basements After Rain",
                "Underground drainage line blocked causing dirty sewage water to seep back into apartment basement parking.",
                Category.WATER, Priority.HIGH, Status.IN_PROGRESS, "DEP-WAT", depWat.getName(),
                "Silver Springs Enclave, Bellandur",
                now.minusHours(14), now.minusHours(4), null, 24, 1, 0, null, "Drainage Suction Team #3"));

        addSeed(new Complaint("CMP-1012", "Sneha Roy", "9341234567", "USR-03",
                "Public Grievance Portal Mobile OTP Not Received",
                "Citizens unable to submit online service requests due to SMS gateway timeout error code 504.",
                Category.IT_SERVICES, Priority.MEDIUM, Status.RESOLVED, "DEP-ITS", depIts.getName(),
                "Online Portal System",
                now.minusDays(2), now.minusHours(30), now.minusHours(22), 48, 0, 5,
                "SMS gateway carrier redundancy restored. Latency back to normal 2.1s.", "DevOps Team"));

        addSeed(new Complaint("CMP-1013", "Farhan Akhtar", "9112233445", "USR-02",
                "Low Voltage Fluctuation Damaging Home Appliances",
                "Voltage dropping below 160V repeatedly every evening. Refrigerator and AC compressors failing.",
                Category.ELECTRICITY, Priority.HIGH, Status.PENDING, "DEP-ELE", depEle.getName(),
                "Sector 3, BTM Layout 2nd Stage",
                now.minusHours(16), now.minusHours(8), null, 24, 0, 0, null, "Grid Maintenance Sub-station"));

        addSeed(new Complaint("CMP-1014", "Preeti Kulkarni", "9223344556", "USR-03",
                "Dead Tree Branch Hanging Dangerously Over Electric Cables",
                "Heavy gust broke eucalyptus bough, resting directly across 11kV overhead wire above sidewalk.",
                Category.ELECTRICITY, Priority.CRITICAL, Status.IN_PROGRESS, "DEP-ELE", depEle.getName(),
                "15th Main Road, Rajajinagar",
                now.minusHours(3), now.minusHours(1), null, 12, 1, 0, null, "Emergency Tree Clearing Crew"));

        addSeed(new Complaint("CMP-1015", "Harish Gowda", "9334455667", "USR-02",
                "Illegal Debris Dumping in Open Stormwater Drain",
                "Construction contractors dumping concrete debris into primary stormwater canal, choking flow.",
                Category.SANITATION, Priority.MEDIUM, Status.NEW, "DEP-SAN", depSan.getName(),
                "Varthur Lake Channel Road",
                now.minusHours(8), now.minusHours(8), null, 48, 0, 0, null, "Unassigned"));

        // Add history logs for seeded complaints
        for (Complaint c : complaints) {
            historyLogs.add(new ComplaintHistory("HIST-INIT-" + c.getId(), c.getId(), c.getCreatedAt(),
                    "Registered", c.getCustomerName(), "Complaint filed in category: " + c.getCategory()));

            if (c.getStatus() == Status.IN_PROGRESS || c.getStatus() == Status.RESOLVED || c.getStatus() == Status.ESCALATED) {
                historyLogs.add(new ComplaintHistory("HIST-PROG-" + c.getId(), c.getId(), c.getUpdatedAt(),
                        "Status Updated to " + c.getStatus(), "Dispatch Officer", "Assigned to: " + c.getAssignedStaff()));
            }

            if (c.getStatus() == Status.RESOLVED) {
                historyLogs.add(new ComplaintHistory("HIST-RES-" + c.getId(), c.getId(), c.getResolvedAt(),
                        "Resolved", c.getAssignedStaff(), "Resolution completed. Citizen rating: " + c.getRating() + " stars"));
            }
        }

        recalculateDepartmentLoads();
    }

    private void addSeed(Complaint complaint) {
        complaints.add(complaint);
    }

    // ==========================================
    // PERSISTENCE: ROBUST SERIALIZATION
    // ==========================================

    public synchronized void saveToFile() {
        try {
            ensureDataDirectory();
            File file = new File(DB_FILE);
            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
                writer.println("# COMPLAINT TRACKER OFFLINE DATABASE");
                writer.println("# Generated: " + LocalDateTime.now().format(ISO_FMT));

                // Save Users
                writer.println("[USERS]");
                for (User u : users) {
                    writer.printf("%s|%s|%s|%s|%s|%s|%s%n",
                            escape(u.getId()), escape(u.getUsername()), escape(u.getPassword()),
                            escape(u.getFullName()), escape(u.getEmail()), escape(u.getPhone()),
                            escape(u.getRole().name()));
                }

                // Save Departments
                writer.println("[DEPARTMENTS]");
                for (Department d : departments) {
                    writer.printf("%s|%s|%s|%d|%d|%.2f%n",
                            escape(d.getId()), escape(d.getName()),
                            escape(d.getCategoryServed() != null ? d.getCategoryServed().name() : "OTHER"),
                            d.getCapacity(), d.getStaffCount(), d.getAvgResolutionHours());
                }

                // Save Complaints
                writer.println("[COMPLAINTS]");
                for (Complaint c : complaints) {
                    writer.printf("%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%d|%d|%d|%s|%s%n",
                            escape(c.getId()), escape(c.getCustomerName()), escape(c.getCustomerContact()),
                            escape(c.getCustomerUserId()), escape(c.getTitle()), escape(c.getDescription()),
                            escape(c.getCategory().name()), escape(c.getPriority().name()), escape(c.getStatus().name()),
                            escape(c.getDepartmentId()), escape(c.getDepartmentName()), escape(c.getLocation()),
                            c.getCreatedAt() != null ? c.getCreatedAt().format(ISO_FMT) : "",
                            c.getUpdatedAt() != null ? c.getUpdatedAt().format(ISO_FMT) : "",
                            c.getResolvedAt() != null ? c.getResolvedAt().format(ISO_FMT) : "",
                            c.getSlaHours(), c.getEscalationLevel(), c.getRating(),
                            escape(c.getFeedback()), escape(c.getAssignedStaff()));
                }

                // Save History
                writer.println("[HISTORY]");
                for (ComplaintHistory h : historyLogs) {
                    writer.printf("%s|%s|%s|%s|%s|%s%n",
                            escape(h.getId()), escape(h.getComplaintId()),
                            h.getTimestamp() != null ? h.getTimestamp().format(ISO_FMT) : "",
                            escape(h.getAction()), escape(h.getPerformedBy()), escape(h.getNotes()));
                }
            }
        } catch (Exception e) {
            System.err.println("Error saving complaints database: " + e.getMessage());
        }
    }

    public synchronized void saveUsers() {
        saveToFile();
    }

    public synchronized boolean loadFromFile() {
        File file = new File(DB_FILE);
        if (!file.exists()) return false;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            complaints.clear();
            users.clear();
            departments.clear();
            historyLogs.clear();

            String line;
            String section = "";

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                if (line.startsWith("[") && line.endsWith("]")) {
                    section = line.toUpperCase();
                    continue;
                }

                String[] parts = line.split("\\|", -1);

                switch (section) {
                    case "[USERS]":
                        if (parts.length >= 7) {
                            users.add(new User(unescape(parts[0]), unescape(parts[1]), unescape(parts[2]),
                                    unescape(parts[3]), unescape(parts[4]), unescape(parts[5]),
                                    Role.fromString(unescape(parts[6]))));
                        }
                        break;
                    case "[DEPARTMENTS]":
                        if (parts.length >= 6) {
                            Department d = new Department(unescape(parts[0]), unescape(parts[1]),
                                    Category.fromString(unescape(parts[2])),
                                    parseInt(parts[3], 10), parseInt(parts[4], 5), parseDouble(parts[5], 24.0));
                            departments.add(d);
                        }
                        break;
                    case "[COMPLAINTS]":
                        if (parts.length >= 20) {
                            Complaint c = new Complaint(
                                    unescape(parts[0]), unescape(parts[1]), unescape(parts[2]), unescape(parts[3]),
                                    unescape(parts[4]), unescape(parts[5]), Category.fromString(unescape(parts[6])),
                                    Priority.fromString(unescape(parts[7])), Status.fromString(unescape(parts[8])),
                                    unescape(parts[9]), unescape(parts[10]), unescape(parts[11]),
                                    parseDate(parts[12]), parseDate(parts[13]), parseDate(parts[14]),
                                    parseInt(parts[15], 24), parseInt(parts[16], 0), parseInt(parts[17], 0),
                                    unescape(parts[18]), unescape(parts[19]));
                            complaints.add(c);
                        }
                        break;
                    case "[HISTORY]":
                        if (parts.length >= 6) {
                            historyLogs.add(new ComplaintHistory(
                                    unescape(parts[0]), unescape(parts[1]), parseDate(parts[2]),
                                    unescape(parts[3]), unescape(parts[4]), unescape(parts[5])));
                        }
                        break;
                }
            }

            recalculateDepartmentLoads();
            return !complaints.isEmpty();
        } catch (Exception e) {
            System.err.println("Failed to load local DB: " + e.getMessage());
            return false;
        }
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("|", "\\p").replace("\n", "\\n").replace("\r", "");
    }

    private static String unescape(String s) {
        if (s == null) return "";
        return s.replace("\\n", "\n").replace("\\p", "|").replace("\\\\", "\\");
    }

    private static int parseInt(String s, int fallback) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return fallback; }
    }

    private static double parseDouble(String s, double fallback) {
        try { return Double.parseDouble(s.trim()); } catch (Exception e) { return fallback; }
    }

    private static LocalDateTime parseDate(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try { return LocalDateTime.parse(s.trim(), ISO_FMT); } catch (Exception e) { return LocalDateTime.now(); }
    }
}
