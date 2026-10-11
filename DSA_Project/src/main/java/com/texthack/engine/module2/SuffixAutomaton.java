package com.texthack.engine.module2;

/**
 * Educational preview and architectural reference for Suffix Automata (DAWG).
 * Provides formal definitions, state-transition characteristics, and comparisons with Suffix Arrays.
 */
public class SuffixAutomaton {

    public static String getOverview() {
        return "SUFFIX AUTOMATON (Directed Acyclic Word Graph - DAWG)\n" +
                "=========================================================\n\n" +
                "Definition:\n" +
                "A Suffix Automaton is the minimal deterministic finite automaton (DFA) that accepts " +
                "all suffixes of a given string S. It represents all 2^(O(n)) substrings of S using at most " +
                "2n - 1 states and 3n - 4 transitions.\n\n" +
                "Key Structural Concepts:\n" +
                "1. Endpos Equivalence Class:\n" +
                "   Two substrings u and v are endpos-equivalent (u ~_S v) if and only if they appear " +
                "   at the exact same set of ending positions in S.\n" +
                "   Each state of the automaton corresponds to an entire equivalence class of substrings.\n\n" +
                "2. Suffix Link (Link):\n" +
                "   Every state u (except initial state) has a suffix link pointing to the state corresponding " +
                "   to the longest proper suffix of the substrings in u that belongs to a different endpos class.\n" +
                "   The suffix links form the 'Link Tree', which is structurally isomorphic to the Suffix Tree of reversed S.\n\n" +
                "3. Online Linear-Time Construction:\n" +
                "   Built incrementally character-by-character in O(n) total time with small constant factor.\n\n" +
                "Comparison with Suffix Arrays:\n" +
                "- Suffix Automaton: True online algorithm; accepts streaming characters in O(1) amortized time per char. " +
                "Answers pattern existence queries in O(|P|) time, independent of alphabet size.\n" +
                "- Suffix Array: Offline structure; requires full text in advance. Binary search lookup takes O(|P| log n) " +
                "or O(|P| + log n) with LCP array. However, Suffix Arrays have lower memory overhead (4n bytes vs ~30n bytes).\n\n" +
                "Curricular Note:\n" +
                "Detailed step-by-step state-cloning construction belongs to Advanced Electives (Advanced Stringology & Combinatorics on Words).";
    }
}
