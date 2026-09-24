package com.samadhan.module6_random;
public class MillerRabin {
    static long pow(long b, long e, long m) { long r = 1; b %= m; while (e > 0) { if ((e & 1) == 1) r = r * b % m; b = b * b % m; e >>= 1; } return r; }
    public static boolean isPrime(long n) {
        if (n < 2) return false;
        long[] bases = {2, 3, 5, 7};
        for (long p : bases) if (n % p == 0) return n == p;
        long d = n - 1; int r = 0; while (d % 2 == 0) { d /= 2; r++; }
        for (long a : bases) {
            long x = pow(a, d, n); if (x == 1 || x == n - 1) continue;
            boolean ok = false;
            for (int i = 1; i < r; i++) { x = x * x % n; if (x == n - 1) { ok = true; break; } }
            if (!ok) return false;
        }
        return true;
    }
    public static long nextPrime(long n) { if (n % 2 == 0) n++; while (!isPrime(n)) n += 2; return n; }
}
