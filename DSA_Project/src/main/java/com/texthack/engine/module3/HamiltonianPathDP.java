package com.texthack.engine.module3;

import com.texthack.datastructures.ManualArrayList;
import com.texthack.metrics.ComplexityAnalyzer;
import com.texthack.metrics.ExecutionTimer;
import com.texthack.model.AlgorithmResult;
import com.texthack.validation.InputValidator;

/**
 * Detects Hamiltonian Paths using Bitmask Dynamic Programming in O(2^n · n²).
 */
public class HamiltonianPathDP {

    public static class HamResult {
        public final boolean exists;
        public final String path;
        public final AlgorithmResult algorithmResult;

        public HamResult(boolean exists, String path, AlgorithmResult algoRes) {
            this.exists = exists;
            this.path = path;
            this.algorithmResult = algoRes;
        }
    }

    public static HamResult findPath(boolean[][] adj) {
        ExecutionTimer timer = new ExecutionTimer();
        timer.start();

        int n = adj.length;
        InputValidator.validateBitmaskSize(n, 16);

        ManualArrayList<String> trace = new ManualArrayList<>();
        int totalStates = 1 << n;
        boolean[][] dp = new boolean[totalStates][n];
        int[][] parent = new int[totalStates][n];

        for (int mask = 0; mask < totalStates; mask++) {
            for (int u = 0; u < n; u++) {
                parent[mask][u] = -1;
            }
        }

        // Base case: path of length 1 starting at any node u
        for (int i = 0; i < n; i++) {
            dp[1 << i][i] = true;
        }

        for (int mask = 1; mask < totalStates; mask++) {
            for (int u = 0; u < n; u++) {
                if (!dp[mask][u]) continue;

                for (int v = 0; v < n; v++) {
                    if ((mask & (1 << v)) == 0 && adj[u][v]) {
                        int nextMask = mask | (1 << v);
                        dp[nextMask][v] = true;
                        parent[nextMask][v] = u;
                    }
                }
            }
        }

        int fullMask = (1 << n) - 1;
        int endNode = -1;
        for (int i = 0; i < n; i++) {
            if (dp[fullMask][i]) {
                endNode = i;
                break;
            }
        }

        boolean exists = (endNode != -1);
        StringBuilder pathStr = new StringBuilder();

        if (exists) {
            ManualArrayList<Integer> rev = new ManualArrayList<>();
            int curr = endNode;
            int currMask = fullMask;
            while (curr != -1 && currMask > 0) {
                rev.add(curr);
                int p = parent[currMask][curr];
                currMask ^= (1 << curr);
                curr = p;
            }

            for (int i = rev.size() - 1; i >= 0; i--) {
                pathStr.append(rev.get(i));
                if (i > 0) pathStr.append(" -> ");
            }
            trace.add("Found Hamiltonian path: " + pathStr.toString());
        } else {
            pathStr.append("No Hamiltonian path exists in this graph.");
            trace.add("No Hamiltonian path visiting all " + n + " vertices.");
        }

        timer.stop();

        AlgorithmResult algoRes = new AlgorithmResult(
                "Hamiltonian Path DP",
                exists ? "Path Found: " + pathStr.toString() : "Path Not Found",
                "Bitmask existence query on graph with " + n + " vertices.",
                timer.getElapsedNanos(),
                timer.getFormattedTime(),
                0, 0,
                ComplexityAnalyzer.getComplexity("Hamiltonian Path DP"),
                trace
        );

        return new HamResult(exists, pathStr.toString(), algoRes);
    }
}
