package com.texthack.graph;

import com.texthack.datastructures.ManualArrayList;
import com.texthack.datastructures.ManualQueue;
import com.texthack.metrics.ExecutionTimer;
import com.texthack.metrics.OperationCounter;
import com.texthack.model.AlgorithmResult;
import com.texthack.metrics.ComplexityAnalyzer;

/**
 * Edmonds-Karp maximum flow algorithm using BFS without java.util.*.
 * Time Complexity: O(V · E^2).
 */
public class EdmondsKarp {

    public static class MaxFlowResult {
        public final int maxFlow;
        public final int augmentations;
        public final ManualArrayList<String> traceLogs;
        public final AlgorithmResult algorithmResult;

        public MaxFlowResult(int maxFlow, int augmentations, ManualArrayList<String> traceLogs, AlgorithmResult algorithmResult) {
            this.maxFlow = maxFlow;
            this.augmentations = augmentations;
            this.traceLogs = traceLogs;
            this.algorithmResult = algorithmResult;
        }
    }

    public static MaxFlowResult computeMaxFlow(MaxFlowNetwork network, int source, int sink) {
        ExecutionTimer timer = new ExecutionTimer();
        OperationCounter counter = new OperationCounter();
        timer.start();

        int n = network.getVertices();
        int totalFlow = 0;
        int augmentations = 0;
        ManualArrayList<String> trace = new ManualArrayList<>();

        trace.add("Beginning Edmonds-Karp algorithm from Source: " + source + " to Sink: " + sink);

        while (true) {
            // Parent array stores the incoming FlowEdge that reached vertex v
            MaxFlowNetwork.FlowEdge[] parentEdge = new MaxFlowNetwork.FlowEdge[n];
            boolean[] visited = new boolean[n];

            ManualQueue<Integer> queue = new ManualQueue<>();
            queue.enqueue(source);
            visited[source] = true;

            while (!queue.isEmpty()) {
                int u = queue.dequeue();
                counter.incrementOperations();

                if (u == sink) break;

                ManualArrayList<MaxFlowNetwork.FlowEdge> edges = network.getEdges(u);
                for (int i = 0; i < edges.size(); i++) {
                    MaxFlowNetwork.FlowEdge edge = edges.get(i);
                    counter.incrementComparisons();

                    if (!visited[edge.to] && edge.remainingCapacity() > 0) {
                        visited[edge.to] = true;
                        parentEdge[edge.to] = edge;
                        queue.enqueue(edge.to);
                    }
                }
            }

            // If sink was not reached, no augmenting path remains
            if (!visited[sink]) {
                break;
            }

            // Find bottleneck capacity along the augmenting path
            int push = Integer.MAX_VALUE;
            for (int v = sink; v != source; v = parentEdge[v].from) {
                push = Math.min(push, parentEdge[v].remainingCapacity());
            }

            // Augment flow and construct path representation
            StringBuilder pathStr = new StringBuilder();
            ManualArrayList<Integer> pathNodes = new ManualArrayList<>();
            for (int v = sink; v != source; v = parentEdge[v].from) {
                pathNodes.add(v);
            }
            pathNodes.add(source);

            for (int i = pathNodes.size() - 1; i >= 0; i--) {
                pathStr.append(pathNodes.get(i));
                if (i > 0) pathStr.append(" -> ");
            }

            for (int v = sink; v != source; v = parentEdge[v].from) {
                MaxFlowNetwork.FlowEdge edge = parentEdge[v];
                edge.flow += push;
                // Update residual back-edge
                network.getEdges(edge.to).get(edge.rev).flow -= push;
            }

            totalFlow += push;
            augmentations++;
            trace.add("Augmentation #" + augmentations + ": Path [" + pathStr.toString() 
                    + "] with Bottleneck Capacity = " + push + " (Current Flow = " + totalFlow + ")");
        }

        timer.stop();
        trace.add("Max flow computation complete. Final Maximum Flow = " + totalFlow 
                + " across " + augmentations + " augmenting paths.");

        AlgorithmResult algoRes = new AlgorithmResult(
                "Edmonds-Karp",
                "Maximum Flow = " + totalFlow + " (Augmentations: " + augmentations + ")",
                "Successfully found " + augmentations + " shortest augmenting paths via BFS.",
                timer.getElapsedNanos(),
                timer.getFormattedTime(),
                counter.getComparisons(),
                counter.getOperations(),
                ComplexityAnalyzer.getComplexity("Edmonds-Karp"),
                trace
        );

        return new MaxFlowResult(totalFlow, augmentations, trace, algoRes);
    }
}
