package com.texthack.datastructures;

/**
 * Custom comparator functional interface without using java.util.Comparator.
 *
 * @param <T> Element type to compare
 */
@FunctionalInterface
public interface ManualComparator<T> {
    int compare(T o1, T o2);
}
