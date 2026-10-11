package com.texthack.engine.module2;

import com.texthack.datastructures.ManualArrayList;
import com.texthack.datastructures.ManualHashMap;
import com.texthack.datastructures.ManualQueue;
import com.texthack.metrics.ComplexityAnalyzer;
import com.texthack.metrics.ExecutionTimer;
import com.texthack.metrics.OperationCounter;
import com.texthack.model.ComplexityInfo;

/**
 * Aho-Corasick multi-pattern search automaton without java.util.*.
 * Preprocesses dictionary of patterns into a trie with failure and output links in O(M).
 * Searches text in O(n + z) time, where z is the total count of pattern occurrences.
 */
public class AhoCorasickMatcher {

    public static class ACNode {
        public final ManualHashMap<Character, ACNode> children = new ManualHashMap<>(8);
        public ACNode failLink;
        public final ManualArrayList<String> outputPatterns = new ManualArrayList<>();
        public final int id;

        public ACNode(int id) {
            this.id = id;
            this.failLink = null;
        }
    }

    public static class MatchOccurrence {
        public final String pattern;
        public final int endIndex;
        public final int startIndex;

        public MatchOccurrence(String pattern, int startIndex, int endIndex) {
            this.pattern = pattern;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        @Override
        public String toString() {
            return "\"" + pattern + "\" at [" + startIndex + ".." + endIndex + "]";
        }
    }

    public static class ACResult {
        public final ManualArrayList<MatchOccurrence> matches;
        public final long executionTimeNanos;
        public final String formattedTime;
        public final long operations;
        public final ComplexityInfo complexityInfo;
        public final ManualArrayList<String> traceLogs;

        public ACResult(ManualArrayList<MatchOccurrence> matches, long timeNanos, String formattedTime,
                        long operations, ComplexityInfo complexity, ManualArrayList<String> trace) {
            this.matches = matches;
            this.executionTimeNanos = timeNanos;
            this.formattedTime = formattedTime;
            this.operations = operations;
            this.complexityInfo = complexity;
            this.traceLogs = trace;
        }
    }

    private ACNode root;
    private int nodeCounter;

    public AhoCorasickMatcher() {
        this.nodeCounter = 0;
        this.root = new ACNode(nodeCounter++);
    }

    public ComplexityInfo getComplexity() {
        return ComplexityAnalyzer.getComplexity("Aho-Corasick");
    }

    /**
     * Builds the Aho-Corasick trie and failure links.
     */
    public void buildAutomaton(ManualArrayList<String> patterns, ManualArrayList<String> trace) {
        this.nodeCounter = 0;
        this.root = new ACNode(nodeCounter++);

        if (trace != null) {
            trace.add("Phase 1: Building Trie for " + patterns.size() + " pattern(s)...");
        }

        // 1. Insert patterns into Trie
        for (int p = 0; p < patterns.size(); p++) {
            String pat = patterns.get(p);
            if (pat == null || pat.isEmpty()) continue;

            ACNode curr = root;
            for (int i = 0; i < pat.length(); i++) {
                char c = pat.charAt(i);
                ACNode next = curr.children.get(c);
                if (next == null) {
                    next = new ACNode(nodeCounter++);
                    curr.children.put(c, next);
                }
                curr = next;
            }
            curr.outputPatterns.add(pat);
            if (trace != null) {
                trace.add("Inserted pattern \"" + pat + "\" ending at Node " + curr.id);
            }
        }

        // 2. BFS to construct Failure and Output Links
        if (trace != null) {
            trace.add("Phase 2: Computing BFS Failure Links...");
        }

        ManualQueue<ACNode> queue = new ManualQueue<>();
        ManualArrayList<Character> rootKeys = root.children.keys();
        for (int i = 0; i < rootKeys.size(); i++) {
            ACNode child = root.children.get(rootKeys.get(i));
            child.failLink = root;
            queue.enqueue(child);
        }

        while (!queue.isEmpty()) {
            ACNode curr = queue.dequeue();
            ManualArrayList<Character> keys = curr.children.keys();

            for (int i = 0; i < keys.size(); i++) {
                char ch = keys.get(i);
                ACNode child = curr.children.get(ch);

                // Find failure link for child
                ACNode fail = curr.failLink;
                while (fail != null && fail.children.get(ch) == null) {
                    fail = fail.failLink;
                }
                child.failLink = (fail != null) ? fail.children.get(ch) : root;

                // Merge dictionary output links
                if (child.failLink != null) {
                    ManualArrayList<String> failOutputs = child.failLink.outputPatterns;
                    for (int o = 0; o < failOutputs.size(); o++) {
                        child.outputPatterns.add(failOutputs.get(o));
                    }
                }

                if (trace != null && child.failLink != null && child.failLink != root) {
                    trace.add(String.format("Node %d on char '%c' -> failure link to Node %d",
                            child.id, ch, child.failLink.id));
                }

                queue.enqueue(child);
            }
        }
    }

    /**
     * Executes multi-pattern search over text.
     */
    public ACResult search(ManualArrayList<String> patterns, String text) {
        ExecutionTimer timer = new ExecutionTimer();
        OperationCounter counter = new OperationCounter();
        timer.start();

        ManualArrayList<MatchOccurrence> results = new ManualArrayList<>();
        ManualArrayList<String> trace = new ManualArrayList<>();

        if (patterns == null || patterns.isEmpty() || text == null || text.isEmpty()) {
            timer.stop();
            return new ACResult(results, timer.getElapsedNanos(), timer.getFormattedTime(),
                    0, getComplexity(), trace);
        }

        buildAutomaton(patterns, trace);

        trace.add("Phase 3: Scanning text of length " + text.length() + " through Aho-Corasick automaton...");

        ACNode curr = root;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            counter.incrementOperations();

            // Follow failure links if transition does not exist
            while (curr != root && curr.children.get(ch) == null) {
                curr = curr.failLink;
                counter.incrementOperations();
            }

            ACNode next = curr.children.get(ch);
            curr = (next != null) ? next : root;

            // Check if current node has outputs
            if (curr.outputPatterns.size() > 0) {
                for (int o = 0; o < curr.outputPatterns.size(); o++) {
                    String pat = curr.outputPatterns.get(o);
                    int start = i - pat.length() + 1;
                    MatchOccurrence match = new MatchOccurrence(pat, start, i);
                    results.add(match);
                    trace.add("Found occurrence: " + match.toString());
                }
            }
        }

        timer.stop();
        trace.add(String.format("Aho-Corasick search complete: %d occurrences matched across %d operations in %s.",
                results.size(), counter.getOperations(), timer.getFormattedTime()));

        return new ACResult(results, timer.getElapsedNanos(), timer.getFormattedTime(),
                counter.getOperations(), getComplexity(), trace);
    }
}
