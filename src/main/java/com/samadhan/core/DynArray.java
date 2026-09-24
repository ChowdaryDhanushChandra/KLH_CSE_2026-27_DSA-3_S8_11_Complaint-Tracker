package com.samadhan.core;
public class DynArray<T> {
    private Object[] a = new Object[8]; private int n;
    public void add(T x) { if (n == a.length) { Object[] b = new Object[n * 2]; for (int i = 0; i < n; i++) b[i] = a[i]; a = b; } a[n++] = x; }
    @SuppressWarnings("unchecked") public T get(int i) { return (T) a[i]; }
    public int size() { return n; }
    public void clear() { n = 0; }
}
