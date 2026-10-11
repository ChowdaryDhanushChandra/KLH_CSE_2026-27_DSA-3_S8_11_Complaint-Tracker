package com.texthack.module2;

import com.texthack.engine.common.StringAlgorithm;
import com.texthack.engine.module2.KMPMatcher;
import com.texthack.engine.module2.NaivePatternMatcher;
import com.texthack.engine.module2.RabinKarpMatcher;
import com.texthack.engine.module2.ZAlgorithmMatcher;
import com.texthack.model.SearchResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test suite for String Pattern Matchers (Naive, KMP, Z-Algorithm, Rabin-Karp).
 * Tests all requested edge cases: empty strings, single chars, pattern longer than text, repeated chars.
 */
public class StringMatchingTest {

    private final StringAlgorithm[] matchers = new StringAlgorithm[]{
            new NaivePatternMatcher(),
            new KMPMatcher(),
            new ZAlgorithmMatcher(),
            new RabinKarpMatcher()
    };

    @Test
    @DisplayName("String Matchers: Standard pattern search with multiple occurrences")
    void testStandardMatching() {
        String text = "ABABDABACDABABCABAB";
        String pattern = "ABAB";

        for (StringAlgorithm matcher : matchers) {
            SearchResult res = matcher.search(pattern, text);
            // Matches at 0, 10, 15
            assertEquals(3, res.getMatchCount(), "Failed for matcher: " + matcher.getName());
            assertEquals(0, res.getMatchIndices().get(0));
            assertEquals(10, res.getMatchIndices().get(1));
            assertEquals(15, res.getMatchIndices().get(2));
        }
    }

    @Test
    @DisplayName("String Matchers: Edge case - Pattern longer than text")
    void testPatternLongerThanText() {
        for (StringAlgorithm matcher : matchers) {
            SearchResult res = matcher.search("LONGERPATTERN", "SHORT");
            assertEquals(0, res.getMatchCount());
            assertTrue(res.getMatchIndices().isEmpty());
        }
    }

    @Test
    @DisplayName("String Matchers: Edge case - Empty strings")
    void testEmptyStrings() {
        for (StringAlgorithm matcher : matchers) {
            assertEquals(0, matcher.search("", "TEXT").getMatchCount());
            assertEquals(0, matcher.search("PAT", "").getMatchCount());
            assertEquals(0, matcher.search("", "").getMatchCount());
        }
    }

    @Test
    @DisplayName("String Matchers: Edge case - Single character strings")
    void testSingleCharacterStrings() {
        for (StringAlgorithm matcher : matchers) {
            SearchResult match = matcher.search("A", "A");
            assertEquals(1, match.getMatchCount());
            assertEquals(0, match.getMatchIndices().get(0));

            SearchResult mismatch = matcher.search("B", "A");
            assertEquals(0, mismatch.getMatchCount());
        }
    }

    @Test
    @DisplayName("String Matchers: Edge case - Highly repetitive characters")
    void testRepetitiveCharacters() {
        String text = "AAAAA";
        String pattern = "AA";

        for (StringAlgorithm matcher : matchers) {
            SearchResult res = matcher.search(pattern, text);
            // Matches at 0, 1, 2, 3
            assertEquals(4, res.getMatchCount(), "Failed for matcher: " + matcher.getName());
        }
    }

    @Test
    @DisplayName("String Matchers: Multilingual Unicode support (Hindi & Telugu)")
    void testMultilingualMatching() {
        String hindiText = "टेक्स्टहैक में एल्गोरिदम महत्वपूर्ण हैं। एल्गोरिदम सीखें।";
        String hindiPat = "एल्गोरिदम";

        for (StringAlgorithm matcher : matchers) {
            SearchResult res = matcher.search(hindiPat, hindiText);
            assertEquals(2, res.getMatchCount(), "Failed Hindi for matcher: " + matcher.getName());
        }
    }
}
