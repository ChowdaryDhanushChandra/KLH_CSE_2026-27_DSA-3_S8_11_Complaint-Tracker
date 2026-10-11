package com.texthack.engine.module3;

/**
 * Educational guide analyzing scenarios where Dynamic Programming fails or is sub-optimal.
 */
public class WhenDPIsWrong {

    public static String getGuide() {
        return "WHEN DYNAMIC PROGRAMMING IS THE WRONG TOOL\n" +
                "===========================================\n\n" +
                "Dynamic Programming is powerful, but applying it blindly leads to suboptimal algorithms, " +
                "out-of-memory errors, or outright incorrect solutions. Here are the 4 canonical failure cases:\n\n" +
                "1. When Greedy Choice Property Holds (DP is Overkill):\n" +
                "   - Example: Fractional Knapsack vs 0/1 Knapsack.\n" +
                "     In Fractional Knapsack, locally picking the highest value/weight ratio guarantees global optimality in O(n log n).\n" +
                "     Applying 2D DP table O(n · W) wastes quadratic time and memory.\n" +
                "   - Example: Minimum Spanning Tree (MST - Prim / Kruskal).\n" +
                "     Greedy edge selection with Union-Find runs in O(E log V). DP would attempt exponential state caching.\n" +
                "   - Example: Huffman Encoding, Activity Selection with earliest finish time.\n\n" +
                "2. When Subproblems Do NOT Overlap (Divide & Conquer is Sufficient):\n" +
                "   - Example: MergeSort and QuickSort.\n" +
                "     Each sub-array is distinct and partitioned disjointly. No subproblem is solved twice.\n" +
                "     Maintaining a DP memo table adds O(n) hashing/lookup overhead without any pruning benefit.\n\n" +
                "3. Lack of Optimal Substructure (Violates Bellman's Principle):\n" +
                "   - Example: Longest Simple Path in a general cyclic directed graph.\n" +
                "     Unlike Shortest Path, the longest simple path between u and v cannot be decomposed into " +
                "     longest simple path from u to w plus longest simple path from w to v, because the two segments " +
                "     might share vertices, violating the simplicity constraint. It is strongly NP-hard!\n\n" +
                "4. Exponential State Space Explosion (Curse of Dimensionality):\n" +
                "   - Example: 0/1 Knapsack with huge capacities (e.g., W = 10^12, integer weights).\n" +
                "     The DP table requires 10^12 entries, exceeding RAM. Branch-and-bound or FPTAS approximation is required.\n" +
                "   - Example: TSP with n = 60 cities.\n" +
                "     Held-Karp DP requires 2^60 · 60 operations (~6.9 × 10^19 ops), completely infeasible. " +
                "     Genetic algorithms, Lin-Kernighan, or integer linear programming (ILP) are used in practice.\n\n" +
                "DP vs GREEDY DECISION MATRIX:\n" +
                "┌──────────────────────┬───────────────────────────────┬───────────────────────────────┐\n" +
                "│ Feature              │ Greedy Paradigm               │ Dynamic Programming           │\n" +
                "├──────────────────────┼───────────────────────────────┼───────────────────────────────┤\n" +
                "│ Choice Strategy      │ Best immediate local choice   │ Considers all sub-choices     │\n" +
                "│ Backtracking         │ Never reconsiders decisions   │ Explores and merges branches  │\n" +
                "│ Proof Obligation     │ Exchange argument / matroid   │ Bellman principle of optimal  │\n" +
                "│ Time Complexity      │ Typically O(n log n) or O(n)  │ Typically O(n²), O(n³), O(2^n)│\n" +
                "│ Space Requirement    │ O(1) or O(n) auxiliary memory │ O(n) to O(n²) memo table      │\n" +
                "└──────────────────────┴───────────────────────────────┴───────────────────────────────┘";
    }
}
