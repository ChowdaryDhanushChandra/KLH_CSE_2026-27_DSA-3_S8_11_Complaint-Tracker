package com.texthack.module2_strings;

import com.texthack.core.ds.DynamicArray;
import com.texthack.core.ds.Queue;
import com.texthack.core.ds.Trie;
import com.texthack.core.ds.Trie.TrieNode;
import com.texthack.core.model.AlgoResult;
import com.texthack.core.model.StepEvent;
import com.texthack.core.model.StepEvent.StepType;
import com.texthack.core.util.OpCounter;
import com.texthack.core.util.Timer;

/**
 * Aho-Corasick Multi-Pattern String Matcher - O(n + m + z).
 * Builds Trie with BFS failure transitions and dictionary output links.
 * Enables streaming multi-pattern text search with single-pass scanning.
 * Pure Java engine, strictly zero java.util.* collections.
 */
public class AhoCorasickMatcher {

    public static class MatchOccurrence {
        private final String pattern;
        private final int startIndex;
        private final int endIndex;

        public MatchOccurrence(String pattern, int startIndex, int endIndex) {
            this.pattern = pattern;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        public String getPattern() { return pattern; }
        public int getStartIndex() { return startIndex; }
        public int getEndIndex() { return endIndex; }

        @Override
        public String toString() {
            return "\"" + pattern + "\" at [" + startIndex + ".." + endIndex + "]";
        }
    }

    public static class AhoCorasickResult {
        private final DynamicArray<MatchOccurrence> matches;
        private final Trie trie;

        public AhoCorasickResult(DynamicArray<MatchOccurrence> matches, Trie trie) {
            this.matches = matches;
            this.trie = trie;
        }

        public DynamicArray<MatchOccurrence> getMatches() { return matches; }
        public Trie getTrie() { return trie; }
    }

    public static final String[] PSEUDOCODE = {
        "buildTrie(patterns)  // Insert patterns into trie",
        "buildFailureLinks()  // BFS compute failure and dict links",
        "curr = root",
        "for i = 0 to n - 1 do",
        "    while curr != root and not curr.hasChild(text[i]) do",
        "        curr = curr.failLink  // Follow failure link",
        "    if curr.hasChild(text[i]) then curr = curr.getChild(text[i])",
        "    traverse dictLink and report all matched patterns ending at i"
    };

