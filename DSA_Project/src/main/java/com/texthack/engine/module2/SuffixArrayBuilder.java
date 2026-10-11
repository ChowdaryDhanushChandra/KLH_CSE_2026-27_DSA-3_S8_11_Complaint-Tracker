package com.texthack.engine.module2;

import com.texthack.datastructures.ManualArrayList;
import com.texthack.metrics.ExecutionTimer;
import com.texthack.util.ManualSort;

/**
 * Constructs Suffix Arrays using prefix-doubling in O(n log² n) time without java.util.*.
 * Also provides detailed conceptual explanation of the linear O(n) SA-IS algorithm.
 */
public class SuffixArrayBuilder {

    public static class SuffixRank {
        public int index;
        public int rank1;
        public int rank2;

        public SuffixRank(int index, int rank1, int rank2) {
            this.index = index;
            this.rank1 = rank1;
            this.rank2 = rank2;
        }
    }

    public static SuffixArray build(String text) {
        ExecutionTimer timer = new ExecutionTimer();
        timer.start();

        ManualArrayList<String> trace = new ManualArrayList<>();
        if (text == null || text.length() == 0) {
            timer.stop();
            return new SuffixArray("", new int[0], timer.getElapsedNanos(), timer.getFormattedTime(), trace);
        }

        int n = text.length();
        trace.add("Building Suffix Array for string of length " + n + " via Prefix Doubling O(n log² n)...");

        SuffixRank[] suffixes = new SuffixRank[n];
        for (int i = 0; i < n; i++) {
            int r2 = (i + 1 < n) ? (int) text.charAt(i + 1) : -1;
            suffixes[i] = new SuffixRank(i, (int) text.charAt(i), r2);
        }

        // Sort by first 2 characters
        ManualSort.mergeSort(suffixes, (a, b) -> {
            if (a.rank1 != b.rank1) return Integer.compare(a.rank1, b.rank1);
            return Integer.compare(a.rank2, b.rank2);
        });

        int[] ind = new int[n];

        // Double prefix lengths: 4, 8, 16...
        for (int k = 4; k < 2 * n; k = k * 2) {
            int rank = 0;
            int prevRank = suffixes[0].rank1;
            suffixes[0].rank1 = rank;
            ind[suffixes[0].index] = 0;

            for (int i = 1; i < n; i++) {
                if (suffixes[i].rank1 == prevRank && suffixes[i].rank2 == suffixes[i - 1].rank2) {
                    prevRank = suffixes[i].rank1;
                    suffixes[i].rank1 = rank;
                } else {
                    prevRank = suffixes[i].rank1;
                    suffixes[i].rank1 = ++rank;
                }
                ind[suffixes[i].index] = i;
            }

            // Assign next half ranks
            for (int i = 0; i < n; i++) {
                int nextIndex = suffixes[i].index + k / 2;
                suffixes[i].rank2 = (nextIndex < n) ? suffixes[ind[nextIndex]].rank1 : -1;
            }

            // Stable merge sort on (rank1, rank2)
            ManualSort.mergeSort(suffixes, (a, b) -> {
                if (a.rank1 != b.rank1) return Integer.compare(a.rank1, b.rank1);
                return Integer.compare(a.rank2, b.rank2);
            });

            trace.add("Completed prefix-doubling round for k = " + k + " (Distinct ranks = " + (rank + 1) + ")");
            if (rank == n - 1) {
                trace.add("All suffix ranks are unique; terminating early.");
                break;
            }
        }

        int[] sa = new int[n];
        for (int i = 0; i < n; i++) {
            sa[i] = suffixes[i].index;
        }

        timer.stop();
        trace.add("Suffix Array successfully constructed in " + timer.getFormattedTime());

        return new SuffixArray(text, sa, timer.getElapsedNanos(), timer.getFormattedTime(), trace);
    }

    /**
     * Conceptual explanation of the linear O(n) SA-IS algorithm.
     */
    public static String getSaisConceptualExplanation() {
        return "SA-IS (Suffix Array by Induced Sorting) Algorithm - O(n) Linear Time Construction:\n\n" +
                "1. Classification of Suffixes:\n" +
                "   Every suffix S[i..n] is classified as S-type (if S[i] < S[i+1] or equal and next is S-type) " +
                "or L-type (if S[i] > S[i+1] or equal and next is L-type). The sentinel '$' is always S-type.\n\n" +
                "2. LMS (Leftmost S-type) Characters:\n" +
                "   A character S[i] is an LMS-character if it is S-type and S[i-1] is L-type. " +
                "LMS-suffixes are strictly fewer than n/2.\n\n" +
                "3. Induced Sorting:\n" +
                "   Using bucket sorting over character frequencies:\n" +
                "   - Place LMS-suffixes into bucket tails.\n" +
                "   - Scan left-to-right to induce L-type suffixes into bucket heads.\n" +
                "   - Scan right-to-left to induce S-type suffixes into bucket tails.\n\n" +
                "4. Recursive Reduction:\n" +
                "   Name each LMS-substring with unique identifiers. If identifiers have duplicates, " +
                "recursively solve the reduced problem of size <= n/2. Recurrence T(n) = T(n/2) + O(n) yields exact O(n) time!";
    }
}
