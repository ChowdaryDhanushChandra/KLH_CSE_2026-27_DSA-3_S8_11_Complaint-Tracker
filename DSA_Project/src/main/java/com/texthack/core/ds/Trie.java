package com.texthack.core.ds;

/**
 * Hand-built Trie (Prefix Tree) supporting Unicode characters,
 * multi-pattern search, and Aho-Corasick automata links.
 * Strictly avoids java.util.* collections.
 */
public class Trie {
    public static class TrieNode {
        public final int id;
        public final char ch;
        public final int depth;
        public final HashTable<Character, TrieNode> children;
        public boolean isEndOfWord;
        public final DynamicArray<String> outputPatterns;
        public TrieNode failLink;
        public TrieNode dictLink;

        public TrieNode(int id, char ch, int depth) {
            this.id = id;
            this.ch = ch;
            this.depth = depth;
            this.children = new HashTable<>();
            this.isEndOfWord = false;
            this.outputPatterns = new DynamicArray<>();
            this.failLink = null;
            this.dictLink = null;
        }

        public TrieNode getChild(char c) {
            return children.get(c);
        }

        public void putChild(char c, TrieNode node) {
            children.put(c, node);
        }

        public boolean hasChild(char c) {
            return children.containsKey(c);
        }
    }

    private final TrieNode root;
    private int nodeCount;

    public Trie() {
        this.nodeCount = 0;
        this.root = new TrieNode(nodeCount++, '\0', 0);
    }

    public TrieNode getRoot() {
        return root;
    }

    public int getNodeCount() {
        return nodeCount;
    }

    public void insert(String word) {
        if (word == null || word.isEmpty()) return;
        TrieNode curr = root;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            TrieNode child = curr.getChild(c);
            if (child == null) {
                child = new TrieNode(nodeCount++, c, curr.depth + 1);
                curr.putChild(c, child);
            }
            curr = child;
        }
        curr.isEndOfWord = true;
        if (!curr.outputPatterns.contains(word)) {
            curr.outputPatterns.add(word);
        }
    }

    public boolean search(String word) {
        if (word == null) return false;
        TrieNode curr = root;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            curr = curr.getChild(c);
            if (curr == null) return false;
        }
        return curr.isEndOfWord;
    }

    public boolean startsWith(String prefix) {
        if (prefix == null) return false;
        TrieNode curr = root;
        for (int i = 0; i < prefix.length(); i++) {
            char c = prefix.charAt(i);
            curr = curr.getChild(c);
            if (curr == null) return false;
        }
        return true;
    }
}
