package complainttracker;

import complainttracker.models.Role;
import complainttracker.services.AuthService;
import complainttracker.views.LoginView;
import complainttracker.views.MainLayoutView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {
    private Stage primaryStage;
    private Scene scene;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("Complaint Tracker – Smart Complaint Management Using Advanced DSA");

        // Initialize with Login Screen
        showLoginScreen();

        stage.setMinWidth(1200);
        stage.setMinHeight(750);
        stage.setWidth(1440);
        stage.setHeight(900);
        stage.show();
    }

    public void showLoginScreen() {
        LoginView loginView = new LoginView(role -> showMainApp());
        scene = new Scene((javafx.scene.Parent) loginView.getView(), 1440, 900);
        applyStylesheet(scene);
        primaryStage.setScene(scene);
    }

    public void showMainApp() {
        MainLayoutView mainView = new MainLayoutView(this::showLoginScreen);
        scene = new Scene((javafx.scene.Parent) mainView.getView(), 1440, 900);
        applyStylesheet(scene);
        primaryStage.setScene(scene);
    }

    private void applyStylesheet(Scene sc) {
        try {
            URL cssUrl = getClass().getResource("/css/complaint_style.css");
            if (cssUrl != null) {
                sc.getStylesheets().add(cssUrl.toExternalForm());
            }
        } catch (Exception e) {
            System.err.println("Notice: Could not load external css stylesheet: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
