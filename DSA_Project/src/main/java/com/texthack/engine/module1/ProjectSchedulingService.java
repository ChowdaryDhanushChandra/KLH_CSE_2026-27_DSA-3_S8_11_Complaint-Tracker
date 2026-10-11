package com.texthack.engine.module1;

import com.texthack.datastructures.ManualArrayList;
import com.texthack.graph.CriticalPathCalculator;
import com.texthack.model.ScheduleTask;

/**
 * Service for evaluating project schedules, critical paths, and parallel makespan approximations.
 */
public class ProjectSchedulingService {

    public CriticalPathCalculator.ScheduleResult calculateSchedule(ManualArrayList<ScheduleTask> tasks, int workers) {
        return CriticalPathCalculator.calculate(tasks, workers);
    }
}
