package complainttracker.services;

import complainttracker.models.Role;
import complainttracker.models.User;
import complainttracker.utils.UIUtils;

/**
 * Centralized Role & Permission Service.
 * Enforces role-based access control (RBAC) across all views and actions.
 */
public class PermissionService {
    private static PermissionService instance;

    private PermissionService() {}

    public static synchronized PermissionService getInstance() {
        if (instance == null) {
            instance = new PermissionService();
        }
        return instance;
    }

    public boolean isAdmin() {
        User u = AuthService.getInstance().getCurrentUser();
        return u != null && u.getRole() == Role.ADMINISTRATOR;
    }

    public boolean isCustomer() {
        User u = AuthService.getInstance().getCurrentUser();
        return u != null && u.getRole() == Role.CUSTOMER;
    }

    public boolean canAccessAdminFeatures() {
        return isAdmin();
    }

    public boolean canManageComplaint(String complaintOwnerUserId, String complaintOwnerName) {
        if (isAdmin()) return true;
        User u = AuthService.getInstance().getCurrentUser();
        if (u == null) return false;
        if (complaintOwnerUserId != null && complaintOwnerUserId.equalsIgnoreCase(u.getId())) return true;
        if (complaintOwnerName != null && complaintOwnerName.equalsIgnoreCase(u.getFullName())) return true;
        return false;
    }

    public boolean verifyAdminAccess() {
        if (!isAdmin()) {
            UIUtils.showError("Access Restricted", "Permission Denied",
                    "You do not have administrative privileges to access this section.\nReturning to Customer Dashboard.");
            return false;
        }
        return true;
    }
}
