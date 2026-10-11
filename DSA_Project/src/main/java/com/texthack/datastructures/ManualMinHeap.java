package com.texthack.datastructures;

/**
 * Custom binary min-heap implementation without java.util.*.
 *
 * @param <T> Element type
 */
public class ManualMinHeap<T> {
    private Object[] heap;
    private int size;
    private final ManualComparator<T> comparator;
    private static final int DEFAULT_CAPACITY = 16;

    public ManualMinHeap() {
        this(DEFAULT_CAPACITY, null);
    }

    public ManualMinHeap(ManualComparator<T> comparator) {
        this(DEFAULT_CAPACITY, comparator);
    }

    public ManualMinHeap(int capacity, ManualComparator<T> comparator) {
        this.heap = new Object[Math.max(capacity, 4)];
        this.size = 0;
        this.comparator = comparator;
    }

    public void insert(T item) {
        if (item == null) {
            throw new IllegalArgumentException("Cannot insert null into heap");
        }
        if (size >= heap.length) {
            resize(heap.length * 2);
        }
        heap[size] = item;
        siftUp(size);
        size++;
    }

    @SuppressWarnings("unchecked")
    public T extractMin() {
        if (isEmpty()) {
            throw new IllegalStateException("Heap is empty");
        }
        T min = (T) heap[0];
        heap[0] = heap[size - 1];
        heap[size - 1] = null;
        size--;
        if (size > 0) {
            siftDown(0);
        }
        return min;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Heap is empty");
        }
        return (T) heap[0];
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        for (int i = 0; i < size; i++) {
            heap[i] = null;
        }
        size = 0;
    }

    @SuppressWarnings("unchecked")
    private int compare(T a, T b) {
        if (comparator != null) {
            return comparator.compare(a, b);
        }
        return ((Comparable<T>) a).compareTo(b);
    }

    private void siftUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            @SuppressWarnings("unchecked")
            T currentVal = (T) heap[index];
            @SuppressWarnings("unchecked")
            T parentVal = (T) heap[parent];
            if (compare(currentVal, parentVal) < 0) {
                swap(index, parent);
                index = parent;
            } else {
                break;
            }
        }
    }

    private void siftDown(int index) {
        while (index * 2 + 1 < size) {
            int left = index * 2 + 1;
            int right = index * 2 + 2;
            int smallest = left;

            if (right < size) {
                @SuppressWarnings("unchecked")
                T leftVal = (T) heap[left];
                @SuppressWarnings("unchecked")
                T rightVal = (T) heap[right];
                if (compare(rightVal, leftVal) < 0) {
                    smallest = right;
                }
            }

            @SuppressWarnings("unchecked")
            T currentVal = (T) heap[index];
            @SuppressWarnings("unchecked")
            T smallVal = (T) heap[smallest];

            if (compare(smallVal, currentVal) < 0) {
                swap(index, smallest);
                index = smallest;
            } else {
                break;
            }
        }
    }

    private void swap(int i, int j) {
        Object temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    private void resize(int newCapacity) {
        Object[] newHeap = new Object[newCapacity];
        for (int i = 0; i < size; i++) {
            newHeap[i] = heap[i];
        }
        heap = newHeap;
    }
}
