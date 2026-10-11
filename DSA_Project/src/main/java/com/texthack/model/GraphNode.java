package com.texthack.model;

/**
 * Model representing a node/vertex in an algorithmic graph.
 */
public class GraphNode {
    private final int id;
    private final String label;

    public GraphNode(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraphNode graphNode = (GraphNode) o;
        return id == graphNode.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return label != null ? label : "Node-" + id;
    }
}
