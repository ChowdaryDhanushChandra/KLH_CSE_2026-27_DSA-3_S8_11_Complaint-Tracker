package com.samadhan.ui;

import com.samadhan.core.*;
import com.samadhan.module1_system.Service;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.function.Function;

public class App extends Application {
    final Service svc = new Service();
    Stage stage; StackPane content = new StackPane(); boolean dark = true;

    public static void main(String[] a) { launch(a); }

    @Override public void start(Stage s) {
        stage = s; s.setTitle("Samadhan – Product & Package Complaint Center"); s.setMinWidth(1000); s.setMinHeight(650); s.setOnCloseRequest(e -> svc.save());
        Runtime.getRuntime().addShutdownHook(new Thread(svc::save));
        show(loginScene()); s.show();
    }
    void show(Parent p) {
        Scene sc = new Scene(p, 1150, 720);
        sc.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(sc); applyTheme();
    }

    // ---------- Login ----------
    Parent loginScene() {
        Label brand = new Label("Samadhan"); brand.getStyleClass().add("brand");
        Label tag = new Label("Every product. Every package. Made right."); tag.getStyleClass().add("tag");
        Label f = new Label("• Finds duplicate complaints as you type\n• Assigns each complaint to the right officer\n• Shows live status from filing to closure"); f.getStyleClass().add("tag");
        VBox left = new VBox(16, brand, tag, f); left.setAlignment(Pos.CENTER_LEFT); left.setPadding(new Insets(60)); left.getStyleClass().add("hero");
        HBox.setHgrow(left, Priority.ALWAYS);

        ToggleGroup g = new ToggleGroup(); ToggleButton cu = new ToggleButton("Customer"), ad = new ToggleButton("Admin");
        cu.setToggleGroup(g); ad.setToggleGroup(g); cu.setSelected(true); cu.getStyleClass().add("seg"); ad.getStyleClass().add("seg");
        g.selectedToggleProperty().addListener((o, a, b) -> { if (b == null) a.setSelected(true); });
        TextField u = new TextField(); u.setPromptText("Username");
        PasswordField p = new PasswordField(); p.setPromptText("Password");
        u.setOnAction(e -> p.requestFocus());
        Label err = new Label(); err.getStyleClass().add("err"); err.setWrapText(true);
        Button go = new Button("Sign in"); go.getStyleClass().add("primary"); go.setMaxWidth(Double.MAX_VALUE);
        Runnable login = () -> {
            String m = svc.login(u.getText().trim(), p.getText(), ad.isSelected() ? "admin" : "customer");
            if (m == null) openShell(); else err.setText(m);
        };
        go.setOnAction(e -> login.run()); p.setOnAction(e -> login.run());
        Button reg = new Button("Create a customer account"); reg.setMaxWidth(Double.MAX_VALUE);
        reg.setOnAction(e -> {
            Dialog<ButtonType> d = new Dialog<>(); d.setTitle("Create account");
            TextField ru = new TextField(); ru.setPromptText("Username"); PasswordField rp = new PasswordField(); rp.setPromptText("Password (6+ characters)");
            d.getDialogPane().setContent(new VBox(10, ru, rp)); d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            d.showAndWait().ifPresent(b -> { if (b == ButtonType.OK) { String m = svc.register(ru.getText().trim(), rp.getText()); err.setText(m == null ? "Account created. Sign in on the Customer tab." : m); } });
        });
        Label h = new Label("Welcome back"); h.getStyleClass().add("h1");
        VBox card = new VBox(14, h, new HBox(cu, ad), u, p, err, go, reg);
        card.setPadding(new Insets(40)); card.setMaxWidth(430); card.setMaxHeight(Region.USE_PREF_SIZE); card.getStyleClass().add("card");
        StackPane right = new StackPane(card); right.getStyleClass().add("rightpane"); right.setPadding(new Insets(30)); right.setPrefWidth(520);
        return new HBox(left, right);
    }

