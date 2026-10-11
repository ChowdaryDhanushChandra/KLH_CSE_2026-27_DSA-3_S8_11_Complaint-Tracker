package com.texthack.engine.module3;

import com.texthack.datastructures.ManualArrayList;
import com.texthack.metrics.ComplexityAnalyzer;
import com.texthack.metrics.ExecutionTimer;
import com.texthack.model.AlgorithmResult;

/**
 * Maximum Weight Independent Set on Trees using Subtree Dynamic Programming in O(V) time.
 */
public class TreeDP {

    public static class TreeDPResult {
        public final int maxIndependentWeight;
        public final int[][] dp;
        public final AlgorithmResult algorithmResult;

        public TreeDPResult(int maxWeight, int[][] dp, AlgorithmResult algoRes) {
            this.maxIndependentWeight = maxWeight;
            this.dp = dp;
            this.algorithmResult = algoRes;
        }
    }

    public static TreeDPResult solveIndependentSet(int n, ManualArrayList<Integer>[] tree, int[] weights) {
        ExecutionTimer timer = new ExecutionTimer();
        timer.start();

        ManualArrayList<String> trace = new ManualArrayList<>();
        if (n == 0) {
            timer.stop();
            AlgorithmResult res = new AlgorithmResult("Tree DP (Independent Set)", "0", "Empty tree",
                    0, "0 ns", 0, 0, ComplexityAnalyzer.getComplexity("Tree DP (Independent Set)"), trace);
            return new TreeDPResult(0, new int[0][0], res);
        }

        // dp[u][0]: max weight when u is EXCLUDED
        // dp[u][1]: max weight when u is INCLUDED
        int[][] dp = new int[n][2];
        boolean[] visited = new boolean[n];

        dfs(0, -1, tree, weights, dp, visited, trace);

        int maxWeight = Math.max(dp[0][0], dp[0][1]);
        timer.stop();

        trace.add(String.format("Tree DP finished: Root Node 0 optimal independent set weight = %d (Exclude: %d, Include: %d)",
                maxWeight, dp[0][0], dp[0][1]));

        AlgorithmResult algoRes = new AlgorithmResult(
                "Tree DP (Independent Set)",
                "Max Independent Set Weight: " + maxWeight,
                "Solved via post-order subtree DP: dp[u][0]=sum(max(dp[v][0],dp[v][1])), dp[u][1]=w[u]+sum(dp[v][0])",
                timer.getElapsedNanos(),
                timer.getFormattedTime(),
                0, 0,
                ComplexityAnalyzer.getComplexity("Tree DP (Independent Set)"),
                trace
        );

        return new TreeDPResult(maxWeight, dp, algoRes);
    }

    private static void dfs(int u, int p, ManualArrayList<Integer>[] tree, int[] weights,
                            int[][] dp, boolean[] visited, ManualArrayList<String> trace) {
        visited[u] = true;
        dp[u][0] = 0;
        dp[u][1] = (weights != null && u < weights.length) ? weights[u] : 1;

        ManualArrayList<Integer> neighbors = tree[u];
        for (int i = 0; i < neighbors.size(); i++) {
            int v = neighbors.get(i);
            if (v != p && !visited[v]) {
                dfs(v, u, tree, weights, dp, visited, trace);
                dp[u][0] += Math.max(dp[v][0], dp[v][1]);
                dp[u][1] += dp[v][0];
            }
        }
        trace.add(String.format("Node %d: dp[u][exclude]=%d, dp[u][include]=%d", u, dp[u][0], dp[u][1]));
    }
}
