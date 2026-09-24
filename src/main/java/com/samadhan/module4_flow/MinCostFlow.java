package com.samadhan.module4_flow;
/** Min-cost max-flow by successive shortest paths (Bellman-Ford): assigns complaints to skilled officers with the least total distance. */
public class MinCostFlow {
    public static int[] assign(int[] cIssue, int[] oIssue, int[] oCap, int[][] cost) {
        int C = cIssue.length, O = oIssue.length, N = C + O + 2, s = 0, t = N - 1, INF = 1 << 29;
        int[][] cap = new int[N][N], w = new int[N][N];
        for (int i = 0; i < C; i++) { cap[s][1 + i] = 1; for (int j = 0; j < O; j++) if (cIssue[i] == oIssue[j]) { cap[1 + i][1 + C + j] = 1; w[1 + i][1 + C + j] = cost[i][j]; w[1 + C + j][1 + i] = -cost[i][j]; } }
        for (int j = 0; j < O; j++) cap[1 + C + j][t] = oCap[j];
        while (true) {
            int[] d = new int[N], par = new int[N]; for (int i = 0; i < N; i++) { d[i] = INF; par[i] = -1; } d[s] = 0;
            boolean ch = true;
            for (int it = 0; it < N && ch; it++) { ch = false; for (int u = 0; u < N; u++) if (d[u] < INF) for (int v = 0; v < N; v++) if (cap[u][v] > 0 && d[u] + w[u][v] < d[v]) { d[v] = d[u] + w[u][v]; par[v] = u; ch = true; } }
            if (d[t] >= INF) break;
            int f = INF; for (int v = t; v != s; v = par[v]) f = Math.min(f, cap[par[v]][v]);
            for (int v = t; v != s; v = par[v]) { cap[par[v]][v] -= f; cap[v][par[v]] += f; }
        }
        int[] r = new int[C];
        for (int i = 0; i < C; i++) { r[i] = -1; for (int j = 0; j < O; j++) if (cIssue[i] == oIssue[j] && cap[1 + C + j][1 + i] > 0) r[i] = j; }
        return r;
    }
}
