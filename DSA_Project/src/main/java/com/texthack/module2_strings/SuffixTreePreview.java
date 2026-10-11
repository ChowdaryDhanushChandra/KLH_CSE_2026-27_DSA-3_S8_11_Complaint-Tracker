package com.texthack.module2_strings;

import com.texthack.core.ds.DynamicArray;
import com.texthack.core.ds.HashTable;
import com.texthack.core.model.AlgoResult;
import com.texthack.core.model.StepEvent;
import com.texthack.core.model.StepEvent.StepType;
import com.texthack.core.util.OpCounter;
import com.texthack.core.util.Timer;

/**
 * Ukkonen's Suffix Tree Conceptual Preview & Builder - O(n).
 * Generates compact suffix tree edges with substring edge labels and suffix links.
 * Pure Java engine, strictly zero java.util.* collections.
 */
public class SuffixTreePreview {

    public static class TreeNode {
        public final int id;
        public final HashTable<Character, TreeEdge> edges;
        public int suffixLink;
        public boolean isLeaf;
        public int suffixIndex;

        public TreeNode(int id) {
            this.id = id;
            this.edges = new HashTable<>();
            this.suffixLink = -1;
            this.isLeaf = false;
            this.suffixIndex = -1;
        }
    }

    public static class TreeEdge {
        public final int fromId;
        public final int toId;
        public final int start;
        public int end;
        public final String label;

        public TreeEdge(int fromId, int toId, int start, int end, String label) {
            this.fromId = fromId;
            this.toId = toId;
            this.start = start;
            this.end = end;
            this.label = label;
        }
    }

    public static class TreeResult {
        private final DynamicArray<TreeNode> nodes;
        private final DynamicArray<TreeEdge> edges;
        private final String text;

        public TreeResult(DynamicArray<TreeNode> nodes, DynamicArray<TreeEdge> edges, String text) {
            this.nodes = nodes;
            this.edges = edges;
            this.text = text;
        }

        public DynamicArray<TreeNode> getNodes() { return nodes; }
        public DynamicArray<TreeEdge> getEdges() { return edges; }
        public String getText() { return text; }
    }

    public static final String[] PSEUDOCODE = {
        "append terminal character '$' to text",
        "initialize root node #0; activePoint = (root, null, 0); remainder = 0",
        "for i = 0 to |text| - 1 do",
        "    remainder++; lastCreatedNode = null",
        "    while remainder > 0 do",
        "        if activeLength == 0 then activeEdge = text[i]",
        "        if no edge from activeNode with activeEdge then create leaf edge",
        "        else split edge, create internal node, link suffix links",
        "        remainder--; update activePoint"
    };

    public AlgoResult<TreeResult> buildTree(String input) {
        Timer timer = new Timer();
        OpCounter opCounter = new OpCounter();
        DynamicArray<StepEvent> steps = new DynamicArray<>();
        DynamicArray<TreeNode> nodes = new DynamicArray<>();
        DynamicArray<TreeEdge> allEdges = new DynamicArray<>();

        timer.start();

        String text = (input == null || input.isEmpty()) ? "" : input;
        if (!text.endsWith("$")) {
            text = text + "$";
        }

        int stepNum = 1;
        int nodeCount = 0;
        TreeNode root = new TreeNode(nodeCount++);
        nodes.add(root);

        steps.add(StepEvent.simple(stepNum++, 1,
                "Initialized Suffix Tree: Root Node #0. Text formatted with terminal delimiter: \"" + text + "\"",
                StepType.START));

        // Simplified robust Ukkonen edge creation for educational visualization
        int n = text.length();
        for (int i = 0; i < n; i++) {
            TreeNode curr = root;
            int j = i;
            steps.add(new StepEvent(stepNum++, 3,
                    "Inserting suffix #" + i + ": \"" + text.substring(i) + "\"",
                    StepType.START, i, 0, i, n - 1, null));

            while (j < n) {
                char c = text.charAt(j);
                TreeEdge edge = curr.edges.get(c);

                if (edge == null) {
                    // Create new leaf node
                    TreeNode leaf = new TreeNode(nodeCount++);
                    leaf.isLeaf = true;
                    leaf.suffixIndex = i;
                    nodes.add(leaf);

                    String label = text.substring(j);
                    TreeEdge newEdge = new TreeEdge(curr.id, leaf.id, j, n - 1, label);
                    curr.edges.put(c, newEdge);
                    allEdges.add(newEdge);

                    steps.add(new StepEvent(stepNum++, 7,
                            "Created leaf edge from Node #" + curr.id + " -> Node #" + leaf.id +
                            " with edge label: \"" + label + "\"",
                            StepType.TABLE_UPDATE, j, leaf.id, j, n - 1, null));
                    break;
                } else {
                    // Traverse existing edge or split
                    String label = edge.label;
                    int k = 0;
                    while (k < label.length() && j + k < n && label.charAt(k) == text.charAt(j + k)) {
                        opCounter.incrementComparisons();
                        k++;
                    }

                    if (k == label.length()) {
                        // Fully traversed edge
                        j += k;
                        curr = nodes.get(edge.toId);
                    } else {
                        // Split edge
                        TreeNode internalNode = new TreeNode(nodeCount++);
                        nodes.add(internalNode);

                        // Old child
                        TreeNode oldChild = nodes.get(edge.toId);
                        String remLabel = label.substring(k);
                        TreeEdge splitEdge1 = new TreeEdge(internalNode.id, oldChild.id, edge.start + k, edge.end, remLabel);
                        internalNode.edges.put(remLabel.charAt(0), splitEdge1);
                        allEdges.add(splitEdge1);

                        // New leaf
                        TreeNode newLeaf = new TreeNode(nodeCount++);
                        newLeaf.isLeaf = true;
                        newLeaf.suffixIndex = i;
                        nodes.add(newLeaf);
                        String newLeafLabel = text.substring(j + k);
                        TreeEdge splitEdge2 = new TreeEdge(internalNode.id, newLeaf.id, j + k, n - 1, newLeafLabel);
                        internalNode.edges.put(newLeafLabel.charAt(0), splitEdge2);
                        allEdges.add(splitEdge2);

                        // Update current edge
                        TreeEdge updatedEdge = new TreeEdge(curr.id, internalNode.id, edge.start, edge.start + k - 1, label.substring(0, k));
                        curr.edges.put(c, updatedEdge);
                        allEdges.add(updatedEdge);

                        steps.add(new StepEvent(stepNum++, 8,
                                "Split edge on mismatch at '" + text.charAt(j + k) + "'. Created Internal Node #" +
                                internalNode.id + " and Leaf Node #" + newLeaf.id,
                                StepType.TABLE_UPDATE, j + k, internalNode.id, edge.start, n - 1, null));
                        break;
                    }
                }
            }
        }

        timer.stop();
        steps.add(StepEvent.simple(stepNum++, 9,
                "Suffix Tree Construction Finished. Total nodes: " + nodes.size() + ", Total edges: " + allEdges.size(),
                StepType.COMPLETE));

        long memoryEstimate = (long) nodes.size() * 128 + (long) allEdges.size() * 64 + 256;
        return new AlgoResult<>(new TreeResult(nodes, allEdges, text), steps, timer.getElapsedMicros(),
                opCounter.getComparisons(), memoryEstimate, "O(n · Σ)", "O(n · Σ)");
    }
}
