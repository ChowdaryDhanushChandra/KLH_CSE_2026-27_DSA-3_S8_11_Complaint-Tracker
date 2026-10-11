package com.texthack.module1;

import com.texthack.engine.module1.DocumentSimilarityService;
import com.texthack.engine.module1.PrimalityTestingService;
import com.texthack.engine.module1.QueryRouter;
import com.texthack.model.AlgorithmResult;
import com.texthack.model.QueryRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test suite for Module 1 services: QueryRouter, DocumentSimilarity, Primality Testing.
 */
public class Module1ServicesTest {

    @Test
    @DisplayName("QueryRouter: Routes PATTERN_SEARCH, FUZZY_MATCHING, and PRIMALITY_TESTING")
    void testQueryRouter() {
        QueryRouter router = new QueryRouter();

        // 1. Pattern Search
        QueryRequest req1 = new QueryRequest(
                QueryRequest.QueryType.PATTERN_SEARCH, "KMP", "abcdefg", "cde");
        AlgorithmResult res1 = router.routeQuery(req1);
        assertTrue(res1.getResultSummary().contains("Matches: 1"));

        // 2. Fuzzy Matching
        QueryRequest req2 = new QueryRequest(
                QueryRequest.QueryType.FUZZY_MATCHING, "Levenshtein Distance", "kitten", "sitting");
        AlgorithmResult res2 = router.routeQuery(req2);
        assertTrue(res2.getResultSummary().contains("3"));

        // 3. Primality Testing
        QueryRequest req3 = new QueryRequest(
                QueryRequest.QueryType.PRIMALITY_TESTING, "Miller-Rabin", "", "", 17, 1, 1, 1);
        AlgorithmResult res3 = router.routeQuery(req3);
        assertTrue(res3.getResultSummary().contains("PRIME"));
    }

    @Test
    @DisplayName("PrimalityTestingService: Miller-Rabin distinguishes primes from composites")
    void testMillerRabinPrimality() {
        PrimalityTestingService service = new PrimalityTestingService();

        // Prime 1000000007
        PrimalityTestingService.PrimalityResult primeRes = service.testMillerRabin(1_000_000_007L, 10);
        assertTrue(primeRes.isPrime);

        // Composite 100
        PrimalityTestingService.PrimalityResult compRes = service.testMillerRabin(100, 10);
        assertFalse(compRes.isPrime);

        // Carmichael number 561 (3 * 11 * 17) - Miller-Rabin detects it as composite!
        PrimalityTestingService.PrimalityResult carmichaelRes = service.testMillerRabin(561, 10);
        assertFalse(carmichaelRes.isPrime);
    }

    @Test
    @DisplayName("DocumentSimilarityService: Identifies Longest Common Substring & Jaccard token overlap")
    void testDocumentSimilarity() {
        DocumentSimilarityService simService = new DocumentSimilarityService();
        String docA = "The quick brown fox jumps over the lazy dog.";
        String docB = "The fast brown fox jumps high into the sky.";

        DocumentSimilarityService.SimilarityResult res = simService.compareDocuments(docA, docB);
        assertTrue(res.similarityScore > 0.0);
        assertTrue(res.longestCommonSubstring.contains("brown fox jumps"));
        assertTrue(res.tokenJaccardSimilarity > 0.2);
    }
}
