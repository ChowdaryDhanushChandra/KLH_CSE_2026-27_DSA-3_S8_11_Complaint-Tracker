package com.texthack.engine.module1;

import com.texthack.graph.EdmondsKarp;
import com.texthack.graph.MaxFlowNetwork;

/**
 * Models academic citation flow and intellectual influence as a maximum flow network.
 * Demonstrates the Edmonds-Karp augmenting path algorithm.
 */
public class CitationFlowService {

    public EdmondsKarp.MaxFlowResult analyzeCitationFlow(int vertices, int[][] edges, int source, int sink) {
        MaxFlowNetwork network = new MaxFlowNetwork(vertices);

        for (int i = 0; i < edges.length; i++) {
            int u = edges[i][0];
            int v = edges[i][1];
            int cap = edges[i][2];
            network.addEdge(u, v, cap);
        }

        return EdmondsKarp.computeMaxFlow(network, source, sink);
    }
}
