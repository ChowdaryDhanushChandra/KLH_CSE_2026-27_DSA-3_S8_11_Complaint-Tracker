package complainttracker.services;

import complainttracker.data.DatabaseHandler;
import complainttracker.models.Complaint;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportService {
    private static ReportService instance;

    private ReportService() {}

    public static synchronized ReportService getInstance() {
        if (instance == null) {
            instance = new ReportService();
        }
        return instance;
    }

    public boolean exportToCSV(File targetFile, List<Complaint> listToExport) {
        if (targetFile == null) return false;
        List<Complaint> data = listToExport != null ? listToExport : DatabaseHandler.getInstance().getComplaints();

        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(targetFile), StandardCharsets.UTF_8))) {
            // CSV Header
            writer.println("Complaint ID,Customer Name,Phone/Email,Title,Category,Priority,Status,Department,Location,Created At,Resolved At,SLA Hours,SLA Breached?,Rating,Feedback");

            for (Complaint c : data) {
                writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%d,\"%s\",%d,\"%s\"%n",
                        clean(c.getId()), clean(c.getCustomerName()), clean(c.getCustomerContact()),
                        clean(c.getTitle()), clean(c.getCategory().getDisplayName()),
                        clean(c.getPriority().getDisplayName()), clean(c.getStatus().getDisplayName()),
                        clean(c.getDepartmentName()), clean(c.getLocation()),
                        clean(c.getFormattedCreatedAt()), clean(c.getFormattedResolvedAt()),
                        c.getSlaHours(), c.isSlaBreached() ? "YES" : "NO",
                        c.getRating(), clean(c.getFeedback()));
            }
            return true;
        } catch (Exception e) {
            System.err.println("Failed to export CSV: " + e.getMessage());
            return false;
        }
    }

    private static String clean(String s) {
        if (s == null) return "";
        return s.replace("\"", "\"\"");
    }
}
