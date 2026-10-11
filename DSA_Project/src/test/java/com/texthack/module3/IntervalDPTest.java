package com.texthack.module3;

import com.texthack.engine.module3.MatrixChainMultiplication;
import com.texthack.engine.module3.OptimalBinarySearchTree;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test suite for Interval Dynamic Programming (Matrix-Chain & Optimal BST).
 */
public class IntervalDPTest {

    @Test
    @DisplayName("MatrixChainMultiplication: Classic 4-matrix sequence")
    void testMatrixChainMultiplication() {
        // Dimensions: A1(40x20), A2(20x30), A3(30x10), A4(10x30)
        // p = [40, 20, 30, 10, 30]
        int[] p = new int[]{40, 20, 30, 10, 30};
        MatrixChainMultiplication.MCMResult res = MatrixChainMultiplication.solve(p);

        // Optimal parens: ((A1 x (A2 x A3)) x A4) -> 20*30*10 + 40*20*10 + 40*10*30 = 6000 + 8000 + 12000 = 26000
        assertEquals(26000, res.minCost);
        assertTrue(res.optimalParenthesization.contains("A1"));
        assertTrue(res.optimalParenthesization.contains("A4"));
    }

    @Test
    @DisplayName("OptimalBinarySearchTree: 3-key probability distribution")
    void testOptimalBinarySearchTree() {
        String[] keys = new String[]{"k1", "k2", "k3"};
        double[] p = new double[]{0.0, 0.4, 0.1, 0.2}; // 1-indexed
        double[] q = new double[]{0.1, 0.1, 0.05, 0.05};

        OptimalBinarySearchTree.OBSTResult res = OptimalBinarySearchTree.compute(keys, p, q);
        assertTrue(res.minCost > 0.0);
        assertTrue(res.rootTable[1][3] >= 1 && res.rootTable[1][3] <= 3);
    }
}
