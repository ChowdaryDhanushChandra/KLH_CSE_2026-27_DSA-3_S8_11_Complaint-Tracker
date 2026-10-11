package com.texthack.model;

import com.texthack.datastructures.ManualArrayList;

/**
 * Model representing a project task in Critical Path Method (CPM) scheduling.
 */
public class ScheduleTask {
    private final int id;
    private final String name;
    private final int duration;
    private final ManualArrayList<Integer> dependencies;

    private int earlyStart;
    private int earlyFinish;
    private int lateStart;
    private int lateFinish;
    private int slack;
    private boolean isCritical;

    public ScheduleTask(int id, String name, int duration) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.dependencies = new ManualArrayList<>();
    }

    public void addDependency(int depId) {
        dependencies.add(depId);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    public ManualArrayList<Integer> getDependencies() {
        return dependencies;
    }

    public int getEarlyStart() {
        return earlyStart;
    }

    public void setEarlyStart(int earlyStart) {
        this.earlyStart = earlyStart;
    }

    public int getEarlyFinish() {
        return earlyFinish;
    }

    public void setEarlyFinish(int earlyFinish) {
        this.earlyFinish = earlyFinish;
    }

    public int getLateStart() {
        return lateStart;
    }

    public void setLateStart(int lateStart) {
        this.lateStart = lateStart;
    }

    public int getLateFinish() {
        return lateFinish;
    }

    public void setLateFinish(int lateFinish) {
        this.lateFinish = lateFinish;
    }

    public int getSlack() {
        return slack;
    }

    public void setSlack(int slack) {
        this.slack = slack;
    }

    public boolean isCritical() {
        return isCritical;
    }

    public void setCritical(boolean critical) {
        isCritical = critical;
    }

    public String getDependenciesFormatted() {
        if (dependencies.isEmpty()) return "None";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < dependencies.size(); i++) {
            sb.append(dependencies.get(i));
            if (i < dependencies.size() - 1) sb.append(", ");
        }
        return sb.toString();
    }
}
