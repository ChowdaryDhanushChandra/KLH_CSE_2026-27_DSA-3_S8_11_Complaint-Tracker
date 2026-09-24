package com.samadhan.module2_strings;
/** Four matchers that also report how many steps they took (used by the Algorithm Lab). Each returns {matches, steps}. */
public class Matchers {
    public static long[] naive(String t, String p) {
        int n = t.length(), m = p.length(); long c = 0, ops = 0;
        for (int i = 0; i + m <= n; i++) { int j = 0; while (j < m) { ops++; if (t.charAt(i + j) != p.charAt(j)) break; j++; } if (j == m) c++; }
        return new long[]{c, ops};
    }
    public static long[] kmp(String t, String p) {
        int n = t.length(), m = p.length(); int[] f = new int[m]; long ops = 0, c = 0;
        for (int i = 1, k = 0; i < m; i++) { while (k > 0 && p.charAt(i) != p.charAt(k)) { k = f[k - 1]; ops++; } ops++; if (p.charAt(i) == p.charAt(k)) k++; f[i] = k; }
        for (int i = 0, k = 0; i < n; i++) { while (k > 0 && t.charAt(i) != p.charAt(k)) { k = f[k - 1]; ops++; } ops++; if (t.charAt(i) == p.charAt(k)) k++; if (k == m) { c++; k = f[k - 1]; } }
        return new long[]{c, ops};
    }
    public static long[] z(String t, String p) {
        String s = p + '\u0001' + t; int n = s.length(), m = p.length(); int[] z = new int[n]; long ops = 0, c = 0;
        for (int i = 1, l = 0, r = 0; i < n; i++) {
            if (i < r) z[i] = Math.min(r - i, z[i - l]);
            while (i + z[i] < n && s.charAt(z[i]) == s.charAt(i + z[i])) { z[i]++; ops++; }
            ops++; if (i + z[i] > r) { l = i; r = i + z[i]; } if (z[i] >= m) c++;
        }
        return new long[]{c, ops};
    }
    public static long[] rk(String t, String p) {
        int n = t.length(), m = p.length(); if (m > n) return new long[]{0, 0};
        long M1 = 1_000_000_007L, M2 = 998_244_353L, B = 131, h1 = 0, h2 = 0, t1 = 0, t2 = 0, p1 = 1, p2 = 1, ops = 0, c = 0;
        for (int i = 0; i < m; i++) {
            h1 = (h1 * B + p.charAt(i)) % M1; h2 = (h2 * B + p.charAt(i)) % M2; t1 = (t1 * B + t.charAt(i)) % M1; t2 = (t2 * B + t.charAt(i)) % M2;
            if (i > 0) { p1 = p1 * B % M1; p2 = p2 * B % M2; }
        }
        for (int i = 0; ; i++) {
            ops++; if (h1 == t1 && h2 == t2) c++; if (i + m >= n) break;
            t1 = ((t1 - t.charAt(i) * p1 % M1 + M1) * B + t.charAt(i + m)) % M1; t2 = ((t2 - t.charAt(i) * p2 % M2 + M2) * B + t.charAt(i + m)) % M2;
        }
        return new long[]{c, ops};
    }
}
