package com.texthack.core.ds;

import com.texthack.core.util.ManualSort;
import com.texthack.core.util.ManualStringUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CoreDsTest {

    @Test
    public void testDynamicArray() {
        DynamicArray<String> arr = new DynamicArray<>();
        assertTrue(arr.isEmpty());
        arr.add("A");
        arr.add("B");
        arr.add("C");
        assertEquals(3, arr.size());
        assertEquals("B", arr.get(1));
        arr.set(1, "Z");
        assertEquals("Z", arr.get(1));
        assertEquals("Z", arr.remove(1));
        assertEquals(2, arr.size());
        assertEquals("C", arr.get(1));
    }

    @Test
    public void testHashTable() {
        HashTable<String, Integer> map = new HashTable<>();
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);
        assertEquals(3, map.size());
        assertEquals(2, map.get("two"));
        assertTrue(map.containsKey("one"));
        assertFalse(map.containsKey("four"));
        assertEquals(2, map.remove("two"));
        assertEquals(2, map.size());
        assertNull(map.get("two"));
    }

    @Test
    public void testMinHeap() {
        MinHeap<Integer> heap = new MinHeap<>();
        heap.insert(40);
        heap.insert(10);
        heap.insert(30);
        heap.insert(20);
        assertEquals(10, heap.peek());
        assertEquals(10, heap.extractMin());
        assertEquals(20, heap.extractMin());
        assertEquals(30, heap.extractMin());
        assertEquals(40, heap.extractMin());
        assertTrue(heap.isEmpty());
    }

    @Test
    public void testQueue() {
        Queue<String> q = new Queue<>();
        q.enqueue("First");
        q.enqueue("Second");
        q.enqueue("Third");
        assertEquals("First", q.dequeue());
        assertEquals("Second", q.peek());
        assertEquals("Second", q.dequeue());
        assertEquals("Third", q.dequeue());
        assertTrue(q.isEmpty());
    }

    @Test
    public void testStack() {
        Stack<Integer> s = new Stack<>();
        s.push(100);
        s.push(200);
        assertEquals(200, s.peek());
        assertEquals(200, s.pop());
        assertEquals(100, s.pop());
        assertTrue(s.isEmpty());
    }

    @Test
    public void testLinkedList() {
        LinkedList<String> list = new LinkedList<>();
        list.addLast("A");
        list.addLast("B");
        list.addFirst("Start");
        assertEquals(3, list.size());
        assertEquals("Start", list.get(0));
        assertEquals("A", list.get(1));
        assertEquals("B", list.get(2));
        assertEquals("Start", list.removeFirst());
        assertEquals("B", list.removeLast());
        assertEquals(1, list.size());
    }

    @Test
    public void testTrie() {
        Trie trie = new Trie();
        trie.insert("apple");
        trie.insert("app");
        assertTrue(trie.search("apple"));
        assertTrue(trie.search("app"));
        assertFalse(trie.search("appl"));
        assertTrue(trie.startsWith("app"));
        assertFalse(trie.startsWith("ban"));
    }

    @Test
    public void testManualSortAndStringUtil() {
        DynamicArray<Integer> nums = new DynamicArray<>();
        nums.add(5);
        nums.add(2);
        nums.add(8);
        nums.add(1);
        ManualSort.mergeSort(nums, Integer::compare);
        assertEquals(1, (int) nums.get(0));
        assertEquals(2, (int) nums.get(1));
        assertEquals(5, (int) nums.get(2));
        assertEquals(8, (int) nums.get(3));

        DynamicArray<String> tokens = ManualStringUtil.split("hello world test", ' ');
        assertEquals(3, tokens.size());
        assertEquals("hello", tokens.get(0));
        assertEquals("world", tokens.get(1));
        assertEquals("test", tokens.get(2));
    }
}
