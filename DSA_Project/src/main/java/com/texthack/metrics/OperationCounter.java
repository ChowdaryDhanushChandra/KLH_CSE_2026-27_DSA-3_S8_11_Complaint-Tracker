package com.texthack.metrics;

/**
 * Tracks fundamental operations and comparisons executed by algorithms.
 */
public class OperationCounter {
    private long comparisons;
    private long operations;

    public void incrementComparisons() {
        comparisons++;
    }

    public void addComparisons(long count) {
        comparisons += count;
    }

    public void incrementOperations() {
        operations++;
    }

    public void addOperations(long count) {
        operations += count;
    }

    public long getComparisons() {
        return comparisons;
    }

    public long getOperations() {
        return operations;
    }

    public void reset() {
        comparisons = 0;
        operations = 0;
    }
}
