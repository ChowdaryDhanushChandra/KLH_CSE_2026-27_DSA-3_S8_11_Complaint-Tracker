package com.texthack.engine.module2;

import com.texthack.datastructures.ManualArrayList;
import com.texthack.engine.common.StringAlgorithm;
import com.texthack.metrics.ComplexityAnalyzer;
import com.texthack.metrics.ExecutionTimer;
import com.texthack.metrics.OperationCounter;
import com.texthack.model.ComplexityInfo;
import com.texthack.model.SearchResult;

/**
 * Gusfield's Z-Algorithm for linear string matching without java.util.*.
 * Computes Z-array using sliding match window [L, R] in O(n + m) time.
 */
public class ZAlgorithmMatcher implements StringAlgorithm {

    @Override
    public String getName() {
        return "Z-Algorithm Matcher";
    }

    @Override
    public ComplexityInfo getComplexity() {
        return ComplexityAnalyzer.getComplexity("Z-Algorithm");
    }

    /**
     * Constructs the Z-array for string s.
     */
    public int[] computeZArray(String s, OperationCounter counter, ManualArrayList<String> trace) {
        int n = s.length();
        int[] z = new int[n];
        int l = 0, r = 0;

        if (trace != null) {
            trace.add("Constructing Z-array for concatenated string (length = " + n + ")");
        }

        for (int i = 1; i < n; i++) {
            if (i > r) {
                l = r = i;
                while (r < n && s.charAt(r - l) == s.charAt(r)) {
                    if (counter != null) counter.incrementComparisons();
                    r++;
                }
                if (counter != null && r < n) counter.incrementComparisons();
                z[i] = r - l;
                r--;
                if (trace != null && z[i] > 0) {
                    trace.add(String.format("Z[%d] = %d (New Z-box [%d, %d])", i, z[i], l, r));
                }
            } else {
                int k = i - l;
                if (z[k] < r - i + 1) {
                    z[i] = z[k];
                } else {
                    l = i;
                    while (r < n && s.charAt(r - l) == s.charAt(r)) {
                        if (counter != null) counter.incrementComparisons();
                        r++;
                    }
                    if (counter != null && r < n) counter.incrementComparisons();
                    z[i] = r - l;
                    r--;
                    if (trace != null) {
                        trace.add(String.format("Z[%d] = %d (Expanded Z-box [%d, %d])", i, z[i], l, r));
                    }
                }
            }
        }
        return z;
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
            trace.add("Pattern length m=" + m + " > text length n=" + n + ". No matches possible.");
            timer.stop();
            return new SearchResult(pattern, matches, 0, timer.getElapsedNanos(), timer.getFormattedTime(), getComplexity(), trace);
        }

        // Delimiter that is unlikely to be in text or pattern
        char delim = '\u0001';
        String concat = pattern + delim + text;
        trace.add("Constructed concatenated string: P + '$' + T");

        int[] z = computeZArray(concat, counter, trace);

        int concatLen = concat.length();
        for (int i = m + 1; i < concatLen; i++) {
            if (z[i] == m) {
                int textIndex = i - (m + 1);
                matches.add(textIndex);
                trace.add(String.format("Z-value equals pattern length %d at concat index %d -> Text match at %d: '%s'",
                        m, i, textIndex, text.substring(textIndex, textIndex + m)));
            }
        }

        timer.stop();
        trace.add(String.format("Z-Algorithm finished: %d matches found using %d comparisons in %s.",
                matches.size(), counter.getComparisons(), timer.getFormattedTime()));

        return new SearchResult(pattern, matches, counter.getComparisons(),
                timer.getElapsedNanos(), timer.getFormattedTime(), getComplexity(), trace);
    }
}
