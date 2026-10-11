package com.texthack.module2_strings;

import com.texthack.core.ds.DynamicArray;
import com.texthack.core.model.AlgoResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Module2StringsTest {

    private NaiveMatcher naive;
    private KMPMatcher kmp;
    private ZFunctionMatcher zFunc;
    private RabinKarpMatcher rabinKarp;
    private AhoCorasickMatcher ahoCorasick;
    private SuffixArrayBuilder saBuilder;
    private KasaiLCP kasai;
    private SuffixAutomatonPreview sam;
    private SuffixTreePreview tree;

    @BeforeEach
    public void setup() {
        naive = new NaiveMatcher();
        kmp = new KMPMatcher();
        zFunc = new ZFunctionMatcher();
        rabinKarp = new RabinKarpMatcher();
        ahoCorasick = new AhoCorasickMatcher();
        saBuilder = new SuffixArrayBuilder();
        kasai = new KasaiLCP();
        sam = new SuffixAutomatonPreview();
        tree = new SuffixTreePreview();
    }

    @Test
    public void testNaiveMatcherBasicAndOverlapping() {
        AlgoResult<DynamicArray<Integer>> res = naive.search("AABAACAADAABAABA", "AABA");
        assertEquals(3, res.getOutput().size());
        assertEquals(0, (int) res.getOutput().get(0));
        assertEquals(9, (int) res.getOutput().get(1));
        assertEquals(12, (int) res.getOutput().get(2));
        assertTrue(res.getSteps().size() > 0, "Step trace must not be empty");
        assertTrue(res.getComparisons() > 0);
    }

    @Test
    public void testKMPMatcherAndLpsTable() {
        String text = "ABABDABACDABABCABAB";
        String pattern = "ABABCABAB";
        AlgoResult<KMPMatcher.KMPResult> res = kmp.search(text, pattern);

        assertEquals(1, res.getOutput().getMatches().size());
        assertEquals(10, (int) res.getOutput().getMatches().get(0));

        // Check LPS table for "ABABCABAB":
        // A B A B C A B A B
        // 0 0 1 2 0 1 2 3 4
        int[] lps = res.getOutput().getLpsTable();
        assertEquals(9, lps.length);
        assertEquals(0, lps[0]);
        assertEquals(0, lps[1]);
        assertEquals(1, lps[2]);
        assertEquals(2, lps[3]);
        assertEquals(0, lps[4]);
        assertEquals(1, lps[5]);
        assertEquals(2, lps[6]);
        assertEquals(3, lps[7]);
        assertEquals(4, lps[8]);
        assertTrue(res.getSteps().size() > 0);
    }

    @Test
    public void testZFunctionMatcher() {
        String text = "GEEKS FOR GEEKS";
        String pattern = "GEEKS";
        AlgoResult<ZFunctionMatcher.ZResult> res = zFunc.search(text, pattern);

        assertEquals(2, res.getOutput().getMatches().size());
        assertEquals(0, (int) res.getOutput().getMatches().get(0));
        assertEquals(10, (int) res.getOutput().getMatches().get(1));
        assertTrue(res.getOutput().getZArray().length > 0);
        assertTrue(res.getSteps().size() > 0);
    }

    @Test
    public void testRabinKarpDoubleHashing() {
        String text = "THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG";
        String pattern = "THE";
        AlgoResult<RabinKarpMatcher.RabinKarpResult> res = rabinKarp.search(text, pattern);

        assertEquals(2, res.getOutput().getMatches().size());
        assertEquals(0, (int) res.getOutput().getMatches().get(0));
        assertEquals(31, (int) res.getOutput().getMatches().get(1));
        assertTrue(res.getOutput().getPatternHash1() > 0);
        assertTrue(res.getOutput().getPatternHash2() > 0);
        assertTrue(res.getSteps().size() > 0);
    }

    @Test
    public void testAhoCorasickMultiPattern() {
        DynamicArray<String> patterns = new DynamicArray<>();
        patterns.add("he");
        patterns.add("she");
        patterns.add("his");
        patterns.add("hers");

        String text = "ushers";
        AlgoResult<AhoCorasickMatcher.AhoCorasickResult> res = ahoCorasick.search(text, patterns);

        // In "ushers", matches: "she" at [1..3], "he" at [2..3], "hers" at [2..5]
        assertEquals(3, res.getOutput().getMatches().size());
        assertTrue(res.getSteps().size() > 0);
        assertNotNull(res.getOutput().getTrie());
    }

    @Test
    public void testSuffixArrayPrefixDoubling() {
        String text = "banana$";
        AlgoResult<int[]> res = saBuilder.buildSuffixArray(text);
        int[] sa = res.getOutput();

        assertEquals(text.length(), sa.length);
        // Suffixes of "banana$":
        // 6: $
        // 5: a$
        // 3: ana$
        // 1: anana$
        // 0: banana$
        // 4: na$
        // 2: nana$
        assertArrayEquals(new int[]{6, 5, 3, 1, 0, 4, 2}, sa);
        assertTrue(res.getSteps().size() > 0);
    }

    @Test
    public void testKasaiLCPArray() {
        String text = "banana$";
        int[] sa = new int[]{6, 5, 3, 1, 0, 4, 2};
        AlgoResult<int[]> res = kasai.computeLCP(text, sa);
        int[] lcp = res.getOutput();

        assertEquals(7, lcp.length);
        assertEquals(0, lcp[0]); // $ has no previous suffix
        assertEquals(0, lcp[1]); // LCP("$", "a$") = 0
        assertEquals(1, lcp[2]); // LCP("a$", "ana$") = 1 ("a")
        assertEquals(3, lcp[3]); // LCP("ana$", "anana$") = 3 ("ana")
        assertEquals(0, lcp[4]); // LCP("anana$", "banana$") = 0
        assertEquals(0, lcp[5]); // LCP("banana$", "na$") = 0
        assertEquals(2, lcp[6]); // LCP("na$", "nana$") = 2 ("na")
        assertTrue(res.getSteps().size() > 0);
    }

    @Test
    public void testSuffixAutomatonPreview() {
        String text = "abcb";
        AlgoResult<SuffixAutomatonPreview.SAMResult> res = sam.buildAutomaton(text);
        assertNotNull(res.getOutput());
        assertTrue(res.getOutput().getStateCount() <= 2 * text.length());
        assertTrue(res.getSteps().size() > 0);
    }

    @Test
    public void testSuffixTreePreview() {
        String text = "banana";
        AlgoResult<SuffixTreePreview.TreeResult> res = tree.buildTree(text);
        assertNotNull(res.getOutput());
        assertTrue(res.getOutput().getNodes().size() > 0);
        assertTrue(res.getOutput().getEdges().size() > 0);
        assertTrue(res.getSteps().size() > 0);
    }
}
