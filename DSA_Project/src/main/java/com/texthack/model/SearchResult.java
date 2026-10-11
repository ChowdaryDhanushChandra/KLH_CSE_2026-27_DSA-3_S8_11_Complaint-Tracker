package com.texthack.model;

import com.texthack.datastructures.ManualArrayList;

/**
 * Encapsulates the output of a pattern search algorithm.
 */
public class SearchResult {
    private final String pattern;
    private final ManualArrayList<Integer> matchIndices;
    private final long comparisons;
    private final long executionTimeNanos;
    private final String formattedTime;
    private final ComplexityInfo complexityInfo;
    private final ManualArrayList<String> stepTraces;

    public SearchResult(String pattern, ManualArrayList<Integer> matchIndices,
                        long comparisons, long executionTimeNanos, String formattedTime,
                        ComplexityInfo complexityInfo, ManualArrayList<String> stepTraces) {
        this.pattern = pattern;
        this.matchIndices = matchIndices != null ? matchIndices : new ManualArrayList<>();
        this.comparisons = comparisons;
        this.executionTimeNanos = executionTimeNanos;
        this.formattedTime = formattedTime;
        this.complexityInfo = complexityInfo;
        this.stepTraces = stepTraces != null ? stepTraces : new ManualArrayList<>();
    }

    public String getPattern() {
        return pattern;
    }

    public ManualArrayList<Integer> getMatchIndices() {
        return matchIndices;
    }

    public int getMatchCount() {
        return matchIndices.size();
    }

    public long getComparisons() {
        return comparisons;
    }

    public long getExecutionTimeNanos() {
        return executionTimeNanos;
    }

    public String getFormattedTime() {
        return formattedTime;
    }

    public ComplexityInfo getComplexityInfo() {
        return complexityInfo;
    }

    public ManualArrayList<String> getStepTraces() {
        return stepTraces;
    }

    public String getIndicesFormatted() {
        if (matchIndices.isEmpty()) {
            return "None (No matches found)";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < matchIndices.size(); i++) {
            sb.append(matchIndices.get(i));
            if (i < matchIndices.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
