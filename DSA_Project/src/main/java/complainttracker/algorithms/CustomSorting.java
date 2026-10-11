package complainttracker.algorithms;

import java.util.Comparator;
import java.util.List;

/**
 * Custom Sorting and Searching Algorithms implemented from scratch.
 * - QuickSort (with median-of-three pivot choice)
 * - MergeSort (stable sorting)
 * - BinarySearch
 */
public class CustomSorting {

    public static class SortMetrics {
        public final String algorithmName;
        public final int comparisonCount;
        public final int swapCount;
        public final long executionNanos;

        public SortMetrics(String algorithmName, int comparisonCount, int swapCount, long executionNanos) {
            this.algorithmName = algorithmName;
            this.comparisonCount = comparisonCount;
            this.swapCount = swapCount;
            this.executionNanos = executionNanos;
        }
    }

    /**
     * QuickSort implementation with comparison counting.
     */
    public static <T> SortMetrics quickSort(List<T> list, Comparator<T> comparator) {
        long start = System.nanoTime();
        int[] counters = new int[2]; // [0] = comparisons, [1] = swaps
        if (list != null && list.size() > 1) {
            quickSortInternal(list, 0, list.size() - 1, comparator, counters);
        }
        long elapsed = System.nanoTime() - start;
        return new SortMetrics("QuickSort (Median-of-3)", counters[0], counters[1], elapsed);
    }

    private static <T> void quickSortInternal(List<T> list, int low, int high, Comparator<T> comparator, int[] counters) {
        if (low < high) {
            int p = partition(list, low, high, comparator, counters);
            quickSortInternal(list, low, p - 1, comparator, counters);
            quickSortInternal(list, p + 1, high, comparator, counters);
        }
    }

    private static <T> int partition(List<T> list, int low, int high, Comparator<T> comparator, int[] counters) {
        // Median of three pivot selection
        int mid = low + (high - low) / 2;
        if (comparator.compare(list.get(low), list.get(mid)) > 0) swap(list, low, mid, counters);
        if (comparator.compare(list.get(low), list.get(high)) > 0) swap(list, low, high, counters);
        if (comparator.compare(list.get(mid), list.get(high)) > 0) swap(list, mid, high, counters);
        swap(list, mid, high, counters);

        T pivot = list.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            counters[0]++; // comparison
            if (comparator.compare(list.get(j), pivot) <= 0) {
                i++;
                swap(list, i, j, counters);
            }
        }
        swap(list, i + 1, high, counters);
        return i + 1;
    }

    /**
     * MergeSort implementation with stable sorting.
     */
    public static <T> SortMetrics mergeSort(List<T> list, Comparator<T> comparator) {
        long start = System.nanoTime();
        int[] counters = new int[2]; // [0] = comparisons, [1] = movements
        if (list != null && list.size() > 1) {
            mergeSortInternal(list, 0, list.size() - 1, comparator, counters);
        }
        long elapsed = System.nanoTime() - start;
        return new SortMetrics("MergeSort (Divide & Conquer)", counters[0], counters[1], elapsed);
    }

    private static <T> void mergeSortInternal(List<T> list, int left, int right, Comparator<T> comparator, int[] counters) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSortInternal(list, left, mid, comparator, counters);
            mergeSortInternal(list, mid + 1, right, comparator, counters);
            merge(list, left, mid, right, comparator, counters);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void merge(List<T> list, int left, int mid, int right, Comparator<T> comparator, int[] counters) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        Object[] L = new Object[n1];
        Object[] R = new Object[n2];

        for (int i = 0; i < n1; i++) L[i] = list.get(left + i);
        for (int j = 0; j < n2; j++) R[j] = list.get(mid + 1 + j);

        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            counters[0]++;
            if (comparator.compare((T) L[i], (T) R[j]) <= 0) {
                list.set(k, (T) L[i]);
                i++;
            } else {
                list.set(k, (T) R[j]);
                j++;
            }
            counters[1]++;
            k++;
        }

        while (i < n1) {
            list.set(k, (T) L[i]);
            i++;
            k++;
            counters[1]++;
        }

        while (j < n2) {
            list.set(k, (T) R[j]);
            j++;
            k++;
            counters[1]++;
        }
    }

    private static <T> void swap(List<T> list, int i, int j, int[] counters) {
        if (i == j) return;
        T temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
        counters[1]++;
    }

    /**
     * Binary Search on sorted list.
     */
    public static <T> int binarySearch(List<T> sortedList, T target, Comparator<T> comparator) {
        if (sortedList == null || sortedList.isEmpty()) return -1;
        int low = 0;
        int high = sortedList.size() - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int cmp = comparator.compare(sortedList.get(mid), target);
            if (cmp == 0) return mid;
            if (cmp < 0) low = mid + 1;
            else high = mid - 1;
        }
        return -1;
    }
}
