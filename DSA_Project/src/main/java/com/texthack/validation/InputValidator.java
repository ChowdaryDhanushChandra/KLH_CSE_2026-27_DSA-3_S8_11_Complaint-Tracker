package com.texthack.validation;

/**
 * Validates user inputs across all modules, guarding against invalid states,
 * exponential explosions, negative costs, or malformed strings.
 */
public final class InputValidator {

    private InputValidator() {}

    public static void validateNotEmpty(String input, String fieldName) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
    }

    public static void validatePatternAndText(String pattern, String text) {
        if (pattern == null || pattern.isEmpty()) {
            throw new IllegalArgumentException("Search pattern cannot be empty.");
        }
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Search text cannot be empty.");
        }
    }

    public static void validateBitmaskSize(int n, int maxAllowed) {
        if (n < 1) {
            throw new IllegalArgumentException("Size must be at least 1 (provided: " + n + ").");
        }
        if (n > maxAllowed) {
            throw new IllegalArgumentException("Input size " + n + " exceeds maximum safe limit of " 
                + maxAllowed + " for exponential bitmask DP.");
        }
    }

    public static void validateNonNegative(int value, String fieldName) {
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative (provided: " + value + ").");
        }
    }

    public static void validatePrimeCandidate(long n) {
        if (n < 2) {
            throw new IllegalArgumentException("Candidate number for primality must be >= 2 (provided: " + n + ").");
        }
    }
}
