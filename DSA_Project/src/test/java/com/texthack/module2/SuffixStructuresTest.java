package com.texthack.module2;

import com.texthack.engine.module2.KasaiLCP;
import com.texthack.engine.module2.SuffixArray;
import com.texthack.engine.module2.SuffixArrayBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test suite for Suffix Array construction and Kasai's LCP algorithm.
 */
public class SuffixStructuresTest {

    @Test
    @DisplayName("SuffixArray & KasaiLCP: Classical 'banana' verification")
    void testBananaSuffixArrayAndLCP() {
        String text = "banana";
        SuffixArray sa = SuffixArrayBuilder.build(text);

        assertEquals(6, sa.length());
        int[] expectedSA = new int[]{5, 3, 1, 0, 4, 2};
        for (int i = 0; i < 6; i++) {
            assertEquals(expectedSA[i], sa.get(i), "Suffix array mismatch at index " + i);
        }

        KasaiLCP.LCPResult lcp = KasaiLCP.compute(sa);
        assertEquals(6, lcp.lcp.length);
        // Suffixes:
        // 0: "a"
        // 1: "ana"   -> LCP with "a" = 1
        // 2: "anana" -> LCP with "ana" = 3
        // 3: "banana"-> LCP with "anana" = 0
        // 4: "na"    -> LCP with "banana" = 0
        // 5: "nana"  -> LCP with "na" = 2
        assertEquals(0, lcp.lcp[0]);
        assertEquals(1, lcp.lcp[1]);
        assertEquals(3, lcp.lcp[2]);
        assertEquals(0, lcp.lcp[3]);
        assertEquals(0, lcp.lcp[4]);
        assertEquals(2, lcp.lcp[5]);
    }

    @Test
    @DisplayName("SuffixArray: Edge cases - Empty string and single character")
    void testSuffixArrayEdgeCases() {
        SuffixArray empty = SuffixArrayBuilder.build("");
        assertEquals(0, empty.length());

        SuffixArray single = SuffixArrayBuilder.build("A");
        assertEquals(1, single.length());
        assertEquals(0, single.get(0));

        KasaiLCP.LCPResult lcpSingle = KasaiLCP.compute(single);
        assertEquals(0, lcpSingle.lcp[0]);
    }
}
