package complainttracker.algorithms;

/**
 * Custom Hash Map implemented from scratch using Separate Chaining for collision resolution.
 * Supports dynamic rehashing, bucket inspection, and search tracing for UI visualization.
 *
 * @param <K> Key type
 * @param <V> Value type
 */
public class CustomHashMap<K, V> {
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

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
        public Entry<K, V> getNext() { return next; }

        @Override
        public String toString() {
            return key + " => " + value;
        }
    }

    public static class HashTrace {
        public final String keyStr;
        public final int hashCode;
        public final int bucketIndex;
        public final int comparisons;
        public final boolean found;
        public final String pathDescription;

        public HashTrace(String keyStr, int hashCode, int bucketIndex, int comparisons, boolean found, String pathDescription) {
            this.keyStr = keyStr;
            this.hashCode = hashCode;
            this.bucketIndex = bucketIndex;
            this.comparisons = comparisons;
            this.found = found;
            this.pathDescription = pathDescription;
        }
    }

    @SuppressWarnings("unchecked")
    private Entry<K, V>[] buckets;
    private int size;
    private int capacity;
    private final float loadFactor;

    @SuppressWarnings("unchecked")
    public CustomHashMap(int initialCapacity, float loadFactor) {
        this.capacity = initialCapacity > 0 ? initialCapacity : DEFAULT_INITIAL_CAPACITY;
        this.loadFactor = loadFactor > 0 ? loadFactor : DEFAULT_LOAD_FACTOR;
        this.buckets = new Entry[this.capacity];
        this.size = 0;
    }

    public CustomHashMap() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    private int computeHash(K key) {
        if (key == null) return 0;
        int h = key.hashCode();
        // High-bit spreading similar to Java HashMap
        return h ^ (h >>> 16);
    }

    private int getIndex(int hash, int tableCapacity) {
        return Math.abs(hash) % tableCapacity;
    }

    public V put(K key, V value) {
        if (size >= capacity * loadFactor) {
            resize();
        }

        int hash = computeHash(key);
        int index = getIndex(hash, capacity);

        Entry<K, V> current = buckets[index];
        while (current != null) {
            if (current.hash == hash && (current.key == key || (current.key != null && current.key.equals(key)))) {
                V oldVal = current.value;
                current.value = value;
                return oldVal;
            }
            current = current.next;
        }

        // Insert at head of bucket chain
        Entry<K, V> newEntry = new Entry<>(key, value, hash, buckets[index]);
        buckets[index] = newEntry;
        size++;
        return null;
    }

    public V get(K key) {
        int hash = computeHash(key);
        int index = getIndex(hash, capacity);

        Entry<K, V> current = buckets[index];
        while (current != null) {
            if (current.hash == hash && (current.key == key || (current.key != null && current.key.equals(key)))) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }

    public boolean containsKey(K key) {
        return get(key) != null;
    }

    public V remove(K key) {
        int hash = computeHash(key);
        int index = getIndex(hash, capacity);

        Entry<K, V> current = buckets[index];
        Entry<K, V> prev = null;

        while (current != null) {
            if (current.hash == hash && (current.key == key || (current.key != null && current.key.equals(key)))) {
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

    public HashTrace traceSearch(K key) {
        int hash = computeHash(key);
        int index = getIndex(hash, capacity);
        int comparisons = 0;
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Key='%s' -> HashCode=%d -> Bucket[%d]. ", String.valueOf(key), hash, index));

        Entry<K, V> current = buckets[index];
        if (current == null) {
            sb.append("Bucket is empty. Key NOT found in 0 comparisons.");
            return new HashTrace(String.valueOf(key), hash, index, 0, false, sb.toString());
        }

        sb.append("Traversing chain: ");
        int nodeIdx = 0;
        while (current != null) {
            comparisons++;
            sb.append(String.format("[Node %d: Key='%s']", nodeIdx, current.key));
            if (current.hash == hash && (current.key == key || (current.key != null && current.key.equals(key)))) {
                sb.append(String.format(" -> MATCH FOUND in %d comparison(s)!", comparisons));
                return new HashTrace(String.valueOf(key), hash, index, comparisons, true, sb.toString());
            }
            sb.append(" -> ");
            nodeIdx++;
            current = current.next;
        }

        sb.append(String.format("End of chain reached. Key NOT found after %d comparison(s).", comparisons));
        return new HashTrace(String.valueOf(key), hash, index, comparisons, false, sb.toString());
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        int newCapacity = capacity * 2;
        Entry<K, V>[] newBuckets = new Entry[newCapacity];

        for (int i = 0; i < capacity; i++) {
            Entry<K, V> current = buckets[i];
            while (current != null) {
                Entry<K, V> next = current.next;
                int newIndex = getIndex(current.hash, newCapacity);
                current.next = newBuckets[newIndex];
                newBuckets[newIndex] = current;
                current = next;
            }
        }

        this.buckets = newBuckets;
        this.capacity = newCapacity;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int getCapacity() {
        return capacity;
    }

    public Entry<K, V>[] getBuckets() {
        return buckets;
    }

    @SuppressWarnings("unchecked")
    public V[] values(Class<V> clazz) {
        V[] arr = (V[]) java.lang.reflect.Array.newInstance(clazz, size);
        int idx = 0;
        for (int i = 0; i < capacity; i++) {
            Entry<K, V> curr = buckets[i];
            while (curr != null) {
                arr[idx++] = curr.value;
                curr = curr.next;
            }
        }
        return arr;
    }

    @SuppressWarnings("unchecked")
    public void clear() {
        this.buckets = new Entry[DEFAULT_INITIAL_CAPACITY];
        this.capacity = DEFAULT_INITIAL_CAPACITY;
        this.size = 0;
    }
}
