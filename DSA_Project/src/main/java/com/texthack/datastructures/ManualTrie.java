package com.texthack.datastructures;

/**
 * Custom Trie (Prefix Tree) data structure implemented without java.util.*.
 * Uses a ManualHashMap for character transitions to support full Unicode character sets.
 */
public class ManualTrie {

    public static class TrieNode {
        public final ManualHashMap<Character, TrieNode> children;
        public boolean isEndOfWord;
        public String word;
        public int patternIndex;

        public TrieNode() {
            this.children = new ManualHashMap<>(8);
            this.isEndOfWord = false;
            this.word = null;
            this.patternIndex = -1;
        }
    }

    private final TrieNode root;
    private int wordCount;

    public ManualTrie() {
        this.root = new TrieNode();
        this.wordCount = 0;
    }

    public TrieNode getRoot() {
        return root;
    }

    public void insert(String word) {
        insert(word, wordCount);
    }

    public void insert(String word, int patternIndex) {
        if (word == null || word.length() == 0) return;
        TrieNode current = root;
        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            TrieNode child = current.children.get(ch);
            if (child == null) {
                child = new TrieNode();
                current.children.put(ch, child);
            }
            current = child;
        }
        if (!current.isEndOfWord) {
            current.isEndOfWord = true;
            wordCount++;
        }
        current.word = word;
        current.patternIndex = patternIndex;
    }

    public boolean search(String word) {
        if (word == null || word.length() == 0) return false;
        TrieNode node = findNode(word);
        return node != null && node.isEndOfWord;
    }

    public boolean startsWith(String prefix) {
        if (prefix == null || prefix.length() == 0) return true;
        return findNode(prefix) != null;
    }

    public TrieNode findNode(String prefix) {
        TrieNode current = root;
        for (int i = 0; i < prefix.length(); i++) {
            char ch = prefix.charAt(i);
            current = current.children.get(ch);
            if (current == null) {
                return null;
            }
        }
        return current;
    }

    public ManualArrayList<String> getWordsWithPrefix(String prefix) {
        ManualArrayList<String> results = new ManualArrayList<>();
        TrieNode node = findNode(prefix);
        if (node != null) {
            collectWords(node, results);
        }
        return results;
    }

    private void collectWords(TrieNode node, ManualArrayList<String> list) {
        if (node == null) return;
        if (node.isEndOfWord && node.word != null) {
            list.add(node.word);
        }
        ManualArrayList<Character> keys = node.children.keys();
        for (int i = 0; i < keys.size(); i++) {
            collectWords(node.children.get(keys.get(i)), list);
        }
    }

    public int getWordCount() {
        return wordCount;
    }

    public void clear() {
        root.children.clear();
        wordCount = 0;
    }
}
