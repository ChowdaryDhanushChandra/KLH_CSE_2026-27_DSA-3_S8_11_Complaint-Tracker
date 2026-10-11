package com.texthack.engine.module1;

import com.texthack.datastructures.ManualArrayList;
import com.texthack.metrics.ComplexityAnalyzer;
import com.texthack.metrics.ExecutionTimer;
import com.texthack.model.AlgorithmResult;
import com.texthack.validation.InputValidator;

/**
 * Primality testing engine implementing Modular Exponentiation,
 * Fermat Primality Test, and Miller-Rabin Probabilistic Primality Test without java.util.*.
 */
public class PrimalityTestingService {

    public static class PrimalityResult {
        public final long candidate;
        public final boolean isPrime;
        public final boolean isProbabilistic;
        public final double confidence;
        public final String statusLabel;
        public final AlgorithmResult algorithmResult;

        public PrimalityResult(long candidate, boolean isPrime, boolean isProbabilistic,
                               double confidence, String statusLabel, AlgorithmResult algoRes) {
            this.candidate = candidate;
            this.isPrime = isPrime;
            this.isProbabilistic = isProbabilistic;
            this.confidence = confidence;
            this.statusLabel = statusLabel;
            this.algorithmResult = algoRes;
        }
    }

    /**
     * Modular exponentiation: computes (base^exp) % mod in O(log exp) time.
     */
    public static long modularExponentiation(long base, long exp, long mod) {
        if (mod == 1) return 0;
        long result = 1;
        base = base % mod;
        if (base < 0) base += mod;

        while (exp > 0) {
            if ((exp & 1) == 1) {
                result = multiplyMod(result, base, mod);
            }
            base = multiplyMod(base, base, mod);
            exp >>= 1;
        }
        return result;
    }

    /**
     * Modular multiplication (a * b) % mod avoiding 64-bit overflow.
     */
    public static long multiplyMod(long a, long b, long mod) {
        long res = 0;
        a %= mod;
        b %= mod;
        while (b > 0) {
            if ((b & 1) == 1) {
                res = (res + a) % mod;
            }
            a = (a * 2) % mod;
            b >>= 1;
        }
        return res;
    }

    /**
     * Fermat Primality Test using multiple bases.
     */
    public PrimalityResult testFermat(long n, int iterations) {
        InputValidator.validatePrimeCandidate(n);
        ExecutionTimer timer = new ExecutionTimer();
        timer.start();

        ManualArrayList<String> trace = new ManualArrayList<>();
        trace.add(String.format("Starting Fermat Primality Test on n = %d with %d test bases...", n, iterations));

        if (n == 2 || n == 3) {
            timer.stop();
            return createDeterministicResult(n, true, timer, "Fermat Primality", trace);
        }
        if (n % 2 == 0) {
            timer.stop();
            trace.add(n + " is even and > 2 -> Definitely Composite.");
            return createDeterministicResult(n, false, timer, "Fermat Primality", trace);
        }

        // Selected deterministic and pseudo-random bases
        long[] testBases = getBases(n, iterations);

        for (int i = 0; i < testBases.length; i++) {
            long a = testBases[i];
            long rem = modularExponentiation(a, n - 1, n);
            trace.add(String.format("Base a = %d: %d^(%d) mod %d = %d", a, a, n - 1, n, rem));

            if (rem != 1) {
                timer.stop();
                trace.add(String.format("Base a = %d failed Fermat condition (%d != 1) -> DEFINITELY COMPOSITE", a, rem));
                AlgorithmResult algoRes = new AlgorithmResult(
                        "Fermat Primality Test",
                        "Definitely Composite (Witness: " + a + ")",
                        "Fermat condition violated for base " + a + ". (a^(n-1) mod n != 1)",
                        timer.getElapsedNanos(), timer.getFormattedTime(),
                        testBases.length, 0, ComplexityAnalyzer.getComplexity("Fermat Primality"), trace
                );
                return new PrimalityResult(n, false, false, 1.0, "DEFINITELY COMPOSITE", algoRes);
            }
        }

        timer.stop();
        double errorRate = Math.pow(0.5, testBases.length);
        double confidence = (1.0 - errorRate) * 100.0;
        trace.add(String.format("Passed all %d Fermat bases. PROBABILISTIC PRIME (Confidence: ~%.2f%%)",
                testBases.length, confidence));
        trace.add("Note: Fermat test can be deceived by Carmichael numbers (e.g., 561, 1105, 1729).");

        AlgorithmResult algoRes = new AlgorithmResult(
                "Fermat Primality Test",
                String.format("PROBABILISTIC PRIME (Confidence: %.2f%%)", confidence),
                "Passed Fermat test across " + testBases.length + " bases. Caution: Carmichael numbers pass Fermat.",
                timer.getElapsedNanos(), timer.getFormattedTime(),
                testBases.length, 0, ComplexityAnalyzer.getComplexity("Fermat Primality"), trace
        );

        return new PrimalityResult(n, true, true, confidence, "PROBABILISTIC PRIME", algoRes);
    }

