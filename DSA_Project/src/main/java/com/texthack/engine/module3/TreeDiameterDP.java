package com.texthack.engine.module3;

import com.texthack.datastructures.ManualArrayList;
import com.texthack.metrics.ComplexityAnalyzer;
import com.texthack.metrics.ExecutionTimer;
import com.texthack.model.AlgorithmResult;

/**
 * Computes Tree Diameter (longest simple path between any pair of nodes)
 * in linear O(V) time using a single dynamic programming DFS pass.
 */
public class TreeDiameterDP {

    public static class DiameterResult {
        public final int diameter;
        public final int[] maxDownPath;
        public final AlgorithmResult algorithmResult;

        public DiameterResult(int diameter, int[] maxDownPath, AlgorithmResult algoRes) {
            this.diameter = diameter;
            this.maxDownPath = maxDownPath;
            this.algorithmResult = algoRes;
        }
    }

    public static DiameterResult compute(int n, ManualArrayList<Integer>[] tree) {
        ExecutionTimer timer = new ExecutionTimer();
        timer.start();

        ManualArrayList<String> trace = new ManualArrayList<>();
        if (n <= 1) {
            timer.stop();
            AlgorithmResult res = new AlgorithmResult("Tree Diameter DP", "Diameter: 0", "Trivial tree",
                    0, "0 ns", 0, 0, ComplexityAnalyzer.getComplexity("Tree Diameter DP"), trace);
            return new DiameterResult(0, new int[n], res);
        }

        int[] maxDown = new int[n];
        int[] globalDiameter = new int[]{0};

        dfs(0, -1, tree, maxDown, globalDiameter, trace);

        timer.stop();
        trace.add("Tree diameter computation complete: Maximum Path Length = " + globalDiameter[0] + " edges.");

        AlgorithmResult algoRes = new AlgorithmResult(
                "Tree Diameter DP",
                "Tree Diameter: " + globalDiameter[0] + " edges",
                "Computed via single bottom-up DP pass tracking top two child branch depths per node.",
                timer.getElapsedNanos(),
                timer.getFormattedTime(),
                0, 0,
                ComplexityAnalyzer.getComplexity("Tree Diameter DP"),
                trace
        );

        return new DiameterResult(globalDiameter[0], maxDown, algoRes);
    }

    private static void dfs(int u, int p, ManualArrayList<Integer>[] tree,
                            int[] maxDown, int[] globalDiameter, ManualArrayList<String> trace) {
        int firstMax = -1;
        int secondMax = -1;

        ManualArrayList<Integer> neighbors = tree[u];
        for (int i = 0; i < neighbors.size(); i++) {
            int v = neighbors.get(i);
            if (v != p) {
                dfs(v, u, tree, maxDown, globalDiameter, trace);
                int path = maxDown[v];
                if (path > firstMax) {
                    secondMax = firstMax;
                    firstMax = path;
                } else if (path > secondMax) {
                    secondMax = path;
                }
            }
        }

        maxDown[u] = firstMax + 1;

        int throughU = (firstMax + 1) + (secondMax + 1);
        if (throughU > globalDiameter[0]) {
            globalDiameter[0] = throughU;
            trace.add(String.format("New maximum diameter %d found through Node %d (Branches: %d and %d)",
                    throughU, u, firstMax + 1, secondMax + 1));
        }
    }
}
