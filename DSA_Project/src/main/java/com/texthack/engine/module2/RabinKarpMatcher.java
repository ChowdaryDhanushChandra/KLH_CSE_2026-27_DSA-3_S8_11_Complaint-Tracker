package com.texthack.engine.module2;

import com.texthack.datastructures.ManualArrayList;
import com.texthack.engine.common.StringAlgorithm;
import com.texthack.metrics.ComplexityAnalyzer;
import com.texthack.metrics.ExecutionTimer;
import com.texthack.metrics.OperationCounter;
import com.texthack.model.ComplexityInfo;
import com.texthack.model.SearchResult;

/**
 * Rabin-Karp String Matcher with polynomial rolling hash and collision verification.
 * Average-case time: O(n + m), Worst-case time: O(n · m), Space: O(1).
 */
public class RabinKarpMatcher implements StringAlgorithm {

    private static final int BASE = 256;
    private static final long MODULUS = 1_000_000_007L;

    @Override
    public String getName() {
        return "Rabin-Karp Matcher";
    }

    @Override
    public ComplexityInfo getComplexity() {
        return ComplexityAnalyzer.getComplexity("Rabin-Karp");
    }

    @Override
    public SearchResult search(String pattern, String text) {
        ExecutionTimer timer = new ExecutionTimer();
        OperationCounter counter = new OperationCounter();
        timer.start();

        ManualArrayList<Integer> matches = new ManualArrayList<>();
        ManualArrayList<String> trace = new ManualArrayList<>();

        if (pattern == null || text == null || pattern.length() == 0 || text.length() == 0) {
            timer.stop();
            return new SearchResult(pattern, matches, 0, timer.getElapsedNanos(), timer.getFormattedTime(), getComplexity(), trace);
        }

        int m = pattern.length();
        int n = text.length();

        if (m > n) {
            trace.add("Pattern length m=" + m + " exceeds text length n=" + n);
            timer.stop();
            return new SearchResult(pattern, matches, 0, timer.getElapsedNanos(), timer.getFormattedTime(), getComplexity(), trace);
        }

        // h = BASE^(m-1) % MODULUS
        long h = 1;
        for (int i = 0; i < m - 1; i++) {
            h = (h * BASE) % MODULUS;
        }

        long patternHash = 0;
        long windowHash = 0;

        // Compute initial hashes for pattern and first window of text
        for (int i = 0; i < m; i++) {
            patternHash = (BASE * patternHash + pattern.charAt(i)) % MODULUS;
            windowHash = (BASE * windowHash + text.charAt(i)) % MODULUS;
        }

        trace.add(String.format("Initial hashes: Pattern Hash = %d, Window[0..%d] Hash = %d",
                patternHash, m - 1, windowHash));

        int collisions = 0;

        // Slide window over text
        for (int i = 0; i <= n - m; i++) {
            counter.incrementOperations();

            if (patternHash == windowHash) {
                // Potential match: perform exact verification to protect against spurious collisions
                boolean match = true;
                for (int j = 0; j < m; j++) {
                    counter.incrementComparisons();
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        match = false;
                        break;
                    }
                }

                if (match) {
                    matches.add(i);
                    trace.add(String.format("Hash match verified at offset %d: '%s'", i, text.substring(i, i + m)));
                } else {
                    collisions++;
                    trace.add(String.format("Spurious hash collision detected at offset %d (Hashes matched: %d, but chars differed)",
                            i, windowHash));
                }
            }

            // Rolling hash update to next window
            if (i < n - m) {
                windowHash = (BASE * (windowHash - text.charAt(i) * h) + text.charAt(i + m)) % MODULUS;
                if (windowHash < 0) {
                    windowHash += MODULUS;
                }
            }
        }

        timer.stop();
        trace.add(String.format("Rabin-Karp complete: %d matches found, %d collisions, %d char comparisons in %s.",
                matches.size(), collisions, counter.getComparisons(), timer.getFormattedTime()));

        return new SearchResult(pattern, matches, counter.getComparisons(),
                timer.getElapsedNanos(), timer.getFormattedTime(), getComplexity(), trace);
    }
}
