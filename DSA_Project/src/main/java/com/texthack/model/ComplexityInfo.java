package com.texthack.model;

/**
 * Encapsulates theoretical time and space complexity information.
 */
public class ComplexityInfo {
    private final String timeComplexityBest;
    private final String timeComplexityAverage;
    private final String timeComplexityWorst;
    private final String spaceComplexity;
    private final String theoreticalExplanation;

    public ComplexityInfo(String timeBest, String timeAvg, String timeWorst, String space, String explanation) {
        this.timeComplexityBest = timeBest;
        this.timeComplexityAverage = timeAvg;
        this.timeComplexityWorst = timeWorst;
        this.spaceComplexity = space;
        this.theoreticalExplanation = explanation;
    }

    public String getTimeComplexityBest() {
        return timeComplexityBest;
    }

    public String getTimeComplexityAverage() {
        return timeComplexityAverage;
    }

    public String getTimeComplexityWorst() {
        return timeComplexityWorst;
    }

    public String getSpaceComplexity() {
        return spaceComplexity;
    }

    public String getTheoreticalExplanation() {
        return theoreticalExplanation;
    }

    public String getSummary() {
        return "Time: " + timeComplexityWorst + " | Space: " + spaceComplexity;
    }
}
