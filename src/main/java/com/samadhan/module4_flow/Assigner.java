package com.samadhan.module4_flow;
/** Edmonds-Karp max-flow: source -> complaints -> matching-skill officers (capacity) -> sink. */
public class Assigner {
    public static int[] assign(int[] cIssue, int[] oIssue, int[] oCap) {
        int C = cIssue.length, O = oIssue.length, N = C + O + 2, s = 0, t = N - 1;
        int[][] cap = new int[N][N];
        for (int i = 0; i < C; i++) { cap[s][1 + i] = 1; for (int j = 0; j < O; j++) if (cIssue[i] == oIssue[j]) cap[1 + i][1 + C + j] = 1; }
        for (int j = 0; j < O; j++) cap[1 + C + j][t] = oCap[j];
        while (true) {
            int[] par = new int[N]; for (int i = 0; i < N; i++) par[i] = -1; par[s] = s;
            int[] q = new int[N]; int h = 0, e = 0; q[e++] = s;
            while (h < e && par[t] < 0) { int u = q[h++]; for (int v = 0; v < N; v++) if (par[v] < 0 && cap[u][v] > 0) { par[v] = u; q[e++] = v; } }
            if (par[t] < 0) break;
            int f = Integer.MAX_VALUE; for (int v = t; v != s; v = par[v]) f = Math.min(f, cap[par[v]][v]);
            for (int v = t; v != s; v = par[v]) { cap[par[v]][v] -= f; cap[v][par[v]] += f; }
        }
        int[] r = new int[C];
        for (int i = 0; i < C; i++) { r[i] = -1; for (int j = 0; j < O; j++) if (cIssue[i] == oIssue[j] && cap[1 + C + j][1 + i] > 0) r[i] = j; }
        return r;
    }
}
