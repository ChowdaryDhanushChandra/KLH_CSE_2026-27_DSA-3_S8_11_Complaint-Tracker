package com.texthack.engine.module3;

import com.texthack.datastructures.ManualArrayList;
import com.texthack.metrics.ComplexityAnalyzer;
import com.texthack.metrics.ExecutionTimer;
import com.texthack.model.AlignmentResult;
import com.texthack.model.ComplexityInfo;

/**
 * Needleman-Wunsch dynamic programming algorithm for global sequence alignment.
 */
public class NeedlemanWunsch {

    public static AlignmentResult align(String s, String t, int matchReward, int mismatchPenalty, int gapPenalty) {
        ExecutionTimer timer = new ExecutionTimer();
        timer.start();

        if (s == null) s = "";
        if (t == null) t = "";

        int n = s.length();
        int m = t.length();

        int[][] dp = new int[n + 1][m + 1];

        // Base cases: cumulative gap penalty
        for (int i = 0; i <= n; i++) dp[i][0] = i * gapPenalty;
        for (int j = 0; j <= m; j++) dp[0][j] = j * gapPenalty;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int scoreDiag = dp[i - 1][j - 1] + (s.charAt(i - 1) == t.charAt(j - 1) ? matchReward : mismatchPenalty);
                int scoreUp = dp[i - 1][j] + gapPenalty;
                int scoreLeft = dp[i][j - 1] + gapPenalty;
                dp[i][j] = Math.max(scoreDiag, Math.max(scoreUp, scoreLeft));
            }
        }

        // Traceback from (n, m) to (0, 0)
        StringBuilder alignA = new StringBuilder();
        StringBuilder alignB = new StringBuilder();
        ManualArrayList<String> ops = new ManualArrayList<>();

        int i = n;
        int j = m;
        while (i > 0 || j > 0) {
            int current = dp[i][j];
            if (i > 0 && j > 0 && current == dp[i - 1][j - 1] + (s.charAt(i - 1) == t.charAt(j - 1) ? matchReward : mismatchPenalty)) {
                alignA.append(s.charAt(i - 1));
                alignB.append(t.charAt(j - 1));
                ops.add((s.charAt(i - 1) == t.charAt(j - 1) ? "MATCH " : "MISMATCH ") + "'" + s.charAt(i - 1) + "' - '" + t.charAt(j - 1) + "'");
                i--;
                j--;
            } else if (i > 0 && current == dp[i - 1][j] + gapPenalty) {
                alignA.append(s.charAt(i - 1));
                alignB.append('-');
                ops.add("GAP in B (consume '" + s.charAt(i - 1) + "' from A)");
                i--;
            } else {
                alignA.append('-');
                alignB.append(t.charAt(j - 1));
                ops.add("GAP in A (consume '" + t.charAt(j - 1) + "' from B)");
                j--;
            }
        }

        alignA.reverse();
        alignB.reverse();

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
        ComplexityInfo complexity = ComplexityAnalyzer.getComplexity("Needleman-Wunsch");

        return new AlignmentResult(
                "Needleman-Wunsch Global Alignment",
                dp[n][m],
                alignA.toString(),
                alignB.toString(),
                forwardOps,
                dp, rowLabels, colLabels,
                timer.getElapsedNanos(),
                timer.getFormattedTime(),
                complexity
        );
    }
}
