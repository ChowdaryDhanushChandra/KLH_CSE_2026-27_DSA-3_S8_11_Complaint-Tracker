package com.texthack.core.model;

/**
 * Model representing a multilingual corpus text (English, Hindi, Telugu).
 */
public class Corpus {
    public enum Language {
        ENGLISH("English", "en"),
        HINDI("हिंदी (Hindi)", "hi"),
        TELUGU("తెలుగు (Telugu)", "te");

        private final String displayName;
        private final String code;

        Language(String displayName, String code) {
            this.displayName = displayName;
            this.code = code;
        }

        public String getDisplayName() { return displayName; }
        public String getCode() { return code; }
    }

    private final Language language;
    private final String title;
    private final String content;
    private final int characterCount;
    private final int wordCount;

    public Corpus(Language language, String title, String content) {
        this.language = language;
        this.title = title;
        this.content = content != null ? content : "";
        this.characterCount = this.content.length();
        this.wordCount = calculateWordCount(this.content);
    }

    public Language getLanguage() { return language; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public int getCharacterCount() { return characterCount; }
    public int getWordCount() { return wordCount; }

    private static int calculateWordCount(String s) {
        if (s == null || s.isEmpty()) return 0;
        int count = 0;
        boolean inWord = false;
        for (int i = 0; i < s.length(); i++) {
            if (Character.isWhitespace(s.charAt(i))) {
                inWord = false;
            } else if (!inWord) {
                inWord = true;
                count++;
            }
        }
        return count;
    }
}
