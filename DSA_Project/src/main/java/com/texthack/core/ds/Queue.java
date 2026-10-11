package com.texthack.core.ds;

/**
 * Hand-built generic FIFO Queue backed by a circular array buffer.
 * Automatically doubles capacity when full.
 * Strictly avoids java.util.* collections.
 *
 * @param <T> Element type
 */
public class Queue<T> {
    private static final int DEFAULT_CAPACITY = 16;
    private Object[] data;
    private int head;
    private int tail;
    private int size;

    public Queue() {
        this(DEFAULT_CAPACITY);
    }

    public Queue(int capacity) {
        this.data = new Object[Math.max(capacity, 4)];
        this.head = 0;
        this.tail = 0;
        this.size = 0;
    }

    public void enqueue(T item) {
        if (size == data.length) {
            resize(data.length * 2);
        }
        data[tail] = item;
        tail = (tail + 1) % data.length;
        size++;
    }

    @SuppressWarnings("unchecked")
    public T dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }
        T item = (T) data[head];
        data[head] = null;
        head = (head + 1) % data.length;
        size--;
        return item;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }
        return (T) data[head];
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        for (int i = 0; i < data.length; i++) {
            data[i] = null;
        }
        head = 0;
        tail = 0;
        size = 0;
    }

    private void resize(int newCapacity) {
        Object[] newData = new Object[newCapacity];
        for (int i = 0; i < size; i++) {
            newData[i] = data[(head + i) % data.length];
        }
        this.data = newData;
        this.head = 0;
        this.tail = size;
    }
}
