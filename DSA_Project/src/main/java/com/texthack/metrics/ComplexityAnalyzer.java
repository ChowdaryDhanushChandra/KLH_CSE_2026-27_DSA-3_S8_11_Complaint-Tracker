package com.texthack.metrics;

import com.texthack.model.ComplexityInfo;

/**
 * Standard repository of asymptotic time and space complexities for all TextHack algorithms.
 */
public final class ComplexityAnalyzer {

    private ComplexityAnalyzer() {}

    public static ComplexityInfo getComplexity(String algorithmName) {
        if (algorithmName == null) {
            return new ComplexityInfo("O(1)", "O(1)", "O(1)", "O(1)", "General algorithm.");
        }

        switch (algorithmName) {
            case "Naive String Matching":
            case "Naive":
                return new ComplexityInfo("O(n)", "O(n + m)", "O(n · m)", "O(1)",
                        "Slide pattern character by character. Worst case occurs with repeated prefix patterns like 'AAAAAB' in 'AAAAAAAAAA'.");

            case "Knuth-Morris-Pratt":
            case "KMP":
                return new ComplexityInfo("O(n + m)", "O(n + m)", "O(n + m)", "O(m)",
                        "Precomputes Longest Proper Prefix that is also a Suffix (LPS) array in O(m). Never backtracks text pointer.");

            case "Z-Algorithm":
            case "Z-Algorithm Matcher":
                return new ComplexityInfo("O(n + m)", "O(n + m)", "O(n + m)", "O(n + m)",
                        "Constructs Z-array over P + '$' + T using sliding window [L, R] bounding prefix matches.");

            case "Rabin-Karp":
            case "Rabin-Karp Matcher":
                return new ComplexityInfo("O(n + m)", "O(n + m)", "O(n · m)", "O(1)",
                        "Uses polynomial rolling hash with modular arithmetic. Hash collisions trigger character-by-character verification.");

            case "Aho-Corasick":
                return new ComplexityInfo("O(n + m + z)", "O(n + m + z)", "O(n + m + z)", "O(m · Σ)",
                        "Constructs trie with BFS failure and dictionary links for multi-pattern linear dictionary matching.");

            case "Suffix Array":
            case "Suffix Array (O(n log² n))":
                return new ComplexityInfo("O(n log² n)", "O(n log² n)", "O(n log² n)", "O(n)",
                        "Prefix doubling construction with manual mergesort. Linear SA-IS achieves O(n) in advanced theory.");

            case "Kasai LCP":
            case "Kasai LCP Array":
                return new ComplexityInfo("O(n)", "O(n)", "O(n)", "O(n)",
                        "Constructs Longest Common Prefix array in linear time using the theorem LCP[rank[i]] >= LCP[rank[i-1]] - 1.");

            case "Levenshtein Distance":
            case "Wagner-Fischer":
                return new ComplexityInfo("O(n · m)", "O(n · m)", "O(n · m)", "O(n · m)",
                        "2D Dynamic Programming table computing minimum insert, delete, and replace operations.");

            case "Damerau-Levenshtein":
                return new ComplexityInfo("O(n · m)", "O(n · m)", "O(n · m)", "O(n · m)",
                        "Extends Levenshtein with adjacent character transpositions: dp[i][j] checked against dp[i-2][j-2] + cost.");

            case "Weighted Edit Distance":
                return new ComplexityInfo("O(n · m)", "O(n · m)", "O(n · m)", "O(n · m)",
                        "Wagner-Fischer with user-customized costs for insertion, deletion, and substitution.");

            case "Needleman-Wunsch":
                return new ComplexityInfo("O(n · m)", "O(n · m)", "O(n · m)", "O(n · m)",
                        "Global sequence alignment maximizing similarity score using match reward, mismatch penalty, and gap cost.");

            case "Smith-Waterman":
                return new ComplexityInfo("O(n · m)", "O(n · m)", "O(n · m)", "O(n · m)",
                        "Local sequence alignment with non-negative score bounding (max(0, ...)), discovering optimal local motifs.");

            case "Matrix-Chain Multiplication":
                return new ComplexityInfo("O(n³)", "O(n³)", "O(n³)", "O(n²)",
                        "Interval DP determining optimal parenthesization of matrix product sequences.");

            case "Optimal Binary Search Tree":
            case "Optimal BST":
                return new ComplexityInfo("O(n³)", "O(n³)", "O(n³)", "O(n²)",
                        "Interval DP minimizing expected search cost given search frequencies (reducible to O(n²) with Knuth's optimization).");

            case "Travelling Salesman (TSP DP)":
            case "Travelling Salesman Problem":
                return new ComplexityInfo("O(2^n · n²)", "O(2^n · n²)", "O(2^n · n²)", "O(2^n · n)",
                        "Held-Karp bitmask DP solving NP-hard TSP dramatically faster than O(n!) brute-force factorial enumeration.");

            case "Hamiltonian Path DP":
                return new ComplexityInfo("O(2^n · n²)", "O(2^n · n²)", "O(2^n · n²)", "O(2^n · n)",
                        "Bitmask DP checking whether a simple path visits every vertex in the graph exactly once.");

            case "Set Cover DP":
                return new ComplexityInfo("O(2^n · m)", "O(2^n · m)", "O(2^n · m)", "O(2^n)",
                        "Bitmask DP finding minimum subset combination covering universe of n elements.");

            case "Tree DP (Independent Set)":
                return new ComplexityInfo("O(V)", "O(V)", "O(V)", "O(V)",
                        "Subtree DP computing Maximum Weight Independent Set via dp[u][0] (exclude) and dp[u][1] (include).");

            case "Tree Diameter DP":
                return new ComplexityInfo("O(V)", "O(V)", "O(V)", "O(V)",
                        "Computes tree diameter in single post-order traversal tracking the two longest down-paths from each node.");

            case "Tree Centroid DP":
                return new ComplexityInfo("O(V)", "O(V)", "O(V)", "O(V)",
                        "Finds tree centroid (vertex whose removal leaves no component of size > V/2) in linear time.");

            case "Rerooting DP":
                return new ComplexityInfo("O(V)", "O(V)", "O(V)", "O(V)",
                        "Two-pass DFS computing the sum of distances from every vertex to all other vertices in linear time.");

            case "SOS DP (Sum Over Subsets)":
                return new ComplexityInfo("O(n · 2^n)", "O(n · 2^n)", "O(n · 2^n)", "O(2^n)",
                        "SOS DP computes sum over submasks in O(n · 2^n), beating naive submask iteration O(3^n).");

            case "Edmonds-Karp (Max-Flow)":
            case "Edmonds-Karp":
                return new ComplexityInfo("O(V · E²)", "O(V · E²)", "O(V · E²)", "O(V + E)",
                        "Ford-Fulkerson using BFS for shortest augmenting paths, guaranteeing polynomial time convergence.");

            case "Critical Path Method (CPM)":
            case "Project Scheduling":
                return new ComplexityInfo("O(V + E)", "O(V + E)", "O(V + E)", "O(V + E)",
                        "Topological sorting on DAG computing Earliest Start/Finish and Latest Start/Finish to identify critical path.");

            case "Miller-Rabin Primality":
                return new ComplexityInfo("O(k · log³ n)", "O(k · log³ n)", "O(k · log³ n)", "O(log n)",
                        "Probabilistic randomized test with error probability <= 4^(-k) using modular exponentiation.");

            case "Fermat Primality":
                return new ComplexityInfo("O(k · log³ n)", "O(k · log³ n)", "O(k · log³ n)", "O(log n)",
                        "Probabilistic test using Fermat's Little Theorem a^(n-1) = 1 (mod n); vulnerable to Carmichael numbers.");

            default:
                return new ComplexityInfo("O(n)", "O(n)", "O(n²)", "O(n)", "Standard algorithmic execution.");
        }
    }
}
