package complainttracker.views;

import complainttracker.models.Complaint;
import complainttracker.models.User;
import complainttracker.services.AuthService;
import complainttracker.services.ComplaintService;
import complainttracker.services.NotificationService;
import complainttracker.services.PermissionService;
import complainttracker.utils.UIUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

import java.util.HashMap;
import java.util.Map;

public class MainLayoutView {
    private final Runnable onLogout;
    private BorderPane rootPane;
    private StackPane contentArea;
    private final Map<String, Button> navButtons = new HashMap<>();
    private String currentViewKey;
    private Complaint selectedComplaintForInspection = null;
    private Label lblNotifBadge;
    private Button btnNotif;

    public MainLayoutView(Runnable onLogout) {
        this.onLogout = onLogout;
    }

    public Node getView() {
        rootPane = new BorderPane();
        rootPane.getStyleClass().add("root");

        // 1. Sidebar (Role-tailored)
        VBox sidebar = buildSidebar();
        rootPane.setLeft(sidebar);

        // 2. Topbar & Content Area inside a VBox
        VBox mainContainer = new VBox();
        HBox topbar = buildTopbar();
        contentArea = new StackPane();
        VBox.setVgrow(contentArea, javafx.scene.layout.Priority.ALWAYS);

        mainContainer.getChildren().addAll(topbar, contentArea);
        rootPane.setCenter(mainContainer);

        // 3. Show default view according to role
        User user = AuthService.getInstance().getCurrentUser();
        if (user != null && user.isAdmin()) {
            navigateTo("ADMIN_DASHBOARD");
        } else {
            navigateTo("CUSTOMER_DASHBOARD");
        }

        return rootPane;
    }

