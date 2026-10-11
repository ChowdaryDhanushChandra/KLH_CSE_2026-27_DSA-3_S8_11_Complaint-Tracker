package com.texthack.engine.module2;

import com.texthack.datastructures.ManualArrayList;
import com.texthack.engine.common.StringAlgorithm;
import com.texthack.metrics.ComplexityAnalyzer;
import com.texthack.metrics.ExecutionTimer;
import com.texthack.metrics.OperationCounter;
import com.texthack.model.ComplexityInfo;
import com.texthack.model.SearchResult;

/**
 * Naive (Brute-Force) String Matching Algorithm without java.util.*.
 * Slides pattern over text character by character.
 * Worst-case time: O(n · m), Best-case time: O(n).
 */
public class NaivePatternMatcher implements StringAlgorithm {

    @Override
    public String getName() {
        return "Naive Pattern Matcher";
    }

    @Override
    public ComplexityInfo getComplexity() {
        return ComplexityAnalyzer.getComplexity("Naive String Matching");
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

        trace.add("Beginning Naive Search: Text length n = " + n + ", Pattern length m = " + m);

        if (m > n) {
            trace.add("Pattern is longer than text (m > n). No match is possible.");
            timer.stop();
            return new SearchResult(pattern, matches, 0, timer.getElapsedNanos(), timer.getFormattedTime(), getComplexity(), trace);
        }

        for (int i = 0; i <= n - m; i++) {
            int j = 0;
            while (j < m) {
                counter.incrementComparisons();
                if (text.charAt(i + j) != pattern.charAt(j)) {
                    break;
                }
                j++;
            }

            if (j == m) {
                matches.add(i);
                trace.add(String.format("Match found at offset %d (Window: '%s')", i, text.substring(i, i + m)));
            }
        }

        timer.stop();
        trace.add(String.format("Naive search completed: %d matches found using %d character comparisons.",
                matches.size(), counter.getComparisons()));

        return new SearchResult(pattern, matches, counter.getComparisons(),
                timer.getElapsedNanos(), timer.getFormattedTime(), getComplexity(), trace);
    }
}
