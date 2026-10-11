package complainttracker.utils;

import complainttracker.models.Priority;
import complainttracker.models.Status;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Optional;

public class UIUtils {

    public static Label createPriorityBadge(Priority priority) {
        Label badge = new Label(priority != null ? priority.getDisplayName() : "Medium");
        badge.getStyleClass().add("badge");
        String bg = "#EAB308";
        String fg = "#FFFFFF";

        if (priority == Priority.CRITICAL) {
            bg = "#EF4444";
        } else if (priority == Priority.HIGH) {
            bg = "#F97316";
        } else if (priority == Priority.LOW) {
            bg = "#10B981";
        }

        badge.setStyle("-fx-background-color: " + bg + "; -fx-text-fill: " + fg + ";");
        return badge;
    }

    public static Label createStatusBadge(Status status) {
        Label badge = new Label(status != null ? status.getDisplayName() : "New");
        badge.getStyleClass().add("badge");
        String bg = "#3B82F6";
        String fg = "#FFFFFF";

        if (status == Status.RESOLVED) {
            bg = "#10B981";
        } else if (status == Status.IN_PROGRESS) {
            bg = "#8B5CF6";
        } else if (status == Status.PENDING) {
            bg = "#F59E0B";
        } else if (status == Status.ESCALATED) {
            bg = "#DC2626";
        } else if (status == Status.REJECTED) {
            bg = "#6B7280";
        }

        badge.setStyle("-fx-background-color: " + bg + "; -fx-text-fill: " + fg + ";");
        return badge;
    }

    public static VBox createStatCard(String title, String value, String subtext, String accentColor) {
        VBox card = new VBox(6);
        card.getStyleClass().add("stat-card");
        card.setPrefWidth(180);

        Label lblTitle = new Label(title.toUpperCase());
        lblTitle.getStyleClass().add("stat-title");

        Label lblValue = new Label(value);
        lblValue.getStyleClass().add("stat-value");
        lblValue.setStyle("-fx-text-fill: " + accentColor + ";");

        Label lblSub = new Label(subtext);
        lblSub.setStyle("-fx-font-size: 11px; -fx-text-fill: #94A3B8;");

        card.getChildren().addAll(lblTitle, lblValue, lblSub);
        return card;
    }

    public static void showInfo(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static boolean showConfirmation(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
