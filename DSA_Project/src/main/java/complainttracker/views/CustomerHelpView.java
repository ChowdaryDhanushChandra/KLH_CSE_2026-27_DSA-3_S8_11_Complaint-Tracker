package complainttracker.views;

import complainttracker.utils.UIUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class CustomerHelpView {
    private final Runnable onFileComplaint;

    public CustomerHelpView() {
        this(null);
    }

    public CustomerHelpView(Runnable onFileComplaint) {
        this.onFileComplaint = onFileComplaint;
    }

    public Node getView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(24));

        Label lblTitle = new Label("Citizen Support, FAQs & Emergency Helplines");
        lblTitle.getStyleClass().add("page-title");
        Label lblSub = new Label("Find answers to common questions or reach out to departmental response helplines.");
        lblSub.getStyleClass().add("page-subtitle");
        VBox headerBox = new VBox(2, lblTitle, lblSub);

        // Emergency Helplines Card
        VBox helplinesCard = new VBox(14);
        helplinesCard.getStyleClass().add("card");
        helplinesCard.setStyle("-fx-background-color: #FEF2F2; -fx-border-color: #FECACA;");

        Label lblEmTitle = new Label("🚨 24/7 Rapid Emergency Response Helplines");
        lblEmTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #DC2626;");

        HBox helplinesGrid = new HBox(16);
        helplinesGrid.getChildren().addAll(
                createContactBox("Water & Sewerage Emergency", "Toll Free: 1916", "sewerage.rapid@gov.in"),
                createContactBox("Electrical & Power Board", "Toll Free: 1912", "bescom.trouble@gov.in"),
                createContactBox("Road Hazards & Infrastructure", "Control: 080-22221188", "roads.safety@gov.in"),
                createContactBox("Municipal Grievance Helpline", "Toll Free: 1533", "grievances@citygov.org")
        );

        helplinesCard.getChildren().addAll(lblEmTitle, helplinesGrid);

        // FAQ Section
        VBox faqCard = new VBox(12);
        faqCard.getStyleClass().add("card");

        Label lblFaqTitle = new Label("Frequently Asked Questions");
        lblFaqTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #0F172A;");

        Accordion accordion = new Accordion();

        TitledPane q1 = new TitledPane("How does the system track my grievance?",
                new Label("When you register a complaint, our automated system assigns a unique Tracking ID (e.g. CMP-1001) and routes it to the designated department using algorithmic capacity balancing. You can use the 'Track Complaint' tab to check live status at any time."));

        TitledPane q2 = new TitledPane("What does the 'Duplicate Complaint' warning mean?",
                new Label("To eliminate duplicate work and resolve issues faster, our system uses the Wagner-Fischer Dynamic Programming algorithm to check if an identical issue has already been reported near your location. If you see this warning, you can view the existing ticket or proceed if your issue is distinct."));

        TitledPane q3 = new TitledPane("What are the Service Level Agreement (SLA) turnaround targets?",
                new Label("Critical Emergencies: 12 Hours | High Priority: 24 Hours | Medium Priority: 48 Hours | Low Priority: 72 Hours. If a grievance is not resolved within the SLA window, it is escalated directly to the Division Supervisor."));

        TitledPane q4 = new TitledPane("How can I provide feedback after resolution?",
                new Label("Once field technicians mark your complaint as 'RESOLVED', you can click 'Rate Service & Provide Feedback' on your complaint card to award 1 to 5 stars and submit remarks regarding resolution quality."));

        accordion.getPanes().addAll(q1, q2, q3, q4);
        accordion.setExpandedPane(q1);

        faqCard.getChildren().addAll(lblFaqTitle, accordion);

        // Contact Support Form
        VBox messageCard = new VBox(12);
        messageCard.getStyleClass().add("card");

        Label lblMsgTitle = new Label("Send Direct Message to Citizen Support Desk");
        lblMsgTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #0F172A;");

        TextField txtSubject = new TextField();
        txtSubject.setPromptText("Message subject or related Complaint ID...");

        TextArea txtBody = new TextArea();
        txtBody.setPromptText("Type your question, inquiry, or assistance request...");
        txtBody.setPrefRowCount(4);

        Button btnSend = new Button("✉ Submit Inquiry to Support");
        btnSend.getStyleClass().add("btn-primary");
        btnSend.setOnAction(e -> {
            String s = txtSubject.getText().trim();
            String b = txtBody.getText().trim();
            if (s.isEmpty() || b.isEmpty()) {
                UIUtils.showError("Inquiry Error", "Incomplete Form", "Please provide a subject and message body.");
            } else {
                UIUtils.showInfo("Inquiry Sent", "Support Request Logged",
                        "Your inquiry has been received by our citizen support team. A representative will contact you via email/SMS shortly.");
                txtSubject.clear();
                txtBody.clear();
            }
        });

        messageCard.getChildren().addAll(lblMsgTitle, txtSubject, txtBody, btnSend);

        root.getChildren().addAll(headerBox, helplinesCard, faqCard, messageCard);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        return scroll;
    }

    private VBox createContactBox(String name, String phone, String email) {
        VBox box = new VBox(4);
        box.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #FCA5A5; -fx-border-radius: 8px; -fx-padding: 10px 14px;");
        box.setPrefWidth(210);

        Label lblName = new Label(name);
        lblName.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #991B1B;");

        Label lblPhone = new Label(phone);
        lblPhone.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

        Label lblEmail = new Label(email);
        lblEmail.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");

        box.getChildren().addAll(lblName, lblPhone, lblEmail);
        return box;
    }
}
