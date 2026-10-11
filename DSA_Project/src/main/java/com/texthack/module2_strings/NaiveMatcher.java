package com.texthack.module2_strings;

import com.texthack.core.ds.DynamicArray;
import com.texthack.core.model.AlgoResult;
import com.texthack.core.model.StepEvent;
import com.texthack.core.model.StepEvent.StepType;
import com.texthack.core.util.OpCounter;
import com.texthack.core.util.Timer;

/**
 * Naive (Brute-Force) String Pattern Matcher - O(nm).
 * Emits detailed step events for every character comparison, match, mismatch, and window shift.
 * Pure Java engine, strictly zero java.util.* collections.
 */
public class NaiveMatcher {

    public static final String[] PSEUDOCODE = {
        "for i = 0 to n - m do",
        "    j = 0",
        "    while j < m and text[i + j] == pattern[j] do",
        "        j = j + 1",
        "    if j == m then",
        "        report match at index i",
        "    shift window to i + 1"
    };

    public AlgoResult<DynamicArray<Integer>> search(String text, String pattern) {
        Timer timer = new Timer();
        OpCounter opCounter = new OpCounter();
        DynamicArray<StepEvent> steps = new DynamicArray<>();
        DynamicArray<Integer> matches = new DynamicArray<>();

        timer.start();

        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            timer.stop();
            steps.add(StepEvent.simple(1, 1, "Input validation complete: pattern empty or longer than text.", StepType.COMPLETE));
            return new AlgoResult<>(matches, steps, timer.getElapsedMicros(), 0, 0, "O(n · m)", "O(1)");
        }

        int n = text.length();
        int m = pattern.length();
        int stepNum = 1;

        steps.add(StepEvent.simple(stepNum++, 1, "Initialize search: text length n=" + n + ", pattern length m=" + m, StepType.START));

        for (int i = 0; i <= n - m; i++) {
            steps.add(new StepEvent(stepNum++, 1, "Aligning pattern at text index i=" + i,
                    StepType.SHIFT_WINDOW, i, 0, i, i + m - 1, null));

            int j = 0;
            steps.add(new StepEvent(stepNum++, 2, "Reset pattern index j=0", StepType.START, i, j, i, i + m - 1, null));

            while (j < m) {
                opCounter.incrementComparisons();
                char tc = text.charAt(i + j);
                char pc = pattern.charAt(j);

                if (tc == pc) {
                    steps.add(new StepEvent(stepNum++, 3,
                            "Match: text[" + (i + j) + "] ('" + tc + "') == pattern[" + j + "] ('" + pc + "')",
                            StepType.COMPARE_MATCH, i + j, j, i, i + m - 1, null));
                    j++;
                    steps.add(new StepEvent(stepNum++, 4, "Increment j=" + j, StepType.START, i + j, j, i, i + m - 1, null));
                } else {
                    steps.add(new StepEvent(stepNum++, 3,
                            "Mismatch: text[" + (i + j) + "] ('" + tc + "') != pattern[" + j + "] ('" + pc + "')",
                            StepType.COMPARE_MISMATCH, i + j, j, i, i + m - 1, null));
                    break;
                }
            }

            if (j == m) {
                matches.add(i);
                steps.add(new StepEvent(stepNum++, 6, "Pattern fully matched at text index i=" + i + "!",
                        StepType.PATTERN_FOUND, i, m - 1, i, i + m - 1, null));
            } else {
                steps.add(new StepEvent(stepNum++, 7, "Mismatch found. Shifting window from index i=" + i,
                        StepType.SHIFT_WINDOW, i, j, i, i + m - 1, null));
            }
        }

        timer.stop();
        steps.add(StepEvent.simple(stepNum++, 7, "Search finished. Total matches found: " + matches.size(), StepType.COMPLETE));

        long memoryEstimate = (long) (n + m) * 2 + (long) matches.size() * 4 + 64;
        return new AlgoResult<>(matches, steps, timer.getElapsedMicros(), opCounter.getComparisons(),
                memoryEstimate, "O(n · m)", "O(1)");
    }
}
