package com.texthack.engine.module3;

import com.texthack.datastructures.ManualArrayList;
import com.texthack.metrics.ComplexityAnalyzer;
import com.texthack.metrics.ExecutionTimer;
import com.texthack.model.AlgorithmResult;

/**
 * Finds Tree Centroid(s) in linear O(V) time using subtree size Dynamic Programming.
 * A centroid vertex minimizes the maximum component size upon deletion (each component <= n/2).
 */
public class TreeCentroidDP {

    public static class CentroidResult {
        public final ManualArrayList<Integer> centroids;
        public final int[] subtreeSizes;
        public final AlgorithmResult algorithmResult;

        public CentroidResult(ManualArrayList<Integer> centroids, int[] sizes, AlgorithmResult algoRes) {
            this.centroids = centroids;
            this.subtreeSizes = sizes;
            this.algorithmResult = algoRes;
        }
    }

    public static CentroidResult findCentroids(int n, ManualArrayList<Integer>[] tree) {
        ExecutionTimer timer = new ExecutionTimer();
        timer.start();

        ManualArrayList<String> trace = new ManualArrayList<>();
        if (n <= 1) {
            timer.stop();
            ManualArrayList<Integer> c = new ManualArrayList<>();
            if (n == 1) c.add(0);
            AlgorithmResult res = new AlgorithmResult("Tree Centroid DP", "Centroid: [0]", "Single node",
                    0, "0 ns", 0, 0, ComplexityAnalyzer.getComplexity("Tree Centroid DP"), trace);
            return new CentroidResult(c, new int[]{1}, res);
        }

        int[] sz = new int[n];
        ManualArrayList<Integer> centroids = new ManualArrayList<>();

        // Compute subtree sizes
        computeSizes(0, -1, tree, sz, trace);

        // Identify centroids
        checkCentroids(0, -1, n, tree, sz, centroids, trace);

        timer.stop();
        trace.add("Centroid search complete: Found " + centroids.size() + " centroid(s): " + centroids.toString());

        AlgorithmResult algoRes = new AlgorithmResult(
                "Tree Centroid DP",
                "Centroid(s): " + centroids.toString(),
                "Identified vertex where removing it yields no connected component with size > " + (n / 2),
                timer.getElapsedNanos(),
                timer.getFormattedTime(),
                0, 0,
                ComplexityAnalyzer.getComplexity("Tree Centroid DP"),
                trace
        );

        return new CentroidResult(centroids, sz, algoRes);
    }

    private static void computeSizes(int u, int p, ManualArrayList<Integer>[] tree, int[] sz, ManualArrayList<String> trace) {
        sz[u] = 1;
        ManualArrayList<Integer> neighbors = tree[u];
        for (int i = 0; i < neighbors.size(); i++) {
            int v = neighbors.get(i);
            if (v != p) {
                computeSizes(v, u, tree, sz, trace);
                sz[u] += sz[v];
            }
        }
    }

    private static void checkCentroids(int u, int p, int n, ManualArrayList<Integer>[] tree,
                                       int[] sz, ManualArrayList<Integer> centroids, ManualArrayList<String> trace) {
        boolean isCentroid = true;
        ManualArrayList<Integer> neighbors = tree[u];

        for (int i = 0; i < neighbors.size(); i++) {
            int v = neighbors.get(i);
            if (v != p) {
                if (sz[v] > n / 2) {
                    isCentroid = false;
                }
                checkCentroids(v, u, n, tree, sz, centroids, trace);
            }
        }

        if (n - sz[u] > n / 2) {
            isCentroid = false;
        }

        if (isCentroid) {
            centroids.add(u);
            trace.add("Node " + u + " is a centroid: all adjacent component sizes <= " + (n / 2));
        }
    }
}
