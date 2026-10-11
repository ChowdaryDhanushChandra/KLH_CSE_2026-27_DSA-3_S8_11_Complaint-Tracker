package complainttracker.algorithms;

import complainttracker.models.Complaint;
import java.util.ArrayList;
import java.util.List;

/**
 * Dynamic Programming - Wagner-Fischer Levenshtein Edit Distance
 * Used for intelligent Duplicate Complaint Detection and Text Similarity Analysis.
 */
public class EditDistanceDuplicateDetector {

    public static class DPResult {
        public final String str1;
        public final String str2;
        public final int[][] matrix;
        public final int distance;
        public final double similarityScore; // 0.0 to 100.0
        public final List<String> operations; // Backtracked alignment steps

        public DPResult(String str1, String str2, int[][] matrix, int distance, double similarityScore, List<String> operations) {
            this.str1 = str1;
            this.str2 = str2;
            this.matrix = matrix;
            this.distance = distance;
            this.similarityScore = similarityScore;
            this.operations = operations;
        }
    }

    public static class DuplicateCheckResult {
        public final boolean isDuplicate;
        public final Complaint matchedComplaint;
        public final double similarityScore;
        public final String reason;
        public final DPResult dpResult;

        public DuplicateCheckResult(boolean isDuplicate, Complaint matchedComplaint, double similarityScore, String reason, DPResult dpResult) {
            this.isDuplicate = isDuplicate;
            this.matchedComplaint = matchedComplaint;
            this.similarityScore = similarityScore;
            this.reason = reason;
            this.dpResult = dpResult;
        }

        public static DuplicateCheckResult noDuplicate() {
            return new DuplicateCheckResult(false, null, 0.0, "No duplicate complaint detected.", null);
        }
    }

    /**
     * Compute full DP Wagner-Fischer matrix and backtracked path.
     */
    public static DPResult computeMatrix(String s1, String s2) {
        String a = s1 != null ? s1.trim() : "";
        String b = s2 != null ? s2.trim() : "";

        int m = a.length();
        int n = b.length();

        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;

        for (int i = 1; i <= m; i++) {
            char c1 = Character.toLowerCase(a.charAt(i - 1));
            for (int j = 1; j <= n; j++) {
                char c2 = Character.toLowerCase(b.charAt(j - 1));
                if (c1 == c2) {
                    dp[i][j] = dp[i - 1][j - 1]; // Match
                } else {
                    int insertCost = dp[i][j - 1] + 1;
                    int deleteCost = dp[i - 1][j] + 1;
                    int replaceCost = dp[i - 1][j - 1] + 1;
                    dp[i][j] = Math.min(replaceCost, Math.min(insertCost, deleteCost));
                }
            }
        }

        int distance = dp[m][n];
        int maxLen = Math.max(m, n);
        double similarity = maxLen == 0 ? 100.0 : Math.max(0.0, (1.0 - (double) distance / maxLen) * 100.0);

        // Backtrack path to find specific edit operations
        List<String> ops = new ArrayList<>();
        int i = m, j = n;
        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && Character.toLowerCase(a.charAt(i - 1)) == Character.toLowerCase(b.charAt(j - 1))) {
                ops.add(0, String.format("Keep '%c'", a.charAt(i - 1)));
                i--; j--;
            } else if (i > 0 && j > 0 && dp[i][j] == dp[i - 1][j - 1] + 1) {
                ops.add(0, String.format("Substitute '%c' -> '%c'", a.charAt(i - 1), b.charAt(j - 1)));
                i--; j--;
            } else if (i > 0 && dp[i][j] == dp[i - 1][j] + 1) {
                ops.add(0, String.format("Delete '%c'", a.charAt(i - 1)));
                i--;
            } else if (j > 0 && dp[i][j] == dp[i][j - 1] + 1) {
                ops.add(0, String.format("Insert '%c'", b.charAt(j - 1)));
                j--;
            } else {
                break;
            }
        }

        return new DPResult(a, b, dp, distance, similarity, ops);
    }

    /**
     * Fast distance computation without storing entire trace.
     */
    public static int computeDistance(String s1, String s2) {
        String a = s1 != null ? s1.toLowerCase().trim() : "";
        String b = s2 != null ? s2.toLowerCase().trim() : "";
        int m = a.length();
        int n = b.length();
        if (m == 0) return n;
        if (n == 0) return m;

        int[] prev = new int[n + 1];
        int[] curr = new int[n + 1];

        for (int j = 0; j <= n; j++) prev[j] = j;

        for (int i = 1; i <= m; i++) {
            curr[0] = i;
            for (int j = 1; j <= n; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    curr[j] = prev[j - 1];
                } else {
                    curr[j] = 1 + Math.min(prev[j - 1], Math.min(curr[j - 1], prev[j]));
                }
            }
            System.arraycopy(curr, 0, prev, 0, n + 1);
        }
        return prev[n];
    }

    public static double computeSimilarity(String s1, String s2) {
        int dist = computeDistance(s1, s2);
        int maxLen = Math.max(s1 != null ? s1.trim().length() : 0, s2 != null ? s2.trim().length() : 0);
        if (maxLen == 0) return 100.0;
        return Math.max(0.0, (1.0 - (double) dist / maxLen) * 100.0);
    }

    /**
     * Checks if a complaint is a duplicate of any existing complaint.
     * Evaluates both title similarity and description similarity.
     */
    public static DuplicateCheckResult findDuplicate(Complaint newComplaint, Iterable<Complaint> existingComplaints, double thresholdPercentage) {
        if (newComplaint == null || existingComplaints == null) return DuplicateCheckResult.noDuplicate();

        Complaint bestMatch = null;
        double highestSimilarity = 0.0;
        DPResult bestDpResult = null;
        String matchReason = "";

        for (Complaint existing : existingComplaints) {
            // Don't compare complaint with itself
            if (newComplaint.getId() != null && newComplaint.getId().equals(existing.getId())) {
                continue;
            }

            // Title similarity has highest weight (70% title, 30% description or best of title)
            double titleSim = computeSimilarity(newComplaint.getTitle(), existing.getTitle());
            double descSim = computeSimilarity(newComplaint.getDescription(), existing.getDescription());
            
            // Composite similarity score
            double compositeSim = (titleSim * 0.7) + (descSim * 0.3);
            double effectiveSim = Math.max(titleSim, compositeSim);

            if (effectiveSim > highestSimilarity) {
                highestSimilarity = effectiveSim;
                bestMatch = existing;
                matchReason = String.format("Title similarity: %.1f%%, Description similarity: %.1f%%", titleSim, descSim);
            }
        }

        if (bestMatch != null && highestSimilarity >= thresholdPercentage) {
            bestDpResult = computeMatrix(newComplaint.getTitle(), bestMatch.getTitle());
            return new DuplicateCheckResult(
                    true,
                    bestMatch,
                    highestSimilarity,
                    String.format("Potential duplicate found with ID [%s] (%.1f%% match). %s",
                            bestMatch.getId(), highestSimilarity, matchReason),
                    bestDpResult
            );
        }

        return DuplicateCheckResult.noDuplicate();
    }
}
