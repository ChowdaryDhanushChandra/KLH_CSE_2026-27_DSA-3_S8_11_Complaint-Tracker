package com.texthack.engine.module1;

import com.texthack.engine.module3.DamerauLevenshteinDistance;
import com.texthack.engine.module3.LevenshteinDistance;
import com.texthack.engine.module3.WeightedEditDistance;
import com.texthack.model.AlignmentResult;
import com.texthack.validation.InputValidator;

/**
 * Service managing fuzzy matching and edit distance computations across text queries.
 */
public class FuzzyMatchingService {

    public AlignmentResult computeFuzzyMatch(String algorithm, String s, String t,
                                            int insertCost, int deleteCost, int replaceCost) {
        InputValidator.validateNotEmpty(s, "String A");
        InputValidator.validateNotEmpty(t, "String B");

        if (algorithm == null) algorithm = "Levenshtein";

        switch (algorithm) {
            case "Damerau-Levenshtein":
                return DamerauLevenshteinDistance.compute(s, t);

            case "Weighted Edit Distance":
                InputValidator.validateNonNegative(insertCost, "Insert Cost");
                InputValidator.validateNonNegative(deleteCost, "Delete Cost");
                InputValidator.validateNonNegative(replaceCost, "Replace Cost");
                return WeightedEditDistance.compute(s, t, insertCost, deleteCost, replaceCost);

            case "Levenshtein Distance":
            case "Wagner-Fischer":
            default:
                return LevenshteinDistance.compute(s, t);
        }
    }
}
