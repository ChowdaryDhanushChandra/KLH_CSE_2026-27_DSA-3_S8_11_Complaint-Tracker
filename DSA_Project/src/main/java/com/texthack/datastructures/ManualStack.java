package com.texthack.datastructures;

/**
 * Custom LIFO stack implementation without java.util.*.
 * Built using an internal dynamic array.
 *
 * @param <T> Element type
 */
public class ManualStack<T> {
    private Object[] data;
    private int top;
    private static final int INITIAL_CAPACITY = 16;

    public ManualStack() {
        data = new Object[INITIAL_CAPACITY];
        top = -1;
    }

    public void push(T item) {
        if (top + 1 >= data.length) {
            resize(data.length * 2);
        }
        data[++top] = item;
    }

    @SuppressWarnings("unchecked")
    public T pop() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack underflow: stack is empty");
        }
        T item = (T) data[top];
        data[top--] = null;
        return item;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }
        return (T) data[top];
    }

    public boolean isEmpty() {
        return top == -1;
    }

    public int size() {
        return top + 1;
    }

    public void clear() {
        for (int i = 0; i <= top; i++) {
            data[i] = null;
        }
        top = -1;
    }

    private void resize(int newCapacity) {
        Object[] newData = new Object[newCapacity];
        for (int i = 0; i <= top; i++) {
            newData[i] = data[i];
        }
        data = newData;
    }
}
