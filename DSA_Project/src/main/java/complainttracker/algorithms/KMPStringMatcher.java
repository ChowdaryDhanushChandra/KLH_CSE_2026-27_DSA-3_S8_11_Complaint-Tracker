package complainttracker.algorithms;

import java.util.ArrayList;
import java.util.List;

/**
 * Knuth-Morris-Pratt (KMP) String Searching Algorithm.
 * Time Complexity: O(N + M) where N = text length, M = pattern length.
 * Space Complexity: O(M) for the LPS (Longest Prefix Suffix) preprocessing array.
 */
public class KMPStringMatcher {

    /**
     * Compute LPS (Longest Proper Prefix which is also Suffix) array.
     */
    public static int[] computeLPSArray(String pattern) {
        if (pattern == null || pattern.isEmpty()) return new int[0];
        int m = pattern.length();
        int[] lps = new int[m];
        int len = 0;
        int i = 1;

        lps[0] = 0; // lps[0] is always 0

        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1]; // Fallback without advancing i
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

    /**
     * Search pattern in text with full execution metrics and trace.
     */
    public static StringMatchResult search(String text, String pattern) {
        long startTime = System.nanoTime();
        List<Integer> matches = new ArrayList<>();
        List<String> traces = new ArrayList<>();
        int comparisons = 0;

        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return new StringMatchResult("Knuth-Morris-Pratt (KMP)", matches, 0,
                    System.nanoTime() - startTime, traces, new int[0]);
        }

        int[] lps = computeLPSArray(pattern);
        traces.add(String.format("Precomputed LPS table for pattern '%s': %s", pattern, formatArray(lps)));

        int n = text.length();
        int m = pattern.length();
        int i = 0; // index for text
        int j = 0; // index for pattern

        while (i < n) {
            comparisons++;
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;

                if (j == m) {
                    int matchIdx = i - j;
                    matches.add(matchIdx);
                    traces.add(String.format("MATCH FOUND at text index %d! Full pattern '%s' matched.", matchIdx, pattern));
                    j = lps[j - 1];
                }
            } else {
                if (j != 0) {
                    traces.add(String.format("Mismatch at text[%d] ('%c') != pattern[%d] ('%c'). Falling back j to LPS[%d] = %d (skipping redundant checks).",
                            i, text.charAt(i), j, pattern.charAt(j), j - 1, lps[j - 1]));
                    j = lps[j - 1];
                } else {
                    traces.add(String.format("Mismatch at text[%d] ('%c') != pattern[0] ('%c'). Advancing text index to %d.",
                            i, text.charAt(i), pattern.charAt(0), i + 1));
                    i++;
                }
            }
        }

        long elapsed = System.nanoTime() - startTime;
        return new StringMatchResult("Knuth-Morris-Pratt (KMP)", matches, comparisons, elapsed, traces, lps);
    }

    private static String formatArray(int[] arr) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}
