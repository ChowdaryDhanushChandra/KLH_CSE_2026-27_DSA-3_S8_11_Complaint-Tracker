package com.texthack.engine.module3;

import com.texthack.datastructures.ManualArrayList;
import com.texthack.metrics.ComplexityAnalyzer;
import com.texthack.metrics.ExecutionTimer;
import com.texthack.model.AlgorithmResult;
import com.texthack.model.ComplexityInfo;

/**
 * Matrix Chain Multiplication using Interval Dynamic Programming without java.util.*.
 * Computes minimum scalar multiplications and optimal parenthesization tree in O(n^3) time.
 */
public class MatrixChainMultiplication {

    public static class MCMResult {
        public final int minCost;
        public final String optimalParenthesization;
        public final int[][] costTable;
        public final int[][] splitTable;
        public final AlgorithmResult algorithmResult;

        public MCMResult(int minCost, String optimalParens, int[][] costTable, int[][] splitTable, AlgorithmResult algoRes) {
            this.minCost = minCost;
            this.optimalParenthesization = optimalParens;
            this.costTable = costTable;
            this.splitTable = splitTable;
            this.algorithmResult = algoRes;
        }
    }

    public static MCMResult solve(int[] p) {
        ExecutionTimer timer = new ExecutionTimer();
        timer.start();

        ManualArrayList<String> trace = new ManualArrayList<>();
        if (p == null || p.length <= 1) {
            timer.stop();
            AlgorithmResult emptyRes = new AlgorithmResult("Matrix Chain Multiplication", "0 multiplications", "No matrices",
                    0, "0 ns", 0, 0, ComplexityAnalyzer.getComplexity("Matrix-Chain Multiplication"), trace);
            return new MCMResult(0, "", new int[0][0], new int[0][0], emptyRes);
        }

        int n = p.length - 1; // Number of matrices A1..An
        int[][] m = new int[n + 1][n + 1];
        int[][] s = new int[n + 1][n + 1];

        // Length 1 chains have 0 cost
        for (int i = 1; i <= n; i++) {
            m[i][i] = 0;
        }

        trace.add("Beginning Interval DP for " + n + " matrices...");

        // l is the chain length
        for (int l = 2; l <= n; l++) {
            for (int i = 1; i <= n - l + 1; i++) {
                int j = i + l - 1;
                m[i][j] = Integer.MAX_VALUE;

                for (int k = i; k < j; k++) {
                    int q = m[i][k] + m[k + 1][j] + p[i - 1] * p[k] * p[j];
                    if (q < m[i][j]) {
                        m[i][j] = q;
                        s[i][j] = k;
                    }
                }
                trace.add(String.format("Optimal subchain A[%d..%d]: min scalar multiplications = %d (Split at k = %d)",
                        i, j, m[i][j], s[i][j]));
            }
        }

        StringBuilder parens = new StringBuilder();
        constructOptimalParens(s, 1, n, parens);

        timer.stop();
        trace.add("Optimal Parenthesization: " + parens.toString());

        AlgorithmResult algoRes = new AlgorithmResult(
                "Matrix-Chain Multiplication",
                "Minimum Scalar Multiplications: " + m[1][n],
                "Optimal Parenthesization: " + parens.toString(),
                timer.getElapsedNanos(),
                timer.getFormattedTime(),
                0, 0,
                ComplexityAnalyzer.getComplexity("Matrix-Chain Multiplication"),
                trace
        );

        return new MCMResult(m[1][n], parens.toString(), m, s, algoRes);
    }

    private static void constructOptimalParens(int[][] s, int i, int j, StringBuilder sb) {
        if (i == j) {
            sb.append("A").append(i);
        } else {
            sb.append("(");
            constructOptimalParens(s, i, s[i][j], sb);
            sb.append(" × ");
            constructOptimalParens(s, s[i][j] + 1, j, sb);
            sb.append(")");
        }
    }
}
