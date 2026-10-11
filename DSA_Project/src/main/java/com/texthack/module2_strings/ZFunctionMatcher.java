package com.texthack.module2_strings;

import com.texthack.core.ds.DynamicArray;
import com.texthack.core.model.AlgoResult;
import com.texthack.core.model.StepEvent;
import com.texthack.core.model.StepEvent.StepType;
import com.texthack.core.util.OpCounter;
import com.texthack.core.util.Timer;

/**
 * Z-Algorithm (Z-Function) String Pattern Matcher - O(n + m).
 * Computes the Z-array over S = P + '$' + T using the [L, R] sliding match interval window.
 * Pure Java engine, strictly zero java.util.* collections.
 */
public class ZFunctionMatcher {

    public static class ZResult {
        private final DynamicArray<Integer> matches;
        private final int[] zArray;
        private final String combinedString;

        public ZResult(DynamicArray<Integer> matches, int[] zArray, String combinedString) {
            this.matches = matches;
            this.zArray = zArray;
            this.combinedString = combinedString;
        }

        public DynamicArray<Integer> getMatches() { return matches; }
        public int[] getZArray() { return zArray; }
        public String getCombinedString() { return combinedString; }
    }

    public static final String[] PSEUDOCODE = {
        "S = P + '$' + T; L = 0, R = 0",
        "for i = 1 to |S| - 1 do",
        "    if i <= R then",
        "        if Z[i - L] < R - i + 1 then Z[i] = Z[i - L]",
        "        else L = i; while R < |S| and S[R] == S[R - L] do R++; Z[i] = R - L; R--",
        "    else",
        "        L = R = i; while R < |S| and S[R] == S[R - L] do R++; Z[i] = R - L; R--",
        "    if i > |P| and Z[i] == |P| then report match at i - (|P| + 1)"
    };

    public AlgoResult<ZResult> search(String text, String pattern) {
        Timer timer = new Timer();
        OpCounter opCounter = new OpCounter();
        DynamicArray<StepEvent> steps = new DynamicArray<>();
        DynamicArray<Integer> matches = new DynamicArray<>();

        timer.start();

        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            timer.stop();
            steps.add(StepEvent.simple(1, 1, "Input validation complete: invalid input.", StepType.COMPLETE));
            return new AlgoResult<>(new ZResult(matches, new int[0], ""), steps, timer.getElapsedMicros(), 0, 0, "O(n + m)", "O(n + m)");
        }

        int m = pattern.length();
        int n = text.length();
        String combined = pattern + "$" + text;
        int len = combined.length();
        int[] z = new int[len];
        int stepNum = 1;

        steps.add(StepEvent.simple(stepNum++, 1,
                "Formed combined string S = P + '$' + T (Length: " + len + "). Initializing [L, R] = [0, 0]",
                StepType.START));

        int l = 0;
        int r = 0;

        for (int i = 1; i < len; i++) {
            if (i > r) {
                l = r = i;
                steps.add(new StepEvent(stepNum++, 7,
                        "Index i=" + i + " > R=" + r + " (outside Z-box). Setting L=R=" + i + " to start naive comparisons.",
                        StepType.SHIFT_WINDOW, i, 0, l, r, z.clone()));

                while (r < len && combined.charAt(r) == combined.charAt(r - l)) {
                    opCounter.incrementComparisons();
                    steps.add(new StepEvent(stepNum++, 7,
                            "Comparison match: S[" + r + "] ('" + combined.charAt(r) + "') == S[" + (r - l) + "] ('" + combined.charAt(r - l) + "')",
                            StepType.COMPARE_MATCH, r, r - l, l, r, z.clone()));
                    r++;
                }
                if (r < len) {
                    opCounter.incrementComparisons();
                    steps.add(new StepEvent(stepNum++, 7,
                            "Comparison mismatch: S[" + r + "] ('" + combined.charAt(r) + "') != S[" + (r - l) + "] ('" + combined.charAt(r - l) + "')",
                            StepType.COMPARE_MISMATCH, r, r - l, l, r, z.clone()));
                }
                z[i] = r - l;
                r--;
                steps.add(new StepEvent(stepNum++, 7,
                        "Z[" + i + "] = " + z[i] + ". Updated Z-box: [" + l + ", " + r + "]",
                        StepType.TABLE_UPDATE, i, z[i], l, r, z.clone()));
            } else {
                int k = i - l;
                steps.add(new StepEvent(stepNum++, 3,
                        "Index i=" + i + " <= R=" + r + " (inside Z-box [" + l + ", " + r + "]). Checking prefix index k = i - L = " + k,
                        StepType.START, i, k, l, r, z.clone()));

                if (z[k] < r - i + 1) {
                    z[i] = z[k];
                    steps.add(new StepEvent(stepNum++, 4,
                            "Z[" + k + "] = " + z[k] + " < " + (r - i + 1) + " (fits inside box). Reusing: Z[" + i + "] = " + z[i],
                            StepType.TABLE_UPDATE, i, z[i], l, r, z.clone()));
                } else {
                    l = i;
                    steps.add(new StepEvent(stepNum++, 5,
                            "Z[" + k + "] >= remaining box length. Resetting L=" + l + " and continuing character comparisons beyond R=" + r,
                            StepType.SHIFT_WINDOW, i, k, l, r, z.clone()));

                    r++;
                    while (r < len && combined.charAt(r) == combined.charAt(r - l)) {
                        opCounter.incrementComparisons();
                        steps.add(new StepEvent(stepNum++, 5,
                                "Comparison match: S[" + r + "] ('" + combined.charAt(r) + "') == S[" + (r - l) + "] ('" + combined.charAt(r - l) + "')",
                                StepType.COMPARE_MATCH, r, r - l, l, r, z.clone()));
                        r++;
                    }
                    if (r < len) {
                        opCounter.incrementComparisons();
                        steps.add(new StepEvent(stepNum++, 5,
                                "Comparison mismatch: S[" + r + "] ('" + combined.charAt(r) + "') != S[" + (r - l) + "] ('" + combined.charAt(r - l) + "')",
                                StepType.COMPARE_MISMATCH, r, r - l, l, r, z.clone()));
                    }
                    z[i] = r - l;
                    r--;
                    steps.add(new StepEvent(stepNum++, 5,
                            "Z[" + i + "] = " + z[i] + ". Updated Z-box: [" + l + ", " + r + "]",
                            StepType.TABLE_UPDATE, i, z[i], l, r, z.clone()));
                }
            }

            // Check if pattern matched
            if (i > m && z[i] == m) {
                int matchIndex = i - (m + 1);
                matches.add(matchIndex);
                steps.add(new StepEvent(stepNum++, 8,
                        "Z[" + i + "] == pattern length " + m + "! Pattern match found at text index " + matchIndex,
                        StepType.PATTERN_FOUND, i, matchIndex, matchIndex, matchIndex + m - 1, z.clone()));
            }
        }

        timer.stop();
        steps.add(StepEvent.simple(stepNum++, 8,
                "Z-Algorithm complete. Total matches: " + matches.size(), StepType.COMPLETE));

        long memoryEstimate = (long) len * 6 + (long) matches.size() * 4 + 128;
        return new AlgoResult<>(new ZResult(matches, z, combined), steps, timer.getElapsedMicros(),
                opCounter.getComparisons(), memoryEstimate, "O(n + m)", "O(n + m)");
    }
}
