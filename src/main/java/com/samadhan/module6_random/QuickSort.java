package com.samadhan.module6_random;
import com.samadhan.core.Complaint;
/** Randomized quicksort (expected O(n log n)), highest priority first. */
public class QuickSort {
    static final java.util.Random R = new java.util.Random();
    public static void sort(Complaint[] a, int lo, int hi) {
        while (lo < hi) {
            swap(a, lo + R.nextInt(hi - lo + 1), hi); int piv = a[hi].priority, i = lo;
            for (int j = lo; j < hi; j++) if (a[j].priority > piv) swap(a, i++, j);
            swap(a, i, hi);
            if (i - lo < hi - i) { sort(a, lo, i - 1); lo = i + 1; } else { sort(a, i + 1, hi); hi = i - 1; }
        }
    }
    static void swap(Complaint[] a, int i, int j) { Complaint t = a[i]; a[i] = a[j]; a[j] = t; }
}
