package com.texthack.module2_strings;

import com.texthack.core.ds.DynamicArray;
import com.texthack.core.model.AlgoResult;
import com.texthack.core.model.StepEvent;
import com.texthack.core.model.StepEvent.StepType;
import com.texthack.core.util.OpCounter;
import com.texthack.core.util.Timer;

/**
 * Kasai's Linear-Time LCP (Longest Common Prefix) Array Algorithm - O(n).
 * Leverages the rank-inversion theorem: LCP[rank[i]] >= LCP[rank[i-1]] - 1.
 * Pure Java engine, strictly zero java.util.* collections.
 */
public class KasaiLCP {

    public static final String[] PSEUDOCODE = {
        "compute rank array: rank[SA[i]] = i",
        "h = 0",
        "for i = 0 to n - 1 do",
        "    if rank[i] > 0 then",
        "        k = SA[rank[i] - 1]  // Previous suffix in sorted order",
        "        while i + h < n and k + h < n and text[i + h] == text[k + h] do h++",
        "        LCP[rank[i]] = h",
        "        if h > 0 then h = h - 1  // Kasai invariant: drop by at most 1"
    };

    public AlgoResult<int[]> computeLCP(String text, int[] sa) {
        Timer timer = new Timer();
        OpCounter opCounter = new OpCounter();
        DynamicArray<StepEvent> steps = new DynamicArray<>();

        timer.start();

        if (text == null || sa == null || sa.length == 0 || text.length() != sa.length) {
            timer.stop();
            steps.add(StepEvent.simple(1, 1, "Input validation complete: invalid string or suffix array.", StepType.COMPLETE));
            return new AlgoResult<>(new int[0], steps, timer.getElapsedMicros(), 0, 0, "O(n)", "O(n)");
        }

        int n = text.length();
        int[] lcp = new int[n];
        int[] rank = new int[n];
        int stepNum = 1;

        steps.add(StepEvent.simple(stepNum++, 1,
                "Phase 1: Inverting Suffix Array to compute rank positions (rank[SA[i]] = i)...", StepType.START));

        for (int i = 0; i < n; i++) {
            rank[sa[i]] = i;
            opCounter.incrementArrayAccesses();
        }

        int h = 0; // Current prefix match length
        steps.add(StepEvent.simple(stepNum++, 2,
                "Phase 2: Scanning original text indices i=0 to " + (n - 1) + " with Kasai invariant (h=0)...", StepType.START));

        for (int i = 0; i < n; i++) {
            int r = rank[i];
            steps.add(new StepEvent(stepNum++, 3,
                    "Evaluating text index i=" + i + " (Suffix: \"" + text.substring(i) +
                    "\") at rank[" + i + "] = " + r + " with current h=" + h,
                    StepType.START, i, r, i, n - 1, lcp.clone()));

            if (r > 0) {
                int k = sa[r - 1]; // Previous suffix in sorted order
                steps.add(new StepEvent(stepNum++, 5,
                        "Comparing with preceding suffix at SA[" + (r - 1) + "] = " + k + " (\"" + text.substring(k) + "\")",
                        StepType.SHIFT_WINDOW, i, k, i, n - 1, lcp.clone()));

                while (i + h < n && k + h < n && text.charAt(i + h) == text.charAt(k + h)) {
                    opCounter.incrementComparisons();
                    steps.add(new StepEvent(stepNum++, 6,
                            "Character match at offset h=" + h + ": text[" + (i + h) + "] ('" + text.charAt(i + h) +
                            "') == text[" + (k + h) + "] ('" + text.charAt(k + h) + "'). Incrementing h=" + (h + 1),
                            StepType.COMPARE_MATCH, i + h, k + h, i, i + h, lcp.clone()));
                    h++;
                }

                if (i + h < n && k + h < n) {
                    opCounter.incrementComparisons();
                    steps.add(new StepEvent(stepNum++, 6,
                            "Character mismatch at offset h=" + h + ": text[" + (i + h) + "] ('" + text.charAt(i + h) +
                            "') != text[" + (k + h) + "] ('" + text.charAt(k + h) + "')",
                            StepType.COMPARE_MISMATCH, i + h, k + h, i, i + h, lcp.clone()));
                }

                lcp[r] = h;
                steps.add(new StepEvent(stepNum++, 7,
                        "Recorded LCP[rank[" + i + "]] = LCP[" + r + "] = " + h,
                        StepType.TABLE_UPDATE, r, h, 0, n - 1, lcp.clone()));

                if (h > 0) {
                    h--;
                    steps.add(new StepEvent(stepNum++, 8,
                            "Kasai Theorem guarantee: decrementing h to " + h + " for next index i=" + (i + 1),
                            StepType.START, i, h, 0, n - 1, lcp.clone()));
                }
            } else {
                lcp[0] = 0; // First suffix has no predecessor
                steps.add(new StepEvent(stepNum++, 4,
                        "rank[" + i + "] = 0 (first lexicographical suffix). LCP[0] = 0.",
                        StepType.TABLE_UPDATE, 0, 0, 0, 0, lcp.clone()));
            }
        }

        timer.stop();
        steps.add(new StepEvent(stepNum++, 8,
                "Kasai LCP Construction Complete. All LCP values computed in O(n) time.",
                StepType.COMPLETE, 0, 0, 0, n - 1, lcp));

        long memoryEstimate = (long) n * 8 + 128;
        return new AlgoResult<>(lcp, steps, timer.getElapsedMicros(), opCounter.getComparisons(),
                memoryEstimate, "O(n)", "O(n)");
    }
}
