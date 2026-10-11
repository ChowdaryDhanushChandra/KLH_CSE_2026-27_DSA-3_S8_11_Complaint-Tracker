package complainttracker.algorithms;

import complainttracker.models.Complaint;
import complainttracker.models.Priority;
import complainttracker.models.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Multi-Criteria Complaint Scheduling Engine.
 * Evaluates pending complaints using weighted scoring of:
 * - Priority Level (w1)
 * - SLA Urgency / Overdue status (w2)
 * - Age / Waiting time to prevent starvation (w3)
 * - Department Load / Escalation (w4)
 */
public class ComplaintScheduler {

    public static class ScheduledItem {
        public final Complaint complaint;
        public final double priorityScore;
        public final double slaScore;
        public final double agingScore;
        public final double totalScore;
        public int rank;
        public final String scoreBreakdown;

        public ScheduledItem(Complaint complaint, double priorityScore, double slaScore,
                             double agingScore, double totalScore, String scoreBreakdown) {
            this.complaint = complaint;
            this.priorityScore = priorityScore;
            this.slaScore = slaScore;
            this.agingScore = agingScore;
            this.totalScore = totalScore;
            this.scoreBreakdown = scoreBreakdown;
        }

        public Complaint getComplaint() { return complaint; }
        public double getTotalScore() { return totalScore; }
        public int getRank() { return rank; }
        public String getScoreBreakdown() { return scoreBreakdown; }
    }

    public static class SchedulePlan {
        public final List<ScheduledItem> items;
        public final CustomSorting.SortMetrics sortMetrics;
        public final String formulaExplanation;

        public SchedulePlan(List<ScheduledItem> items, CustomSorting.SortMetrics sortMetrics, String formulaExplanation) {
            this.items = items;
            this.sortMetrics = sortMetrics;
            this.formulaExplanation = formulaExplanation;
        }
    }

    public static SchedulePlan schedule(List<Complaint> complaints, double wPriority, double wSLA, double wAging) {
        List<ScheduledItem> items = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (Complaint c : complaints) {
            // Only schedule active / non-resolved complaints
            if (c.getStatus() == Status.RESOLVED || c.getStatus() == Status.REJECTED) {
                continue;
            }

            // 1. Priority Base Score
            double pScore;
            if (c.getPriority() == Priority.CRITICAL) pScore = 100.0;
            else if (c.getPriority() == Priority.HIGH) pScore = 75.0;
            else if (c.getPriority() == Priority.MEDIUM) pScore = 50.0;
            else pScore = 25.0;

            // 2. SLA Urgency Score (higher score if overdue or close to breach)
            double slaScore;
            LocalDateTime deadline = c.getSlaDeadline();
            long minutesRemaining = Duration.between(now, deadline).toMinutes();

            if (minutesRemaining <= 0) {
                // Overdue: bonus score proportional to hours overdue (capped at 150)
                long hoursOverdue = Math.abs(minutesRemaining) / 60;
                slaScore = 100.0 + Math.min(50.0, hoursOverdue * 2.0);
            } else {
                long totalSlaMinutes = c.getSlaHours() * 60L;
                double fractionElapsed = (double) (totalSlaMinutes - minutesRemaining) / Math.max(1, totalSlaMinutes);
                slaScore = Math.max(0.0, fractionElapsed * 100.0);
            }

            // 3. Waiting Time / Aging Score (prevent low-priority starvation)
            long waitHours = c.getAgeHours();
            double agingScore = Math.min(100.0, waitHours * 2.5);

            // Escalation factor
            double escalationBonus = c.getEscalationLevel() * 15.0;

            // Weighted Total Score
            double totalScore = (pScore * wPriority) + (slaScore * wSLA) + (agingScore * wAging) + escalationBonus;

            String breakdown = String.format("P:%.0f(x%.2f) + SLA:%.0f(x%.2f) + Age:%.0f(x%.2f) + Esc:%.0f => Total: %.1f",
                    pScore, wPriority, slaScore, wSLA, agingScore, wAging, escalationBonus, totalScore);

            items.add(new ScheduledItem(c, pScore, slaScore, agingScore, totalScore, breakdown));
        }

        // Sort descending by totalScore using custom QuickSort
        CustomSorting.SortMetrics metrics = CustomSorting.quickSort(items, (a, b) -> Double.compare(b.totalScore, a.totalScore));

        // Assign ranks 1..N
        for (int i = 0; i < items.size(); i++) {
            items.get(i).rank = i + 1;
        }

        String explanation = String.format("Score = (Priority * %.2f) + (SLA Urgency * %.2f) + (Waiting Time * %.2f) + Escalation Bonus",
                wPriority, wSLA, wAging);

        return new SchedulePlan(items, metrics, explanation);
    }
}
