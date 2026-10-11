package com.texthack.datastructures;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test suite verifying manual data structures without java.util.*.
 */
public class ManualDataStructuresTest {

    @Test
    @DisplayName("ManualArrayList: Dynamic resizing, insertion, removal, bounds")
    void testManualArrayList() {
        ManualArrayList<String> list = new ManualArrayList<>(2);
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());

        list.add("Alpha");
        list.add("Beta");
        list.add("Gamma"); // Trigger dynamic resize

        assertEquals(3, list.size());
        assertEquals("Alpha", list.get(0));
        assertEquals("Beta", list.get(1));
        assertEquals("Gamma", list.get(2));

        list.add(1, "Inserted");
        assertEquals(4, list.size());
        assertEquals("Inserted", list.get(1));
        assertEquals("Beta", list.get(2));

        String removed = list.remove(1);
        assertEquals("Inserted", removed);
        assertEquals(3, list.size());

        assertTrue(list.contains("Alpha"));
        assertFalse(list.contains("Delta"));

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(10));
    }

    @Test
    @DisplayName("ManualStack: LIFO semantics, empty check, underflow")
    void testManualStack() {
        ManualStack<Integer> stack = new ManualStack<>();
        assertTrue(stack.isEmpty());

        stack.push(10);
        stack.push(20);
        stack.push(30);

        assertEquals(30, stack.peek());
        assertEquals(30, stack.pop());
        assertEquals(20, stack.pop());
        assertEquals(10, stack.pop());
        assertTrue(stack.isEmpty());

        assertThrows(IllegalStateException.class, stack::pop);
    }

    @Test
    @DisplayName("ManualQueue: FIFO circular buffer semantics, resizing")
    void testManualQueue() {
        ManualQueue<String> queue = new ManualQueue<>(4);
        assertTrue(queue.isEmpty());

        queue.enqueue("A");
        queue.enqueue("B");
        queue.enqueue("C");

        assertEquals("A", queue.peek());
        assertEquals("A", queue.dequeue());
        assertEquals("B", queue.dequeue());

        queue.enqueue("D");
        queue.enqueue("E");
        queue.enqueue("F"); // Circular wrap and resize

        assertEquals("C", queue.dequeue());
        assertEquals("D", queue.dequeue());
        assertEquals("E", queue.dequeue());
        assertEquals("F", queue.dequeue());
        assertTrue(queue.isEmpty());

        assertThrows(IllegalStateException.class, queue::dequeue);
    }

    @Test
    @DisplayName("ManualDeque: Double-ended operations")
    void testManualDeque() {
        ManualDeque<Integer> deque = new ManualDeque<>();
        deque.addFirst(1);
        deque.addLast(2);
        deque.addFirst(0);

        assertEquals(3, deque.size());
        assertEquals(0, deque.peekFirst());
        assertEquals(2, deque.peekLast());

        assertEquals(0, deque.pollFirst());
        assertEquals(2, deque.pollLast());
        assertEquals(1, deque.pollFirst());
        assertTrue(deque.isEmpty());
    }

    @Test
    @DisplayName("ManualHashMap: Put, get, collision handling, keys/values extraction")
    void testManualHashMap() {
        ManualHashMap<String, Integer> map = new ManualHashMap<>(4);
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);
        map.put("four", 4);

        assertEquals(4, map.size());
        assertEquals(1, map.get("one"));
        assertEquals(4, map.get("four"));
        assertNull(map.get("five"));

        // Overwrite
        map.put("one", 100);
        assertEquals(100, map.get("one"));

        // Remove
        assertEquals(2, map.remove("two"));
        assertEquals(3, map.size());
        assertFalse(map.containsKey("two"));

        ManualArrayList<String> keys = map.keys();
        assertEquals(3, keys.size());
    }

    @Test
    @DisplayName("ManualMinHeap & ManualPriorityQueue: Min-heap invariant")
    void testManualPriorityQueue() {
        ManualPriorityQueue<Integer> pq = new ManualPriorityQueue<>(Integer::compare);
        pq.add(40);
        pq.add(10);
        pq.add(30);
        pq.add(5);
        pq.add(20);

        assertEquals(5, pq.poll());
        assertEquals(10, pq.poll());
        assertEquals(20, pq.poll());
        assertEquals(30, pq.poll());
        assertEquals(40, pq.poll());
        assertNull(pq.poll());
    }

    @Test
    @DisplayName("ManualTrie: Prefix insertion, exact search, prefix query")
    void testManualTrie() {
        ManualTrie trie = new ManualTrie();
        trie.insert("apple");
        trie.insert("app");
        trie.insert("application");

        assertTrue(trie.search("apple"));
        assertTrue(trie.search("app"));
        assertFalse(trie.search("appl"));
        assertTrue(trie.startsWith("appl"));

        ManualArrayList<String> prefixWords = trie.getWordsWithPrefix("app");
        assertEquals(3, prefixWords.size());
    }

    @Test
    @DisplayName("ManualDisjointSet: Union-Find with path compression and rank")
    void testManualDisjointSet() {
        ManualDisjointSet dsu = new ManualDisjointSet(6);
        assertEquals(6, dsu.count());

        assertTrue(dsu.union(0, 1));
        assertTrue(dsu.union(1, 2));
        assertFalse(dsu.union(0, 2)); // Already connected

        assertTrue(dsu.connected(0, 2));
        assertFalse(dsu.connected(0, 3));
        assertEquals(4, dsu.count());
    }
}
