package complainttracker.services;

import complainttracker.algorithms.EditDistanceDuplicateDetector;
import complainttracker.algorithms.EditDistanceDuplicateDetector.DuplicateCheckResult;
import complainttracker.data.DatabaseHandler;
import complainttracker.models.Complaint;

public class DuplicateDetectionService {
    private static DuplicateDetectionService instance;
    private double thresholdPercentage = 70.0; // Default 70% similarity

    private DuplicateDetectionService() {}

    public static synchronized DuplicateDetectionService getInstance() {
        if (instance == null) {
            instance = new DuplicateDetectionService();
        }
        return instance;
    }

    public double getThresholdPercentage() {
        return thresholdPercentage;
    }

    public void setThresholdPercentage(double thresholdPercentage) {
        this.thresholdPercentage = Math.max(30.0, Math.min(99.0, thresholdPercentage));
    }

    public DuplicateCheckResult checkComplaint(Complaint newComplaint) {
        return EditDistanceDuplicateDetector.findDuplicate(
                newComplaint,
                DatabaseHandler.getInstance().getComplaints(),
                this.thresholdPercentage
        );
    }
}
