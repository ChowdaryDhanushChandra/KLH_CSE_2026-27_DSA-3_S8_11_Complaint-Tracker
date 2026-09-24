package com.samadhan.module2_strings;
/** Multi-keyword matcher: trie + failure links. Used to suggest the complaint category from free text. */
public class AhoCorasick {
    final int[][] go = new int[600][128]; final int[] fail = new int[600], out = new int[600]; int n = 1;
    public void add(String w, int id) { int s = 0; for (char ch : w.toCharArray()) { if (go[s][ch] == 0) go[s][ch] = n++; s = go[s][ch]; } out[s] = id + 1; }
    public void build() {
        int[] q = new int[n]; int h = 0, e = 0;
        for (int c = 0; c < 128; c++) if (go[0][c] != 0) q[e++] = go[0][c];
        while (h < e) {
            int u = q[h++]; if (out[u] == 0) out[u] = out[fail[u]];
            for (int c = 0; c < 128; c++) { int v = go[u][c]; if (v != 0) { fail[v] = go[fail[u]][c]; q[e++] = v; } else go[u][c] = go[fail[u]][c]; }
        }
    }
    public int[] count(String t, int groups) {
        int[] r = new int[groups]; int s = 0;
        for (char ch : t.toLowerCase().toCharArray()) { if (ch >= 128) { s = 0; continue; } s = go[s][ch]; if (out[s] > 0) r[out[s] - 1]++; }
        return r;
    }
}
