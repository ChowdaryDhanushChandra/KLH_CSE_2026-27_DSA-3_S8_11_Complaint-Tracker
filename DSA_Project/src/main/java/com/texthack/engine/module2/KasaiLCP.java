package com.texthack.engine.module2;

import com.texthack.datastructures.ManualArrayList;
import com.texthack.metrics.ComplexityAnalyzer;
import com.texthack.metrics.ExecutionTimer;
import com.texthack.model.ComplexityInfo;

/**
 * Kasai's algorithm for linear-time O(n) LCP (Longest Common Prefix) array construction without java.util.*.
 */
public class KasaiLCP {

    public static class LCPResult {
        public final int[] lcp;
        public final int[] sa;
        public final String text;
        public final long executionTimeNanos;
        public final String formattedTime;
        public final ManualArrayList<String> traceLogs;
        public final ComplexityInfo complexityInfo;

        public LCPResult(int[] lcp, int[] sa, String text, long timeNanos, String formattedTime,
                         ManualArrayList<String> trace, ComplexityInfo complexity) {
            this.lcp = lcp;
            this.sa = sa;
            this.text = text;
            this.executionTimeNanos = timeNanos;
            this.formattedTime = formattedTime;
            this.traceLogs = trace;
            this.complexityInfo = complexity;
        }
    }

    public static LCPResult compute(SuffixArray suffixArray) {
        ExecutionTimer timer = new ExecutionTimer();
        timer.start();

        String text = suffixArray.getText();
        int[] sa = suffixArray.getArray();
        int n = sa.length;

        ManualArrayList<String> trace = new ManualArrayList<>();

        if (n == 0) {
            timer.stop();
            return new LCPResult(new int[0], sa, text, timer.getElapsedNanos(), timer.getFormattedTime(),
                    trace, ComplexityAnalyzer.getComplexity("Kasai LCP"));
        }

        trace.add("Starting Kasai algorithm on text of length " + n + " with precomputed Suffix Array...");

        // 1. Invert the suffix array to get ranks: rank[sa[i]] = i
        int[] rank = new int[n];
        for (int i = 0; i < n; i++) {
            rank[sa[i]] = i;
        }

        int[] lcp = new int[n];
        int h = 0; // Current match length

        for (int i = 0; i < n; i++) {
            if (rank[i] > 0) {
                int j = sa[rank[i] - 1]; // Preceding suffix in lexicographical order

                while (i + h < n && j + h < n && text.charAt(i + h) == text.charAt(j + h)) {
                    h++;
                }

                lcp[rank[i]] = h;
                trace.add(String.format("LCP at rank %d between suffix %d (\"%s\") and suffix %d (\"%s\") = %d",
                        rank[i], j, previewSuffix(text, j), i, previewSuffix(text, i), h));

                if (h > 0) {
                    h--; // By Kasai's theorem, next LCP is at least h - 1
                }
            } else {
                lcp[0] = 0; // First suffix in sorted order has no predecessor
            }
        }

        timer.stop();
        trace.add("Kasai LCP computation complete in " + timer.getFormattedTime() + " (Strictly linear O(n) operations).");

        return new LCPResult(lcp, sa, text, timer.getElapsedNanos(), timer.getFormattedTime(),
                trace, ComplexityAnalyzer.getComplexity("Kasai LCP"));
    }

    private static String previewSuffix(String text, int start) {
        int maxLen = 12;
        if (start + maxLen >= text.length()) {
            return text.substring(start);
        }
        return text.substring(start, start + maxLen) + "...";
    }
}
