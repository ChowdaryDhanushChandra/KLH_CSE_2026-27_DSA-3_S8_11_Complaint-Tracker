package complainttracker.algorithms;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom Circular Array FIFO Queue implemented from scratch.
 *
 * @param <T> Element type
 */
public class CustomQueue<T> {
    private static final int DEFAULT_CAPACITY = 32;

    @SuppressWarnings("unchecked")
    private T[] data = (T[]) new Object[DEFAULT_CAPACITY];
    private int front = 0;
    private int rear = 0;
    private int count = 0;

    public CustomQueue() {
        this(DEFAULT_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    public CustomQueue(int capacity) {
        data = (T[]) new Object[Math.max(4, capacity)];
        front = 0;
        rear = 0;
        count = 0;
    }

    public void enqueue(T item) {
        if (item == null) return;
        if (count == data.length) {
            resize();
        }
        data[rear] = item;
        rear = (rear + 1) % data.length;
        count++;
    }

    public T dequeue() {
        if (count == 0) return null;
        T item = data[front];
        data[front] = null;
        front = (front + 1) % data.length;
        count--;
        return item;
    }

    public T peek() {
        if (count == 0) return null;
        return data[front];
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        int newCap = data.length * 2;
        T[] newData = (T[]) new Object[newCap];
        for (int i = 0; i < count; i++) {
            newData[i] = data[(front + i) % data.length];
        }
        data = newData;
        front = 0;
        rear = count;
    }

    public int size() {
        return count;
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public List<T> toList() {
        List<T> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add(data[(front + i) % data.length]);
        }
        return list;
    }

    public void clear() {
        for (int i = 0; i < count; i++) {
            data[(front + i) % data.length] = null;
        }
        front = 0;
        rear = 0;
        count = 0;
    }
}
