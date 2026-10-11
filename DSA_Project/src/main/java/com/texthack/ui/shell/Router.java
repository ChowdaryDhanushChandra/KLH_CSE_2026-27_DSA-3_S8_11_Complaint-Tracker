package com.texthack.ui.shell;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Manages view routing and smooth 200ms animated transitions.
 */
public class Router {

    public enum Route {
        DASHBOARD("Dashboard", "Overview & Metrics"),
        MODULE1_SYSTEM("Module 1: System", "Query Engine & Reductions"),
        MODULE2_STRINGS("Module 2: Strings", "Exact & Multi-Pattern Matchers"),
        MODULE3_DP("Module 3: Adv DP", "Alignment, Interval & Bitmask DP"),
        MODULE4_FLOW("Module 4: Flow", "Edmonds-Karp & Min-Cut"),
        MODULE5_NP("Module 5: NP & Reductions", "SAT, HAM & Approximation"),
        MODULE6_RANDOMIZED("Module 6: Randomized", "Quicksort & Miller-Rabin"),
        COMPARISON_HUB("Comparison Hub", "Side-by-Side Benchmarking"),
        CORPUS_EXPLORER("Corpus Explorer", "Multilingual Indian Text"),
        ALGORITHM_CANON("Algorithm Canon", "CLRS & Textbook Concordance"),
        ALGORITHM_DESIGN("Algorithm Design", "Scenario Quiz & Analysis"),
        ABOUT("About", "Team & Project Specification");

        private final String title;
        private final String subtitle;

        Route(String title, String subtitle) {
            this.title = title;
            this.subtitle = subtitle;
        }

        public String getTitle() { return title; }
        public String getSubtitle() { return subtitle; }
    }

    @FunctionalInterface
    public interface RouteChangeListener {
        void onRouteChange(Route newRoute);
    }

    private final StackPane container;
    private final Map<Route, Node> viewCache = new HashMap<>();
    private final Map<Route, Supplier<Node>> viewSuppliers = new HashMap<>();
    private Route currentRoute;
    private RouteChangeListener listener;

    public Router(StackPane container) {
        this.container = container;
    }

    public void registerView(Route route, Supplier<Node> supplier) {
        viewSuppliers.put(route, supplier);
    }

    public void setRouteChangeListener(RouteChangeListener listener) {
        this.listener = listener;
    }

    public Route getCurrentRoute() {
        return currentRoute;
    }

    public void navigateTo(Route route) {
        if (route == null) return;
        this.currentRoute = route;

        Node targetView = viewCache.get(route);
        if (targetView == null && viewSuppliers.containsKey(route)) {
            targetView = viewSuppliers.get(route).get();
            viewCache.put(route, targetView);
        }

        if (targetView != null) {
            container.getChildren().clear();
            targetView.setOpacity(0.0);
            container.getChildren().add(targetView);

            // Smooth 200ms fade transition
            FadeTransition ft = new FadeTransition(Duration.millis(200), targetView);
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.play();
        }

        if (listener != null) {
            listener.onRouteChange(route);
        }
    }
}
