package com.texthack.core.ds;

/**
 * Hand-built generic LIFO Stack backed by a dynamically resizing array.
 * Strictly avoids java.util.* collections.
 *
 * @param <T> Element type
 */
public class Stack<T> {
    private static final int DEFAULT_CAPACITY = 16;
    private Object[] elements;
    private int top;

    public Stack() {
        this(DEFAULT_CAPACITY);
    }

    public Stack(int capacity) {
        this.elements = new Object[Math.max(capacity, 4)];
        this.top = 0;
    }

    public void push(T item) {
        if (top == elements.length) {
            resize(elements.length * 2);
        }
        elements[top++] = item;
    }

    @SuppressWarnings("unchecked")
    public T pop() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }
        T item = (T) elements[--top];
        elements[top] = null;
        return item;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }
        return (T) elements[top - 1];
    }

    public int size() {
        return top;
    }

    public boolean isEmpty() {
        return top == 0;
    }

    public void clear() {
        for (int i = 0; i < top; i++) {
            elements[i] = null;
        }
        top = 0;
    }

    private void resize(int newCapacity) {
        Object[] newElements = new Object[newCapacity];
        System.arraycopy(elements, 0, newElements, 0, top);
        this.elements = newElements;
    }
}
