package complainttracker.algorithms;

import java.util.ArrayList;
import java.util.List;

public class StringMatchResult {
    private final String algorithmName;
    private final List<Integer> matchIndices;
    private final int totalComparisons;
    private final long executionTimeNanos;
    private final List<String> traceSteps;
    private final int[] auxiliaryArray; // e.g. LPS array for KMP

    public StringMatchResult(String algorithmName, List<Integer> matchIndices, int totalComparisons,
                             long executionTimeNanos, List<String> traceSteps, int[] auxiliaryArray) {
        this.algorithmName = algorithmName;
        this.matchIndices = matchIndices != null ? matchIndices : new ArrayList<>();
        this.totalComparisons = totalComparisons;
        this.executionTimeNanos = executionTimeNanos;
        this.traceSteps = traceSteps != null ? traceSteps : new ArrayList<>();
        this.auxiliaryArray = auxiliaryArray;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public List<Integer> getMatchIndices() {
        return matchIndices;
    }

    public int getMatchCount() {
        return matchIndices.size();
    }

    public boolean hasMatch() {
        return !matchIndices.isEmpty();
    }

    public int getTotalComparisons() {
        return totalComparisons;
    }

    public long getExecutionTimeNanos() {
        return executionTimeNanos;
    }

    public double getExecutionTimeMicros() {
        return executionTimeNanos / 1000.0;
    }

    public List<String> getTraceSteps() {
        return traceSteps;
    }

    public int[] getAuxiliaryArray() {
        return auxiliaryArray;
    }
}
