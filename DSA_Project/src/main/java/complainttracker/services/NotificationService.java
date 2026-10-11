package complainttracker.services;

import complainttracker.data.DatabaseHandler;
import complainttracker.models.Complaint;
import complainttracker.models.Department;
import complainttracker.models.Priority;
import complainttracker.models.Status;
import complainttracker.models.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationService {
    private static NotificationService instance;
    private final List<NotificationItem> notifications = new ArrayList<>();
    private final List<String> readNotificationIds = new ArrayList<>();

    private NotificationService() {
        refreshNotifications();
    }

    public static synchronized NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }

    public synchronized void refreshNotifications() {
        notifications.clear();
        User currentUser = AuthService.getInstance().getCurrentUser();
        List<Complaint> complaints = DatabaseHandler.getInstance().getComplaints();

        if (currentUser != null && currentUser.isAdmin()) {
            // ADMIN NOTIFICATIONS
            for (Complaint c : complaints) {
                // 1. Critical Emergency Alert
                if (c.getPriority() == Priority.CRITICAL && c.getStatus() != Status.RESOLVED) {
                    notifications.add(new NotificationItem(
                            "NOTIF-CRIT-" + c.getId(),
                            "🚨 Urgent: Critical Grievance [" + c.getId() + "]",
                            c.getTitle() + " in " + c.getLocation() + " requires immediate action.",
                            c.getCreatedAt(),
                            "CRITICAL",
                            c.getId()
                    ));
                }

                // 2. SLA Breached Alert
                if (c.isSlaBreached() && c.getStatus() != Status.RESOLVED) {
                    notifications.add(new NotificationItem(
                            "NOTIF-SLA-" + c.getId(),
                            "⏱️ SLA Breached: [" + c.getId() + "]",
                            "Grievance exceeded " + c.getSlaHours() + "h target resolution deadline.",
                            c.getCreatedAt().plusHours(c.getSlaHours()),
                            "WARNING",
                            c.getId()
                    ));
                }

                // 3. Unassigned Complaint Alert
                if ((c.getDepartmentId() == null || "DEP-GEN".equalsIgnoreCase(c.getDepartmentId()))
                        && c.getStatus() == Status.NEW) {
                    notifications.add(new NotificationItem(
                            "NOTIF-UNAS-" + c.getId(),
                            "📌 Unassigned Complaint: [" + c.getId() + "]",
                            "Awaiting municipal routing. Run Network Flow auto-allocation or assign manually.",
                            c.getCreatedAt(),
                            "INFO",
                            c.getId()
                    ));
                }
            }

            // 4. Department Overload Alerts
            for (Department d : DatabaseHandler.getInstance().getDepartments()) {
                if (d.getLoadPercentage() >= 85.0) {
                    notifications.add(new NotificationItem(
                            "NOTIF-DEPT-" + d.getId(),
                            "⚠️ High Department Load: " + d.getName(),
                            "Current active cases (" + d.getCurrentLoad() + "/" + d.getCapacity() + ") at " +
                                    String.format("%.0f%%", d.getLoadPercentage()) + " capacity.",
                            LocalDateTime.now(),
                            "WARNING",
                            null
                    ));
                }
            }
        } else if (currentUser != null) {
            // CUSTOMER NOTIFICATIONS
            String userId = currentUser.getId();
            String userName = currentUser.getFullName();

            for (Complaint c : complaints) {
                boolean isMine = (c.getCustomerUserId() != null && c.getCustomerUserId().equalsIgnoreCase(userId))
                        || (c.getCustomerName() != null && c.getCustomerName().equalsIgnoreCase(userName));

                if (isMine) {
                    // Status update
                    if (c.getStatus() == Status.IN_PROGRESS) {
                        notifications.add(new NotificationItem(
                                "NOTIF-STAT-" + c.getId(),
                                "🛠️ Work In Progress: [" + c.getId() + "]",
                                "Your grievance '" + c.getTitle() + "' is actively being serviced by " + c.getDepartmentName() +
                                        (c.getAssignedStaff() != null ? " (" + c.getAssignedStaff() + ")" : "") + ".",
                                c.getUpdatedAt(),
                                "INFO",
                                c.getId()
                        ));
                    } else if (c.getStatus() == Status.RESOLVED) {
                        notifications.add(new NotificationItem(
                                "NOTIF-RES-" + c.getId(),
                                "✅ Grievance Resolved: [" + c.getId() + "]",
                                "Your grievance has been resolved! Please submit your satisfaction rating.",
                                c.getResolvedAt() != null ? c.getResolvedAt() : c.getUpdatedAt(),
                                "SUCCESS",
                                c.getId()
                        ));
                    } else if (c.getStatus() == Status.NEW || c.getStatus() == Status.PENDING) {
                        notifications.add(new NotificationItem(
                                "NOTIF-INTAKE-" + c.getId(),
                                "📝 Ticket Registered: [" + c.getId() + "]",
                                "Your complaint has been logged and assigned SLA window of " + c.getSlaHours() + " hours.",
                                c.getCreatedAt(),
                                "INFO",
                                c.getId()
                        ));
                    }
                }
            }
        }

        // Apply read status
        for (NotificationItem item : notifications) {
            if (readNotificationIds.contains(item.getId())) {
                item.setRead(true);
            }
        }

        // Sort newest first
        notifications.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
    }

    public List<NotificationItem> getNotifications() {
        refreshNotifications();
        return Collections.unmodifiableList(notifications);
    }

    public int getUnreadCount() {
        int count = 0;
        for (NotificationItem item : getNotifications()) {
            if (!item.isRead()) count++;
        }
        return count;
    }

    public void markAsRead(String id) {
        if (!readNotificationIds.contains(id)) {
            readNotificationIds.add(id);
        }
        for (NotificationItem item : notifications) {
            if (item.getId().equals(id)) {
                item.setRead(true);
            }
        }
    }

    public void markAllAsRead() {
        for (NotificationItem item : notifications) {
            item.setRead(true);
            if (!readNotificationIds.contains(item.getId())) {
                readNotificationIds.add(item.getId());
            }
        }
    }
}
