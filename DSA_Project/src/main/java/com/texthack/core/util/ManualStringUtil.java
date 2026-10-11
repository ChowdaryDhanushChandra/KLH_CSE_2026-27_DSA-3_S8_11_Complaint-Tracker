package com.texthack.core.util;

import com.texthack.core.ds.DynamicArray;

/**
 * Hand-built string utilities replacing regex and java.util.Scanner / StringTokenizer.
 * Completely zero java.util.* dependency.
 */
public class ManualStringUtil {

    public static DynamicArray<String> split(String str, char delimiter) {
        DynamicArray<String> tokens = new DynamicArray<>();
        if (str == null) return tokens;

        int len = str.length();
        int start = 0;
        for (int i = 0; i < len; i++) {
            if (str.charAt(i) == delimiter) {
                tokens.add(str.substring(start, i));
                start = i + 1;
            }
        }
        if (start <= len) {
            tokens.add(str.substring(start, len));
        }
        return tokens;
    }

    public static DynamicArray<String> splitWhitespace(String str) {
        DynamicArray<String> tokens = new DynamicArray<>();
        if (str == null) return tokens;

        int len = str.length();
        int start = -1;
        for (int i = 0; i < len; i++) {
            if (Character.isWhitespace(str.charAt(i))) {
                if (start != -1) {
                    tokens.add(str.substring(start, i));
                    start = -1;
                }
            } else {
                if (start == -1) {
                    start = i;
                }
            }
        }
        if (start != -1) {
            tokens.add(str.substring(start, len));
        }
        return tokens;
    }

    public static String trim(String str) {
        if (str == null) return "";
        int start = 0;
        int end = str.length() - 1;
        while (start <= end && Character.isWhitespace(str.charAt(start))) {
            start++;
        }
        while (end >= start && Character.isWhitespace(str.charAt(end))) {
            end--;
        }
        return (start <= end) ? str.substring(start, end + 1) : "";
    }

    public static int parsePositiveInt(String s, int defaultValue) {
        if (s == null) return defaultValue;
        String t = trim(s);
        if (t.isEmpty()) return defaultValue;
        long val = 0;
        for (int i = 0; i < t.length(); i++) {
            char c = t.charAt(i);
            if (c < '0' || c > '9') return defaultValue;
            val = val * 10 + (c - '0');
            if (val > Integer.MAX_VALUE) return defaultValue;
        }
        return (int) val;
    }
}
