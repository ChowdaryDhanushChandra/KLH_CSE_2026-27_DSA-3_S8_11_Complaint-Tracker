package complainttracker.algorithms;

import java.util.ArrayList;
import java.util.List;

/**
 * Rabin-Karp String Searching Algorithm using Polynomial Rolling Hash.
 * Average Time: O(N + M)
 * Worst Case: O(N * M) (when excessive hash collisions occur)
 * Space: O(1)
 */
public class RabinKarpMatcher {
    private static final int BASE = 256;
    private static final long MODULUS = 1_000_000_007L;

    public static StringMatchResult search(String text, String pattern) {
        long startTime = System.nanoTime();
        List<Integer> matches = new ArrayList<>();
        List<String> traces = new ArrayList<>();
        int comparisons = 0;

        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return new StringMatchResult("Rabin-Karp Rolling Hash", matches, 0,
                    System.nanoTime() - startTime, traces, new int[0]);
        }

        int n = text.length();
        int m = pattern.length();

        // Compute h = (BASE^(m-1)) % MODULUS
        long h = 1;
        for (int i = 0; i < m - 1; i++) {
            h = (h * BASE) % MODULUS;
        }

        long patternHash = 0;
        long windowHash = 0;

        // Calculate the initial hash of pattern and first window of text
        for (int i = 0; i < m; i++) {
            patternHash = (BASE * patternHash + pattern.charAt(i)) % MODULUS;
            windowHash = (BASE * windowHash + text.charAt(i)) % MODULUS;
        }

        traces.add(String.format("Pattern '%s' Hash: %d | Base=%d, Mod=%d", pattern, patternHash, BASE, MODULUS));

        // Slide the pattern over text
        for (int i = 0; i <= n - m; i++) {
            comparisons++; // Hash comparison

            if (patternHash == windowHash) {
                traces.add(String.format("Window[%d..%d] Hash match (%d). Verifying characters...",
                        i, i + m - 1, windowHash));

                // Hash matches -> verify character by character to avoid spurious collision
                boolean exactMatch = true;
                for (int j = 0; j < m; j++) {
                    comparisons++; // Character comparison
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        exactMatch = false;
                        traces.add(String.format("  Spurious hit! Mismatch at offset %d ('%c' != '%c')",
                                j, text.charAt(i + j), pattern.charAt(j)));
                        break;
                    }
                }

                if (exactMatch) {
                    matches.add(i);
                    traces.add(String.format("  EXACT MATCH confirmed at index %d!", i));
                }
            }

            // Calculate rolling hash for next window:
            // Remove leading digit, multiply by BASE, add trailing digit
            if (i < n - m) {
                windowHash = (BASE * (windowHash - text.charAt(i) * h) + text.charAt(i + m)) % MODULUS;
                if (windowHash < 0) {
                    windowHash += MODULUS;
                }
            }
        }

        long elapsed = System.nanoTime() - startTime;
        return new StringMatchResult("Rabin-Karp Rolling Hash", matches, comparisons, elapsed, traces, new int[0]);
    }
}