    public AlgoResult<AhoCorasickResult> search(String text, DynamicArray<String> patterns) {
        Timer timer = new Timer();
        OpCounter opCounter = new OpCounter();
        DynamicArray<StepEvent> steps = new DynamicArray<>();
        DynamicArray<MatchOccurrence> matches = new DynamicArray<>();

        timer.start();

        if (text == null || patterns == null || patterns.isEmpty()) {
            timer.stop();
            Trie emptyTrie = new Trie();
            steps.add(StepEvent.simple(1, 1, "Input validation complete: text or patterns empty.", StepType.COMPLETE));
            return new AlgoResult<>(new AhoCorasickResult(matches, emptyTrie), steps, timer.getElapsedMicros(), 0, 0, "O(n + m + z)", "O(m · Σ)");
        }

        int stepNum = 1;
        Trie trie = new Trie();

        // 1. Build Trie
        steps.add(StepEvent.simple(stepNum++, 1,
                "Phase 1: Building Trie prefix tree for " + patterns.size() + " patterns...", StepType.START));
        for (int p = 0; p < patterns.size(); p++) {
            String pat = patterns.get(p);
            trie.insert(pat);
            steps.add(StepEvent.simple(stepNum++, 1, "Inserted pattern \"" + pat + "\" into Trie.", StepType.TABLE_UPDATE));
        }

        // 2. Build Failure Links using BFS
        steps.add(StepEvent.simple(stepNum++, 2,
                "Phase 2: Computing BFS Failure Links and Dictionary Links across Trie nodes...", StepType.START));
        buildFailureLinks(trie, steps, stepNum);
        stepNum = steps.size() + 1;

        // 3. Scan Text
        steps.add(StepEvent.simple(stepNum++, 3,
                "Phase 3: Scanning text (Length: " + text.length() + ") with Aho-Corasick automaton...", StepType.START));

        TrieNode curr = trie.getRoot();
        int n = text.length();

        for (int i = 0; i < n; i++) {
            char c = text.charAt(i);
            opCounter.incrementComparisons();

            steps.add(new StepEvent(stepNum++, 4,
                    "Reading text[" + i + "] = '" + c + "'. Current Trie Node: #" + curr.id + " ('" + curr.ch + "')",
                    StepType.TRIE_VISIT, i, curr.id, i, i, curr.id));

            while (curr != trie.getRoot() && !curr.hasChild(c)) {
                opCounter.incrementComparisons();
                TrieNode old = curr;
                curr = curr.failLink;
                steps.add(new StepEvent(stepNum++, 6,
                        "No edge for '" + c + "' from Node #" + old.id + ". Following failure link to Node #" + curr.id,
                        StepType.FAILURE_LINK_FOLLOWED, i, curr.id, i, i, curr.id));
            }

            if (curr.hasChild(c)) {
                curr = curr.getChild(c);
                steps.add(new StepEvent(stepNum++, 7,
                        "Traversing edge '" + c + "' to Node #" + curr.id,
                        StepType.TRIE_VISIT, i, curr.id, i, i, curr.id));
            } else {
                curr = trie.getRoot();
            }

            // Check matches at curr and follow dictLink
            TrieNode temp = curr;
            while (temp != null && temp != trie.getRoot()) {
                if (temp.isEndOfWord) {
                    for (int m = 0; m < temp.outputPatterns.size(); m++) {
                        String matchedPat = temp.outputPatterns.get(m);
                        int startIdx = i - matchedPat.length() + 1;
                        matches.add(new MatchOccurrence(matchedPat, startIdx, i));
                        steps.add(new StepEvent(stepNum++, 8,
                                "Match found: \"" + matchedPat + "\" at text indices [" + startIdx + ".." + i + "]",
                                StepType.PATTERN_FOUND, i, temp.id, startIdx, i, matchedPat));
                    }
                }
                temp = temp.dictLink;
            }
        }

        timer.stop();
        steps.add(StepEvent.simple(stepNum++, 8,
                "Aho-Corasick matching finished. Total occurrences: " + matches.size(), StepType.COMPLETE));

        long memoryEstimate = (long) trie.getNodeCount() * 128 + (long) matches.size() * 32 + 256;
        return new AlgoResult<>(new AhoCorasickResult(matches, tririeResultWrapper(trie)), steps,
                timer.getElapsedMicros(), opCounter.getComparisons(), memoryEstimate, "O(n + m + z)", "O(m · Σ)");
    }

    private Trie tririeResultWrapper(Trie trie) {
        return trie;
    }

    private void buildFailureLinks(Trie trie, DynamicArray<StepEvent> steps, int startStep) {
        Queue<TrieNode> queue = new Queue<>();
        TrieNode root = trie.getRoot();
        root.failLink = root;
        int stepNum = startStep;

        DynamicArray<TrieNode> rootChildren = root.children.values();
        for (int i = 0; i < rootChildren.size(); i++) {
            TrieNode child = rootChildren.get(i);
            child.failLink = root;
            queue.enqueue(child);
            steps.add(new StepEvent(stepNum++, 2,
                    "Root child Node #" + child.id + " ('" + child.ch + "') failure link set to root.",
                    StepType.TABLE_UPDATE, -1, child.id, 0, 0, null));
        }

        while (!queue.isEmpty()) {
            TrieNode curr = queue.dequeue();
            DynamicArray<Character> keys = curr.children.keys();

            for (int k = 0; k < keys.size(); k++) {
                char c = keys.get(k);
                TrieNode child = curr.getChild(c);

                TrieNode f = curr.failLink;
                while (f != root && !f.hasChild(c)) {
                    f = f.failLink;
                }

                if (f.hasChild(c) && f.getChild(c) != child) {
                    child.failLink = f.getChild(c);
                } else {
                    child.failLink = root;
                }

                // Setup dictionary output link
                if (child.failLink.isEndOfWord) {
                    child.dictLink = child.failLink;
                } else {
                    child.dictLink = child.failLink.dictLink;
                }

                steps.add(new StepEvent(stepNum++, 2,
                        "Node #" + child.id + " ('" + child.ch + "') linked: failLink -> Node #" + child.failLink.id +
                        (child.dictLink != null ? ", dictLink -> Node #" + child.dictLink.id : ""),
                        StepType.TABLE_UPDATE, curr.id, child.id, 0, 0, null));

                queue.enqueue(child);
            }
        }
    }
}
