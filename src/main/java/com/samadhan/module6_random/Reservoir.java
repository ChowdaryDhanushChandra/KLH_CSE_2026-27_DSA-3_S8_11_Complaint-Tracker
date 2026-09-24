package com.samadhan.module6_random;
public class Reservoir {
    public static int[] sample(int n, int k, java.util.Random rnd) {
        int[] res = new int[Math.min(k, n)];
        for (int i = 0; i < n; i++) { if (i < res.length) res[i] = i; else { int j = rnd.nextInt(i + 1); if (j < res.length) res[j] = i; } }
        return res;
    }
}
