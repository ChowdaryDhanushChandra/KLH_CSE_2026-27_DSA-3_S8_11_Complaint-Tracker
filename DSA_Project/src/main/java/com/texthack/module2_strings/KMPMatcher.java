package com.texthack.module2_strings;

import com.texthack.core.ds.DynamicArray;
import com.texthack.core.model.AlgoResult;
import com.texthack.core.model.StepEvent;
import com.texthack.core.model.StepEvent.StepType;
import com.texthack.core.util.OpCounter;
import com.texthack.core.util.Timer;

/**
 * Knuth-Morris-Pratt (KMP) String Pattern Matcher - O(n + m).
 * Computes failure function (LPS table) and executes linear text scanning without backtracking text pointer.
 * Pure Java engine, strictly zero java.util.* collections.
 */
public class KMPMatcher {

    public static class KMPResult {
        private final DynamicArray<Integer> matches;
        private final int[] lpsTable;

        public KMPResult(DynamicArray<Integer> matches, int[] lpsTable) {
            this.matches = matches;
            this.lpsTable = lpsTable;
        }

        public DynamicArray<Integer> getMatches() { return matches; }
        public int[] getLpsTable() { return lpsTable; }
    }

    public static final String[] PSEUDOCODE = {
        "computeLPS(P, m, lps)  // Precompute failure function",
        "i = 0, j = 0",
        "while i < n do",
        "    if pattern[j] == text[i] then i++, j++",
        "    if j == m then report match at (i - j), j = lps[j - 1]",
        "    else if i < n and pattern[j] != text[i] then",
        "        if j != 0 then j = lps[j - 1]  // Failure link fallback",
        "        else i++"
    };

    public AlgoResult<KMPResult> search(String text, String pattern) {
        Timer timer = new Timer();
        OpCounter opCounter = new OpCounter();
        DynamicArray<StepEvent> steps = new DynamicArray<>();
        DynamicArray<Integer> matches = new DynamicArray<>();

        timer.start();

        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            timer.stop();
            int[] emptyLps = (pattern != null && !pattern.isEmpty()) ? new int[pattern.length()] : new int[0];
            steps.add(StepEvent.simple(1, 1, "Input validation complete: invalid input.", StepType.COMPLETE));
            return new AlgoResult<>(new KMPResult(matches, emptyLps), steps, timer.getElapsedMicros(), 0, 0, "O(n + m)", "O(m)");
        }

        int n = text.length();
        int m = pattern.length();
        int[] lps = new int[m];
        int stepNum = 1;

        // Phase 1: Compute LPS Array
        steps.add(StepEvent.simple(stepNum++, 1, "Phase 1: Precomputing LPS (Failure Function) Table for pattern: \"" + pattern + "\"", StepType.START));
        computeLpsTable(pattern, lps, steps, opCounter, stepNum);
        stepNum = steps.size() + 1;

        // Phase 2: KMP Text Scanning
        steps.add(StepEvent.simple(stepNum++, 2, "Phase 2: Scanning text with LPS table. Initializing i=0, j=0.", StepType.START));
        int i = 0; // index for text
        int j = 0; // index for pattern

        while (i < n) {
            opCounter.incrementComparisons();
            char tc = text.charAt(i);
            char pc = pattern.charAt(j);

            if (pc == tc) {
                steps.add(new StepEvent(stepNum++, 4,
                        "Character match: text[" + i + "] ('" + tc + "') == pattern[" + j + "] ('" + pc + "'). Advancing i=" + (i + 1) + ", j=" + (j + 1),
                        StepType.COMPARE_MATCH, i, j, i - j, i - j + m - 1, lps.clone()));
                i++;
                j++;
            }

            if (j == m) {
                int matchPos = i - j;
                matches.add(matchPos);
                steps.add(new StepEvent(stepNum++, 5,
                        "Pattern found at index " + matchPos + "! Shifting j to lps[" + (j - 1) + "] = " + lps[j - 1],
                        StepType.PATTERN_FOUND, matchPos, j - 1, matchPos, matchPos + m - 1, lps.clone()));
                j = lps[j - 1];
            } else if (i < n && pattern.charAt(j) != text.charAt(i)) {
                opCounter.incrementComparisons();
                tc = text.charAt(i);
                pc = pattern.charAt(j);

                if (j != 0) {
                    int prevJ = j;
                    j = lps[j - 1];
                    steps.add(new StepEvent(stepNum++, 7,
                            "Mismatch text[" + i + "] ('" + tc + "') != pattern[" + prevJ + "] ('" + pc + "'). Falling back via LPS: j = lps[" + (prevJ - 1) + "] = " + j,
                            StepType.FAILURE_LINK_FOLLOWED, i, prevJ, i - prevJ, i - prevJ + m - 1, lps.clone()));
                } else {
                    steps.add(new StepEvent(stepNum++, 8,
                            "Mismatch text[" + i + "] ('" + tc + "') != pattern[0] ('" + pc + "') at j=0. Advancing text index i=" + (i + 1),
                            StepType.COMPARE_MISMATCH, i, 0, i, i + m - 1, lps.clone()));
                    i++;
                }
            }
        }

        timer.stop();
        steps.add(StepEvent.simple(stepNum++, 5, "KMP Search Complete. Total matches found: " + matches.size(), StepType.COMPLETE));

        long memoryEstimate = (long) (n + m) * 2 + (long) m * 4 + (long) matches.size() * 4 + 128;
        return new AlgoResult<>(new KMPResult(matches, lps), steps, timer.getElapsedMicros(),
                opCounter.getComparisons(), memoryEstimate, "O(n + m)", "O(m)");
    }

    private void computeLpsTable(String pattern, int[] lps, DynamicArray<StepEvent> steps, OpCounter opCounter, int startStep) {
        int m = pattern.length();
        int len = 0; // length of previous longest prefix suffix
        int i = 1;
        lps[0] = 0;
        int stepNum = startStep;

        steps.add(new StepEvent(stepNum++, 1, "lps[0] = 0 (Base case for single character prefix)",
                StepType.TABLE_UPDATE, 0, 0, 0, 0, lps.clone()));

        while (i < m) {
            opCounter.incrementComparisons();
            char curr = pattern.charAt(i);
            char prev = pattern.charAt(len);

            if (curr == prev) {
                len++;
                lps[i] = len;
                steps.add(new StepEvent(stepNum++, 1,
                        "LPS Match: pattern[" + i + "] ('" + curr + "') == pattern[" + (len - 1) + "] ('" + prev + "') -> lps[" + i + "] = " + len,
                        StepType.TABLE_UPDATE, i, len - 1, 0, i, lps.clone()));
                i++;
            } else {
                if (len != 0) {
                    int oldLen = len;
                    len = lps[len - 1];
                    steps.add(new StepEvent(stepNum++, 1,
                            "LPS Mismatch at i=" + i + ". Backtracking len from " + oldLen + " to lps[" + (oldLen - 1) + "] = " + len,
                            StepType.FAILURE_LINK_FOLLOWED, i, oldLen, 0, i, lps.clone()));
                } else {
                    lps[i] = 0;
                    steps.add(new StepEvent(stepNum++, 1,
                            "LPS Mismatch at i=" + i + " with len=0 -> lps[" + i + "] = 0. Advancing i=" + (i + 1),
                            StepType.TABLE_UPDATE, i, 0, 0, i, lps.clone()));
                    i++;
                }
            }
        }
    }
}
