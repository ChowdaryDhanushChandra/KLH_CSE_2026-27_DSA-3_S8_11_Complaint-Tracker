package com.texthack.engine.module3;

import com.texthack.datastructures.ManualArrayList;
import com.texthack.metrics.ComplexityAnalyzer;
import com.texthack.metrics.ExecutionTimer;
import com.texthack.model.AlignmentResult;
import com.texthack.model.ComplexityInfo;

/**
 * Weighted Edit Distance with customizable insertion, deletion, and replacement costs.
 */
public class WeightedEditDistance {

    public static AlignmentResult compute(String s, String t, int insertCost, int deleteCost, int replaceCost) {
        ExecutionTimer timer = new ExecutionTimer();
        timer.start();

        if (s == null) s = "";
        if (t == null) t = "";

        int n = s.length();
        int m = t.length();

        int[][] dp = new int[n + 1][m + 1];

        // Base cases scaled by custom costs
        for (int i = 0; i <= n; i++) dp[i][0] = i * deleteCost;
        for (int j = 0; j <= m; j++) dp[0][j] = j * insertCost;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (s.charAt(i - 1) == t.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    int del = dp[i - 1][j] + deleteCost;
                    int ins = dp[i][j - 1] + insertCost;
                    int rep = dp[i - 1][j - 1] + replaceCost;
                    dp[i][j] = Math.min(del, Math.min(ins, rep));
                }
            }
        }

        // Traceback
        ManualArrayList<String> ops = new ManualArrayList<>();
        int i = n;
        int j = m;
        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && s.charAt(i - 1) == t.charAt(j - 1) && dp[i][j] == dp[i - 1][j - 1]) {
                ops.add("KEEP '" + s.charAt(i - 1) + "' (Cost: 0)");
                i--;
                j--;
            } else if (i > 0 && j > 0 && dp[i][j] == dp[i - 1][j - 1] + replaceCost) {
                ops.add(String.format("REPLACE '%c' with '%c' (Cost: %d)", s.charAt(i - 1), t.charAt(j - 1), replaceCost));
                i--;
                j--;
            } else if (i > 0 && dp[i][j] == dp[i - 1][j] + deleteCost) {
                ops.add(String.format("DELETE '%c' (Cost: %d)", s.charAt(i - 1), deleteCost));
                i--;
            } else {
                ops.add(String.format("INSERT '%c' (Cost: %d)", t.charAt(j - 1), insertCost));
                j--;
            }
        }

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
        ComplexityInfo complexity = ComplexityAnalyzer.getComplexity("Weighted Edit Distance");

        return new AlignmentResult(
                "Weighted Edit Distance (Ins=" + insertCost + ", Del=" + deleteCost + ", Rep=" + replaceCost + ")",
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
