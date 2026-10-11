package com.texthack.engine.common;

import com.texthack.model.ComplexityInfo;
import com.texthack.model.SearchResult;

/**
 * Common contract for all string pattern matching algorithms.
 */
public interface StringAlgorithm {
    String getName();
    ComplexityInfo getComplexity();
    SearchResult search(String pattern, String text);
}
