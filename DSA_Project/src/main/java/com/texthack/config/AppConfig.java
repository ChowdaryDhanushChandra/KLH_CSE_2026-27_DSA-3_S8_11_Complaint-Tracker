package com.texthack.config;

/**
 * Global application constants and configuration.
 */
public final class AppConfig {
    public static final String APP_TITLE = "TextHack – Advanced Algorithm Question Bank";
    public static final String APP_VERSION = "1.0.0";
    public static final String COURSE_NAME = "Data Structures and Algorithms – 3";
    public static final String[] TEAM_MEMBERS = {
            "B Surya Prakash Reddy",
            "CH Meghanad Sai Reddy",
            "C Dhanush Chandra"
    };

    public static final String CORPUS_ENGLISH_PATH = "/corpus/sample_english.txt";
    public static final String CORPUS_HINDI_PATH = "/corpus/sample_hindi.txt";
    public static final String CORPUS_TELUGU_PATH = "/corpus/sample_telugu.txt";

    public static final String CSS_STYLE_PATH = "/styles/application.css";

    private AppConfig() {}
}
