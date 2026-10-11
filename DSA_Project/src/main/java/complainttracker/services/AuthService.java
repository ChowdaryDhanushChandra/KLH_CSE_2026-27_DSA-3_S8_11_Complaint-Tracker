package complainttracker.services;

import complainttracker.data.DatabaseHandler;
import complainttracker.models.Role;
import complainttracker.models.User;

public class AuthService {
    private static AuthService instance;
    private User currentUser;

    private AuthService() {}

    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    public boolean login(String username, String password) {
        User user = DatabaseHandler.getInstance().authenticate(username, password);
        if (user != null) {
            this.currentUser = user;
            return true;
        }
        return false;
    }

    public void loginAsDemo(Role role) {
        for (User u : DatabaseHandler.getInstance().getUsers()) {
            if (u.getRole() == role) {
                this.currentUser = u;
                return;
            }
        }
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }
}
