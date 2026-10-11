package complainttracker.algorithms;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom Binary Min-Heap Priority Queue implemented from scratch.
 * Provides O(log N) insert and extract operations, dynamic resizing,
 * and operation tracing for visual algorithm demonstrations.
 *
 * @param <T> Element type that implements Comparable
 */
public class CustomPriorityQueue<T extends Comparable<T>> {
    private static final int DEFAULT_CAPACITY = 32;

    @SuppressWarnings("unchecked")
    private T[] heap = (T[]) new Comparable[DEFAULT_CAPACITY];
    private int size = 0;

    public static class HeapStep {
        public final String action;
        public final int index1;
        public final int index2;
        public final String description;

        public HeapStep(String action, int index1, int index2, String description) {
            this.action = action;
            this.index1 = index1;
            this.index2 = index2;
            this.description = description;
        }

        @Override
        public String toString() {
            return action + ": " + description;
        }
    }

    public CustomPriorityQueue() {
        this(DEFAULT_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    public CustomPriorityQueue(int initialCapacity) {
        heap = (T[]) new Comparable[Math.max(4, initialCapacity)];
        size = 0;
    }

    public void insert(T item) {
        insertWithTrace(item, null);
    }

    public void insertWithTrace(T item, List<HeapStep> traceList) {
        if (item == null) return;
        ensureCapacity();
        heap[size] = item;
        int current = size;
        size++;

        if (traceList != null) {
            traceList.add(new HeapStep("INSERT", current, -1,
                    "Inserted new element at leaf index " + current + ": " + item));
        }

        // Heapify up (bubble up)
        while (current > 0) {
            int parent = (current - 1) / 2;
            if (heap[current].compareTo(heap[parent]) < 0) {
                if (traceList != null) {
                    traceList.add(new HeapStep("SWAP", current, parent,
                            String.format("Violated Min-Heap property! Swapping child [%d] (%s) with parent [%d] (%s)",
                                    current, heap[current], parent, heap[parent])));
                }
                swap(current, parent);
                current = parent;
            } else {
                if (traceList != null) {
                    traceList.add(new HeapStep("STABLE", current, parent,
                            String.format("Heap property satisfied at index [%d] <= parent [%d].", current, parent)));
                }
                break;
            }
        }
    }

    public T poll() {
        return pollWithTrace(null);
    }

    public T pollWithTrace(List<HeapStep> traceList) {
        if (size == 0) return null;
        T root = heap[0];
        T last = heap[size - 1];
        heap[size - 1] = null;
        size--;

        if (size > 0) {
            heap[0] = last;
            if (traceList != null) {
                traceList.add(new HeapStep("EXTRACT_ROOT", 0, size,
                        "Extracted root (" + root + "). Moved last element (" + last + ") to root. Heapifying down..."));
            }
            heapifyDown(0, traceList);
        } else if (traceList != null) {
            traceList.add(new HeapStep("EXTRACT_ROOT", 0, -1, "Extracted final root (" + root + "). Heap is now empty."));
        }

        return root;
    }

    public T peek() {
        if (size == 0) return null;
        return heap[0];
    }

    private void heapifyDown(int index, List<HeapStep> traceList) {
        while (true) {
            int leftChild = 2 * index + 1;
            int rightChild = 2 * index + 2;
            int smallest = index;

            if (leftChild < size && heap[leftChild].compareTo(heap[smallest]) < 0) {
                smallest = leftChild;
            }
            if (rightChild < size && heap[rightChild].compareTo(heap[smallest]) < 0) {
                smallest = rightChild;
            }

            if (smallest != index) {
                if (traceList != null) {
                    traceList.add(new HeapStep("HEAPIFY_DOWN_SWAP", index, smallest,
                            String.format("Parent [%d] > Child [%d]. Swapping elements.", index, smallest)));
                }
                swap(index, smallest);
                index = smallest;
            } else {
                if (traceList != null) {
                    traceList.add(new HeapStep("HEAPIFY_COMPLETE", index, -1,
                            "Subtree rooted at index [" + index + "] satisfies heap property."));
                }
                break;
            }
        }
    }

    private void swap(int i, int j) {
        T temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    @SuppressWarnings("unchecked")
    private void ensureCapacity() {
        if (size >= heap.length) {
            T[] newHeap = (T[]) new Comparable[heap.length * 2];
            System.arraycopy(heap, 0, newHeap, 0, heap.length);
            heap = newHeap;
        }
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @SuppressWarnings("unchecked")
    public T[] toArray(Class<T> clazz) {
        T[] arr = (T[]) java.lang.reflect.Array.newInstance(clazz, size);
        System.arraycopy(heap, 0, arr, 0, size);
        return arr;
    }

    public List<T> toList() {
        List<T> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(heap[i]);
        }
        return list;
    }

    public void clear() {
        for (int i = 0; i < size; i++) {
            heap[i] = null;
        }
        size = 0;
    }
}
