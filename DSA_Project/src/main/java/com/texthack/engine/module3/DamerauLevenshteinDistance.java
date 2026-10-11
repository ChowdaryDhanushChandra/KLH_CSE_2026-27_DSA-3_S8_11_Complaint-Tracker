package com.texthack.engine.module3;

import com.texthack.datastructures.ManualArrayList;
import com.texthack.metrics.ComplexityAnalyzer;
import com.texthack.metrics.ExecutionTimer;
import com.texthack.model.AlignmentResult;
import com.texthack.model.ComplexityInfo;

/**
 * Damerau-Levenshtein distance supporting insertions, deletions, substitutions, and transpositions.
 */
public class DamerauLevenshteinDistance {

    public static AlignmentResult compute(String s, String t) {
        ExecutionTimer timer = new ExecutionTimer();
        timer.start();

        if (s == null) s = "";
        if (t == null) t = "";

        int n = s.length();
        int m = t.length();

        int[][] dp = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++) dp[i][0] = i;
        for (int j = 0; j <= m; j++) dp[0][j] = j;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int cost = (s.charAt(i - 1) == t.charAt(j - 1)) ? 0 : 1;

                int del = dp[i - 1][j] + 1;
                int ins = dp[i][j - 1] + 1;
                int sub = dp[i - 1][j - 1] + cost;

                dp[i][j] = Math.min(del, Math.min(ins, sub));

                // Adjacent transposition check
                if (i > 1 && j > 1 && s.charAt(i - 1) == t.charAt(j - 2) && s.charAt(i - 2) == t.charAt(j - 1)) {
                    dp[i][j] = Math.min(dp[i][j], dp[i - 2][j - 2] + 1);
                }
            }
        }

        // Traceback
        ManualArrayList<String> ops = new ManualArrayList<>();
        int i = n;
        int j = m;
        while (i > 0 || j > 0) {
            // Check transposition first
            if (i > 1 && j > 1 && s.charAt(i - 1) == t.charAt(j - 2) && s.charAt(i - 2) == t.charAt(j - 1)
                    && dp[i][j] == dp[i - 2][j - 2] + 1) {
                ops.add(String.format("TRANSPOSE '%c%c' <-> '%c%c' at pos %d",
                        s.charAt(i - 2), s.charAt(i - 1), t.charAt(j - 2), t.charAt(j - 1), i - 2));
                i -= 2;
                j -= 2;
            } else if (i > 0 && j > 0 && s.charAt(i - 1) == t.charAt(j - 1) && dp[i][j] == dp[i - 1][j - 1]) {
                ops.add("KEEP '" + s.charAt(i - 1) + "'");
                i--;
                j--;
            } else if (i > 0 && j > 0 && dp[i][j] == dp[i - 1][j - 1] + 1) {
                ops.add(String.format("REPLACE '%c' with '%c' at pos %d", s.charAt(i - 1), t.charAt(j - 1), i - 1));
                i--;
                j--;
            } else if (i > 0 && dp[i][j] == dp[i - 1][j] + 1) {
                ops.add(String.format("DELETE '%c' at pos %d", s.charAt(i - 1), i - 1));
                i--;
            } else {
                ops.add(String.format("INSERT '%c' at pos %d", t.charAt(j - 1), i));
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
        ComplexityInfo complexity = ComplexityAnalyzer.getComplexity("Damerau-Levenshtein");

        return new AlignmentResult(
                "Damerau-Levenshtein",
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
