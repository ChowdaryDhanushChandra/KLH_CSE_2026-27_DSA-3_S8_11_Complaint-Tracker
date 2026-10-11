package com.texthack.graph;

import com.texthack.datastructures.ManualArrayList;
import com.texthack.datastructures.ManualQueue;
import com.texthack.model.GraphEdge;

/**
 * Directed Graph implemented with adjacency lists without using java.util.*.
 */
public class DirectedGraph {
    private final int vertices;
    private final ManualArrayList<GraphEdge>[] adj;
    private final int[] inDegree;
    private int edgeCount;

    @SuppressWarnings("unchecked")
    public DirectedGraph(int vertices) {
        if (vertices < 0) {
            throw new IllegalArgumentException("Vertices cannot be negative: " + vertices);
        }
        this.vertices = vertices;
        this.adj = new ManualArrayList[vertices];
        this.inDegree = new int[vertices];
        for (int i = 0; i < vertices; i++) {
            adj[i] = new ManualArrayList<>();
        }
        this.edgeCount = 0;
    }

    public void addEdge(int from, int to, int weight) {
        addEdge(from, to, weight, "");
    }

    public void addEdge(int from, int to, int weight, String label) {
        checkVertex(from);
        checkVertex(to);
        adj[from].add(new GraphEdge(from, to, weight, label));
        inDegree[to]++;
        edgeCount++;
    }

    public int getVertices() {
        return vertices;
    }

    public int getEdgeCount() {
        return edgeCount;
    }

    public ManualArrayList<GraphEdge> getEdges(int u) {
        checkVertex(u);
        return adj[u];
    }

    public int getInDegree(int u) {
        checkVertex(u);
        return inDegree[u];
    }

    public int getOutDegree(int u) {
        checkVertex(u);
        return adj[u].size();
    }

    /**
     * Kahn's algorithm for topological sorting of DAG without java.util.*.
     * Returns ordered list of vertex IDs, or empty list if a cycle is detected.
     */
    public ManualArrayList<Integer> topologicalSort() {
        int[] inDeg = new int[vertices];
        for (int i = 0; i < vertices; i++) {
            inDeg[i] = inDegree[i];
        }

        ManualQueue<Integer> queue = new ManualQueue<>();
        for (int i = 0; i < vertices; i++) {
            if (inDeg[i] == 0) {
                queue.enqueue(i);
            }
        }

        ManualArrayList<Integer> topoOrder = new ManualArrayList<>();
        while (!queue.isEmpty()) {
            int u = queue.dequeue();
            topoOrder.add(u);

            ManualArrayList<GraphEdge> neighbors = adj[u];
            for (int i = 0; i < neighbors.size(); i++) {
                int v = neighbors.get(i).getTo();
                inDeg[v]--;
                if (inDeg[v] == 0) {
                    queue.enqueue(v);
                }
            }
        }

        if (topoOrder.size() != vertices) {
            // Cycle detected
            return new ManualArrayList<>();
        }
        return topoOrder;
    }

    private void checkVertex(int v) {
        if (v < 0 || v >= vertices) {
            throw new IndexOutOfBoundsException("Vertex " + v + " out of bounds [0, " + (vertices - 1) + "]");
        }
    }
}