    // ---------- Shell ----------
    void openShell() {
        boolean admin = "admin".equals(svc.role);
        VBox side = new VBox(6); side.getStyleClass().add("side"); side.setPrefWidth(220); side.setPadding(new Insets(20, 12, 20, 12));
        Label t = new Label("Samadhan"); t.getStyleClass().add("brand2"); side.getChildren().add(t);
        if (admin) { nav(side, "Dashboard", this::dashboard); nav(side, "All Complaints", this::allComplaints); nav(side, "Auto-Assignment", this::assignment); nav(side, "Pickup Routes", this::routes); nav(side, "Insights", this::insights); nav(side, "Escalation Paths", this::escalation); nav(side, "Algorithm Lab", this::lab); nav(side, "Resolution Optimizer", this::optimizer); nav(side, "Audit & Security", this::audit); }
        else { nav(side, "Home", this::home); nav(side, "My Complaints", this::mine); nav(side, "Track", this::track); }
        Region sp = new Region(); VBox.setVgrow(sp, Priority.ALWAYS);
        Label who = new Label("Signed in as " + svc.user);
        Button th = new Button("Switch light / dark"); th.setMaxWidth(Double.MAX_VALUE); th.setOnAction(e -> { dark = !dark; applyTheme(); });
        Button out = new Button("Log out"); out.setMaxWidth(Double.MAX_VALUE); out.setOnAction(e -> { svc.save(); svc.logout(); show(loginScene()); });
        side.getChildren().addAll(sp, who, th, out);
        content = new StackPane(); content.setPadding(new Insets(28));
        BorderPane root = new BorderPane(content); root.setLeft(side);
        show(root);
        if (admin) dashboard(); else home();
    }
    void nav(VBox side, String name, Runnable r) { Button b = new Button(name); b.getStyleClass().add("nav"); b.setMaxWidth(Double.MAX_VALUE); b.setOnAction(e -> r.run()); side.getChildren().add(b); }
    void set(String title, Node body) { Label h = new Label(title); h.getStyleClass().add("h1"); VBox v = new VBox(16, h, body); content.getChildren().setAll(v); }

