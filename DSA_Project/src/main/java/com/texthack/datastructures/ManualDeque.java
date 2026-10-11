package com.texthack.datastructures;

/**
 * Custom double-ended queue (Deque) using a circular array buffer without java.util.*.
 *
 * @param <T> Element type
 */
public class ManualDeque<T> {
    private Object[] data;
    private int head;
    private int tail;
    private int count;
    private static final int INITIAL_CAPACITY = 16;

    public ManualDeque() {
        data = new Object[INITIAL_CAPACITY];
        head = 0;
        tail = 0;
        count = 0;
    }

    public void addLast(T item) {
        if (count == data.length) {
            resize(data.length * 2);
        }
        data[tail] = item;
        tail = (tail + 1) % data.length;
        count++;
    }

    public void addFirst(T item) {
        if (count == data.length) {
            resize(data.length * 2);
        }
        head = (head - 1 + data.length) % data.length;
        data[head] = item;
        count++;
    }

    @SuppressWarnings("unchecked")
    public T pollFirst() {
        if (isEmpty()) {
            throw new IllegalStateException("Deque is empty");
        }
        T item = (T) data[head];
        data[head] = null;
        head = (head + 1) % data.length;
        count--;
        return item;
    }

    @SuppressWarnings("unchecked")
    public T pollLast() {
        if (isEmpty()) {
            throw new IllegalStateException("Deque is empty");
        }
        tail = (tail - 1 + data.length) % data.length;
        T item = (T) data[tail];
        data[tail] = null;
        count--;
        return item;
    }

    @SuppressWarnings("unchecked")
    public T peekFirst() {
        if (isEmpty()) {
            throw new IllegalStateException("Deque is empty");
        }
        return (T) data[head];
    }

    @SuppressWarnings("unchecked")
    public T peekLast() {
        if (isEmpty()) {
            throw new IllegalStateException("Deque is empty");
        }
        int idx = (tail - 1 + data.length) % data.length;
        return (T) data[idx];
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
