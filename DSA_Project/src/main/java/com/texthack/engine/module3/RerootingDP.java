package com.texthack.engine.module3;

import com.texthack.datastructures.ManualArrayList;
import com.texthack.metrics.ComplexityAnalyzer;
import com.texthack.metrics.ExecutionTimer;
import com.texthack.model.AlgorithmResult;

/**
 * Tree Rerooting Technique: computes sum of distances to all vertices for EVERY node in O(V) time.
 * Avoids naive O(V²) repeated traversals.
 */
public class RerootingDP {

    public static class RerootResult {
        public final long[] allDistancesSum;
        public final int bestRoot;
        public final long minDistanceSum;
        public final AlgorithmResult algorithmResult;

        public RerootResult(long[] allDistances, int bestRoot, long minDist, AlgorithmResult algoRes) {
            this.allDistancesSum = allDistances;
            this.bestRoot = bestRoot;
            this.minDistanceSum = minDist;
            this.algorithmResult = algoRes;
        }
    }

    public static RerootResult computeAllDistances(int n, ManualArrayList<Integer>[] tree) {
        ExecutionTimer timer = new ExecutionTimer();
        timer.start();

        ManualArrayList<String> trace = new ManualArrayList<>();
        if (n <= 1) {
            timer.stop();
            AlgorithmResult res = new AlgorithmResult("Rerooting DP", "Dist: 0", "Trivial tree",
                    0, "0 ns", 0, 0, ComplexityAnalyzer.getComplexity("Rerooting DP"), trace);
            return new RerootResult(new long[n], 0, 0, res);
        }

        int[] sz = new int[n];
        long[] inDist = new long[n];
        long[] ans = new long[n];

        // Pass 1: Bottom-up DP to compute subtree sizes and in-distances for root 0
        dfs1(0, -1, tree, sz, inDist, trace);
        ans[0] = inDist[0];
        trace.add(String.format("Pass 1 (Bottom-up) finished: Root 0 total distance sum = %d", ans[0]));

        // Pass 2: Top-down rerooting transition: ans[v] = ans[u] - sz[v] + (n - sz[v])
        dfs2(0, -1, n, tree, sz, ans, trace);
        trace.add("Pass 2 (Top-down Rerooting) finished for all " + n + " nodes in O(V) time.");

        int bestRoot = 0;
        long minDist = ans[0];
        for (int i = 1; i < n; i++) {
            if (ans[i] < minDist) {
                minDist = ans[i];
                bestRoot = i;
            }
        }

        timer.stop();
        trace.add(String.format("Optimal Tree Center / Best Root = Node %d with minimum total distance = %d",
                bestRoot, minDist));

        AlgorithmResult algoRes = new AlgorithmResult(
                "Rerooting DP",
                "Best Center Node: " + bestRoot + " (Sum of distances: " + minDist + ")",
                "Solved in 2 linear DFS passes. ans[v] = ans[u] - sz[v] + (n - sz[v])",
                timer.getElapsedNanos(),
                timer.getFormattedTime(),
                0, 0,
                ComplexityAnalyzer.getComplexity("Rerooting DP"),
                trace
        );

        return new RerootResult(ans, bestRoot, minDist, algoRes);
    }

    private static void dfs1(int u, int p, ManualArrayList<Integer>[] tree, int[] sz, long[] inDist, ManualArrayList<String> trace) {
        sz[u] = 1;
        inDist[u] = 0;

        ManualArrayList<Integer> neighbors = tree[u];
        for (int i = 0; i < neighbors.size(); i++) {
            int v = neighbors.get(i);
            if (v != p) {
                dfs1(v, u, tree, sz, inDist, trace);
                sz[u] += sz[v];
                inDist[u] += inDist[v] + sz[v];
            }
        }
    }

    private static void dfs2(int u, int p, int n, ManualArrayList<Integer>[] tree, int[] sz, long[] ans, ManualArrayList<String> trace) {
        ManualArrayList<Integer> neighbors = tree[u];
        for (int i = 0; i < neighbors.size(); i++) {
            int v = neighbors.get(i);
            if (v != p) {
                // Rerooting equation
                ans[v] = ans[u] - sz[v] + (n - sz[v]);
                trace.add(String.format("Reroot from %d to %d: ans[%d] = %d - %d + (%d - %d) = %d",
                        u, v, v, ans[u], sz[v], n, sz[v], ans[v]));
                dfs2(v, u, n, tree, sz, ans, trace);
            }
        }
    }
}
