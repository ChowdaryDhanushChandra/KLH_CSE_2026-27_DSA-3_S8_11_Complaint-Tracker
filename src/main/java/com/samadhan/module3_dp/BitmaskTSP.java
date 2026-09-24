package com.samadhan.module3_dp;
/** Exact shortest round trip from stop 0 through all stops, O(2^n * n^2) bitmask DP. */
public class BitmaskTSP {
    public static int[] route(int[] x, int[] y) {
        int n = x.length, F = 1 << n; double INF = 1e18; double[][] d = new double[n][n];
        for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) d[i][j] = Math.hypot(x[i] - x[j], y[i] - y[j]);
        double[][] dp = new double[F][n]; int[][] pr = new int[F][n];
        for (int m = 0; m < F; m++) for (int j = 0; j < n; j++) dp[m][j] = INF;
        dp[1][0] = 0;
        for (int m = 1; m < F; m += 2) for (int j = 0; j < n; j++) if ((m >> j & 1) == 1 && dp[m][j] < INF)
            for (int k = 0; k < n; k++) if ((m >> k & 1) == 0) { int m2 = m | 1 << k; double v = dp[m][j] + d[j][k]; if (v < dp[m2][k]) { dp[m2][k] = v; pr[m2][k] = j; } }
        double best = INF; int last = 1;
        for (int j = 1; j < n; j++) { double v = dp[F - 1][j] + d[j][0]; if (v < best) { best = v; last = j; } }
        int[] ord = new int[n]; int m = F - 1;
        for (int i = n - 1; i >= 1; i--) { ord[i] = last; int p = pr[m][last]; m ^= 1 << last; last = p; }
        return ord;
    }
}
