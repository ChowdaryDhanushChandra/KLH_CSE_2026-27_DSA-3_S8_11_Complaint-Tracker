package com.texthack.ui.views;

import com.texthack.ui.components.Card;
import com.texthack.ui.components.ComplexityBadge;
import com.texthack.ui.shell.Router;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Standard curriculum overview view for algorithmic modules.
 */
public class PlaceholderModuleView extends ScrollPane {

    public PlaceholderModuleView(String moduleTitle, String description, String complexity,
                                  String[] algorithmList, Router router) {
        setFitToWidth(true);
        setStyle("-fx-background-color: transparent;");

        VBox content = new VBox(16);
        content.setPadding(new Insets(20));

        Label title = new Label(moduleTitle);
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #38BDF8;");

        Label sub = new Label(description);
        sub.setWrapText(true);
        sub.setStyle("-fx-font-size: 13px; -fx-text-fill: #94A3B8;");

        ComplexityBadge badge = new ComplexityBadge(complexity);
        HBox headerRow = new HBox(12, title, badge);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        VBox algoBox = new VBox(8);
        for (String algo : algorithmList) {
            Label algoLbl = new Label("• " + algo);
            algoLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #F8FAFC;");
            algoBox.getChildren().add(algoLbl);
        }

        Button exploreStringsBtn = new Button("⚡ Explore Active Module 2: String Algorithms →");
        exploreStringsBtn.getStyleClass().add("primary-btn");
        exploreStringsBtn.setOnAction(e -> router.navigateTo(Router.Route.MODULE2_STRINGS));

        Button compareBtn = new Button("🔬 Open Comparison Hub →");
        compareBtn.getStyleClass().add("secondary-btn");
        compareBtn.setOnAction(e -> router.navigateTo(Router.Route.COMPARISON_HUB));

        HBox btnRow = new HBox(12, exploreStringsBtn, compareBtn);

        Card card = new Card("Curriculum Algorithmic Canon", "Topics & Hand-Built Engine Specification",
                new VBox(14, algoBox, btnRow));

        content.getChildren().addAll(headerRow, sub, card);
        setContent(content);
    }
}
