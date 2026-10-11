package com.texthack.ui.views;

import com.texthack.config.AppConfig;
import com.texthack.ui.components.Card;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

/**
 * About and credits view displaying course info and team member attributions.
 */
public class AboutView extends ScrollPane {

    public AboutView() {
        setFitToWidth(true);
        setStyle("-fx-background-color: transparent;");

        VBox content = new VBox(16);
        content.setPadding(new Insets(20));

        Label title = new Label("ℹ About TextHack");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #38BDF8;");

        Label sub = new Label("Advanced Algorithm Question Bank & Text Analytics Workbench");
        sub.setStyle("-fx-font-size: 12px; -fx-text-fill: #94A3B8;");

        VBox infoBox = new VBox(8);
        infoBox.getChildren().addAll(
                new Label("Course: " + AppConfig.COURSE_NAME),
                new Label("Authors: " + String.join(", ", AppConfig.TEAM_MEMBERS)),
                new Label("Framework: JavaFX 21.0.2 on Java 25 LTS"),
                new Label("Engine Constraint: Strictly ZERO java.util.* collections (Hand-built core.ds)"),
                new Label("Offline Corpus: English, Hindi (Devanagari), Telugu"),
                new Label("Module Status: Module 2 (String Algorithms) fully implemented with step player & animated traces.")
        );
        for (javafx.scene.Node n : infoBox.getChildren()) {
            if (n instanceof Label) {
                ((Label) n).setStyle("-fx-font-size: 13px; -fx-text-fill: #F8FAFC;");
            }
        }

        Card card = new Card("Project Specification & Attributions", "DSA-3 Educational Workbench", infoBox);

        content.getChildren().addAll(new VBox(4, title, sub), card);
        setContent(content);
    }
}
