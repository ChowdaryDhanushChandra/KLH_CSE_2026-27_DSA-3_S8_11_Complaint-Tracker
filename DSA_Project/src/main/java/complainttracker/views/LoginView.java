package complainttracker.views;

import complainttracker.models.Role;
import complainttracker.services.AuthService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.function.Consumer;

public class LoginView {
    private final Consumer<Role> onLoginSuccess;

    public LoginView(Consumer<Role> onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
    }

    public Parent getView() {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #0F172A, #1E293B, #0F172A);");

        VBox card = new VBox(16);
        card.setMaxWidth(460);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 16px; " +
                "-fx-padding: 32px 30px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 24, 0, 0, 8);");

        // Header Title
        Label lblApp = new Label("COMPLAINT TRACKER");
        lblApp.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        lblApp.setStyle("-fx-text-fill: #0F172A;");

        Label lblSub = new Label("Smart Complaint Management Using Advanced DSA");
        lblSub.setStyle("-fx-text-fill: #2563EB; -fx-font-weight: 600; -fx-font-size: 12px;");

        Label lblDesc = new Label("Centralized Municipal & Organizational Grievance System");
        lblDesc.setStyle("-fx-text-fill: #64748B; -fx-font-size: 11px;");

        VBox headerBox = new VBox(4, lblApp, lblSub, lblDesc);
        headerBox.setAlignment(Pos.CENTER);

        // Form fields
        Label lblUser = new Label("Username / ID");
        lblUser.getStyleClass().add("field-label");
        TextField txtUsername = new TextField("admin");
        txtUsername.setPromptText("e.g. admin or customer1");

        Label lblPass = new Label("Password");
        lblPass.getStyleClass().add("field-label");
        PasswordField txtPassword = new PasswordField();
        txtPassword.setText("admin123");
        txtPassword.setPromptText("Enter password");

        // Remember Me & Forgot Password row
        CheckBox chkRemember = new CheckBox("Remember login");
        chkRemember.setSelected(true);
        chkRemember.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblHint = new Label("Default: admin123 / user123");
        lblHint.setStyle("-fx-font-size: 11px; -fx-text-fill: #94A3B8;");

        HBox optionsRow = new HBox(chkRemember, spacer, lblHint);
        optionsRow.setAlignment(Pos.CENTER_LEFT);

        Label lblErr = new Label();
        lblErr.setStyle("-fx-text-fill: #EF4444; -fx-font-size: 12px;");
        lblErr.setVisible(false);

        Button btnLogin = new Button("Sign In to Portal");
        btnLogin.getStyleClass().add("btn-primary");
        btnLogin.setMaxWidth(Double.MAX_VALUE);
        btnLogin.setStyle("-fx-padding: 11px; -fx-font-size: 14px;");

        btnLogin.setOnAction(e -> {
            String u = txtUsername.getText().trim();
            String p = txtPassword.getText().trim();
            if (AuthService.getInstance().login(u, p)) {
                lblErr.setVisible(false);
                onLoginSuccess.accept(AuthService.getInstance().getCurrentUser().getRole());
            } else {
                lblErr.setText("Invalid credentials. Use demo 1-click buttons below.");
                lblErr.setVisible(true);
            }
        });

        // Demo Buttons for Fast Evaluation
        Separator sep = new Separator();
        sep.setPadding(new Insets(4, 0, 4, 0));

        Label lblDemo = new Label("FAST 1-CLICK ROLE EVALUATION");
        lblDemo.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #94A3B8;");

        Button btnDemoAdmin = new Button("🛡️ Demo Login as Administrator (Operations Center)");
        btnDemoAdmin.getStyleClass().add("btn-secondary");
        btnDemoAdmin.setMaxWidth(Double.MAX_VALUE);
        btnDemoAdmin.setStyle("-fx-font-size: 12px; -fx-padding: 8px; -fx-alignment: CENTER_LEFT;");
        btnDemoAdmin.setOnAction(e -> {
            AuthService.getInstance().loginAsDemo(Role.ADMINISTRATOR);
            onLoginSuccess.accept(Role.ADMINISTRATOR);
        });

        Button btnDemoCust = new Button("👤 Demo Login as Citizen / Customer (Self-Service)");
        btnDemoCust.getStyleClass().add("btn-secondary");
        btnDemoCust.setMaxWidth(Double.MAX_VALUE);
        btnDemoCust.setStyle("-fx-font-size: 12px; -fx-padding: 8px; -fx-alignment: CENTER_LEFT;");
        btnDemoCust.setOnAction(e -> {
            AuthService.getInstance().loginAsDemo(Role.CUSTOMER);
            onLoginSuccess.accept(Role.CUSTOMER);
        });

        VBox formBox = new VBox(8,
                new VBox(4, lblUser, txtUsername),
                new VBox(4, lblPass, txtPassword),
                optionsRow,
                lblErr,
                btnLogin,
                sep,
                lblDemo,
                btnDemoAdmin,
                btnDemoCust
        );

        card.getChildren().addAll(headerBox, formBox);
        root.getChildren().add(card);
        return root;
    }
}
