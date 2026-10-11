package com.texthack.util;

import com.texthack.datastructures.ManualArrayList;

/**
 * Custom String utilities implemented without using java.util.* or regex.
 */
public final class ManualStringUtil {

    private ManualStringUtil() {}

    /**
     * Splits string by whitespace (spaces, tabs, newlines) manually.
     */
    public static ManualArrayList<String> splitWhitespace(String input) {
        ManualArrayList<String> tokens = new ManualArrayList<>();
        if (input == null || input.length() == 0) {
            return tokens;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
                if (sb.length() > 0) {
                    tokens.add(sb.toString());
                    sb.setLength(0);
                }
            } else {
                sb.append(ch);
            }
        }
        if (sb.length() > 0) {
            tokens.add(sb.toString());
        }
        return tokens;
    }

    /**
     * Splits string by a specific delimiter character.
     */
    public static ManualArrayList<String> split(String input, char delimiter) {
        ManualArrayList<String> tokens = new ManualArrayList<>();
        if (input == null) return tokens;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (ch == delimiter) {
                tokens.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(ch);
            }
        }
        tokens.add(sb.toString());
        return tokens;
    }

    /**
     * Trims leading and trailing whitespace manually.
     */
    public static String trim(String s) {
        if (s == null) return "";
        int start = 0;
        int end = s.length() - 1;
        while (start <= end && s.charAt(start) <= ' ') {
            start++;
        }
        while (end >= start && s.charAt(end) <= ' ') {
            end--;
        }
        return (start > end) ? "" : s.substring(start, end + 1);
    }

    /**
     * Parses integer safely with default value.
     */
    public static int parseInt(String s, int defaultVal) {
        if (s == null) return defaultVal;
        String t = trim(s);
        if (t.isEmpty()) return defaultVal;
        try {
            return Integer.parseInt(t);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    /**
     * Parses long safely with default value.
     */
    public static long parseLong(String s, long defaultVal) {
        if (s == null) return defaultVal;
        String t = trim(s);
        if (t.isEmpty()) return defaultVal;
        try {
            return Long.parseLong(t);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    /**
     * Reverses a string manually.
     */
    public static String reverse(String s) {
        if (s == null) return "";
        char[] chars = new char[s.length()];
        for (int i = 0; i < s.length(); i++) {
            chars[i] = s.charAt(s.length() - 1 - i);
        }
        return new String(chars);
    }
}
