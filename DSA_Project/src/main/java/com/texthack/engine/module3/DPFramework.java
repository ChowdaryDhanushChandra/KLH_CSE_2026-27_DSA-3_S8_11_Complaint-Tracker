package com.texthack.engine.module3;

/**
 * Educational repository explaining the foundational Dynamic Programming framework.
 */
public class DPFramework {

    public static String getFrameworkOverview() {
        return "DYNAMIC PROGRAMMING ARCHITECTURAL FRAMEWORK\n" +
                "============================================\n\n" +
                "1. Core Hallmarks of Dynamic Programming:\n" +
                "   - Optimal Substructure: An optimal solution to the problem contains within it " +
                "     optimal solutions to subproblems (e.g., shortest paths, longest common subsequence).\n" +
                "   - Overlapping Subproblems: A recursive algorithm revisits the exact same subproblems " +
                "     repeatedly rather than generating new subproblems at each step.\n\n" +
                "2. Memoization (Top-Down) vs Tabulation (Bottom-Up):\n" +
                "   ┌────────────────────┬─────────────────────────────────┬─────────────────────────────────┐\n" +
                "   │ Dimension          │ Memoization (Top-Down)          │ Tabulation (Bottom-Up)          │\n" +
                "   ├────────────────────┼─────────────────────────────────┼─────────────────────────────────┤\n" +
                "   │ State Evaluation   │ On demand / lazy recursion      │ Iterative systematic filling    │\n" +
                "   │ Call Stack         │ Incurs O(depth) stack frames    │ O(1) iterative call stack       │\n" +
                "   │ State Space        │ Computes only reachable states  │ Evaluates entire table space    │\n" +
                "   │ Memory Layout      │ Recursive cache / hash map      │ Contiguous multidimensional tab │\n" +
                "   │ Space Optimization │ Difficult to optimize memory    │ Rolling array / state reduction │\n" +
                "   └────────────────────┴─────────────────────────────────┴─────────────────────────────────┘\n\n" +
                "3. Standard 4-Step Recipe for DP Problem Solving:\n" +
                "   Step 1: Characterize the structure of an optimal solution.\n" +
                "   Step 2: Recursively define the value of an optimal solution (Recurrence Relation).\n" +
                "   Step 3: Compute the value of an optimal solution (typically bottom-up with topological order).\n" +
                "   Step 4: Construct an optimal solution from computed information (Traceback / Predecessor pointers).";
    }
}
