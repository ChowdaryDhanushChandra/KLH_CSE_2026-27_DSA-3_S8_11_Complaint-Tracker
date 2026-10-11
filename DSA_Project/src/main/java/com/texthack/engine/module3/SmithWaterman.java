package com.texthack.engine.module3;

import com.texthack.datastructures.ManualArrayList;
import com.texthack.metrics.ComplexityAnalyzer;
import com.texthack.metrics.ExecutionTimer;
import com.texthack.model.AlignmentResult;
import com.texthack.model.ComplexityInfo;

/**
 * Smith-Waterman dynamic programming algorithm for local sequence alignment.
 * Clamps negative scores to zero and traces back from peak score cell.
 */
public class SmithWaterman {

    public static AlignmentResult align(String s, String t, int matchReward, int mismatchPenalty, int gapPenalty) {
        ExecutionTimer timer = new ExecutionTimer();
        timer.start();

        if (s == null) s = "";
        if (t == null) t = "";

        int n = s.length();
        int m = t.length();

        int[][] dp = new int[n + 1][m + 1];

        int maxScore = 0;
        int maxI = 0;
        int maxJ = 0;

        // Base cases: 0
        for (int i = 0; i <= n; i++) dp[i][0] = 0;
        for (int j = 0; j <= m; j++) dp[0][j] = 0;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int scoreDiag = dp[i - 1][j - 1] + (s.charAt(i - 1) == t.charAt(j - 1) ? matchReward : mismatchPenalty);
                int scoreUp = dp[i - 1][j] + gapPenalty;
                int scoreLeft = dp[i][j - 1] + gapPenalty;

                // Local alignment condition: max with 0
                dp[i][j] = Math.max(0, Math.max(scoreDiag, Math.max(scoreUp, scoreLeft)));

                if (dp[i][j] > maxScore) {
                    maxScore = dp[i][j];
                    maxI = i;
                    maxJ = j;
                }
            }
        }

        // Traceback starting from (maxI, maxJ) until score becomes 0
        StringBuilder localA = new StringBuilder();
        StringBuilder localB = new StringBuilder();
        ManualArrayList<String> ops = new ManualArrayList<>();

        int i = maxI;
        int j = maxJ;
        while (i > 0 && j > 0 && dp[i][j] > 0) {
            int current = dp[i][j];
            int diag = dp[i - 1][j - 1] + (s.charAt(i - 1) == t.charAt(j - 1) ? matchReward : mismatchPenalty);
            int up = dp[i - 1][j] + gapPenalty;

            if (current == diag) {
                localA.append(s.charAt(i - 1));
                localB.append(t.charAt(j - 1));
                ops.add("LOCAL MATCH/MISMATCH '" + s.charAt(i - 1) + "' - '" + t.charAt(j - 1) + "'");
                i--;
                j--;
            } else if (current == up) {
                localA.append(s.charAt(i - 1));
                localB.append('-');
                ops.add("LOCAL GAP in B");
                i--;
            } else {
                localA.append('-');
                localB.append(t.charAt(j - 1));
                ops.add("LOCAL GAP in A");
                j--;
            }
        }

        localA.reverse();
        localB.reverse();

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
        ComplexityInfo complexity = ComplexityAnalyzer.getComplexity("Smith-Waterman");

        return new AlignmentResult(
                "Smith-Waterman Local Alignment (Peak Score: " + maxScore + " at [" + maxI + "," + maxJ + "])",
                maxScore,
                localA.toString(),
                localB.toString(),
                forwardOps,
                dp, rowLabels, colLabels,
                timer.getElapsedNanos(),
                timer.getFormattedTime(),
                complexity
        );
    }
}
