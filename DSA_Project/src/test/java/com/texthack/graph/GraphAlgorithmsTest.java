package com.texthack.graph;

import com.texthack.datastructures.ManualArrayList;
import com.texthack.model.ScheduleTask;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test suite for Graph Algorithms: DirectedGraph, Edmonds-Karp Max-Flow, and CPM.
 */
public class GraphAlgorithmsTest {

    @Test
    @DisplayName("DirectedGraph: Topological Sort on valid DAG")
    void testTopologicalSortValid() {
        DirectedGraph g = new DirectedGraph(4);
        g.addEdge(0, 1, 1);
        g.addEdge(0, 2, 1);
        g.addEdge(1, 3, 1);
        g.addEdge(2, 3, 1);

        ManualArrayList<Integer> order = g.topologicalSort();
        assertEquals(4, order.size());
        assertEquals(0, order.get(0));
        assertEquals(3, order.get(3));
    }

    @Test
    @DisplayName("DirectedGraph: Cycle detection returns empty topological order")
    void testTopologicalSortCycle() {
        DirectedGraph g = new DirectedGraph(3);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 0, 1); // Cycle 0 -> 1 -> 2 -> 0

        ManualArrayList<Integer> order = g.topologicalSort();
        assertTrue(order.isEmpty());
    }

    @Test
    @DisplayName("Edmonds-Karp: Classical 4-node flow network")
    void testEdmondsKarpMaxFlow() {
        MaxFlowNetwork net = new MaxFlowNetwork(4);
        // Source 0, Sink 3
        net.addEdge(0, 1, 10);
        net.addEdge(0, 2, 10);
        net.addEdge(1, 2, 2);
        net.addEdge(1, 3, 8);
        net.addEdge(2, 3, 9);

        EdmondsKarp.MaxFlowResult res = EdmondsKarp.computeMaxFlow(net, 0, 3);
        // Maximum flow: 0->1->3 (8), 0->1->2->3 (1), 0->2->3 (8) = 17
        assertEquals(17, res.maxFlow);
        assertTrue(res.augmentations >= 2);
    }

    @Test
    @DisplayName("Edmonds-Karp: Disconnected graph and zero capacity")
    void testEdmondsKarpDisconnectedAndZero() {
        MaxFlowNetwork net = new MaxFlowNetwork(4);
        net.addEdge(0, 1, 0); // Zero capacity
        net.addEdge(2, 3, 10); // Disconnected component

        EdmondsKarp.MaxFlowResult res = EdmondsKarp.computeMaxFlow(net, 0, 3);
        assertEquals(0, res.maxFlow);
    }

    @Test
    @DisplayName("CriticalPathCalculator: CPM forward/backward passes and critical path identification")
    void testCriticalPathCalculation() {
        ManualArrayList<ScheduleTask> tasks = new ManualArrayList<>();
        ScheduleTask a = new ScheduleTask(0, "A", 3);
        ScheduleTask b = new ScheduleTask(1, "B", 4);
        b.addDependency(0);
        ScheduleTask c = new ScheduleTask(2, "C", 2);
        c.addDependency(0);
        ScheduleTask d = new ScheduleTask(3, "D", 5);
        d.addDependency(1);
        d.addDependency(2);

        tasks.add(a);
        tasks.add(b);
        tasks.add(c);
        tasks.add(d);

        CriticalPathCalculator.ScheduleResult res = CriticalPathCalculator.calculate(tasks, 2);

        // Path A(3) -> B(4) -> D(5) = 12
        // Path A(3) -> C(2) -> D(5) = 10
        assertEquals(12, res.projectDuration);
        assertTrue(tasks.get(0).isCritical()); // A
        assertTrue(tasks.get(1).isCritical()); // B
        assertFalse(tasks.get(2).isCritical()); // C has slack 2
        assertEquals(2, tasks.get(2).getSlack());
        assertTrue(tasks.get(3).isCritical()); // D
    }
}
