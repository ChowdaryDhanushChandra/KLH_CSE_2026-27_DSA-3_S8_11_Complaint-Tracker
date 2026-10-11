package com.texthack.engine.module3;

import com.texthack.datastructures.ManualArrayList;
import com.texthack.metrics.ComplexityAnalyzer;
import com.texthack.metrics.ExecutionTimer;
import com.texthack.model.AlgorithmResult;
import com.texthack.validation.InputValidator;

/**
 * Exact Set Cover problem solved via Bitmask Dynamic Programming in O(2^n · m).
 */
public class SetCoverDP {

    public static class SetCoverResult {
        public final int minSubsets;
        public final ManualArrayList<Integer> chosenSubsetIndices;
        public final AlgorithmResult algorithmResult;

        public SetCoverResult(int minSubsets, ManualArrayList<Integer> chosenSubsets, AlgorithmResult algoRes) {
            this.minSubsets = minSubsets;
            this.chosenSubsetIndices = chosenSubsets;
            this.algorithmResult = algoRes;
        }
    }

    public static SetCoverResult solve(int universeSize, int[] subsetMasks, String[] subsetNames) {
        ExecutionTimer timer = new ExecutionTimer();
        timer.start();

        InputValidator.validateBitmaskSize(universeSize, 16);

        int totalStates = 1 << universeSize;
        int m = subsetMasks.length;
        ManualArrayList<String> trace = new ManualArrayList<>();

        int[] dp = new int[totalStates];
        int[] parentMask = new int[totalStates];
        int[] chosenSubset = new int[totalStates];

        for (int i = 0; i < totalStates; i++) {
            dp[i] = 1_000_000;
            parentMask[i] = -1;
            chosenSubset[i] = -1;
        }
        dp[0] = 0;

        for (int mask = 0; mask < totalStates; mask++) {
            if (dp[mask] >= 1_000_000) continue;

            for (int k = 0; k < m; k++) {
                int nextMask = mask | subsetMasks[k];
                if (dp[mask] + 1 < dp[nextMask]) {
                    dp[nextMask] = dp[mask] + 1;
                    parentMask[nextMask] = mask;
                    chosenSubset[nextMask] = k;
                }
            }
        }

        int target = totalStates - 1;
        int minSubsets = dp[target];
        ManualArrayList<Integer> chosen = new ManualArrayList<>();
        StringBuilder summary = new StringBuilder();

        if (minSubsets < 1_000_000) {
            int curr = target;
            while (curr > 0) {
                int k = chosenSubset[curr];
                chosen.add(k);
                curr = parentMask[curr];
            }
            summary.append("Covered universe with ").append(chosen.size()).append(" subsets: ");
            for (int i = 0; i < chosen.size(); i++) {
                summary.append(subsetNames[chosen.get(i)]);
                if (i < chosen.size() - 1) summary.append(", ");
            }
            trace.add(summary.toString());
        } else {
            summary.append("Universe cannot be completely covered with the provided subsets.");
            trace.add("Incomplete cover.");
        }

        timer.stop();

        AlgorithmResult algoRes = new AlgorithmResult(
                "Set Cover DP",
                minSubsets < 1_000_000 ? "Min Subsets: " + minSubsets : "Uncoverable",
                summary.toString(),
                timer.getElapsedNanos(),
                timer.getFormattedTime(),
                0, 0,
                ComplexityAnalyzer.getComplexity("Set Cover DP"),
                trace
        );

        return new SetCoverResult(minSubsets, chosen, algoRes);
    }
}
