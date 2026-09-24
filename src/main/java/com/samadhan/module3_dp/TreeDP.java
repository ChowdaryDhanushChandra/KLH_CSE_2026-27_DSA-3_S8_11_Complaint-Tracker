package com.samadhan.module3_dp;
/** Tree DP on a parent-array tree (parent index < child index): longest path (diameter) by cost, in O(n). */
public class TreeDP {
    public static int diameter(int[] par, int[] w) {
        int n = par.length; int[] b1 = new int[n], b2 = new int[n]; int best = 0;
        for (int u = n - 1; u > 0; u--) {
            best = Math.max(best, b1[u] + b2[u]); int v = b1[u] + w[u], p = par[u];
            if (v > b1[p]) { b2[p] = b1[p]; b1[p] = v; } else if (v > b2[p]) b2[p] = v;
        }
        return Math.max(best, b1[0] + b2[0]);
    }
}
