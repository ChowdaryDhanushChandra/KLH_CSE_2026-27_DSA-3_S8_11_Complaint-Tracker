package com.texthack.datastructures;

/**
 * Custom FIFO queue implementation using a circular array buffer without java.util.*.
 *
 * @param <T> Element type
 */
public class ManualQueue<T> {
    private Object[] data;
    private int head;
    private int tail;
    private int count;
    private static final int INITIAL_CAPACITY = 16;

    public ManualQueue() {
        this(INITIAL_CAPACITY);
    }

    public ManualQueue(int initialCapacity) {
        data = new Object[Math.max(initialCapacity, 4)];
        head = 0;
        tail = 0;
        count = 0;
    }

    public void enqueue(T item) {
        if (count == data.length) {
            resize(data.length * 2);
        }
        data[tail] = item;
        tail = (tail + 1) % data.length;
        count++;
    }

    @SuppressWarnings("unchecked")
    public T dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue underflow: queue is empty");
        }
        T item = (T) data[head];
        data[head] = null;
        head = (head + 1) % data.length;
        count--;
        return item;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }
        return (T) data[head];
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public int size() {
        return count;
    }

    public void clear() {
        for (int i = 0; i < data.length; i++) {
            data[i] = null;
        }
        head = 0;
        tail = 0;
        count = 0;
    }

    private void resize(int newCapacity) {
        Object[] newData = new Object[newCapacity];
        for (int i = 0; i < count; i++) {
            newData[i] = data[(head + i) % data.length];
        }
        data = newData;
        head = 0;
        tail = count;
    }
}
