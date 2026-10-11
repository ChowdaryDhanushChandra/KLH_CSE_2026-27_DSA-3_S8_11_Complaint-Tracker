package com.texthack.module3;

import com.texthack.engine.module3.HamiltonianPathDP;
import com.texthack.engine.module3.SOSDP;
import com.texthack.engine.module3.SetCoverDP;
import com.texthack.engine.module3.TravellingSalesmanDP;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test suite for Bitmask DP (Travelling Salesman, Hamiltonian Path, Set Cover, SOS DP).
 */
public class BitmaskDPTest {

    @Test
    @DisplayName("TravellingSalesmanDP: 4-city metric tour")
    void testTSPBitmaskDP() {
        int[][] dist = new int[][]{
                {0, 10, 15, 20},
                {10, 0, 35, 25},
                {15, 35, 0, 30},
                {20, 25, 30, 0}
        };

        TravellingSalesmanDP.TSPResult res = TravellingSalesmanDP.solve(dist);
        // Tour: 0 -> 1 -> 3 -> 2 -> 0: 10 + 25 + 30 + 15 = 80
        assertEquals(80, res.minCost);
        assertEquals(5, res.tour.size()); // 4 nodes + return to 0
    }

    @Test
    @DisplayName("TravellingSalesmanDP: Guards against exponential explosion n > 16")
    void testTSPGuard() {
        int[][] huge = new int[17][17];
        assertThrows(IllegalArgumentException.class, () -> TravellingSalesmanDP.solve(huge));
    }

    @Test
    @DisplayName("HamiltonianPathDP: Valid line graph vs disconnected graph")
    void testHamiltonianPath() {
        boolean[][] lineGraph = new boolean[][]{
                {false, true, false},
                {true, false, true},
                {false, true, false}
        };
        assertTrue(HamiltonianPathDP.findPath(lineGraph).exists);

        boolean[][] disconnected = new boolean[][]{
                {false, false, false},
                {false, false, false},
                {false, false, false}
        };
        assertFalse(HamiltonianPathDP.findPath(disconnected).exists);
    }

    @Test
    @DisplayName("SetCoverDP: Optimal subset selection")
    void testSetCoverDP() {
        // Universe {0, 1, 2, 3} (size 4)
        int universeSize = 4;
        int[] subsets = new int[]{
                0b0011, // {0, 1}
                0b1100, // {2, 3}
                0b0110  // {1, 2}
        };
        String[] names = new String[]{"S1", "S2", "S3"};

        SetCoverDP.SetCoverResult res = SetCoverDP.solve(universeSize, subsets, names);
        // Optimal cover is S1 + S2 = 2 subsets
        assertEquals(2, res.minSubsets);
        assertEquals(2, res.chosenSubsetIndices.size());
    }

    @Test
    @DisplayName("SOSDP: Sum Over Subsets correctness for 3 bits")
    void testSOSDP() {
        int n = 3;
        int[] a = new int[]{1, 2, 3, 4, 5, 6, 7, 8};
        SOSDP.SOSResult res = SOSDP.compute(n, a);

        // F[7] (mask 111) should be sum of all a[0..7] = 36
        assertEquals(36, res.sosValues[7]);
        // F[0] (mask 000) should be a[0] = 1
        assertEquals(1, res.sosValues[0]);
        // F[3] (mask 011) should be a[0] + a[1] + a[2] + a[3] = 1 + 2 + 3 + 4 = 10
        assertEquals(10, res.sosValues[3]);
    }
}
