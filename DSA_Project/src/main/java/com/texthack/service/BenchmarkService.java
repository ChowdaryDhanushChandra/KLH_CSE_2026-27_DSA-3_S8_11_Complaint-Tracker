package com.texthack.service;

import com.texthack.datastructures.ManualArrayList;
import com.texthack.engine.module2.KMPMatcher;
import com.texthack.engine.module2.NaivePatternMatcher;
import com.texthack.engine.module2.RabinKarpMatcher;
import com.texthack.engine.module2.ZAlgorithmMatcher;
import com.texthack.engine.module3.DamerauLevenshteinDistance;
import com.texthack.engine.module3.LevenshteinDistance;
import com.texthack.engine.module3.WeightedEditDistance;
import com.texthack.model.AlignmentResult;
import com.texthack.model.SearchResult;

/**
 * Service for benchmarking and executing comparative multi-algorithm runs on identical inputs.
 */
public class BenchmarkService {

    public static class ComparisonRow {
        private final String algorithmName;
        private final String executionTime;
        private final long comparisonsOrOps;
        private final String resultSummary;
        private final String timeComplexity;
        private final String spaceComplexity;

        public ComparisonRow(String name, String time, long ops, String result, String timeComp, String spaceComp) {
            this.algorithmName = name;
            this.executionTime = time;
            this.comparisonsOrOps = ops;
            this.resultSummary = result;
            this.timeComplexity = timeComp;
            this.spaceComplexity = spaceComp;
        }

        public String getAlgorithmName() {
            return algorithmName;
        }

        public String getExecutionTime() {
            return executionTime;
        }

        public long getComparisonsOrOps() {
            return comparisonsOrOps;
        }

        public String getResultSummary() {
            return resultSummary;
        }

        public String getTimeComplexity() {
            return timeComplexity;
        }

        public String getSpaceComplexity() {
            return spaceComplexity;
        }
    }

    public ManualArrayList<ComparisonRow> compareStringMatchers(String pattern, String text) {
        ManualArrayList<ComparisonRow> rows = new ManualArrayList<>();

        // 1. Naive
        NaivePatternMatcher naive = new NaivePatternMatcher();
        SearchResult resNaive = naive.search(pattern, text);
        rows.add(new ComparisonRow(
                "Naive Matching", resNaive.getFormattedTime(), resNaive.getComparisons(),
                resNaive.getMatchCount() + " matches", "O(n · m)", "O(1)"
        ));

        // 2. KMP
        KMPMatcher kmp = new KMPMatcher();
        SearchResult resKMP = kmp.search(pattern, text);
        rows.add(new ComparisonRow(
                "Knuth-Morris-Pratt (KMP)", resKMP.getFormattedTime(), resKMP.getComparisons(),
                resKMP.getMatchCount() + " matches", "O(n + m)", "O(m)"
        ));

        // 3. Z-Algorithm
        ZAlgorithmMatcher zAlgo = new ZAlgorithmMatcher();
        SearchResult resZ = zAlgo.search(pattern, text);
        rows.add(new ComparisonRow(
                "Z-Algorithm", resZ.getFormattedTime(), resZ.getComparisons(),
                resZ.getMatchCount() + " matches", "O(n + m)", "O(n + m)"
        ));

        // 4. Rabin-Karp
        RabinKarpMatcher rk = new RabinKarpMatcher();
        SearchResult resRK = rk.search(pattern, text);
        rows.add(new ComparisonRow(
                "Rabin-Karp", resRK.getFormattedTime(), resRK.getComparisons(),
                resRK.getMatchCount() + " matches", "O(n + m) avg", "O(1)"
        ));

        return rows;
    }

    public ManualArrayList<ComparisonRow> compareEditDistances(String s, String t) {
        ManualArrayList<ComparisonRow> rows = new ManualArrayList<>();

        // Levenshtein
        AlignmentResult lev = LevenshteinDistance.compute(s, t);
        rows.add(new ComparisonRow(
                "Levenshtein Distance", lev.getFormattedTime(), lev.getOperations().size(),
                "Distance: " + lev.getDistanceOrScore(), "O(n · m)", "O(n · m)"
        ));

        // Damerau-Levenshtein
        AlignmentResult dam = DamerauLevenshteinDistance.compute(s, t);
        rows.add(new ComparisonRow(
                "Damerau-Levenshtein", dam.getFormattedTime(), dam.getOperations().size(),
                "Distance: " + dam.getDistanceOrScore(), "O(n · m)", "O(n · m)"
        ));

        // Weighted
        AlignmentResult weighted = WeightedEditDistance.compute(s, t, 1, 1, 2);
        rows.add(new ComparisonRow(
                "Weighted Edit Distance (Sub=2)", weighted.getFormattedTime(), weighted.getOperations().size(),
                "Cost: " + weighted.getDistanceOrScore(), "O(n · m)", "O(n · m)"
        ));

        return rows;
    }
}
