package com.texthack.datastructures;

/**
 * Custom Priority Queue implemented without java.util.*.
 * Encapsulates a binary min-heap for O(log n) insertion and extraction.
 *
 * @param <T> Element type
 */
public class ManualPriorityQueue<T> {
    private final ManualMinHeap<T> heap;

    public ManualPriorityQueue() {
        this.heap = new ManualMinHeap<>();
    }

    public ManualPriorityQueue(ManualComparator<T> comparator) {
        this.heap = new ManualMinHeap<>(comparator);
    }

    public ManualPriorityQueue(int initialCapacity, ManualComparator<T> comparator) {
        this.heap = new ManualMinHeap<>(initialCapacity, comparator);
    }

    public void offer(T item) {
        heap.insert(item);
    }

    public void add(T item) {
        heap.insert(item);
    }

    public T poll() {
        if (heap.isEmpty()) {
            return null;
        }
        return heap.extractMin();
    }

    public T peek() {
        if (heap.isEmpty()) {
            return null;
        }
        return heap.peek();
    }

    public int size() {
        return heap.size();
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    public void clear() {
        heap.clear();
    }
}
