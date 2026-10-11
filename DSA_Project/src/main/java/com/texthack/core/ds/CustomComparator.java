package com.texthack.core.ds;

/**
 * Custom functional interface for comparisons in algorithmic data structures.
 * Strictly avoids java.util.Comparator to ensure zero java.util.* dependency.
 *
 * @param <T> Element type to compare
 */
@FunctionalInterface
public interface CustomComparator<T> {
    int compare(T a, T b);
}
