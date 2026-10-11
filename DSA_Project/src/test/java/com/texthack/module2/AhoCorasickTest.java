package com.texthack.module2;

import com.texthack.datastructures.ManualArrayList;
import com.texthack.engine.module2.AhoCorasickMatcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test suite for Aho-Corasick multi-pattern dictionary search.
 */
public class AhoCorasickTest {

    @Test
    @DisplayName("Aho-Corasick: Multi-pattern dictionary search with overlaps")
    void testAhoCorasickMultiPattern() {
        AhoCorasickMatcher ac = new AhoCorasickMatcher();
        ManualArrayList<String> patterns = new ManualArrayList<>();
        patterns.add("he");
        patterns.add("she");
        patterns.add("his");
        patterns.add("hers");

        String text = "ushers";
        AhoCorasickMatcher.ACResult res = ac.search(patterns, text);

        // "ushers":
        // "she" at [1..3]
        // "he" at [2..3]
        // "hers" at [2..5]
        assertEquals(3, res.matches.size());
    }

    @Test
    @DisplayName("Aho-Corasick: Edge case - No matches found")
    void testAhoCorasickNoMatch() {
        AhoCorasickMatcher ac = new AhoCorasickMatcher();
        ManualArrayList<String> patterns = new ManualArrayList<>();
        patterns.add("xyz");
        patterns.add("123");

        AhoCorasickMatcher.ACResult res = ac.search(patterns, "abcdef");
        assertEquals(0, res.matches.size());
    }
}