    private VBox buildSidebar() {
        User user = AuthService.getInstance().getCurrentUser();
        boolean isAdmin = user != null && user.isAdmin();

        VBox sidebar = new VBox();
        sidebar.setPrefWidth(250);
        sidebar.getStyleClass().add("sidebar");

        // Brand header
        VBox brandBox = new VBox(4);
        brandBox.getStyleClass().add("sidebar-brand-box");

        Label lblBrand = new Label(isAdmin ? "COMPLAINT TRACKER" : "CITIZEN PORTAL");
        lblBrand.getStyleClass().add("sidebar-title");

        Label lblSub = new Label(isAdmin ? "Operations & DSA Control Hub" : "Public Grievance Redressal");
        lblSub.getStyleClass().add("sidebar-subtitle");

        Label lblRoleTag = new Label(isAdmin ? "🛡️ ADMINISTRATOR CONSOLE" : "👤 CITIZEN SELF-SERVICE");
        lblRoleTag.setStyle(isAdmin ?
                "-fx-background-color: #1E3A8A; -fx-text-fill: #93C5FD; -fx-padding: 3px 8px; -fx-background-radius: 10px; -fx-font-size: 10px; -fx-font-weight: bold;" :
                "-fx-background-color: #064E3B; -fx-text-fill: #A7F3D0; -fx-padding: 3px 8px; -fx-background-radius: 10px; -fx-font-size: 10px; -fx-font-weight: bold;");

        brandBox.getChildren().addAll(lblBrand, lblSub, lblRoleTag);

        // Nav items container inside ScrollPane so it handles small window heights gracefully
        VBox navBox = new VBox(4);
        navBox.setPadding(new Insets(12, 10, 12, 10));

        if (isAdmin) {
            // ==================== ADMIN SIDEBAR ====================
            navBox.getChildren().add(createNavHeader("GRIEVANCE OPERATIONS"));
            navBox.getChildren().add(createNavButton("ADMIN_DASHBOARD", "📊  Operations Dashboard"));
            navBox.getChildren().add(createNavButton("ALL_COMPLAINTS", "📋  All Complaints (Master)"));
            navBox.getChildren().add(createNavButton("REGISTER", "✍  Register Intake"));
            navBox.getChildren().add(createNavButton("PRIORITY_QUEUE", "⚡  Priority Queue (Min-Heap)"));
            navBox.getChildren().add(createNavButton("DEPARTMENTS", "🏢  Department Flow"));

            navBox.getChildren().add(createNavHeader("ADVANCED ALGORITHMS"));
            navBox.getChildren().add(createNavButton("SEARCH_LAB", "🔍  Search & Pattern Lab"));
            navBox.getChildren().add(createNavButton("ALGO_LAB", "🧪  Algorithm Studio"));

            navBox.getChildren().add(createNavHeader("MANAGEMENT & GOVERNANCE"));
            navBox.getChildren().add(createNavButton("USER_MGMT", "👥  User Management"));
            navBox.getChildren().add(createNavButton("SLA_MGMT", "⏱️  SLA & Escalation Center"));

            navBox.getChildren().add(createNavHeader("MONITORING & LOGS"));
            navBox.getChildren().add(createNavButton("ANALYTICS", "📈  Analytics & Reports"));
            navBox.getChildren().add(createNavButton("HISTORY", "📜  Audit & History Trail"));
            navBox.getChildren().add(createNavButton("NOTIFICATIONS", "🔔  System Dispatches"));

            navBox.getChildren().add(createNavHeader("SYSTEM"));
            navBox.getChildren().add(createNavButton("SETTINGS", "⚙  System Settings"));
            navBox.getChildren().add(createNavButton("ADMIN_PROFILE", "🛡️  Admin Profile"));
        } else {
            // ==================== CUSTOMER SIDEBAR ====================
            navBox.getChildren().add(createNavHeader("MY GRIEVANCES"));
            navBox.getChildren().add(createNavButton("CUSTOMER_DASHBOARD", "📊  My Dashboard"));
            navBox.getChildren().add(createNavButton("REGISTER", "✍  File New Complaint"));
            navBox.getChildren().add(createNavButton("MY_COMPLAINTS", "📋  My Complaints"));
            navBox.getChildren().add(createNavButton("TRACK", "🔍  Track Grievance Live"));
            navBox.getChildren().add(createNavButton("HISTORY", "📜  My Complaint History"));

            navBox.getChildren().add(createNavHeader("COMMUNICATIONS & HELP"));
            navBox.getChildren().add(createNavButton("NOTIFICATIONS", "🔔  Notifications"));
            navBox.getChildren().add(createNavButton("HELP", "❓  Citizen Help & FAQs"));

            navBox.getChildren().add(createNavHeader("ACCOUNT"));
            navBox.getChildren().add(createNavButton("CUSTOMER_PROFILE", "👤  My Profile"));
            navBox.getChildren().add(createNavButton("SETTINGS", "⚙  Preferences"));
        }

        ScrollPane navScroll = new ScrollPane(navBox);
        navScroll.setFitToWidth(true);
        navScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(navScroll, javafx.scene.layout.Priority.ALWAYS);

        // User footer
        VBox footerBox = new VBox(6);
        footerBox.setPadding(new Insets(14));
        footerBox.setStyle("-fx-background-color: #0B1120; -fx-border-color: #1E293B; -fx-border-width: 1px 0 0 0;");

        Label lblUser = new Label(user != null ? user.getFullName() : "Guest User");
        lblUser.setStyle("-fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-font-size: 12px;");

        Label lblRole = new Label(user != null ? "Role: " + user.getRole().getTitle() : "Guest");
        lblRole.setStyle("-fx-text-fill: " + (isAdmin ? "#38BDF8;" : "#34D399;") + " -fx-font-size: 11px;");

        Button btnLogout = new Button("🚪 Sign Out");
        btnLogout.getStyleClass().add("btn-secondary");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.setStyle("-fx-font-size: 11px; -fx-padding: 6px;");
        btnLogout.setOnAction(e -> {
            AuthService.getInstance().logout();
            onLogout.run();
        });

        footerBox.getChildren().addAll(lblUser, lblRole, btnLogout);

        sidebar.getChildren().addAll(brandBox, navScroll, footerBox);
        return sidebar;
    }

