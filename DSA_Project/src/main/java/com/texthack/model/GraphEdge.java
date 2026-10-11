package com.texthack.model;

/**
 * Model representing a directed edge with capacity/weight and flow.
 */
public class GraphEdge {
    private final int from;
    private final int to;
    private final int capacity;
    private int flow;
    private final String label;

    public GraphEdge(int from, int to, int capacity) {
        this(from, to, capacity, "");
    }

    public GraphEdge(int from, int to, int capacity, String label) {
        this.from = from;
        this.to = to;
        this.capacity = capacity;
        this.flow = 0;
        this.label = label;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getFlow() {
        return flow;
    }

    public void setFlow(int flow) {
        this.flow = flow;
    }

    public int getResidualCapacity() {
        return capacity - flow;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return from + " -> " + to + " (flow: " + flow + "/" + capacity + (label.isEmpty() ? "" : " [" + label + "]") + ")";
    }
}
