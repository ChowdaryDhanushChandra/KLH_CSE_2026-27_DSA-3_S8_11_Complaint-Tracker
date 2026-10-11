package com.texthack.engine.module3;

import com.texthack.datastructures.ManualArrayList;
import com.texthack.metrics.ComplexityAnalyzer;
import com.texthack.metrics.ExecutionTimer;
import com.texthack.model.AlgorithmResult;
import com.texthack.validation.InputValidator;

/**
 * Held-Karp algorithm for Travelling Salesman Problem (TSP) via Bitmask Dynamic Programming.
 * Time Complexity: O(2^n · n²), Space Complexity: O(2^n · n).
 * Strictly guards against n > 16 to avoid exponential memory allocation.
 */
public class TravellingSalesmanDP {

    public static final int MAX_SAFE_NODES = 16;
    private static final int INF = 1_000_000_000;

    public static class TSPResult {
        public final int minCost;
        public final ManualArrayList<Integer> tour;
        public final String tourString;
        public final AlgorithmResult algorithmResult;

        public TSPResult(int minCost, ManualArrayList<Integer> tour, String tourString, AlgorithmResult algoRes) {
            this.minCost = minCost;
            this.tour = tour;
            this.tourString = tourString;
            this.algorithmResult = algoRes;
        }
    }

    public static TSPResult solve(int[][] dist) {
        ExecutionTimer timer = new ExecutionTimer();
        timer.start();

        ManualArrayList<String> trace = new ManualArrayList<>();

        if (dist == null || dist.length == 0) {
            timer.stop();
            AlgorithmResult res = new AlgorithmResult("Travelling Salesman (TSP DP)", "0", "Empty matrix",
                    0, "0 ns", 0, 0, ComplexityAnalyzer.getComplexity("Travelling Salesman (TSP DP)"), trace);
            return new TSPResult(0, new ManualArrayList<>(), "Empty", res);
        }

        int n = dist.length;
        InputValidator.validateBitmaskSize(n, MAX_SAFE_NODES);

        int totalStates = 1 << n;
        int[][] dp = new int[totalStates][n];
        int[][] parent = new int[totalStates][n];

        for (int mask = 0; mask < totalStates; mask++) {
            for (int u = 0; u < n; u++) {
                dp[mask][u] = INF;
                parent[mask][u] = -1;
            }
        }

        // Start at vertex 0 with mask (1 << 0)
        dp[1][0] = 0;
        trace.add("Initialized TSP Bitmask DP: Start at Node 0. Total bitmask states = 2^" + n + " = " + totalStates);

        for (int mask = 1; mask < totalStates; mask++) {
            for (int u = 0; u < n; u++) {
                if ((mask & (1 << u)) == 0 || dp[mask][u] >= INF) continue;

                for (int v = 0; v < n; v++) {
                    if ((mask & (1 << v)) == 0 && dist[u][v] > 0 && dist[u][v] < INF) {
                        int nextMask = mask | (1 << v);
                        int newCost = dp[mask][u] + dist[u][v];
                        if (newCost < dp[nextMask][v]) {
                            dp[nextMask][v] = newCost;
                            parent[nextMask][v] = u;
                        }
                    }
                }
            }
        }

        // Close the tour returning to vertex 0
        int fullMask = (1 << n) - 1;
        int minCost = INF;
        int lastNode = -1;

        for (int u = 1; u < n; u++) {
            if (dist[u][0] > 0 && dist[u][0] < INF && dp[fullMask][u] < INF) {
                int cost = dp[fullMask][u] + dist[u][0];
                if (cost < minCost) {
                    minCost = cost;
                    lastNode = u;
                }
            }
        }

        ManualArrayList<Integer> tour = new ManualArrayList<>();
        StringBuilder tourStr = new StringBuilder();

        if (minCost < INF) {
            int curr = lastNode;
            int currMask = fullMask;
            ManualArrayList<Integer> revTour = new ManualArrayList<>();
            revTour.add(0); // return to 0

            while (curr != -1 && currMask > 0) {
                revTour.add(curr);
                int p = parent[currMask][curr];
                currMask = currMask ^ (1 << curr);
                curr = p;
            }

            for (int i = revTour.size() - 1; i >= 0; i--) {
                tour.add(revTour.get(i));
                tourStr.append(revTour.get(i));
                if (i > 0) tourStr.append(" -> ");
            }
            trace.add("Found optimal Hamiltonian cycle: " + tourStr.toString() + " with Total Distance = " + minCost);
        } else {
            tourStr.append("No valid Hamiltonian cycle exists in this graph.");
            trace.add("Graph is not fully connected for Hamiltonian cycle.");
        }

        timer.stop();

        AlgorithmResult algoRes = new AlgorithmResult(
                "Travelling Salesman (TSP DP)",
                minCost < INF ? "Min Tour Cost: " + minCost : "No Cycle",
                "Optimal Tour: " + tourStr.toString(),
                timer.getElapsedNanos(),
                timer.getFormattedTime(),
                0, 0,
                ComplexityAnalyzer.getComplexity("Travelling Salesman (TSP DP)"),
                trace
        );

        return new TSPResult(minCost, tour, tourStr.toString(), algoRes);
    }
}
