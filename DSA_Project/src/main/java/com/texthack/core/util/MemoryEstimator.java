package com.texthack.core.util;

/**
 * Utility for estimating memory consumption of data structures and algorithm executions.
 */
public class MemoryEstimator {

    /**
     * Estimates JVM heap memory delta around an algorithmic run.
     */
    public static long estimateCurrentUsedMemory() {
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }

    /**
     * Estimates theoretical array memory footprint in bytes:
     * Header (16 bytes) + length (4 bytes) + padding (4 bytes) + elements * elementSize.
     */
    public static long estimateArrayBytes(int length, int elementBytes) {
        return 24L + ((long) length * elementBytes);
    }

    /**
     * Estimates string memory footprint in bytes.
     */
    public static long estimateStringBytes(String str) {
        if (str == null) return 0;
        return 24L + (str.length() * 2L);
    }
}
