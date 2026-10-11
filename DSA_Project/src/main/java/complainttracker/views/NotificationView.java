package complainttracker.views;

import complainttracker.data.DatabaseHandler;
import complainttracker.models.Complaint;
import complainttracker.services.NotificationItem;
import complainttracker.services.NotificationService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;
import java.util.function.Consumer;

public class NotificationView {
    private final Consumer<Complaint> onInspectComplaint;
    private final Runnable onBadgeUpdate;
    private VBox notificationsListContainer;
    private Label lblUnreadBadge;

    public NotificationView(Consumer<Complaint> onInspectComplaint, Runnable onBadgeUpdate) {
        this.onInspectComplaint = onInspectComplaint;
        this.onBadgeUpdate = onBadgeUpdate;
    }

    public Node getView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(24));
        root.setFillWidth(true);
        root.setStyle("-fx-background-color: #F8FAFC;");

        // 1. Header
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        HBox titleRow = new HBox(8);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label lblTitle = new Label("🔔 Notification & Dispatch Center");
        lblTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        lblUnreadBadge = new Label(NotificationService.getInstance().getUnreadCount() + " New");
        lblUnreadBadge.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-padding: 3px 8px; -fx-background-radius: 12px; -fx-font-weight: bold; -fx-font-size: 11px;");
        lblUnreadBadge.setVisible(NotificationService.getInstance().getUnreadCount() > 0);

        titleRow.getChildren().addAll(lblTitle, lblUnreadBadge);

        Label lblSub = new Label("Real-time operational alerts, SLA milestones, and status dispatches");
        lblSub.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");
        titleBox.getChildren().addAll(titleRow, lblSub);

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Button btnMarkAll = new Button("✓ Mark All as Read");
        btnMarkAll.getStyleClass().add("btn-secondary");
        btnMarkAll.setOnAction(e -> {
            NotificationService.getInstance().markAllAsRead();
            refreshList();
            if (onBadgeUpdate != null) onBadgeUpdate.run();
        });

        Button btnRefresh = new Button("↻ Refresh");
        btnRefresh.getStyleClass().add("btn-secondary");
        btnRefresh.setOnAction(e -> {
            NotificationService.getInstance().refreshNotifications();
            refreshList();
            if (onBadgeUpdate != null) onBadgeUpdate.run();
        });

        header.getChildren().addAll(titleBox, spacer, btnMarkAll, btnRefresh);

        // 2. Notifications Container
        notificationsListContainer = new VBox(12);
        notificationsListContainer.setFillWidth(true);

        refreshList();

        root.getChildren().addAll(header, notificationsListContainer);

        ScrollPane sp = new ScrollPane(root);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        return sp;
    }

    private void refreshList() {
        notificationsListContainer.getChildren().clear();
        List<NotificationItem> items = NotificationService.getInstance().getNotifications();

        int unreadCount = NotificationService.getInstance().getUnreadCount();
        lblUnreadBadge.setText(unreadCount + " New");
        lblUnreadBadge.setVisible(unreadCount > 0);

        if (items.isEmpty()) {
            VBox emptyBox = new VBox(12);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(40));
            emptyBox.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 12px; -fx-border-color: #E2E8F0; -fx-border-radius: 12px;");

            Label lblEmptyIcon = new Label("📭");
            lblEmptyIcon.setStyle("-fx-font-size: 40px;");

            Label lblEmptyText = new Label("No notifications at this time");
            lblEmptyText.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #475569;");

            Label lblEmptySub = new Label("You are completely caught up! New dispatches will appear here automatically.");
            lblEmptySub.setStyle("-fx-font-size: 12px; -fx-text-fill: #94A3B8;");

            emptyBox.getChildren().addAll(lblEmptyIcon, lblEmptyText, lblEmptySub);
            notificationsListContainer.getChildren().add(emptyBox);
            return;
        }

        for (NotificationItem item : items) {
            notificationsListContainer.getChildren().add(buildNotificationCard(item));
        }
    }

    private HBox buildNotificationCard(NotificationItem item) {
        HBox card = new HBox(16);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(14, 18, 14, 18));

        String borderAccent;
        String iconText;
        switch (item.getSeverity()) {
            case "CRITICAL":
                borderAccent = "#EF4444";
                iconText = "🚨";
                break;
            case "WARNING":
                borderAccent = "#F59E0B";
                iconText = "⚠️";
                break;
            case "SUCCESS":
                borderAccent = "#10B981";
                iconText = "✅";
                break;
            default:
                borderAccent = "#3B82F6";
                iconText = "ℹ️";
                break;
        }

        String bgStyle = item.isRead() ?
                "-fx-background-color: #FFFFFF; -fx-border-color: #E2E8F0; -fx-border-width: 1px; " :
                "-fx-background-color: #F8FAFC; -fx-border-color: " + borderAccent + " #CBD5E1 #CBD5E1 #CBD5E1; -fx-border-width: 1px 1px 1px 4px; ";

        card.setStyle(bgStyle + "-fx-background-radius: 10px; -fx-border-radius: 10px;");

        Label lblIcon = new Label(iconText);
        lblIcon.setStyle("-fx-font-size: 24px;");

        VBox contentBox = new VBox(4);
        HBox.setHgrow(contentBox, javafx.scene.layout.Priority.ALWAYS);

        HBox titleRow = new HBox(8);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label lblTitle = new Label(item.getTitle());
        lblTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: " + (item.isRead() ? "normal" : "bold") + "; -fx-text-fill: #0F172A;");

        Label lblTime = new Label("•  " + item.getFormattedTimestamp());
        lblTime.setStyle("-fx-font-size: 11px; -fx-text-fill: #94A3B8;");

        titleRow.getChildren().addAll(lblTitle, lblTime);

        Label lblMsg = new Label(item.getMessage());
        lblMsg.setStyle("-fx-font-size: 12px; -fx-text-fill: #475569;");
        lblMsg.setWrapText(true);

        contentBox.getChildren().addAll(titleRow, lblMsg);

        HBox actionBox = new HBox(8);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        if (!item.isRead()) {
            Button btnRead = new Button("Mark Read");
            btnRead.setStyle("-fx-font-size: 11px; -fx-padding: 4px 8px; -fx-background-color: #F1F5F9; -fx-text-fill: #475569; -fx-background-radius: 6px;");
            btnRead.setOnAction(e -> {
                NotificationService.getInstance().markAsRead(item.getId());
                refreshList();
                if (onBadgeUpdate != null) onBadgeUpdate.run();
            });
            actionBox.getChildren().add(btnRead);
        }

        if (item.getRelatedComplaintId() != null) {
            Button btnView = new Button("🔍 View Ticket");
            btnView.setStyle("-fx-font-size: 11px; -fx-padding: 4px 10px; -fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6px;");
            btnView.setOnAction(e -> {
                NotificationService.getInstance().markAsRead(item.getId());
                Complaint c = DatabaseHandler.getInstance().findComplaintById(item.getRelatedComplaintId());
                if (c != null && onInspectComplaint != null) {
                    onInspectComplaint.accept(c);
                }
            });
            actionBox.getChildren().add(btnView);
        }

        card.getChildren().addAll(lblIcon, contentBox, actionBox);
        return card;
    }
}
