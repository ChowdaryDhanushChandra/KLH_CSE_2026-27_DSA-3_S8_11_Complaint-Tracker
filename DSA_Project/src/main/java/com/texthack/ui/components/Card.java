package com.texthack.ui.components;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Reusable Card component with 12px rounded borders, subtle drop shadow,
 * and standard padding per the TextHack design system.
 */
public class Card extends VBox {

    public Card() {
        super(10);
        getStyleClass().add("card");
        setPadding(new Insets(16));
    }

    public Card(String title, String subtitle, Node content) {
        this();
        if (title != null && !title.isEmpty()) {
            Label titleLabel = new Label(title);
            titleLabel.getStyleClass().add("card-title");
            getChildren().add(titleLabel);
        }
        if (subtitle != null && !subtitle.isEmpty()) {
            Label subLabel = new Label(subtitle);
            subLabel.getStyleClass().add("card-subtitle");
            getChildren().add(subLabel);
        }
        if (content != null) {
            getChildren().add(content);
        }
    }

    public Card(Node... children) {
        this();
        getChildren().addAll(children);
    }
}
