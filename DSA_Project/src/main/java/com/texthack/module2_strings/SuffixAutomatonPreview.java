package com.texthack.module2_strings;

import com.texthack.core.ds.DynamicArray;
import com.texthack.core.ds.HashTable;
import com.texthack.core.model.AlgoResult;
import com.texthack.core.model.StepEvent;
import com.texthack.core.model.StepEvent.StepType;
import com.texthack.core.util.OpCounter;
import com.texthack.core.util.Timer;

/**
 * Suffix Automaton (DAWG - Directed Acyclic Word Graph) - Linear O(n) construction.
 * Compact finite automaton representing all suffixes and substrings of a string in O(n) states.
 * Pure Java engine, strictly zero java.util.* collections.
 */
public class SuffixAutomatonPreview {

    public static class SAMState {
        public final int id;
        public int len;
        public int link;
        public final HashTable<Character, Integer> next;
        public boolean isClone;

        public SAMState(int id, int len, int link, boolean isClone) {
            this.id = id;
            this.len = len;
            this.link = link;
            this.isClone = isClone;
            this.next = new HashTable<>();
        }
    }

    public static class SAMResult {
        private final DynamicArray<SAMState> states;
        private final String input;

        public SAMResult(DynamicArray<SAMState> states, String input) {
            this.states = states;
            this.input = input;
        }

        public DynamicArray<SAMState> getStates() { return states; }
        public String getInput() { return input; }
        public int getStateCount() { return states.size(); }
    }

    public static final String[] PSEUDOCODE = {
        "init: create root state 0 (len=0, link=-1); last = 0",
        "for each char c in text do",
        "    cur = newState(len = len[last] + 1)",
        "    p = last; while p != -1 and not next[p][c] do next[p][c] = cur; p = link[p]",
        "    if p == -1 then link[cur] = 0",
        "    else q = next[p][c]",
        "        if len[p] + 1 == len[q] then link[cur] = q",
        "        else clone = cloneState(q, len = len[p] + 1); redirect links to clone; link[cur] = link[q] = clone",
        "    last = cur"
    };

    public AlgoResult<SAMResult> buildAutomaton(String text) {
        Timer timer = new Timer();
        OpCounter opCounter = new OpCounter();
        DynamicArray<StepEvent> steps = new DynamicArray<>();
        DynamicArray<SAMState> states = new DynamicArray<>();

        timer.start();

        if (text == null || text.isEmpty()) {
            timer.stop();
            steps.add(StepEvent.simple(1, 1, "Input text is empty.", StepType.COMPLETE));
            return new AlgoResult<>(new SAMResult(states, ""), steps, timer.getElapsedMicros(), 0, 0, "O(n · Σ)", "O(n · Σ)");
        }

        int stepNum = 1;
        // State 0: Root
        states.add(new SAMState(0, 0, -1, false));
        int last = 0;

        steps.add(StepEvent.simple(stepNum++, 1,
                "Initialized Suffix Automaton: Root State #0 (len=0, link=-1).",
                StepType.START));

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            int cur = states.size();
            SAMState curState = new SAMState(cur, states.get(last).len + 1, -1, false);
            states.add(curState);

            steps.add(new StepEvent(stepNum++, 3,
                    "Reading text[" + i + "] = '" + c + "'. Created new State #" + cur +
                    " (len=" + curState.len + ")",
                    StepType.START, i, cur, i, i, cur));

            int p = last;
            while (p != -1 && !states.get(p).next.containsKey(c)) {
                opCounter.incrementArrayAccesses();
                states.get(p).next.put(c, cur);
                steps.add(new StepEvent(stepNum++, 4,
                        "Added transition from State #" + p + " via '" + c + "' -> State #" + cur,
                        StepType.TABLE_UPDATE, p, cur, 0, 0, cur));
                p = states.get(p).link;
            }

            if (p == -1) {
                curState.link = 0;
                steps.add(new StepEvent(stepNum++, 5,
                        "No ancestor had transition on '" + c + "'. Set suffix link of State #" + cur + " -> Root (State #0)",
                        StepType.FAILURE_LINK_FOLLOWED, cur, 0, 0, 0, cur));
            } else {
                int q = states.get(p).next.get(c);
                if (states.get(p).len + 1 == states.get(q).len) {
                    curState.link = q;
                    steps.add(new StepEvent(stepNum++, 7,
                            "Continuous transition found: len[p] + 1 == len[q] (" + states.get(q).len + "). Set link of State #" + cur + " -> State #" + q,
                            StepType.FAILURE_LINK_FOLLOWED, cur, q, 0, 0, cur));
                } else {
                    int clone = states.size();
                    SAMState qState = states.get(q);
                    SAMState cloneState = new SAMState(clone, states.get(p).len + 1, qState.link, true);

                    // Copy transitions from q to clone
                    DynamicArray<Character> keys = qState.next.keys();
                    for (int k = 0; k < keys.size(); k++) {
                        char chKey = keys.get(k);
                        cloneState.next.put(chKey, qState.next.get(chKey));
                    }
                    states.add(cloneState);

                    steps.add(new StepEvent(stepNum++, 8,
                            "Split required: Cloned State #" + q + " into Clone State #" + clone +
                            " (len=" + cloneState.len + ", link=" + cloneState.link + ")",
                            StepType.TABLE_UPDATE, q, clone, 0, 0, clone));

                    while (p != -1 && states.get(p).next.get(c) != null && states.get(p).next.get(c) == q) {
                        opCounter.incrementArrayAccesses();
                        states.get(p).next.put(c, clone);
                        steps.add(new StepEvent(stepNum++, 8,
                                "Redirected transition from State #" + p + " via '" + c + "' -> Clone State #" + clone,
                                StepType.TABLE_UPDATE, p, clone, 0, 0, clone));
                        p = states.get(p).link;
                    }

                    qState.link = clone;
                    curState.link = clone;
                    steps.add(new StepEvent(stepNum++, 8,
                            "Updated suffix links: State #" + q + " and State #" + cur + " now point to Clone #" + clone,
                            StepType.FAILURE_LINK_FOLLOWED, cur, clone, 0, 0, clone));
                }
            }
            last = cur;
        }

        timer.stop();
        steps.add(StepEvent.simple(stepNum++, 9,
                "Suffix Automaton Construction Complete. Total States: " + states.size() +
                " (<= 2n-1 = " + (2 * text.length() - 1) + ")",
                StepType.COMPLETE));

        long memoryEstimate = (long) states.size() * 128 + 256;
        return new AlgoResult<>(new SAMResult(states, text), steps, timer.getElapsedMicros(),
                opCounter.getComparisons(), memoryEstimate, "O(n · Σ)", "O(n · Σ)");
    }
}
