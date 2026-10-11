package com.texthack.engine.module1;

import com.texthack.engine.module2.KMPMatcher;
import com.texthack.engine.module2.NaivePatternMatcher;
import com.texthack.engine.module2.RabinKarpMatcher;
import com.texthack.engine.module2.ZAlgorithmMatcher;
import com.texthack.model.SearchResult;
import com.texthack.validation.InputValidator;

/**
 * Service orchestrating pattern matching query execution across Naive, KMP, Z, and Rabin-Karp.
 */
public class PatternSearchService {

    private final NaivePatternMatcher naiveMatcher = new NaivePatternMatcher();
    private final KMPMatcher kmpMatcher = new KMPMatcher();
    private final ZAlgorithmMatcher zMatcher = new ZAlgorithmMatcher();
    private final RabinKarpMatcher rkMatcher = new RabinKarpMatcher();

    public SearchResult executeSearch(String algorithm, String pattern, String text) {
        InputValidator.validatePatternAndText(pattern, text);

        if (algorithm == null) algorithm = "KMP";

        switch (algorithm) {
            case "Naive":
            case "Naive String Matching":
                return naiveMatcher.search(pattern, text);

            case "Z-Algorithm":
            case "Z-Algorithm Matcher":
                return zMatcher.search(pattern, text);

            case "Rabin-Karp":
            case "Rabin-Karp Matcher":
                return rkMatcher.search(pattern, text);

            case "KMP":
            case "Knuth-Morris-Pratt":
            default:
                return kmpMatcher.search(pattern, text);
        }
    }
}
