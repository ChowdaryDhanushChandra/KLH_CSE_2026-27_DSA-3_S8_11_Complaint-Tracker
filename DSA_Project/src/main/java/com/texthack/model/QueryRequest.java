package com.texthack.model;

/**
 * Encapsulates a user query submitted to the TextHack analytics engine.
 */
public class QueryRequest {
    public enum QueryType {
        PATTERN_SEARCH,
        FUZZY_MATCHING,
        DOCUMENT_SIMILARITY,
        CITATION_FLOW,
        PROJECT_SCHEDULING,
        PRIMALITY_TESTING
    }

    private final QueryType queryType;
    private final String algorithm;
    private final String inputA;
    private final String inputB;
    private final long numberParam;
    private final int costInsert;
    private final int costDelete;
    private final int costReplace;

    public QueryRequest(QueryType queryType, String algorithm, String inputA, String inputB) {
        this(queryType, algorithm, inputA, inputB, 0, 1, 1, 1);
    }

    public QueryRequest(QueryType queryType, String algorithm, String inputA, String inputB,
                        long numberParam, int costInsert, int costDelete, int costReplace) {
        this.queryType = queryType;
        this.algorithm = algorithm;
        this.inputA = inputA != null ? inputA : "";
        this.inputB = inputB != null ? inputB : "";
        this.numberParam = numberParam;
        this.costInsert = costInsert;
        this.costDelete = costDelete;
        this.costReplace = costReplace;
    }

    public QueryType getQueryType() {
        return queryType;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getInputA() {
        return inputA;
    }

    public String getInputB() {
        return inputB;
    }

    public long getNumberParam() {
        return numberParam;
    }

    public int getCostInsert() {
        return costInsert;
    }

    public int getCostDelete() {
        return costDelete;
    }

    public int getCostReplace() {
        return costReplace;
    }
}
