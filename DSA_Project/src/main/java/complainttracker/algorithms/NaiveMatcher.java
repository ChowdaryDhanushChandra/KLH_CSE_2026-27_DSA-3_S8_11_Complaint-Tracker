package complainttracker.algorithms;

import java.util.ArrayList;
import java.util.List;

/**
 * Naive (Brute-Force) String Matching Algorithm.
 * Time: O(N * M)
 * Space: O(1)
 */
public class NaiveMatcher {

    public static StringMatchResult search(String text, String pattern) {
        long startTime = System.nanoTime();
        List<Integer> matches = new ArrayList<>();
        List<String> traces = new ArrayList<>();
        int comparisons = 0;

        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return new StringMatchResult("Naive Brute-Force", matches, 0,
                    System.nanoTime() - startTime, traces, new int[0]);
        }

        int n = text.length();
        int m = pattern.length();

        for (int i = 0; i <= n - m; i++) {
            boolean match = true;
            for (int j = 0; j < m; j++) {
                comparisons++;
                if (text.charAt(i + j) != pattern.charAt(j)) {
                    match = false;
                    break;
                }
            }
            if (match) {
                matches.add(i);
                traces.add(String.format("Match at index %d after checking window '%s'", i, text.substring(i, i + m)));
            }
        }

        long elapsed = System.nanoTime() - startTime;
        return new StringMatchResult("Naive Brute-Force", matches, comparisons, elapsed, traces, new int[0]);
    }
}
