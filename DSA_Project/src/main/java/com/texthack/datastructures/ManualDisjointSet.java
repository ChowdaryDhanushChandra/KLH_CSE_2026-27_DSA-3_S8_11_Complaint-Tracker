package com.texthack.datastructures;

/**
 * Custom Disjoint Set (Union-Find) with path compression and union by rank.
 * Operates in nearly O(1) amortized time via inverse Ackermann alpha(n).
 */
public class ManualDisjointSet {
    private final int[] parent;
    private final int[] rank;
    private int count;

    public ManualDisjointSet(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Size cannot be negative");
        }
        parent = new int[n];
        rank = new int[n];
        count = n;
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }

    public int find(int i) {
        checkIndex(i);
        if (parent[i] != i) {
            parent[i] = find(parent[i]); // Path compression
        }
        return parent[i];
    }

    public boolean union(int i, int j) {
        int rootI = find(i);
        int rootJ = find(j);
        if (rootI == rootJ) {
            return false;
        }
        // Union by rank
        if (rank[rootI] < rank[rootJ]) {
            parent[rootI] = rootJ;
        } else if (rank[rootI] > rank[rootJ]) {
            parent[rootJ] = rootI;
        } else {
            parent[rootJ] = rootI;
            rank[rootI]++;
        }
        count--;
        return true;
    }

    public boolean connected(int i, int j) {
        return find(i) == find(j);
    }

    public int count() {
        return count;
    }

    private void checkIndex(int i) {
        if (i < 0 || i >= parent.length) {
            throw new IndexOutOfBoundsException("Element " + i + " is out of bounds [0, " + (parent.length - 1) + "]");
        }
    }
}