    /**
     * Miller-Rabin Probabilistic Primality Test.
     * Theoretical error probability <= 4^(-k).
     */
    public PrimalityResult testMillerRabin(long n, int iterations) {
        InputValidator.validatePrimeCandidate(n);
        ExecutionTimer timer = new ExecutionTimer();
        timer.start();

        ManualArrayList<String> trace = new ManualArrayList<>();
        trace.add(String.format("Starting Miller-Rabin Test on n = %d with k = %d rounds...", n, iterations));

        if (n == 2 || n == 3) {
            timer.stop();
            return createDeterministicResult(n, true, timer, "Miller-Rabin Primality", trace);
        }
        if (n % 2 == 0) {
            timer.stop();
            trace.add(n + " is even and > 2 -> Definitely Composite.");
            return createDeterministicResult(n, false, timer, "Miller-Rabin Primality", trace);
        }

        // Factor n - 1 as d * 2^s with d odd
        long d = n - 1;
        int s = 0;
        while ((d & 1) == 0) {
            d >>= 1;
            s++;
        }
        trace.add(String.format("Decomposed n - 1 = 2^%d · %d", s, d));

        long[] bases = getBases(n, iterations);

        for (int b = 0; b < bases.length; b++) {
            long a = bases[b];
            long x = modularExponentiation(a, d, n);

            trace.add(String.format("Round %d (Base a = %d): a^d mod n = %d", b + 1, a, x));

            if (x == 1 || x == n - 1) {
                trace.add(String.format("Base %d passed immediate condition (x == 1 or x == n - 1)", a));
                continue;
            }

            boolean composite = true;
            for (int r = 1; r < s; r++) {
                x = multiplyMod(x, x, n);
                trace.add(String.format("  Squaring step r=%d: x^2 mod n = %d", r, x));
                if (x == n - 1) {
                    composite = false;
                    trace.add(String.format("  x == n - 1 at step %d -> Base %d passes", r, a));
                    break;
                }
            }

            if (composite) {
                timer.stop();
                trace.add(String.format("Nontrivial square root of 1 found: %d is a Miller-Rabin composite witness!", a));
                AlgorithmResult algoRes = new AlgorithmResult(
                        "Miller-Rabin Primality",
                        "DEFINITELY COMPOSITE (Witness: " + a + ")",
                        "Miller-Rabin witness " + a + " proved that " + n + " is composite.",
                        timer.getElapsedNanos(), timer.getFormattedTime(),
                        bases.length, 0, ComplexityAnalyzer.getComplexity("Miller-Rabin Primality"), trace
                );
                return new PrimalityResult(n, false, false, 1.0, "DEFINITELY COMPOSITE", algoRes);
            }
        }

        timer.stop();
        double errorRate = Math.pow(0.25, bases.length);
        double confidence = (1.0 - errorRate) * 100.0;
        trace.add(String.format("Miller-Rabin passed all %d rounds. PROBABILISTIC PRIME (Confidence >= %.6f%%)",
                bases.length, confidence));

        AlgorithmResult algoRes = new AlgorithmResult(
                "Miller-Rabin Primality",
                String.format("PROBABILISTIC PRIME (Confidence: >= %.4f%%)", confidence),
                String.format("Candidate passed %d independent rounds. Theoretical error bound: 4^(-%d) = %.2e",
                        bases.length, bases.length, errorRate),
                timer.getElapsedNanos(), timer.getFormattedTime(),
                bases.length, 0, ComplexityAnalyzer.getComplexity("Miller-Rabin Primality"), trace
        );

        return new PrimalityResult(n, true, true, confidence, "PROBABILISTIC PRIME", algoRes);
    }

    private PrimalityResult createDeterministicResult(long n, boolean isPrime, ExecutionTimer timer,
                                                      String algoName, ManualArrayList<String> trace) {
        String msg = isPrime ? "DEFINITELY PRIME (Small prime constant)" : "DEFINITELY COMPOSITE (Even number)";
        trace.add(msg);
        AlgorithmResult algoRes = new AlgorithmResult(
                algoName, msg, "Direct arithmetic property evaluation.",
                timer.getElapsedNanos(), timer.getFormattedTime(), 1, 0,
                ComplexityAnalyzer.getComplexity(algoName), trace
        );
        return new PrimalityResult(n, isPrime, false, 100.0, msg, algoRes);
    }

    private long[] getBases(long n, int iterations) {
        // Deterministic set of prime bases combined with pseudo-random sequence
        long[] standard = new long[]{2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37};
        int count = Math.min(iterations, standard.length);
        int valid = 0;
        for (int i = 0; i < count; i++) {
            if (standard[i] < n) valid++;
        }
        long[] res = new long[Math.max(valid, 1)];
        int idx = 0;
        for (int i = 0; i < count; i++) {
            if (standard[i] < n) {
                res[idx++] = standard[i];
            }
        }
        if (idx == 0) {
            res[0] = 2;
        }
        return res;
    }
}
