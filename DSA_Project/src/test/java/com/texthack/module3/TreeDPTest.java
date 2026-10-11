package com.texthack.module3;

import com.texthack.datastructures.ManualArrayList;
import com.texthack.engine.module3.RerootingDP;
import com.texthack.engine.module3.TreeCentroidDP;
import com.texthack.engine.module3.TreeDP;
import com.texthack.engine.module3.TreeDiameterDP;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test suite for Dynamic Programming on Trees (Diameter, Centroid, Rerooting, Independent Set).
 */
public class TreeDPTest {

    @SuppressWarnings("unchecked")
    private ManualArrayList<Integer>[] buildLineTree(int n) {
        ManualArrayList<Integer>[] tree = new ManualArrayList[n];
        for (int i = 0; i < n; i++) tree[i] = new ManualArrayList<>();
        for (int i = 0; i < n - 1; i++) {
            tree[i].add(i + 1);
            tree[i + 1].add(i);
        }
        return tree;
    }

    @Test
    @DisplayName("TreeDiameterDP: Line tree of 5 nodes has diameter 4")
    void testTreeDiameterLine() {
        ManualArrayList<Integer>[] tree = buildLineTree(5);
        TreeDiameterDP.DiameterResult res = TreeDiameterDP.compute(5, tree);
        assertEquals(4, res.diameter);
    }

    @Test
    @DisplayName("TreeCentroidDP: Line tree of 5 nodes has center at node 2")
    void testTreeCentroidLine() {
        ManualArrayList<Integer>[] tree = buildLineTree(5);
        TreeCentroidDP.CentroidResult res = TreeCentroidDP.findCentroids(5, tree);
        assertTrue(res.centroids.contains(2));
    }

    @Test
    @DisplayName("RerootingDP: Center node of line tree minimizes all-pairs distance sum")
    void testTreeRerooting() {
        ManualArrayList<Integer>[] tree = buildLineTree(5);
        RerootingDP.RerootResult res = RerootingDP.computeAllDistances(5, tree);
        // Distances from node 2: to 0 is 2, to 1 is 1, to 3 is 1, to 4 is 2 -> sum = 6
        assertEquals(2, res.bestRoot);
        assertEquals(6, res.minDistanceSum);
    }

    @Test
    @DisplayName("TreeDP: Maximum weight independent set on line tree")
    void testTreeIndependentSet() {
        ManualArrayList<Integer>[] tree = buildLineTree(4);
        // Weights: 10, 1, 1, 10 -> pick nodes 0 and 3 -> 20
        int[] weights = new int[]{10, 1, 1, 10};
        TreeDP.TreeDPResult res = TreeDP.solveIndependentSet(4, tree, weights);
        assertEquals(20, res.maxIndependentWeight);
    }
}
