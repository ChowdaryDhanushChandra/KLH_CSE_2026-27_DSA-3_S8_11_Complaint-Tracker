package com.texthack.engine.module3;

import com.texthack.datastructures.ManualArrayList;
import com.texthack.metrics.ComplexityAnalyzer;
import com.texthack.metrics.ExecutionTimer;
import com.texthack.model.AlgorithmResult;
import com.texthack.validation.InputValidator;

/**
 * Sum Over Subsets (SOS) Dynamic Programming without java.util.*.
 * Computes F[mask] = sum_{sub in mask} A[sub] in O(n · 2^n) time,
 * replacing the naive O(3^n) submask enumeration.
 */
public class SOSDP {

    public static class SOSResult {
        public final int[] sosValues;
        public final int[] originalArray;
        public final int bitCount;
        public final AlgorithmResult algorithmResult;

        public SOSResult(int[] sosValues, int[] originalArray, int bitCount, AlgorithmResult algoRes) {
            this.sosValues = sosValues;
            this.originalArray = originalArray;
            this.bitCount = bitCount;
            this.algorithmResult = algoRes;
        }
    }

    public static SOSResult compute(int n, int[] a) {
        ExecutionTimer timer = new ExecutionTimer();
        timer.start();

        InputValidator.validateBitmaskSize(n, 16);
        int totalStates = 1 << n;
        ManualArrayList<String> trace = new ManualArrayList<>();

        int[] f = new int[totalStates];
        for (int i = 0; i < totalStates; i++) {
            f[i] = (i < a.length) ? a[i] : 0;
        }

        trace.add(String.format("Starting SOS DP for n = %d bits (Total masks = %d). Base array initialized.", n, totalStates));

        // Dimension by dimension bit-propagation: O(n * 2^n)
        for (int i = 0; i < n; i++) {
            int bit = 1 << i;
            for (int mask = 0; mask < totalStates; mask++) {
                if ((mask & bit) != 0) {
                    f[mask] += f[mask ^ bit];
                }
            }
            trace.add(String.format("Bit %d propagation complete: F[mask] accumulated submasks differing at bit position %d.", i, i));
        }

        timer.stop();
        trace.add("SOS DP computation finished: O(n · 2^n) = " + (n * totalStates) + " operations vs Naive O(3^n) = " + (int) Math.pow(3, n));

        AlgorithmResult algoRes = new AlgorithmResult(
                "SOS DP (Sum Over Subsets)",
                "Computed F[mask] for " + totalStates + " masks",
                String.format("SOS DP complexity: O(n · 2^n) = %d ops vs Brute force O(3^n) = %d ops.",
                        n * totalStates, (int) Math.pow(3, n)),
                timer.getElapsedNanos(),
                timer.getFormattedTime(),
                0, 0,
                ComplexityAnalyzer.getComplexity("SOS DP (Sum Over Subsets)"),
                trace
        );

        return new SOSResult(f, a, n, algoRes);
    }
}
