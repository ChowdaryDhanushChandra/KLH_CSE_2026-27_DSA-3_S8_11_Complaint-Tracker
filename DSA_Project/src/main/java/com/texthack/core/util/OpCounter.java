package com.texthack.core.util;

/**
 * Tracks primitive algorithmic operations (character comparisons, array writes,
 * hash evaluations, shifts) to provide deep theoretical insights into work performed.
 */
public class OpCounter {
    private long comparisons;
    private long arrayAccesses;
    private long hashComputations;
    private long shifts;

    public OpCounter() {
        reset();
    }

    public void incrementComparisons() {
        comparisons++;
    }

    public void addComparisons(long count) {
        comparisons += count;
    }

    public void incrementArrayAccesses() {
        arrayAccesses++;
    }

    public void incrementHashComputations() {
        hashComputations++;
    }

    public void incrementShifts() {
        shifts++;
    }

    public long getComparisons() { return comparisons; }
    public long getArrayAccesses() { return arrayAccesses; }
    public long getHashComputations() { return hashComputations; }
    public long getShifts() { return shifts; }

    public long getTotalOperations() {
        return comparisons + arrayAccesses + hashComputations + shifts;
    }

    public void reset() {
        comparisons = 0;
        arrayAccesses = 0;
        hashComputations = 0;
        shifts = 0;
    }
}
