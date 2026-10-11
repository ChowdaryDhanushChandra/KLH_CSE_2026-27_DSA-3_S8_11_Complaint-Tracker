package com.texthack.engine.module2;

/**
 * Educational preview and comparison for Suffix Trees.
 * Explains Ukkonen's linear algorithm, compact Patricia representation, and suffix array equivalence.
 */
public class SuffixTreePreview {

    public static String getOverview() {
        return "SUFFIX TREE (Compact Patricia Trie of Suffixes)\n" +
                "================================================\n\n" +
                "Definition:\n" +
                "A Suffix Tree for a string S of length n (terminated with sentinel '$') is a rooted directed tree " +
                "where edges are labeled with non-empty substrings of S, each internal node has at least 2 children, " +
                "no two outgoing edges start with the same character, and each suffix of S corresponds to exactly one leaf.\n\n" +
                "Properties:\n" +
                "1. Size Bounds: Exactly n leaves and at most n - 1 internal nodes. Total nodes <= 2n.\n" +
                "2. Linear Edge Storage: Each edge label is represented compactly by an index pair (start, end) into S, " +
                "   guaranteeing O(n) total space instead of O(n²).\n" +
                "3. Ukkonen's Linear-Time Algorithm (1995):\n" +
                "   Constructs the suffix tree online in O(n) time using three key optimizations:\n" +
                "   - Implicit suffix trees\n" +
                "   - Suffix links connecting internal nodes\n" +
                "   - Edge-label extension rule (Rule 3 stop condition & global leaf-end pointer increment)\n\n" +
                "Comparative Analysis:\n" +
                "┌────────────────────┬─────────────────────────────┬──────────────────────────────┐\n" +
                "│ Metric / Feature   │ Suffix Array + LCP          │ Suffix Tree (Ukkonen)        │\n" +
                "├────────────────────┼─────────────────────────────┼──────────────────────────────┤\n" +
                "│ Construction Time  │ O(n) (SA-IS) or O(n log² n) │ O(n · Σ) or O(n log Σ)       │\n" +
                "│ Pattern Search     │ O(|P| log n) or O(|P|+log n)│ O(|P|)                       │\n" +
                "│ Space Consumption  │ ~4 - 8 bytes per char       │ ~20 - 45 bytes per char      │\n" +
                "│ Cache Locality     │ Contiguous array (High)     │ Linked pointer graph (Low)   │\n" +
                "│ Implementation     │ Straightforward arrays      │ Complex pointer & link rules │\n" +
                "└────────────────────┴─────────────────────────────┴──────────────────────────────┘\n\n" +
                "Academic Note:\n" +
                "In contemporary production engines (e.g., Bowtie, BWA, ripgrep), Suffix Arrays combined with the Burrows-Wheeler " +
                "Transform (FM-Index) are universally preferred over Suffix Trees due to memory compactness and hardware cache efficiency.";
    }
}
