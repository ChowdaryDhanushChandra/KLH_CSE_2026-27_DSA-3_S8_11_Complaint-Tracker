package com.texthack.core.model;

/**
 * Benchmark telemetry entry for comparing algorithms in the Comparison Hub.
 */
public class Benchmark {
    private final String algorithmName;
    private final String inputLabel;
    private final int inputLength;
    private final long elapsedMicros;
    private final long comparisons;
    private final long memoryBytes;
    private final String complexity;
    private final boolean verified;

    public Benchmark(String algorithmName, String inputLabel, int inputLength,
                     long elapsedMicros, long comparisons, long memoryBytes,
                     String complexity, boolean verified) {
        this.algorithmName = algorithmName;
        this.inputLabel = inputLabel;
        this.inputLength = inputLength;
        this.elapsedMicros = elapsedMicros;
        this.comparisons = comparisons;
        this.memoryBytes = memoryBytes;
        this.complexity = complexity;
        this.verified = verified;
    }

    public String getAlgorithmName() { return algorithmName; }
    public String getInputLabel() { return inputLabel; }
    public int getInputLength() { return inputLength; }
    public long getElapsedMicros() { return elapsedMicros; }
    public long getComparisons() { return comparisons; }
    public long getMemoryBytes() { return memoryBytes; }
    public String getComplexity() { return complexity; }
    public boolean isVerified() { return verified; }
}
