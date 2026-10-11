package com.texthack.module2_strings;

import com.texthack.core.ds.DynamicArray;
import com.texthack.core.model.AlgoResult;
import com.texthack.core.model.StepEvent;
import com.texthack.core.model.StepEvent.StepType;
import com.texthack.core.util.ManualSort;
import com.texthack.core.util.OpCounter;
import com.texthack.core.util.Timer;

/**
 * Suffix Array Builder via Prefix-Doubling Algorithm - O(n log² n).
 * Sorts all suffixes of a text in lexicographical order.
 * Pure Java engine, strictly zero java.util.* collections.
 */
public class SuffixArrayBuilder {

    public static class SuffixItem {
        public int index;
        public int rank1;
        public int rank2;

        public SuffixItem(int index, int rank1, int rank2) {
            this.index = index;
            this.rank1 = rank1;
            this.rank2 = rank2;
        }
    }

    public static final String[] PSEUDOCODE = {
        "assign initial ranks based on text[i] for length 2^0 = 1",
        "for k = 1; k < n; k *= 2 do",
        "    assign rank pairs (rank[i], rank[i + k])",
        "    sort suffixes by rank pairs using MergeSort in O(n log n)",
        "    reassign distinct ranks based on sorted pairs",
        "    if all ranks distinct then break",
        "SA[i] = sorted suffix starting indices"
    };

    public AlgoResult<int[]> buildSuffixArray(String text) {
        Timer timer = new Timer();
        OpCounter opCounter = new OpCounter();
        DynamicArray<StepEvent> steps = new DynamicArray<>();

        timer.start();

        if (text == null || text.isEmpty()) {
            timer.stop();
            steps.add(StepEvent.simple(1, 1, "Input text is empty.", StepType.COMPLETE));
            return new AlgoResult<>(new int[0], steps, timer.getElapsedMicros(), 0, 0, "O(n log² n)", "O(n)");
        }

        int n = text.length();
        int stepNum = 1;

        steps.add(StepEvent.simple(stepNum++, 1,
                "Initializing Prefix-Doubling Suffix Array Construction for text (n=" + n + "): \"" + text + "\"",
                StepType.START));

        DynamicArray<SuffixItem> suffixes = new DynamicArray<>(n);
        int[] rank = new int[n];

        // Step 1: Initial rank by single character
        for (int i = 0; i < n; i++) {
            rank[i] = text.charAt(i);
            int nextRank = (i + 1 < n) ? text.charAt(i + 1) : -1;
            suffixes.add(new SuffixItem(i, rank[i], nextRank));
            opCounter.incrementArrayAccesses();
        }

        steps.add(StepEvent.simple(stepNum++, 1,
                "Round k=0 (Length 1): Assigned initial ranks based on character codes.",
                StepType.TABLE_UPDATE));

        // Sort initial suffixes
        ManualSort.mergeSort(suffixes, (a, b) -> {
            opCounter.incrementComparisons();
            if (a.rank1 != b.rank1) return Integer.compare(a.rank1, b.rank1);
            return Integer.compare(a.rank2, b.rank2);
        });

        // Step 2: Prefix Doubling rounds
        int[] ind = new int[n];
        for (int k = 4; k < 2 * n; k *= 2) {
            int currentK = k / 2;
            int r = 0;
            int prevRank1 = suffixes.get(0).rank1;
            int prevRank2 = suffixes.get(0).rank2;
            suffixes.get(0).rank1 = r;
            ind[suffixes.get(0).index] = 0;

            for (int i = 1; i < n; i++) {
                SuffixItem curr = suffixes.get(i);
                opCounter.incrementComparisons();
                if (curr.rank1 == prevRank1 && curr.rank2 == prevRank2) {
                    prevRank1 = curr.rank1;
                    prevRank2 = curr.rank2;
                    curr.rank1 = r;
                } else {
                    prevRank1 = curr.rank1;
                    prevRank2 = curr.rank2;
                    curr.rank1 = ++r;
                }
                ind[curr.index] = i;
            }

            for (int i = 0; i < n; i++) {
                int nextIndex = suffixes.get(i).index + currentK;
                suffixes.get(i).rank2 = (nextIndex < n) ? suffixes.get(ind[nextIndex]).rank1 : -1;
                opCounter.incrementArrayAccesses();
            }

            ManualSort.mergeSort(suffixes, (a, b) -> {
                opCounter.incrementComparisons();
                if (a.rank1 != b.rank1) return Integer.compare(a.rank1, b.rank1);
                return Integer.compare(a.rank2, b.rank2);
            });

            int[] currentSa = new int[n];
            for (int i = 0; i < n; i++) currentSa[i] = suffixes.get(i).index;

            steps.add(new StepEvent(stepNum++, 4,
                    "Round k=" + currentK + ": Sorted " + n + " suffixes by prefix length " + (currentK * 2) +
                    ". Distinct ranks: " + (r + 1),
                    StepType.TABLE_UPDATE, currentK, r + 1, 0, n - 1, currentSa));

            if (r == n - 1) {
                steps.add(StepEvent.simple(stepNum++, 6,
                        "Early exit: All " + n + " suffixes have unique ranks. Suffix Array fully sorted!",
                        StepType.COMPLETE));
                break;
            }
        }

        int[] sa = new int[n];
        for (int i = 0; i < n; i++) {
            sa[i] = suffixes.get(i).index;
        }

        timer.stop();
        steps.add(new StepEvent(stepNum++, 7,
                "Suffix Array Construction Complete. Final SA order computed.",
                StepType.COMPLETE, 0, 0, 0, n - 1, sa));

        long memoryEstimate = (long) n * 24 + 128;
        return new AlgoResult<>(sa, steps, timer.getElapsedMicros(), opCounter.getComparisons(),
                memoryEstimate, "O(n log² n)", "O(n)");
    }
}
