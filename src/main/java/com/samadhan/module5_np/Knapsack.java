package com.samadhan.module5_np;
public class Knapsack {
    /** Exact 0/1 knapsack (pseudo-polynomial DP over weight). */
    public static boolean[] solve(int[] w, int[] v, int W) {
        int n = w.length; int[][] d = new int[n + 1][W + 1];
        for (int i = 1; i <= n; i++) for (int j = 0; j <= W; j++) { d[i][j] = d[i - 1][j]; if (w[i - 1] <= j) d[i][j] = Math.max(d[i][j], d[i - 1][j - w[i - 1]] + v[i - 1]); }
        boolean[] p = new boolean[n];
        for (int i = n, j = W; i > 0; i--) if (d[i][j] != d[i - 1][j]) { p[i - 1] = true; j -= w[i - 1]; }
        return p;
    }
    /** FPTAS: scale values down by K = eps*vmax/n, then DP over scaled value; result is at least (1-eps) of optimal. */
    public static boolean[] fptas(int[] w, int[] v, int W, double eps) {
        int n = w.length; boolean[] p = new boolean[n]; if (n == 0) return p;
        int vmax = 1; for (int x : v) vmax = Math.max(vmax, x);
        double K = eps * vmax / n; int[] s = new int[n]; int tot = 0, INF = 1 << 29;
        for (int i = 0; i < n; i++) { s[i] = (int) (v[i] / K); tot += s[i]; }
        int[][] d = new int[n + 1][tot + 1]; for (int i = 0; i <= n; i++) for (int j = 0; j <= tot; j++) d[i][j] = INF; d[0][0] = 0;
        for (int i = 1; i <= n; i++) for (int j = 0; j <= tot; j++) { d[i][j] = d[i - 1][j]; if (j >= s[i - 1] && d[i - 1][j - s[i - 1]] + w[i - 1] < d[i][j]) d[i][j] = d[i - 1][j - s[i - 1]] + w[i - 1]; }
        int val = 0; for (int j = 0; j <= tot; j++) if (d[n][j] <= W) val = j;
        for (int i = n; i > 0; i--) if (d[i][val] != d[i - 1][val]) { p[i - 1] = true; val -= s[i - 1]; }
        return p;
    }
}
