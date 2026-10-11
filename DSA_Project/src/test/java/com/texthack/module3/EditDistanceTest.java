package com.texthack.module3;

import com.texthack.engine.module3.DamerauLevenshteinDistance;
import com.texthack.engine.module3.LevenshteinDistance;
import com.texthack.engine.module3.WeightedEditDistance;
import com.texthack.model.AlignmentResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test suite for Edit Distance algorithms (Levenshtein, Damerau-Levenshtein, Weighted).
 */
public class EditDistanceTest {

    @Test
    @DisplayName("Levenshtein: Classical kitten -> sitting edit distance = 3")
    void testLevenshteinKittenSitting() {
        AlignmentResult res = LevenshteinDistance.compute("kitten", "sitting");
        assertEquals(3, res.getDistanceOrScore());
        assertFalse(res.getOperations().isEmpty());
    }

    @Test
    @DisplayName("Levenshtein: Edge cases - Identical strings and empty strings")
    void testLevenshteinEdgeCases() {
        AlignmentResult same = LevenshteinDistance.compute("algorithm", "algorithm");
        assertEquals(0, same.getDistanceOrScore());

        AlignmentResult empty = LevenshteinDistance.compute("", "hello");
        assertEquals(5, empty.getDistanceOrScore());

        AlignmentResult bothEmpty = LevenshteinDistance.compute("", "");
        assertEquals(0, bothEmpty.getDistanceOrScore());
    }

    @Test
    @DisplayName("Damerau-Levenshtein: Transposition detection")
    void testDamerauTransposition() {
        // Transposition of adjacent letters 'A' and 'B': "AB" -> "BA" has Damerau distance 1, Levenshtein distance 2
        AlignmentResult lev = LevenshteinDistance.compute("AB", "BA");
        assertEquals(2, lev.getDistanceOrScore());

        AlignmentResult dam = DamerauLevenshteinDistance.compute("AB", "BA");
        assertEquals(1, dam.getDistanceOrScore());
    }

    @Test
    @DisplayName("Weighted Edit Distance: Custom replacement cost scaling")
    void testWeightedEditDistance() {
        // With replace cost = 10, replacing is avoided in favor of del + ins = 1 + 1 = 2
        AlignmentResult res = WeightedEditDistance.compute("A", "B", 1, 1, 10);
        assertEquals(2, res.getDistanceOrScore());

        // With cheap replace = 1
        AlignmentResult cheap = WeightedEditDistance.compute("A", "B", 5, 5, 1);
        assertEquals(1, cheap.getDistanceOrScore());
    }
}
