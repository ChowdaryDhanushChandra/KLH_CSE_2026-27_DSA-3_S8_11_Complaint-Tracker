package com.texthack.engine.module3;

import com.texthack.datastructures.ManualArrayList;
import com.texthack.metrics.ComplexityAnalyzer;
import com.texthack.metrics.ExecutionTimer;
import com.texthack.model.AlignmentResult;
import com.texthack.model.ComplexityInfo;

/**
 * Wagner-Fischer algorithm for Levenshtein edit distance without java.util.*.
 * Computes minimum insertions, deletions, and substitutions to transform word A into word B.
 */
public class LevenshteinDistance {

    public static AlignmentResult compute(String s, String t) {
        ExecutionTimer timer = new ExecutionTimer();
        timer.start();

        if (s == null) s = "";
        if (t == null) t = "";

        int n = s.length();
        int m = t.length();

        int[][] dp = new int[n + 1][m + 1];

        // Base cases
        for (int i = 0; i <= n; i++) dp[i][0] = i;
        for (int j = 0; j <= m; j++) dp[0][j] = j;

        // DP state transitions
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (s.charAt(i - 1) == t.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    int deleteCost = dp[i - 1][j] + 1;
                    int insertCost = dp[i][j - 1] + 1;
                    int replaceCost = dp[i - 1][j - 1] + 1;
                    dp[i][j] = Math.min(deleteCost, Math.min(insertCost, replaceCost));
                }
            }
        }

        // Backtracking to find operation sequence
        ManualArrayList<String> ops = new ManualArrayList<>();
        int i = n;
        int j = m;
        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && s.charAt(i - 1) == t.charAt(j - 1)) {
                ops.add("KEEP '" + s.charAt(i - 1) + "' at position " + (i - 1));
                i--;
                j--;
            } else if (i > 0 && j > 0 && dp[i][j] == dp[i - 1][j - 1] + 1) {
                ops.add("REPLACE '" + s.charAt(i - 1) + "' with '" + t.charAt(j - 1) + "' at " + (i - 1));
                i--;
                j--;
            } else if (i > 0 && dp[i][j] == dp[i - 1][j] + 1) {
                ops.add("DELETE '" + s.charAt(i - 1) + "' from position " + (i - 1));
                i--;
            } else {
                ops.add("INSERT '" + t.charAt(j - 1) + "' at position " + i);
                j--;
            }
        }

        // Reverse operations to read forward
        ManualArrayList<String> forwardOps = new ManualArrayList<>(ops.size());
        for (int k = ops.size() - 1; k >= 0; k--) {
            forwardOps.add(ops.get(k));
        }

        String[] rowLabels = new String[n + 1];
        rowLabels[0] = "ε";
        for (int r = 1; r <= n; r++) rowLabels[r] = String.valueOf(s.charAt(r - 1));

        String[] colLabels = new String[m + 1];
        colLabels[0] = "ε";
        for (int c = 1; c <= m; c++) colLabels[c] = String.valueOf(t.charAt(c - 1));

        timer.stop();
        ComplexityInfo complexity = ComplexityAnalyzer.getComplexity("Levenshtein Distance");

        return new AlignmentResult(
                "Levenshtein Distance",
                dp[n][m],
                s, t,
                forwardOps,
                dp, rowLabels, colLabels,
                timer.getElapsedNanos(),
                timer.getFormattedTime(),
                complexity
        );
    }
}
