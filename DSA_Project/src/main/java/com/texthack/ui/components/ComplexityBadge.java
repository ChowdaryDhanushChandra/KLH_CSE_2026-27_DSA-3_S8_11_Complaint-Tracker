package com.texthack.ui.components;

import javafx.scene.control.Label;

/**
 * Visual pill badge displaying theoretical time or space complexity (e.g. O(n + m)).
 */
public class ComplexityBadge extends Label {

    public enum BadgeType {
        PRIMARY("complexity-badge"),
        ACCENT("badge-accent"),
        WARNING("badge-warning"),
        DANGER("badge-danger");

        private final String cssClass;
        BadgeType(String cssClass) {
            this.cssClass = cssClass;
        }
        public String getCssClass() { return cssClass; }
    }

    public ComplexityBadge(String text) {
        this(text, BadgeType.PRIMARY);
    }

    public ComplexityBadge(String text, BadgeType type) {
        super(text);
        getStyleClass().add(type.getCssClass());
    }

    public void setBadgeType(BadgeType type) {
        getStyleClass().removeAll("complexity-badge", "badge-accent", "badge-warning", "badge-danger");
        getStyleClass().add(type.getCssClass());
    }
}
