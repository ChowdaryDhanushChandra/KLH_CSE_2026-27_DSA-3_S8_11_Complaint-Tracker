package com.texthack.core.model;

import com.texthack.core.ds.DynamicArray;

/**
 * Generic algorithm result wrapper holding the final answer,
 * chronological execution steps for UI playback, and performance telemetry.
 *
 * @param <T> Output type
 */
public class AlgoResult<T> {
    private final T output;
    private final DynamicArray<StepEvent> steps;
    private final long elapsedMicros;
    private final long comparisons;
    private final long memoryBytes;
    private final String timeComplexity;
    private final String spaceComplexity;

    public AlgoResult(T output, DynamicArray<StepEvent> steps, long elapsedMicros,
                      long comparisons, long memoryBytes, String timeComplexity, String spaceComplexity) {
        this.output = output;
        this.steps = steps != null ? steps : new DynamicArray<>();
        this.elapsedMicros = elapsedMicros;
        this.comparisons = comparisons;
        this.memoryBytes = memoryBytes;
        this.timeComplexity = timeComplexity;
        this.spaceComplexity = spaceComplexity;
    }

    public T getOutput() { return output; }
    public DynamicArray<StepEvent> getSteps() { return steps; }
    public long getElapsedMicros() { return elapsedMicros; }
    public long getComparisons() { return comparisons; }
    public long getMemoryBytes() { return memoryBytes; }
    public String getTimeComplexity() { return timeComplexity; }
    public String getSpaceComplexity() { return spaceComplexity; }
    public int getStepCount() { return steps.size(); }
}
