package com.texthack.model;

import com.texthack.datastructures.ManualArrayList;

/**
 * Result container for dynamic programming edit distance and sequence alignments.
 */
public class AlignmentResult {
    private final String algorithmName;
    private final int distanceOrScore;
    private final String alignedSeq1;
    private final String alignedSeq2;
    private final ManualArrayList<String> operations;
    private final int[][] dpMatrix;
    private final String[] rowLabels;
    private final String[] colLabels;
    private final long executionTimeNanos;
    private final String formattedTime;
    private final ComplexityInfo complexityInfo;

    public AlignmentResult(String algorithmName, int distanceOrScore,
                           String alignedSeq1, String alignedSeq2,
                           ManualArrayList<String> operations,
                           int[][] dpMatrix, String[] rowLabels, String[] colLabels,
                           long executionTimeNanos, String formattedTime,
                           ComplexityInfo complexityInfo) {
        this.algorithmName = algorithmName;
        this.distanceOrScore = distanceOrScore;
        this.alignedSeq1 = alignedSeq1;
        this.alignedSeq2 = alignedSeq2;
        this.operations = operations != null ? operations : new ManualArrayList<>();
        this.dpMatrix = dpMatrix;
        this.rowLabels = rowLabels;
        this.colLabels = colLabels;
        this.executionTimeNanos = executionTimeNanos;
        this.formattedTime = formattedTime;
        this.complexityInfo = complexityInfo;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public int getDistanceOrScore() {
        return distanceOrScore;
    }

    public String getAlignedSeq1() {
        return alignedSeq1;
    }

    public String getAlignedSeq2() {
        return alignedSeq2;
    }

    public ManualArrayList<String> getOperations() {
        return operations;
    }

    public int[][] getDpMatrix() {
        return dpMatrix;
    }

    public String[] getRowLabels() {
        return rowLabels;
    }

    public String[] getColLabels() {
        return colLabels;
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
}
