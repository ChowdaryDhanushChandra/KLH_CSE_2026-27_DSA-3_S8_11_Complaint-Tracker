package com.samadhan.module2_strings;
/** Suffix array by prefix doubling with a hand-written merge sort, O(n log^2 n), plus Kasai's LCP in O(n). */
public class SuffixArray {
    static boolean less(int a, int b, int[] rk, int k, int n) {
        if (rk[a] != rk[b]) return rk[a] < rk[b];
        return (a + k < n ? rk[a + k] : -1) < (b + k < n ? rk[b + k] : -1);
    }
    static void msort(int[] a, int[] t, int lo, int hi, int[] rk, int k, int n) {
        if (hi - lo < 2) return; int m = (lo + hi) / 2; msort(a, t, lo, m, rk, k, n); msort(a, t, m, hi, rk, k, n);
        int i = lo, j = m, o = lo;
        while (i < m && j < hi) t[o++] = less(a[j], a[i], rk, k, n) ? a[j++] : a[i++];
        while (i < m) t[o++] = a[i++]; while (j < hi) t[o++] = a[j++];
        for (o = lo; o < hi; o++) a[o] = t[o];
    }
    public static int[] build(String s) {
        int n = s.length(); int[] sa = new int[n], rk = new int[n], tmp = new int[n], t = new int[n];
        for (int i = 0; i < n; i++) { sa[i] = i; rk[i] = s.charAt(i); }
        for (int k = 1; n > 0; k <<= 1) {
            msort(sa, t, 0, n, rk, k, n); tmp[sa[0]] = 0;
            for (int i = 1; i < n; i++) tmp[sa[i]] = tmp[sa[i - 1]] + (less(sa[i - 1], sa[i], rk, k, n) ? 1 : 0);
            for (int i = 0; i < n; i++) rk[i] = tmp[i];
            if (rk[sa[n - 1]] == n - 1 || k >= n) break;
        }
        return sa;
    }
    public static int[] lcp(String s, int[] sa) {
        int n = s.length(); int[] rank = new int[n], l = new int[n];
        for (int i = 0; i < n; i++) rank[sa[i]] = i;
        for (int i = 0, h = 0; i < n; i++) {
            if (rank[i] > 0) { int j = sa[rank[i] - 1]; while (i + h < n && j + h < n && s.charAt(i + h) == s.charAt(j + h)) h++; l[rank[i]] = h; if (h > 0) h--; } else h = 0;
        }
        return l;
    }
}
