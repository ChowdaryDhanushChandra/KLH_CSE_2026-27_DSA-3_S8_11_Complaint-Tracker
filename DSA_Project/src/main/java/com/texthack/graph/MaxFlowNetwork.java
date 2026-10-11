package com.texthack.graph;

import com.texthack.datastructures.ManualArrayList;

/**
 * Residual graph network representation for maximum flow algorithms without java.util.*.
 */
public class MaxFlowNetwork {

    public static class FlowEdge {
        public final int from;
        public final int to;
        public final int capacity;
        public int flow;
        public int rev; // Index of reciprocal edge in adj[to]

        public FlowEdge(int from, int to, int capacity, int rev) {
            this.from = from;
            this.to = to;
            this.capacity = capacity;
            this.flow = 0;
            this.rev = rev;
        }

        public int remainingCapacity() {
            return capacity - flow;
        }
    }

    private final int vertices;
    private final ManualArrayList<FlowEdge>[] adj;

    @SuppressWarnings("unchecked")
    public MaxFlowNetwork(int vertices) {
        if (vertices < 0) {
            throw new IllegalArgumentException("Vertices cannot be negative");
        }
        this.vertices = vertices;
        this.adj = new ManualArrayList[vertices];
        for (int i = 0; i < vertices; i++) {
            adj[i] = new ManualArrayList<>();
        }
    }

    public void addEdge(int from, int to, int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity must be non-negative");
        }
        FlowEdge forward = new FlowEdge(from, to, capacity, adj[to].size());
        FlowEdge backward = new FlowEdge(to, from, 0, adj[from].size());
        adj[from].add(forward);
        adj[to].add(backward);
    }

    public int getVertices() {
        return vertices;
    }

    public ManualArrayList<FlowEdge> getEdges(int u) {
        return adj[u];
    }
}
