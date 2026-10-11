package com.texthack.module2_strings;

import com.texthack.core.ds.DynamicArray;
import com.texthack.core.model.AlgoResult;
import com.texthack.core.model.StepEvent;
import com.texthack.core.model.StepEvent.StepType;
import com.texthack.core.util.OpCounter;
import com.texthack.core.util.Timer;

/**
 * Rabin-Karp String Pattern Matcher with Double Hashing & Spurious Collision Detection.
 * Hash 1: Base 256, Modulo 1,000,000,007
 * Hash 2: Base 313, Modulo 1,000,000,009
 * Emits step events on hash calculation, collision detection, and match verification.
 * Pure Java engine, strictly zero java.util.* collections.
 */
public class RabinKarpMatcher {

    public static final long MOD1 = 1_000_000_007L;
    public static final long MOD2 = 1_000_000_009L;
    public static final long BASE1 = 256L;
    public static final long BASE2 = 313L;

    public static class RabinKarpResult {
        private final DynamicArray<Integer> matches;
        private final int collisionCount;
        private final long patternHash1;
        private final long patternHash2;

        public RabinKarpResult(DynamicArray<Integer> matches, int collisionCount,
                               long patternHash1, long patternHash2) {
            this.matches = matches;
            this.collisionCount = collisionCount;
            this.patternHash1 = patternHash1;
            this.patternHash2 = patternHash2;
        }

        public DynamicArray<Integer> getMatches() { return matches; }
        public int getCollisionCount() { return collisionCount; }
        public long getPatternHash1() { return patternHash1; }
        public long getPatternHash2() { return patternHash2; }
    }

    public static final String[] PSEUDOCODE = {
        "compute patternHash1, patternHash2 and initial textHash1, textHash2",
        "for i = 0 to n - m do",
        "    if textHash1 == patternHash1 and textHash2 == patternHash2 then",
        "        if verifySubstring(text[i..i+m-1], pattern) then report match at i",
        "        else report spurious double-hash collision",
        "    else if textHash1 == patternHash1 then report single-hash collision",
        "    if i < n - m then",
        "        roll textHash1 and textHash2 using Horner's rule in O(1)"
    };

    public AlgoResult<RabinKarpResult> search(String text, String pattern) {
        Timer timer = new Timer();
        OpCounter opCounter = new OpCounter();
        DynamicArray<StepEvent> steps = new DynamicArray<>();
        DynamicArray<Integer> matches = new DynamicArray<>();

        timer.start();

        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            timer.stop();
            steps.add(StepEvent.simple(1, 1, "Input validation complete: invalid input.", StepType.COMPLETE));
            return new AlgoResult<>(new RabinKarpResult(matches, 0, 0, 0), steps, timer.getElapsedMicros(), 0, 0, "O(n + m)", "O(1)");
        }

        int n = text.length();
        int m = pattern.length();
        int stepNum = 1;
        int collisions = 0;

        // Compute high-order multipliers h1 = BASE1^(m-1) % MOD1, h2 = BASE2^(m-1) % MOD2
        long h1 = 1;
        long h2 = 1;
        for (int i = 0; i < m - 1; i++) {
            h1 = (h1 * BASE1) % MOD1;
            h2 = (h2 * BASE2) % MOD2;
        }

        // Compute initial hashes
        long pHash1 = 0, pHash2 = 0;
        long tHash1 = 0, tHash2 = 0;
        for (int i = 0; i < m; i++) {
            pHash1 = (pHash1 * BASE1 + pattern.charAt(i)) % MOD1;
            pHash2 = (pHash2 * BASE2 + pattern.charAt(i)) % MOD2;
            tHash1 = (tHash1 * BASE1 + text.charAt(i)) % MOD1;
            tHash2 = (tHash2 * BASE2 + text.charAt(i)) % MOD2;
            opCounter.incrementHashComputations();
        }

        steps.add(StepEvent.simple(stepNum++, 1,
                "Computed Pattern Hashes: H1=" + pHash1 + ", H2=" + pHash2 +
                ". Initial Text Window Hashes: H1=" + tHash1 + ", H2=" + tHash2,
                StepType.HASH_CALCULATED));

        // Slide window over text
        for (int i = 0; i <= n - m; i++) {
            steps.add(new StepEvent(stepNum++, 2,
                    "Evaluating window [" + i + ".." + (i + m - 1) + "]: \"" + text.substring(i, i + m) +
                    "\" | H1=" + tHash1 + " (target " + pHash1 + "), H2=" + tHash2 + " (target " + pHash2 + ")",
                    StepType.SHIFT_WINDOW, i, 0, i, i + m - 1, new long[]{tHash1, tHash2}));

            if (tHash1 == pHash1 && tHash2 == pHash2) {
                steps.add(new StepEvent(stepNum++, 3,
                        "Dual Hash Match (H1 & H2) at index " + i + ". Verifying character-by-character...",
                        StepType.COMPARE_MATCH, i, 0, i, i + m - 1, new long[]{tHash1, tHash2}));

                boolean exactMatch = true;
                for (int j = 0; j < m; j++) {
                    opCounter.incrementComparisons();
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        exactMatch = false;
                        break;
                    }
                }

                if (exactMatch) {
                    matches.add(i);
                    steps.add(new StepEvent(stepNum++, 4,
                            "Exact match verified at index " + i + "!",
                            StepType.PATTERN_FOUND, i, 0, i, i + m - 1, new long[]{tHash1, tHash2}));
                } else {
                    collisions++;
                    steps.add(new StepEvent(stepNum++, 5,
                            "SPURIOUS COLLISION: Dual hashes matched but substring chars differed at index " + i + "!",
                            StepType.COLLISION_DETECTED, i, 0, i, i + m - 1, new long[]{tHash1, tHash2}));
                }
            } else if (tHash1 == pHash1) {
                collisions++;
                steps.add(new StepEvent(stepNum++, 6,
                        "SINGLE-HASH COLLISION at index " + i + ": H1 matched (" + tHash1 + ") but H2 differed (" + tHash2 + " != " + pHash2 + ")!",
                        StepType.COLLISION_DETECTED, i, 0, i, i + m - 1, new long[]{tHash1, tHash2}));
            }

            // Roll hashes for next window
            if (i < n - m) {
                tHash1 = (BASE1 * (tHash1 - text.charAt(i) * h1) + text.charAt(i + m)) % MOD1;
                if (tHash1 < 0) tHash1 += MOD1;

                tHash2 = (BASE2 * (tHash2 - text.charAt(i) * h2) + text.charAt(i + m)) % MOD2;
                if (tHash2 < 0) tHash2 += MOD2;

                opCounter.incrementHashComputations();
                steps.add(new StepEvent(stepNum++, 8,
                        "Rolled hash to window [" + (i + 1) + ".." + (i + m) + "]: New H1=" + tHash1 + ", New H2=" + tHash2,
                        StepType.HASH_CALCULATED, i + 1, 0, i + 1, i + m, new long[]{tHash1, tHash2}));
            }
        }

        timer.stop();
        steps.add(StepEvent.simple(stepNum++, 4,
                "Rabin-Karp finished. Matches: " + matches.size() + ", Collisions detected: " + collisions,
                StepType.COMPLETE));

        long memoryEstimate = (long) (n + m) * 2 + (long) matches.size() * 4 + 128;
        return new AlgoResult<>(new RabinKarpResult(matches, collisions, pHash1, pHash2), steps,
                timer.getElapsedMicros(), opCounter.getComparisons(), memoryEstimate, "O(n + m)", "O(1)");
    }
}
