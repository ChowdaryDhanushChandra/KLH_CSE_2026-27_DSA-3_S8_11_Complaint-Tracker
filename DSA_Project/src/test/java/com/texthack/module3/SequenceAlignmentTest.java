package com.texthack.module3;

import com.texthack.engine.module3.NeedlemanWunsch;
import com.texthack.engine.module3.SmithWaterman;
import com.texthack.model.AlignmentResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test suite for Sequence Alignment (Needleman-Wunsch & Smith-Waterman).
 */
public class SequenceAlignmentTest {

    @Test
    @DisplayName("Needleman-Wunsch: Global DNA sequence alignment")
    void testNeedlemanWunschAlignment() {
        String s1 = "GCATGCG";
        String s2 = "GATTACA";

        AlignmentResult res = NeedlemanWunsch.align(s1, s2, 2, -1, -1);
        assertTrue(res.getDistanceOrScore() > 0);
        assertEquals(res.getAlignedSeq1().length(), res.getAlignedSeq2().length());
        assertFalse(res.getOperations().isEmpty());
    }

    @Test
    @DisplayName("Smith-Waterman: Local sequence alignment and non-negative score clamping")
    void testSmithWatermanLocalAlignment() {
        String s1 = "ACACACTA";
        String s2 = "AGCACACA";

        AlignmentResult res = SmithWaterman.align(s1, s2, 2, -1, -1);
        // Optimal local match contains "ACACAC"
        assertTrue(res.getDistanceOrScore() >= 10);
        assertNotNull(res.getAlignedSeq1());
        assertNotNull(res.getAlignedSeq2());
    }
}
