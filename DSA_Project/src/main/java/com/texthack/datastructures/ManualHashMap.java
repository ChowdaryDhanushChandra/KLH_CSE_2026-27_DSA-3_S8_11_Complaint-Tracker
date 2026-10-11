package com.texthack.datastructures;

/**
 * Custom Hash Map using separate chaining without java.util.*.
 * Provides average O(1) time complexity for put, get, and remove.
 *
 * @param <K> Key type
 * @param <V> Value type
 */
public class ManualHashMap<K, V> {

    public static class Entry<K, V> {
        final K key;
        V value;
        Entry<K, V> next;

        public Entry(K key, V value, Entry<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }

    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;

    @SuppressWarnings("unchecked")
    private Entry<K, V>[] buckets = new Entry[DEFAULT_CAPACITY];
    private int size;

    public ManualHashMap() {
        this(DEFAULT_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    public ManualHashMap(int capacity) {
        int cap = 1;
        while (cap < capacity) {
            cap <<= 1;
        }
        buckets = new Entry[cap];
        size = 0;
    }

    private int getBucketIndex(K key) {
        if (key == null) return 0;
        int h = key.hashCode();
        h ^= (h >>> 16);
        return (h & 0x7fffffff) % buckets.length;
    }

    public void put(K key, V value) {
        int index = getBucketIndex(key);
        Entry<K, V> current = buckets[index];
        while (current != null) {
            if (key == null ? current.key == null : key.equals(current.key)) {
                current.value = value;
                return;
            }
            current = current.next;
        }
        // Insert new entry at head of bucket chain
        buckets[index] = new Entry<>(key, value, buckets[index]);
        size++;

        if (size >= buckets.length * LOAD_FACTOR) {
            resize(buckets.length * 2);
        }
    }

    public V get(K key) {
        int index = getBucketIndex(key);
        Entry<K, V> current = buckets[index];
        while (current != null) {
            if (key == null ? current.key == null : key.equals(current.key)) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }

    public V getOrDefault(K key, V defaultValue) {
        V val = get(key);
        return val != null ? val : defaultValue;
    }

    public boolean containsKey(K key) {
        int index = getBucketIndex(key);
        Entry<K, V> current = buckets[index];
        while (current != null) {
            if (key == null ? current.key == null : key.equals(current.key)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public V remove(K key) {
        int index = getBucketIndex(key);
        Entry<K, V> current = buckets[index];
        Entry<K, V> prev = null;
        while (current != null) {
            if (key == null ? current.key == null : key.equals(current.key)) {
                if (prev == null) {
                    buckets[index] = current.next;
                } else {
                    prev.next = current.next;
                }
                size--;
                return current.value;
            }
            prev = current;
            current = current.next;
        }
        return null;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = null;
        }
        size = 0;
    }

    public ManualArrayList<K> keys() {
        ManualArrayList<K> list = new ManualArrayList<>(size);
        for (Entry<K, V> bucket : buckets) {
            Entry<K, V> curr = bucket;
            while (curr != null) {
                list.add(curr.key);
                curr = curr.next;
            }
        }
        return list;
    }

    public ManualArrayList<V> values() {
        ManualArrayList<V> list = new ManualArrayList<>(size);
        for (Entry<K, V> bucket : buckets) {
            Entry<K, V> curr = bucket;
            while (curr != null) {
                list.add(curr.value);
                curr = curr.next;
            }
        }
        return list;
    }

    public ManualArrayList<Entry<K, V>> entries() {
        ManualArrayList<Entry<K, V>> list = new ManualArrayList<>(size);
        for (Entry<K, V> bucket : buckets) {
            Entry<K, V> curr = bucket;
            while (curr != null) {
                list.add(curr);
                curr = curr.next;
            }
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    private void resize(int newCapacity) {
        Entry<K, V>[] oldBuckets = buckets;
        buckets = new Entry[newCapacity];
        size = 0;
        for (Entry<K, V> bucket : oldBuckets) {
            Entry<K, V> curr = bucket;
            while (curr != null) {
                put(curr.key, curr.value);
                curr = curr.next;
            }
        }
    }
}
