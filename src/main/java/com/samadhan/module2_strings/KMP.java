package com.samadhan.module2_strings;
public class KMP {
    public static int count(String text, String pat) {
        String t = text.toLowerCase(), p = pat.toLowerCase(); if (p.isEmpty()) return 0;
        int[] f = new int[p.length()];
        for (int i = 1, k = 0; i < p.length(); i++) { while (k > 0 && p.charAt(i) != p.charAt(k)) k = f[k - 1]; if (p.charAt(i) == p.charAt(k)) k++; f[i] = k; }
        int c = 0;
        for (int i = 0, k = 0; i < t.length(); i++) { while (k > 0 && t.charAt(i) != p.charAt(k)) k = f[k - 1]; if (t.charAt(i) == p.charAt(k)) k++; if (k == p.length()) { c++; k = f[k - 1]; } }
        return c;
    }
}
