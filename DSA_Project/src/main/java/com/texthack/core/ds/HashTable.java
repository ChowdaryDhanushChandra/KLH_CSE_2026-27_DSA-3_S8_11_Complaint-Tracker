package com.texthack.core.ds;

/**
 * Hand-built generic Hash Table with separate-chaining collision resolution
 * and automatic bucket array resizing at 0.75 load factor.
 * Strictly adheres to zero java.util.* collections.
 *
 * @param <K> Key type
 * @param <V> Value type
 */
public class HashTable<K, V> {
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;

    public static class Entry<K, V> {
        public final K key;
        public V value;
        public final int hash;
        public Entry<K, V> next;

        public Entry(K key, V value, int hash, Entry<K, V> next) {
            this.key = key;
            this.value = value;
            this.hash = hash;
            this.next = next;
        }

        public K getKey() { return key; }
        public V getValue() { return value; }
    }

    private Entry<K, V>[] table;
    private int size;
    private int threshold;

    @SuppressWarnings("unchecked")
    public HashTable() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    public HashTable(int initialCapacity) {
        int cap = 1;
        while (cap < initialCapacity) {
            cap <<= 1;
        }
        this.table = (Entry<K, V>[]) new Entry[cap];
        this.size = 0;
        this.threshold = (int) (cap * LOAD_FACTOR);
    }

    public void put(K key, V value) {
        if (key == null) {
            putForNullKey(value);
            return;
        }
        int hash = hash(key);
        int index = indexFor(hash, table.length);

        for (Entry<K, V> e = table[index]; e != null; e = e.next) {
            if (e.hash == hash && (e.key == key || key.equals(e.key))) {
                e.value = value;
                return;
            }
        }

        addEntry(hash, key, value, index);
    }

    public V get(K key) {
        if (key == null) {
            return getForNullKey();
        }
        int hash = hash(key);
        int index = indexFor(hash, table.length);

        for (Entry<K, V> e = table[index]; e != null; e = e.next) {
            if (e.hash == hash && (e.key == key || key.equals(e.key))) {
                return e.value;
            }
        }
        return null;
    }

    public boolean containsKey(K key) {
        if (key == null) {
            for (Entry<K, V> e = table[0]; e != null; e = e.next) {
                if (e.key == null) return true;
            }
            return false;
        }
        int hash = hash(key);
        int index = indexFor(hash, table.length);
        for (Entry<K, V> e = table[index]; e != null; e = e.next) {
            if (e.hash == hash && (e.key == key || key.equals(e.key))) {
                return true;
            }
        }
        return false;
    }

    public V remove(K key) {
        int hash = (key == null) ? 0 : hash(key);
        int index = indexFor(hash, table.length);

        Entry<K, V> prev = null;
        Entry<K, V> curr = table[index];

        while (curr != null) {
            if (curr.hash == hash && (curr.key == key || (key != null && key.equals(curr.key)))) {
                if (prev == null) {
                    table[index] = curr.next;
                } else {
                    prev.next = curr.next;
                }
                size--;
                return curr.value;
            }
            prev = curr;
            curr = curr.next;
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
        for (int i = 0; i < table.length; i++) {
            table[i] = null;
        }
        size = 0;
    }

    public DynamicArray<K> keys() {
        DynamicArray<K> keys = new DynamicArray<>(size);
        for (int i = 0; i < table.length; i++) {
            for (Entry<K, V> e = table[i]; e != null; e = e.next) {
                keys.add(e.key);
            }
        }
        return keys;
    }

    public DynamicArray<V> values() {
        DynamicArray<V> values = new DynamicArray<>(size);
        for (int i = 0; i < table.length; i++) {
            for (Entry<K, V> e = table[i]; e != null; e = e.next) {
                values.add(e.value);
            }
        }
        return values;
    }

    public DynamicArray<Entry<K, V>> entries() {
        DynamicArray<Entry<K, V>> list = new DynamicArray<>(size);
        for (int i = 0; i < table.length; i++) {
            for (Entry<K, V> e = table[i]; e != null; e = e.next) {
                list.add(e);
            }
        }
        return list;
    }

    private void putForNullKey(V value) {
        for (Entry<K, V> e = table[0]; e != null; e = e.next) {
            if (e.key == null) {
                e.value = value;
                return;
            }
        }
        addEntry(0, null, value, 0);
    }

    private V getForNullKey() {
        for (Entry<K, V> e = table[0]; e != null; e = e.next) {
            if (e.key == null) return e.value;
        }
        return null;
    }

    private void addEntry(int hash, K key, V value, int bucketIndex) {
        Entry<K, V> current = table[bucketIndex];
        table[bucketIndex] = new Entry<>(key, value, hash, current);
        if (++size >= threshold) {
            resize(table.length * 2);
        }
    }

    @SuppressWarnings("unchecked")
    private void resize(int newCapacity) {
        Entry<K, V>[] newTable = (Entry<K, V>[]) new Entry[newCapacity];
        transfer(newTable);
        this.table = newTable;
        this.threshold = (int) (newCapacity * LOAD_FACTOR);
    }

    private void transfer(Entry<K, V>[] newTable) {
        for (int i = 0; i < table.length; i++) {
            Entry<K, V> e = table[i];
            while (e != null) {
                Entry<K, V> next = e.next;
                int idx = indexFor(e.hash, newTable.length);
                e.next = newTable[idx];
                newTable[idx] = e;
                e = next;
            }
        }
    }

    private static int hash(Object key) {
        int h = key.hashCode();
        return h ^ (h >>> 16);
    }

    private static int indexFor(int hash, int length) {
        return hash & (length - 1);
    }
}
