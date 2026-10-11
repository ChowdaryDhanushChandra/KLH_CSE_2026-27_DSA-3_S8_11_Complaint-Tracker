package com.texthack.core.util;

import com.texthack.core.ds.CustomComparator;
import com.texthack.core.ds.DynamicArray;

/**
 * Hand-built sorting utilities (MergeSort and QuickSort).
 * Completely replaces Arrays.sort() and Collections.sort() to honor zero java.util.* rule.
 */
public class ManualSort {

    /**
     * Stable MergeSort on a DynamicArray using a custom comparator.
     */
    public static <T> void mergeSort(DynamicArray<T> list, CustomComparator<T> comp) {
        if (list == null || list.size() <= 1) return;
        int n = list.size();
        Object[] aux = new Object[n];
        mergeSortInternal(list, aux, 0, n - 1, comp);
    }

    @SuppressWarnings("unchecked")
    private static <T> void mergeSortInternal(DynamicArray<T> list, Object[] aux, int left, int right, CustomComparator<T> comp) {
        if (left >= right) return;
        int mid = left + (right - left) / 2;
        mergeSortInternal(list, aux, left, mid, comp);
        mergeSortInternal(list, aux, mid + 1, right, comp);
        merge(list, aux, left, mid, right, comp);
    }

    @SuppressWarnings("unchecked")
    private static <T> void merge(DynamicArray<T> list, Object[] aux, int left, int mid, int right, CustomComparator<T> comp) {
        for (int k = left; k <= right; k++) {
            aux[k] = list.get(k);
        }
        int i = left;
        int j = mid + 1;
        for (int k = left; k <= right; k++) {
            if (i > mid) {
                list.set(k, (T) aux[j++]);
            } else if (j > right) {
                list.set(k, (T) aux[i++]);
            } else if (comp.compare((T) aux[j], (T) aux[i]) < 0) {
                list.set(k, (T) aux[j++]);
            } else {
                list.set(k, (T) aux[i++]);
            }
        }
    }

    /**
     * In-place QuickSort on an integer array with median-of-three pivot selection.
     */
    public static void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int p = partition(arr, low, high);
            quickSort(arr, low, p - 1);
            quickSort(arr, p + 1, high);
        }
    }

    private static int partition(int[] arr, int low, int high) {
        int mid = low + (high - low) / 2;
        int pivot = arr[mid];
        // swap mid with high
        int temp = arr[mid];
        arr[mid] = arr[high];
        arr[high] = temp;

        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) {
                i++;
                int t = arr[i];
                arr[i] = arr[j];
                arr[j] = t;
            }
        }
        int t = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = t;
        return i + 1;
    }
}
