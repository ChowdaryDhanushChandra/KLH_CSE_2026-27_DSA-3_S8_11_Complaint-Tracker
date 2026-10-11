module com.texthack {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires static org.junit.jupiter.api;

    exports com.texthack;
    exports com.texthack.config;
    exports com.texthack.model;
    exports com.texthack.controller;
    exports com.texthack.service;
    exports com.texthack.datastructures;
    exports com.texthack.graph;
    exports com.texthack.metrics;
    exports com.texthack.validation;
    exports com.texthack.util;
    exports com.texthack.engine.common;
    exports com.texthack.engine.module1;
    exports com.texthack.engine.module2;
    exports com.texthack.engine.module3;
    exports com.texthack.core.ds;
    exports com.texthack.core.model;
    exports com.texthack.core.util;
    exports com.texthack.module2_strings;
    exports com.texthack.ui.theme;
    exports com.texthack.ui.components;
    exports com.texthack.ui.shell;
    exports com.texthack.ui.views;

    opens com.texthack to javafx.fxml;
    opens com.texthack.controller to javafx.fxml;
    opens com.texthack.model to javafx.base;
    opens com.texthack.ui.views to javafx.base, javafx.fxml;
    opens com.texthack.ui.components to javafx.base, javafx.fxml;
    opens com.texthack.core.ds to org.junit.platform.commons;
    opens com.texthack.core.model to org.junit.platform.commons;
    opens com.texthack.module2_strings to org.junit.platform.commons;

    exports complainttracker;
    exports complainttracker.models;
    exports complainttracker.algorithms;
    exports complainttracker.services;
    exports complainttracker.data;
    exports complainttracker.utils;
    exports complainttracker.views;

    opens complainttracker to javafx.fxml, javafx.graphics;
    opens complainttracker.models to javafx.base;
    opens complainttracker.views to javafx.fxml, javafx.graphics;
    opens complainttracker.algorithms to org.junit.platform.commons;
}
