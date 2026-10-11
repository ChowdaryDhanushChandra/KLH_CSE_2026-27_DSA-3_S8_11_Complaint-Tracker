package com.texthack.engine.module3;

import com.texthack.datastructures.ManualArrayList;
import com.texthack.metrics.ComplexityAnalyzer;
import com.texthack.metrics.ExecutionTimer;
import com.texthack.model.AlgorithmResult;

/**
 * Optimal Binary Search Tree (OBST) interval dynamic programming without java.util.*.
 * Computes minimum expected search cost and optimal root table.
 */
public class OptimalBinarySearchTree {

    public static class OBSTResult {
        public final double minCost;
        public final double[][] costTable;
        public final int[][] rootTable;
        public final AlgorithmResult algorithmResult;

        public OBSTResult(double minCost, double[][] costTable, int[][] rootTable, AlgorithmResult algoRes) {
            this.minCost = minCost;
            this.costTable = costTable;
            this.rootTable = rootTable;
            this.algorithmResult = algoRes;
        }
    }

    public static OBSTResult compute(String[] keys, double[] p, double[] q) {
        ExecutionTimer timer = new ExecutionTimer();
        timer.start();

        int n = keys.length;
        ManualArrayList<String> trace = new ManualArrayList<>();

        double[][] e = new double[n + 2][n + 1];
        double[][] w = new double[n + 2][n + 1];
        int[][] root = new int[n + 1][n + 1];

        // Base cases: dummy keys
        for (int i = 1; i <= n + 1; i++) {
            e[i][i - 1] = q[i - 1];
            w[i][i - 1] = q[i - 1];
        }

        trace.add("Beginning OBST Interval DP for " + n + " search keys...");

        for (int l = 1; l <= n; l++) {
            for (int i = 1; i <= n - l + 1; i++) {
                int j = i + l - 1;
                e[i][j] = Double.MAX_VALUE;
                w[i][j] = w[i][j - 1] + p[j] + q[j];

                for (int r = i; r <= j; r++) {
                    double t = e[i][r - 1] + e[r + 1][j] + w[i][j];
                    if (t < e[i][j]) {
                        e[i][j] = t;
                        root[i][j] = r;
                    }
                }
                trace.add(String.format("Subtree [%s..%s]: Min Expected Cost = %.4f (Optimal Root = '%s')",
                        keys[i - 1], keys[j - 1], e[i][j], keys[root[i][j] - 1]));
            }
        }

        timer.stop();
        double totalCost = e[1][n];
        trace.add(String.format("Optimal BST Construction finished: Total Expected Search Cost = %.4f", totalCost));

        AlgorithmResult algoRes = new AlgorithmResult(
                "Optimal Binary Search Tree",
                String.format("Min Expected Cost: %.4f", totalCost),
                "Root key for full tree: " + (n > 0 ? keys[root[1][n] - 1] : "None"),
                timer.getElapsedNanos(),
                timer.getFormattedTime(),
                0, 0,
                ComplexityAnalyzer.getComplexity("Optimal BST"),
                trace
        );

        return new OBSTResult(totalCost, e, root, algoRes);
    }
}