    // ---------- Customer ----------
    void home() {
        FlowPane tiles = new FlowPane(16, 16);
        for (Issue is : Issue.values()) {
            Button b = new Button(is.label + "\nFile a complaint"); b.setPrefSize(220, 110); b.setWrapText(true);
            b.setStyle("-fx-background-color:" + is.color + ";-fx-text-fill:white;-fx-font-weight:bold;-fx-font-size:15;-fx-background-radius:14;");
            b.setOnAction(e -> issueForm(is)); tiles.getChildren().add(b);
        }
        set("What went wrong with your order?", tiles);
    }
    void issueForm(Issue is) {
        Label hd = new Label(is.label); hd.setStyle("-fx-font-size:22;-fx-text-fill:white;-fx-font-weight:bold;");
        Label wf = new Label("Steps: " + String.join("  >  ", is.flow)); wf.setStyle("-fx-text-fill:white;");
        VBox head = new VBox(6, hd, wf); head.setPadding(new Insets(18)); head.setStyle("-fx-background-color:" + is.color + ";-fx-background-radius:12;");
        TextField title = new TextField(); title.setPromptText("Short title");
        TextArea desc = new TextArea(); desc.setPromptText("Describe the problem"); desc.setPrefRowCount(4);
        TextField f1 = new TextField(), f2 = new TextField(); f1.setPromptText(is.fields[0]); f2.setPromptText(is.fields[1]);
        CheckBox anon = new CheckBox("Keep my report confidential"); anon.setVisible(false); anon.setManaged(false);
        ListView<String> sim = new ListView<>(); sim.setPrefHeight(110);
        Label sug = new Label();
        desc.textProperty().addListener((o, a, b) -> {
            sim.getItems().setAll(svc.similar(b)); Issue g = svc.suggest(b);
            sug.setText(g == null ? "" : g == is ? "Category looks right." : "This sounds like: " + g.label + ". You may want the other form.");
        });
        Button send = new Button("Submit complaint"); send.getStyleClass().add("primary");
        send.setStyle("-fx-background-color:" + is.color + ";");
        send.setOnAction(e -> {
            if (title.getText().isBlank() || desc.getText().isBlank()) { new Alert(Alert.AlertType.WARNING, "Add a title and a description.").show(); return; }
            String d = desc.getText() + "\n[" + is.fields[0] + ": " + f1.getText() + "; " + is.fields[1] + ": " + f2.getText() + "]";
            long id = svc.file(is, title.getText().trim(), d, anon.isSelected());
            svc.save();
            new Alert(Alert.AlertType.INFORMATION, "Complaint filed. Your ticket ID is " + id).show(); mine();
        });
        set("File a complaint", new VBox(10, head, title, desc, f1, f2, anon, sug, new Label("Similar complaints already filed"), sim, send));
    }
    void mine() {
        TextField q = new TextField(); q.setPromptText("Search your complaints");
        TableView<Complaint> t = table(data(null, svc.user));
        q.textProperty().addListener((o, a, b) -> t.setItems(data(b, svc.user)));
        HBox.setHgrow(q, Priority.ALWAYS);
        Button rate = new Button("Rate resolved complaint"); rate.getStyleClass().add("primary");
        rate.setOnAction(e -> {
            Complaint c = t.getSelectionModel().getSelectedItem();
            if (c == null || !c.done()) { new Alert(Alert.AlertType.INFORMATION, "Select a resolved complaint to rate it.").show(); return; }
            ChoiceDialog<Integer> d = new ChoiceDialog<>(5, 1, 2, 3, 4, 5); d.setHeaderText("How well was it resolved? (5 is best)");
            d.showAndWait().ifPresent(v -> { c.rating = v; svc.save(); t.refresh(); });
        });
        VBox.setVgrow(t, Priority.ALWAYS); set("My Complaints", new VBox(10, new HBox(10, q, rate), t));
    }
    void track() {
        TextField id = new TextField(); id.setPromptText("Ticket ID"); VBox out = new VBox(8); Button b = new Button("Track"); b.getStyleClass().add("primary");
        b.setOnAction(e -> {
            out.getChildren().clear(); Complaint c = svc.find(id.getText().trim());
            if (c == null || (!"admin".equals(svc.role) && !java.util.Objects.equals(c.owner, svc.user))) { out.getChildren().add(new Label("No complaint found with that ticket ID.")); return; }
            out.getChildren().add(new Label(c.issue.label + " — " + (c.anon ? "[confidential]" : c.title)));
            for (int i = 0; i < c.issue.flow.length; i++) {
                Label l = new Label((i <= c.status ? "●  " : "○  ") + c.issue.flow[i]);
                l.setStyle("-fx-font-size:15;-fx-text-fill:" + (i <= c.status ? c.issue.color : "#64748B") + ";"); out.getChildren().add(l);
            }
        });
        set("Track a complaint", new VBox(12, new HBox(8, id, b), out));
    }

