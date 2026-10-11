package com.texthack.engine.module1;

import com.texthack.datastructures.ManualArrayList;
import com.texthack.datastructures.ManualHashMap;
import com.texthack.engine.module2.KasaiLCP;
import com.texthack.engine.module2.SuffixArray;
import com.texthack.engine.module2.SuffixArrayBuilder;
import com.texthack.metrics.ComplexityAnalyzer;
import com.texthack.metrics.ExecutionTimer;
import com.texthack.model.AlgorithmResult;
import com.texthack.util.ManualStringUtil;
import com.texthack.validation.InputValidator;

/**
 * Computes document similarity using Suffix Arrays + Kasai LCP (Longest Common Substring)
 * and manual token/n-gram Jaccard coefficient without java.util.*.
 */
public class DocumentSimilarityService {

    public static class SimilarityResult {
        public final double similarityScore; // 0.0 to 1.0
        public final String longestCommonSubstring;
        public final int lcsLength;
        public final double tokenJaccardSimilarity;
        public final AlgorithmResult algorithmResult;

        public SimilarityResult(double score, String lcs, int lcsLength, double jaccard, AlgorithmResult algoRes) {
            this.similarityScore = score;
            this.longestCommonSubstring = lcs;
            this.lcsLength = lcsLength;
            this.tokenJaccardSimilarity = jaccard;
            this.algorithmResult = algoRes;
        }
    }

    public SimilarityResult compareDocuments(String doc1, String doc2) {
        InputValidator.validateNotEmpty(doc1, "Document 1");
        InputValidator.validateNotEmpty(doc2, "Document 2");

        ExecutionTimer timer = new ExecutionTimer();
        timer.start();

        ManualArrayList<String> trace = new ManualArrayList<>();
        int len1 = doc1.length();
        int len2 = doc2.length();

        trace.add(String.format("Comparing Document 1 (length=%d) with Document 2 (length=%d)...", len1, len2));

        // 1. Longest Common Substring via Generalized Suffix Array and Kasai LCP
        char delim1 = '\u0001';
        char delim2 = '\u0002';
        String combined = doc1 + delim1 + doc2 + delim2;

        SuffixArray sa = SuffixArrayBuilder.build(combined);
        KasaiLCP.LCPResult lcpRes = KasaiLCP.compute(sa);

        int maxLcp = 0;
        int lcsStartInCombined = 0;

        int[] saArr = sa.getArray();
        int[] lcpArr = lcpRes.lcp;

        for (int i = 1; i < saArr.length; i++) {
            int pos1 = saArr[i - 1];
            int pos2 = saArr[i];

            // Check if one suffix starts in doc1 and the other in doc2
            boolean oneInDoc1 = (pos1 < len1);
            boolean twoInDoc1 = (pos2 < len1);

            boolean oneInDoc2 = (pos1 > len1 && pos1 < len1 + 1 + len2);
            boolean twoInDoc2 = (pos2 > len1 && pos2 < len1 + 1 + len2);

            if ((oneInDoc1 && twoInDoc2) || (oneInDoc2 && twoInDoc1)) {
                if (lcpArr[i] > maxLcp) {
                    maxLcp = lcpArr[i];
                    lcsStartInCombined = pos1;
                }
            }
        }

        String lcs = "";
        if (maxLcp > 0 && lcsStartInCombined + maxLcp <= combined.length()) {
            lcs = combined.substring(lcsStartInCombined, lcsStartInCombined + maxLcp);
        }
        trace.add(String.format("Found Longest Common Substring (length = %d): \"%s\"",
                maxLcp, (lcs.length() > 40 ? lcs.substring(0, 40) + "..." : lcs)));

        // 2. Token-level Jaccard Similarity using ManualHashMap
        ManualArrayList<String> tokens1 = ManualStringUtil.splitWhitespace(doc1);
        ManualArrayList<String> tokens2 = ManualStringUtil.splitWhitespace(doc2);

        ManualHashMap<String, Boolean> set1 = new ManualHashMap<>(tokens1.size() * 2);
        for (int i = 0; i < tokens1.size(); i++) {
            set1.put(tokens1.get(i).toLowerCase(), Boolean.TRUE);
        }

        ManualHashMap<String, Boolean> set2 = new ManualHashMap<>(tokens2.size() * 2);
        for (int i = 0; i < tokens2.size(); i++) {
            set2.put(tokens2.get(i).toLowerCase(), Boolean.TRUE);
        }

        int intersection = 0;
        ManualArrayList<String> keys1 = set1.keys();
        for (int i = 0; i < keys1.size(); i++) {
            if (set2.containsKey(keys1.get(i))) {
                intersection++;
            }
        }

        int union = set1.size() + set2.size() - intersection;
        double jaccard = (union == 0) ? 0.0 : (double) intersection / union;
        trace.add(String.format("Token Jaccard Coefficient: |A ∩ B| = %d, |A ∪ B| = %d -> Jaccard = %.4f",
                intersection, union, jaccard));

        // Combined similarity heuristic (weighted average of token Jaccard and character coverage)
        double charCoverage = (double) (2 * maxLcp) / (len1 + len2);
        double overallScore = Math.min(1.0, 0.5 * jaccard + 0.5 * charCoverage);

        timer.stop();
        trace.add(String.format("Document similarity evaluation completed in %s.", timer.getFormattedTime()));

        AlgorithmResult algoRes = new AlgorithmResult(
                "Document Similarity",
                String.format("Similarity: %.2f%% | Jaccard: %.2f%% | LCS: %d chars",
                        overallScore * 100, jaccard * 100, maxLcp),
                String.format("Longest Common Substring: \"%s\"\nJaccard Token Overlap: %.4f",
                        lcs, jaccard),
                timer.getElapsedNanos(),
                timer.getFormattedTime(),
                0, 0,
                ComplexityAnalyzer.getComplexity("Suffix Array"),
                trace
        );

        return new SimilarityResult(overallScore, lcs, maxLcp, jaccard, algoRes);
    }
}
