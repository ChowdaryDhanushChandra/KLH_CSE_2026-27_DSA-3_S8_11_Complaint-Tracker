package com.texthack.util;

import com.texthack.datastructures.ManualArrayList;
import com.texthack.datastructures.ManualComparator;

/**
 * Custom sorting algorithms (MergeSort and QuickSort) implemented manually without java.util.*.
 */
public final class ManualSort {

    private ManualSort() {}

    /**
     * Sorts an array using MergeSort with a custom comparator.
     */
    @SuppressWarnings("unchecked")
    public static <T> void mergeSort(T[] array, ManualComparator<T> comparator) {
        if (array == null || array.length <= 1) return;
        T[] aux = (T[]) new Object[array.length];
        mergeSort(array, aux, 0, array.length - 1, comparator);
    }

    private static <T> void mergeSort(T[] a, T[] aux, int low, int high, ManualComparator<T> comp) {
        if (low >= high) return;
        int mid = low + (high - low) / 2;
        mergeSort(a, aux, low, mid, comp);
        mergeSort(a, aux, mid + 1, high, comp);
        merge(a, aux, low, mid, high, comp);
    }

    private static <T> void merge(T[] a, T[] aux, int low, int mid, int high, ManualComparator<T> comp) {
        for (int k = low; k <= high; k++) {
            aux[k] = a[k];
        }
        int i = low, j = mid + 1;
        for (int k = low; k <= high; k++) {
            if (i > mid) {
                a[k] = aux[j++];
            } else if (j > high) {
                a[k] = aux[i++];
            } else if (comp.compare(aux[j], aux[i]) < 0) {
                a[k] = aux[j++];
            } else {
                a[k] = aux[i++];
            }
        }
    }

    /**
     * Sorts a ManualArrayList using MergeSort.
     */
    @SuppressWarnings("unchecked")
    public static <T> void sort(ManualArrayList<T> list, ManualComparator<T> comparator) {
        if (list == null || list.size() <= 1) return;
        T[] temp = (T[]) new Object[list.size()];
        for (int i = 0; i < list.size(); i++) {
            temp[i] = list.get(i);
        }
        mergeSort(temp, comparator);
        for (int i = 0; i < list.size(); i++) {
            list.set(i, temp[i]);
        }
    }

    /**
     * Sorts an integer array using QuickSort.
     */
    public static void quickSort(int[] array) {
        if (array == null || array.length <= 1) return;
        quickSort(array, 0, array.length - 1);
    }

    private static void quickSort(int[] a, int low, int high) {
        if (low < high) {
            int p = partition(a, low, high);
            quickSort(a, low, p - 1);
            quickSort(a, p + 1, high);
        }
    }

    private static int partition(int[] a, int low, int high) {
        int pivot = a[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (a[j] <= pivot) {
                i++;
                int temp = a[i];
                a[i] = a[j];
                a[j] = temp;
            }
        }
        int temp = a[i + 1];
        a[i + 1] = a[high];
        a[high] = temp;
        return i + 1;
    }
}
