package com.texthack.model;

/**
 * Model representing a text document in the corpus.
 */
public class Document {
    private final String id;
    private final String title;
    private final String content;
    private final String language;
    private final int characterCount;
    private final int wordCount;

    public Document(String id, String title, String content, String language) {
        this.id = id;
        this.title = title;
        this.content = content != null ? content : "";
        this.language = language;
        this.characterCount = this.content.length();
        this.wordCount = calculateWordCount(this.content);
    }

    private int calculateWordCount(String text) {
        int words = 0;
        boolean inWord = false;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch <= ' ') {
                inWord = false;
            } else if (!inWord) {
                inWord = true;
                words++;
            }
        }
        return words;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getLanguage() {
        return language;
    }

    public int getCharacterCount() {
        return characterCount;
    }

    public int getWordCount() {
        return wordCount;
    }

    @Override
    public String toString() {
        return title + " (" + language + ", " + characterCount + " chars)";
    }
}