    private Label createNavHeader(String text) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add("nav-section-label");
        return lbl;
    }

    private Button createNavButton(String key, String title) {
        Button btn = new Button(title);
        btn.getStyleClass().add("nav-btn");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> navigateTo(key));
        navButtons.put(key, btn);
        return btn;
    }

    private HBox buildTopbar() {
        User user = AuthService.getInstance().getCurrentUser();
        boolean isAdmin = user != null && user.isAdmin();

        HBox topbar = new HBox(14);
        topbar.getStyleClass().add("topbar");
        topbar.setAlignment(Pos.CENTER_LEFT);

        Label lblBreadcrumb = new Label(isAdmin
                ? "🛡️ Administrator Operations Center • Central Municipal Grievance Node"
                : "🏛️ Citizen Grievance Portal • Public Services Redressal System");
        lblBreadcrumb.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        // Dynamic Notification Button with Live Badge
        btnNotif = new Button();
        btnNotif.getStyleClass().add("btn-secondary");
        btnNotif.setOnAction(e -> navigateTo("NOTIFICATIONS"));
        updateNotificationBadge();

        Button btnRefresh = new Button("↻ Refresh");
        btnRefresh.getStyleClass().add("btn-secondary");
        btnRefresh.setOnAction(e -> {
            ComplaintService.getInstance().refreshIndexes();
            NotificationService.getInstance().refreshNotifications();
            updateNotificationBadge();
            navigateTo(currentViewKey);
        });

        // Role Badge Pill
        Label lblRolePill = new Label(isAdmin ? "🛡️ ADMINISTRATOR" : "👤 CITIZEN");
        lblRolePill.setStyle(isAdmin ?
                "-fx-background-color: #DBEAFE; -fx-text-fill: #1E40AF; -fx-font-weight: bold; -fx-padding: 4px 10px; -fx-background-radius: 12px; -fx-font-size: 11px;" :
                "-fx-background-color: #D1FAE5; -fx-text-fill: #065F46; -fx-font-weight: bold; -fx-padding: 4px 10px; -fx-background-radius: 12px; -fx-font-size: 11px;");

        // User Pill
        HBox userPill = new HBox(6);
        userPill.getStyleClass().add("user-pill");
        userPill.setAlignment(Pos.CENTER);
        Label userText = new Label("● " + (user != null ? user.getUsername() : "Guest"));
        userText.getStyleClass().add("user-pill-text");
        userPill.getChildren().add(userText);

        topbar.getChildren().addAll(lblBreadcrumb, spacer, btnNotif, btnRefresh, lblRolePill, userPill);
        return topbar;
    }

    public void updateNotificationBadge() {
        if (btnNotif == null) return;
        int unread = NotificationService.getInstance().getUnreadCount();
        if (unread > 0) {
            btnNotif.setText("🔔 Notifications (" + unread + ")");
            btnNotif.setStyle("-fx-font-weight: bold; -fx-text-fill: #DC2626;");
        } else {
            btnNotif.setText("🔔 Notifications");
            btnNotif.setStyle("-fx-text-fill: #475569;");
        }
    }

    public void navigateTo(String viewKey) {
        User user = AuthService.getInstance().getCurrentUser();
        boolean isAdmin = user != null && user.isAdmin();

        // RBAC Enforcement: prevent non-admins from accessing admin tools
        if (!isAdmin && (
                "ADMIN_DASHBOARD".equalsIgnoreCase(viewKey) ||
                "ALL_COMPLAINTS".equalsIgnoreCase(viewKey) ||
                "PRIORITY_QUEUE".equalsIgnoreCase(viewKey) ||
                "DEPARTMENTS".equalsIgnoreCase(viewKey) ||
                "SEARCH_LAB".equalsIgnoreCase(viewKey) ||
                "ALGO_LAB".equalsIgnoreCase(viewKey) ||
                "USER_MGMT".equalsIgnoreCase(viewKey) ||
                "SLA_MGMT".equalsIgnoreCase(viewKey) ||
                "ADMIN_PROFILE".equalsIgnoreCase(viewKey)
        )) {
            UIUtils.showAlert(Alert.AlertType.WARNING, "Access Restricted",
                    "Administrative clearance is required to access this system feature.");
            viewKey = "CUSTOMER_DASHBOARD";
        }

        this.currentViewKey = viewKey;

        // Highlight active nav button
        for (Map.Entry<String, Button> entry : navButtons.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(viewKey)) {
                if (!entry.getValue().getStyleClass().contains("active")) {
                    entry.getValue().getStyleClass().add("active");
                }
            } else {
                entry.getValue().getStyleClass().remove("active");
            }
        }

        Node viewNode;
        switch (viewKey) {
            // ADMIN VIEWS
            case "ADMIN_DASHBOARD":
                viewNode = new AdminDashboardView(
                        targetView -> navigateTo(targetView),
                        c -> inspectComplaint(c)
                ).getView();
                break;
            case "ALL_COMPLAINTS":
                viewNode = new ComplaintListView(c -> inspectComplaint(c)).getView();
                break;
            case "PRIORITY_QUEUE":
                viewNode = new PriorityQueueView(c -> inspectComplaint(c)).getView();
                break;
            case "DEPARTMENTS":
                viewNode = new DepartmentView().getView();
                break;
            case "SEARCH_LAB":
                viewNode = new SearchLabView(c -> inspectComplaint(c)).getView();
                break;
            case "ALGO_LAB":
                viewNode = new AlgorithmLabView().getView();
                break;
            case "USER_MGMT":
                viewNode = new AdminUserManagementView().getView();
                break;
            case "SLA_MGMT":
                viewNode = new AdminSlaManagementView(c -> inspectComplaint(c)).getView();
                break;
            case "ADMIN_PROFILE":
                viewNode = new AdminProfileView().getView();
                break;

            // CUSTOMER VIEWS
            case "CUSTOMER_DASHBOARD":
                viewNode = new CustomerDashboardView(
                        targetView -> navigateTo(targetView),
                        c -> {
                            this.selectedComplaintForInspection = c;
                            navigateTo("TRACK");
                        }
                ).getView();
                break;
            case "MY_COMPLAINTS":
                viewNode = new CustomerComplaintListView(
                        c -> {
                            this.selectedComplaintForInspection = c;
                            navigateTo("TRACK");
                        },
                        () -> navigateTo("REGISTER")
                ).getView();
                break;
            case "TRACK":
                viewNode = new CustomerTrackView(this.selectedComplaintForInspection).getView();
                break;
            case "CUSTOMER_PROFILE":
                viewNode = new CustomerProfileView().getView();
                break;
            case "HELP":
                viewNode = new CustomerHelpView(() -> navigateTo("REGISTER")).getView();
                break;

            // SHARED VIEWS
            case "REGISTER":
                viewNode = new ComplaintRegisterView(
                        c -> {
                            if (isAdmin) navigateTo("ALL_COMPLAINTS");
                            else navigateTo("MY_COMPLAINTS");
                        },
                        c -> inspectComplaint(c)
                ).getView();
                break;
            case "HISTORY":
                viewNode = new ComplaintHistoryView(this.selectedComplaintForInspection).getView();
                break;
            case "ANALYTICS":
                viewNode = new AnalyticsView().getView();
                break;
            case "NOTIFICATIONS":
                viewNode = new NotificationView(
                        c -> inspectComplaint(c),
                        this::updateNotificationBadge
                ).getView();
                break;
            case "SETTINGS":
                viewNode = new SettingsView(() -> {
                    if (isAdmin) navigateTo("ADMIN_DASHBOARD");
                    else navigateTo("CUSTOMER_DASHBOARD");
                }).getView();
                break;

            default:
                viewNode = isAdmin ?
                        new AdminDashboardView(this::navigateTo, this::inspectComplaint).getView() :
                        new CustomerDashboardView(this::navigateTo, c -> {
                            this.selectedComplaintForInspection = c;
                            navigateTo("TRACK");
                        }).getView();
                break;
        }

        contentArea.getChildren().setAll(viewNode);
    }

    private void inspectComplaint(Complaint c) {
        User user = AuthService.getInstance().getCurrentUser();
        if (user != null && user.isAdmin()) {
            AdminComplaintDetailDialog.show(c, () -> navigateTo(currentViewKey));
        } else {
            this.selectedComplaintForInspection = c;
            navigateTo("TRACK");
        }
    }
}