    // ---------- Admin ----------
    void dashboard() {
        int tot = svc.all.size(), done = 0; int[] per = new int[Issue.values().length];
        for (int i = 0; i < tot; i++) { Complaint c = svc.all.get(i); per[c.issue.ordinal()]++; if (c.done()) done++; }
        int rs = 0, rn = 0; for (int i = 0; i < tot; i++) if (svc.all.get(i).rating > 0) { rs += svc.all.get(i).rating; rn++; }
        HBox cards = new HBox(14, stat("Total", "" + tot), stat("Resolved", "" + done), stat("Open", "" + (tot - done)), stat("Officers", "" + svc.cap.length), stat("Avg rating", rn == 0 ? "—" : String.format("%.1f", (double) rs / rn)));
        VBox bars = new VBox(10);
        for (Issue is : Issue.values()) {
            Label n = new Label(is.label); n.setPrefWidth(190);
            ProgressBar pb = new ProgressBar(tot == 0 ? 0 : (double) per[is.ordinal()] / tot); pb.setPrefWidth(420); pb.setStyle("-fx-accent:" + is.color + ";");
            bars.getChildren().add(new HBox(12, n, pb, new Label("" + per[is.ordinal()])));
        }
        PieChart pc = new PieChart(); pc.setPrefSize(420, 260); pc.setTitle("Complaints by type");
        for (Issue is : Issue.values()) pc.getData().add(new PieChart.Data(is.label, per[is.ordinal()]));
        ListView<String> ug = new ListView<>(); ug.getItems().setAll(svc.urgent(6)); ug.setPrefSize(420, 240);
        set("Dashboard", new VBox(16, cards, new HBox(20, pc, new VBox(6, new Label("Most urgent open complaints"), ug)), bars));
    }
    VBox stat(String name, String v) { Label b = new Label(v); b.getStyleClass().add("big"); VBox x = new VBox(2, b, new Label(name)); x.getStyleClass().add("stat"); return x; }
    void allComplaints() {
        TextField q = new TextField(); q.setPromptText("Smart search — typo tolerant, works in English, Hindi, Telugu"); HBox.setHgrow(q, Priority.ALWAYS);
        TableView<Complaint> t = table(data(null, null));
        q.textProperty().addListener((o, a, b) -> t.setItems(data(b, null)));
        Button exp = new Button("Export report (CSV)"); exp.setOnAction(e -> new Alert(Alert.AlertType.INFORMATION, "Saved to " + svc.export()).show());
        Button adv = new Button("Move to next step"); adv.getStyleClass().add("primary");
        adv.setOnAction(e -> { Complaint c = t.getSelectionModel().getSelectedItem(); if (c != null && !c.done()) { c.status++; t.refresh(); } });
        VBox.setVgrow(t, Priority.ALWAYS); set("All Complaints", new VBox(10, new HBox(10, q, adv, exp), t));
    }
    void assignment() {
        Label info = new Label(); TableView<Complaint> t = table(data(null, null));
        Button b2 = new Button("Assign at lowest travel cost"); b2.setOnAction(e -> { info.setText(svc.assignMinCost()); t.setItems(data(null, null)); });
        Button b = new Button("Assign open complaints"); b.getStyleClass().add("primary");
        b.setOnAction(e -> { int n = svc.assign(); info.setText(n + " open complaints assigned using max-flow, within each officer's capacity." + (svc.bottleneck.isEmpty() ? "" : " Bottleneck:" + svc.bottleneck)); t.setItems(data(null, null)); });
        VBox.setVgrow(t, Priority.ALWAYS); set("Auto-Assignment", new VBox(10, new HBox(12, b, b2, info), t));
    }
    void optimizer() {
        TextField b = new TextField("12"); ListView<String> l = new ListView<>(); Button go = new Button("Find best set"); go.getStyleClass().add("primary");
        go.setOnAction(e -> { try { l.getItems().setAll(svc.optimize(Integer.parseInt(b.getText().trim()))); } catch (NumberFormatException x) { new Alert(Alert.AlertType.WARNING, "Enter a whole number of hours.").show(); } });
        VBox.setVgrow(l, Priority.ALWAYS);
        TextField eps = new TextField("0.5"); eps.setPrefWidth(70); eps.setPromptText("epsilon");
        Button ap = new Button("Fast approximate (FPTAS)");
        ap.setOnAction(e -> { try { l.getItems().setAll(svc.optimizeApprox(Integer.parseInt(b.getText().trim()), Double.parseDouble(eps.getText().trim()))); } catch (NumberFormatException x) { new Alert(Alert.AlertType.WARNING, "Enter whole hours and a number like 0.5 for epsilon.").show(); } });
        set("Resolution Optimizer", new VBox(10, new Label("Hours available: picks the open complaints with the highest total priority that fit."), new HBox(10, b, go, eps, ap), l));
    }
    void audit() {
        ListView<String> l = new ListView<>(); Label t = new Label();
        Button a = new Button("Pick 5 resolved complaints to audit"); a.getStyleClass().add("primary"); a.setOnAction(e -> l.getItems().setAll(svc.audit(5)));
        Button g = new Button("Generate ticket ID"); g.setOnAction(e -> t.setText("New prime ticket ID: " + svc.newTicket()));
        VBox.setVgrow(l, Priority.ALWAYS); set("Audit & Security", new VBox(10, new HBox(10, a, g), t, l));
    }

