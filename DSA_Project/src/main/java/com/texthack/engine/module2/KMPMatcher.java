package com.texthack.engine.module2;

import com.texthack.datastructures.ManualArrayList;
import com.texthack.engine.common.StringAlgorithm;
import com.texthack.metrics.ComplexityAnalyzer;
import com.texthack.metrics.ExecutionTimer;
import com.texthack.metrics.OperationCounter;
import com.texthack.model.ComplexityInfo;
import com.texthack.model.SearchResult;

/**
 * Knuth-Morris-Pratt (KMP) Pattern Matcher without java.util.*.
 * Precomputes failure function (LPS array) in O(m) time.
 * Overall worst-case time: O(n + m), Space: O(m).
 */
public class KMPMatcher implements StringAlgorithm {

    @Override
    public String getName() {
        return "Knuth-Morris-Pratt (KMP)";
    }

    @Override
    public ComplexityInfo getComplexity() {
        return ComplexityAnalyzer.getComplexity("Knuth-Morris-Pratt");
    }

    /**
     * Computes the Longest Proper Prefix which is also a Suffix (LPS) array.
     */
    public int[] computeLPS(String pattern, OperationCounter counter, ManualArrayList<String> trace) {
        int m = pattern.length();
        int[] lps = new int[m];
        int len = 0;
        int i = 1;
        lps[0] = 0;

        if (trace != null) {
            trace.add("Computing LPS array for pattern: \"" + pattern + "\"");
        }

        while (i < m) {
            if (counter != null) counter.incrementComparisons();
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                if (trace != null) {
                    trace.add(String.format("LPS[%d] = %d (pattern[%d] == pattern[%d] == '%c')",
                            i, len, i, len - 1, pattern.charAt(i)));
                }
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1]; // Fallback to previous LPS length
                } else {
                    lps[i] = 0;
                    if (trace != null) {
                        trace.add(String.format("LPS[%d] = 0 (no prefix match for '%c')", i, pattern.charAt(i)));
                    }
                    i++;
                }
            }
        }
        return lps;
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

        int n = text.length();
        int m = pattern.length();

        if (m > n) {
            trace.add("Pattern is longer than text. No matches possible.");
            timer.stop();
            return new SearchResult(pattern, matches, 0, timer.getElapsedNanos(), timer.getFormattedTime(), getComplexity(), trace);
        }

        int[] lps = computeLPS(pattern, counter, trace);

        StringBuilder lpsStr = new StringBuilder("LPS Table: [");
        for (int k = 0; k < lps.length; k++) {
            lpsStr.append(lps[k]);
            if (k < lps.length - 1) lpsStr.append(", ");
        }
        lpsStr.append("]");
        trace.add(lpsStr.toString());

        trace.add("Starting KMP text scan with linear progression (never decrementing text pointer i)...");

        int i = 0; // Text index
        int j = 0; // Pattern index

        while (i < n) {
            counter.incrementComparisons();
            if (pattern.charAt(j) == text.charAt(i)) {
                i++;
                j++;
            }

            if (j == m) {
                int matchIndex = i - j;
                matches.add(matchIndex);
                trace.add(String.format("Match found at offset %d: '%s'", matchIndex, text.substring(matchIndex, matchIndex + m)));
                j = lps[j - 1];
            } else if (i < n && pattern.charAt(j) != text.charAt(i)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }

        timer.stop();
        trace.add(String.format("KMP search completed: %d matches found using %d comparisons in %s.",
                matches.size(), counter.getComparisons(), timer.getFormattedTime()));

        return new SearchResult(pattern, matches, counter.getComparisons(),
                timer.getElapsedNanos(), timer.getFormattedTime(), getComplexity(), trace);
    }
}
