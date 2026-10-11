package com.texthack.core.model;

/**
 * Represents a single atomic execution step in an algorithm trace.
 * Powers the step-by-step UI animation, pseudocode line highlighting,
 * and state inspector.
 */
public class StepEvent {
    public enum StepType {
        START,
        COMPARE_MATCH,
        COMPARE_MISMATCH,
        SHIFT_WINDOW,
        TABLE_UPDATE,
        HASH_CALCULATED,
        COLLISION_DETECTED,
        TRIE_VISIT,
        FAILURE_LINK_FOLLOWED,
        PATTERN_FOUND,
        COMPLETE
    }

    private final int stepNumber;
    private final int codeLine;
    private final String description;
    private final StepType type;
    private final int primaryIndex;    // e.g. text index i
    private final int secondaryIndex;  // e.g. pattern index j
    private final int rangeStart;      // e.g. window start L
    private final int rangeEnd;        // e.g. window end R
    private final Object stateSnapshot;// optional snapshot of table, hash, or trie state

    public StepEvent(int stepNumber, int codeLine, String description, StepType type,
                     int primaryIndex, int secondaryIndex, int rangeStart, int rangeEnd,
                     Object stateSnapshot) {
        this.stepNumber = stepNumber;
        this.codeLine = codeLine;
        this.description = description;
        this.type = type;
        this.primaryIndex = primaryIndex;
        this.secondaryIndex = secondaryIndex;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.stateSnapshot = stateSnapshot;
    }

    public static StepEvent simple(int stepNumber, int codeLine, String description, StepType type) {
        return new StepEvent(stepNumber, codeLine, description, type, -1, -1, -1, -1, null);
    }

    public static StepEvent compare(int stepNumber, int codeLine, String description, boolean match,
                                    int textIdx, int patternIdx) {
        return new StepEvent(stepNumber, codeLine, description,
                match ? StepType.COMPARE_MATCH : StepType.COMPARE_MISMATCH,
                textIdx, patternIdx, -1, -1, null);
    }

    public int getStepNumber() { return stepNumber; }
    public int getCodeLine() { return codeLine; }
    public String getDescription() { return description; }
    public StepType getType() { return type; }
    public int getPrimaryIndex() { return primaryIndex; }
    public int getSecondaryIndex() { return secondaryIndex; }
    public int getRangeStart() { return rangeStart; }
    public int getRangeEnd() { return rangeEnd; }
    public Object getStateSnapshot() { return stateSnapshot; }

    @Override
    public String toString() {
        return "Step #" + stepNumber + " [Line " + codeLine + "] (" + type + "): " + description;
    }
}
