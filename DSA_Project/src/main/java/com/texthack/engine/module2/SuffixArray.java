package com.texthack.engine.module2;

import com.texthack.datastructures.ManualArrayList;
import com.texthack.metrics.ComplexityAnalyzer;
import com.texthack.model.ComplexityInfo;

/**
 * Encapsulates the Suffix Array data structure and its sorted suffixes.
 */
public class SuffixArray {
    private final String text;
    private final int[] sa;
    private final long executionTimeNanos;
    private final String formattedTime;
    private final ManualArrayList<String> traceLogs;

    public SuffixArray(String text, int[] sa, long timeNanos, String formattedTime, ManualArrayList<String> trace) {
        this.text = text;
        this.sa = sa;
        this.executionTimeNanos = timeNanos;
        this.formattedTime = formattedTime;
        this.traceLogs = trace;
    }

    public String getText() {
        return text;
    }

    public int[] getArray() {
        return sa;
    }

    public int length() {
        return sa.length;
    }

    public int get(int index) {
        return sa[index];
    }

    public String getSuffix(int index) {
        int start = sa[index];
        return text.substring(start);
    }

    public long getExecutionTimeNanos() {
        return executionTimeNanos;
    }

    public String getFormattedTime() {
        return formattedTime;
    }

    public ManualArrayList<String> getTraceLogs() {
        return traceLogs;
    }

    public ComplexityInfo getComplexity() {
        return ComplexityAnalyzer.getComplexity("Suffix Array");
    }
}
