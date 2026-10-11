package com.texthack.model;

import com.texthack.datastructures.ManualArrayList;

/**
 * Standard container representing algorithm execution output and performance metrics.
 */
public class AlgorithmResult {
    private final String algorithmName;
    private final String resultSummary;
    private final String detailedOutput;
    private final long executionTimeNanos;
    private final String formattedTime;
    private final long comparisons;
    private final long operations;
    private final ComplexityInfo complexityInfo;
    private final ManualArrayList<String> stepTraces;

    public AlgorithmResult(String algorithmName, String resultSummary, String detailedOutput,
                           long executionTimeNanos, String formattedTime,
                           long comparisons, long operations,
                           ComplexityInfo complexityInfo, ManualArrayList<String> stepTraces) {
        this.algorithmName = algorithmName;
        this.resultSummary = resultSummary;
        this.detailedOutput = detailedOutput;
        this.executionTimeNanos = executionTimeNanos;
        this.formattedTime = formattedTime;
        this.comparisons = comparisons;
        this.operations = operations;
        this.complexityInfo = complexityInfo;
        this.stepTraces = stepTraces != null ? stepTraces : new ManualArrayList<>();
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public String getResultSummary() {
        return resultSummary;
    }

    public String getDetailedOutput() {
        return detailedOutput;
    }

    public long getExecutionTimeNanos() {
        return executionTimeNanos;
    }

    public String getFormattedTime() {
        return formattedTime;
    }

    public long getComparisons() {
        return comparisons;
    }

    public long getOperations() {
        return operations;
    }

    public ComplexityInfo getComplexityInfo() {
        return complexityInfo;
    }

    public ManualArrayList<String> getStepTraces() {
        return stepTraces;
    }
}