    void routes() {
        ListView<String> l = new ListView<>(); Button b = new Button("Plan shortest pickup route"); b.getStyleClass().add("primary");
        b.setOnAction(e -> l.getItems().setAll(svc.route())); VBox.setVgrow(l, Priority.ALWAYS);
        set("Pickup Routes", new VBox(10, new Label("Finds the shortest round trip through every open damaged or wrong-item pickup."), b, l));
    }

    void insights() {
        BarChart<String, Number> bc = new BarChart<>(new CategoryAxis(), new NumberAxis()); bc.setTitle("Complaints per courier"); bc.setPrefHeight(340);
        XYChart.Series<String, Number> a = new XYChart.Series<>(), b = new XYChart.Series<>(); a.setName("All complaints"); b.setName("Resolved");
        int[][] st = svc.carrierStats();
        for (int i = 0; i < st.length; i++) { a.getData().add(new XYChart.Data<>(svc.carriers[i], st[i][0])); b.getData().add(new XYChart.Data<>(svc.carriers[i], st[i][1])); }
        bc.getData().add(a); bc.getData().add(b);
        Label w = new Label(svc.worstCarrier()), t = new Label(svc.trending()); t.setWrapText(true);
        set("Insights", new VBox(12, bc, w, t));
    }
    void applyTheme() {
        Scene sc = stage.getScene(); sc.getStylesheets().setAll(getClass().getResource("/style.css").toExternalForm());
        if (!dark) sc.getStylesheets().add(getClass().getResource("/style-light.css").toExternalForm());
    }

    void escalation() {
        ListView<String> l = new ListView<>(); l.getItems().setAll(svc.escalation()); VBox.setVgrow(l, Priority.ALWAYS);
        set("Escalation Paths", new VBox(10, new Label("Who a complaint escalates to, level by level, and how long each path takes."), l));
    }
    void lab() {
        TextField p = new TextField("days"); ListView<String> l = new ListView<>(); Button go = new Button("Compare algorithms"); go.getStyleClass().add("primary");
        go.setOnAction(e -> l.getItems().setAll(svc.lab(p.getText().trim())));
        TextField n = new TextField("561"); Label pr = new Label(); Button chk = new Button("Test if prime"); chk.setOnAction(e -> pr.setText(svc.prime(n.getText().trim())));
        VBox.setVgrow(l, Priority.ALWAYS);
        set("Algorithm Lab", new VBox(10, new Label("Runs four string-matching algorithms on all complaint text and compares steps and time."), new HBox(10, p, go), l, new Label("Primality check"), new HBox(10, n, chk), pr));
    }

    // ---------- Helpers ----------
    ObservableList<Complaint> data(String q, String owner) {
        ObservableList<Complaint> l = FXCollections.observableArrayList(); boolean s = q != null && !q.isBlank();
        for (int i = 0; i < svc.all.size(); i++) {
            Complaint c = svc.all.get(i);
            if (owner != null && !owner.equals(c.owner)) continue;
            if (s && ((owner == null && c.anon) || !svc.hits(c.text(), q.trim()))) continue;
            l.add(c);
        }
        return l;
    }
    @SuppressWarnings("deprecation")
    TableView<Complaint> table(ObservableList<Complaint> d) {
        TableView<Complaint> t = new TableView<>(d);
        t.getColumns().add(col("Ticket", c -> "" + c.id)); t.getColumns().add(col("Issue", c -> c.issue.label));
        t.getColumns().add(col("Title", c -> c.anon && "admin".equals(svc.role) ? "[confidential]" : c.title));
        t.getColumns().add(col("Status", Complaint::statusText)); t.getColumns().add(col("Priority", c -> "" + c.priority));
        t.getColumns().add(col("Officer", c -> c.officer < 0 ? "—" : svc.officerName(c.officer)));
        t.getColumns().add(col("Rating", c -> c.rating == 0 ? "—" : "★".repeat(c.rating)));
        t.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); return t;
    }
    TableColumn<Complaint, String> col(String n, Function<Complaint, String> f) {
        TableColumn<Complaint, String> c = new TableColumn<>(n); c.setCellValueFactory(d -> new SimpleStringProperty(f.apply(d.getValue()))); return c;
    }
}
