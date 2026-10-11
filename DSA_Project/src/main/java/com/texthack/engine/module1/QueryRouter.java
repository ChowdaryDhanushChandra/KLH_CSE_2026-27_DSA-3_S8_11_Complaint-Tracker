package com.texthack.engine.module1;

import com.texthack.datastructures.ManualArrayList;
import com.texthack.graph.CriticalPathCalculator;
import com.texthack.graph.EdmondsKarp;
import com.texthack.model.AlgorithmResult;
import com.texthack.model.AlignmentResult;
import com.texthack.model.QueryRequest;
import com.texthack.model.ScheduleTask;
import com.texthack.model.SearchResult;

/**
 * Main query dispatcher for Module 1, delegating requests to specialized domain engines.
 */
public class QueryRouter {

    private final PatternSearchService patternService = new PatternSearchService();
    private final FuzzyMatchingService fuzzyService = new FuzzyMatchingService();
    private final DocumentSimilarityService similarityService = new DocumentSimilarityService();
    private final CitationFlowService citationService = new CitationFlowService();
    private final ProjectSchedulingService schedulingService = new ProjectSchedulingService();
    private final PrimalityTestingService primalityService = new PrimalityTestingService();

    public AlgorithmResult routeQuery(QueryRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Query request cannot be null.");
        }

        switch (request.getQueryType()) {
            case PATTERN_SEARCH: {
                SearchResult sr = patternService.executeSearch(
                        request.getAlgorithm(), request.getInputB(), request.getInputA());
                return new AlgorithmResult(
                        sr.getComplexityInfo().getSummary(),
                        "Matches: " + sr.getMatchCount() + " at indices [" + sr.getIndicesFormatted() + "]",
                        "Pattern: \"" + sr.getPattern() + "\"",
                        sr.getExecutionTimeNanos(),
                        sr.getFormattedTime(),
                        sr.getComparisons(),
                        0,
                        sr.getComplexityInfo(),
                        sr.getStepTraces()
                );
            }

            case FUZZY_MATCHING: {
                AlignmentResult ar = fuzzyService.computeFuzzyMatch(
                        request.getAlgorithm(),
                        request.getInputA(),
                        request.getInputB(),
                        request.getCostInsert(),
                        request.getCostDelete(),
                        request.getCostReplace()
                );
                return new AlgorithmResult(
                        ar.getAlgorithmName(),
                        "Distance / Score: " + ar.getDistanceOrScore(),
                        "Operations count: " + ar.getOperations().size(),
                        ar.getExecutionTimeNanos(),
                        ar.getFormattedTime(),
                        0,
                        0,
                        ar.getComplexityInfo(),
                        ar.getOperations()
                );
            }

            case DOCUMENT_SIMILARITY: {
                DocumentSimilarityService.SimilarityResult sim = similarityService.compareDocuments(
                        request.getInputA(), request.getInputB());
                return sim.algorithmResult;
            }

            case CITATION_FLOW: {
                // Default sample academic flow network if inputs not custom
                int vertices = 6;
                int[][] edges = new int[][]{
                        {0, 1, 10}, {0, 2, 8},
                        {1, 2, 2}, {1, 3, 5},
                        {2, 4, 10},
                        {3, 4, 4}, {3, 5, 7},
                        {4, 5, 10}
                };
                EdmondsKarp.MaxFlowResult mf = citationService.analyzeCitationFlow(vertices, edges, 0, 5);
                return mf.algorithmResult;
            }

            case PROJECT_SCHEDULING: {
                ManualArrayList<ScheduleTask> sampleTasks = new ManualArrayList<>();
                ScheduleTask t0 = new ScheduleTask(0, "Literature Survey", 3);
                ScheduleTask t1 = new ScheduleTask(1, "Algorithm Design", 4);
                t1.addDependency(0);
                ScheduleTask t2 = new ScheduleTask(2, "Data Collection", 2);
                t2.addDependency(0);
                ScheduleTask t3 = new ScheduleTask(3, "Implementation", 5);
                t3.addDependency(1);
                t3.addDependency(2);
                ScheduleTask t4 = new ScheduleTask(4, "Benchmarking", 3);
                t4.addDependency(3);
                ScheduleTask t5 = new ScheduleTask(5, "Paper Writing", 4);
                t5.addDependency(4);

                sampleTasks.add(t0);
                sampleTasks.add(t1);
                sampleTasks.add(t2);
                sampleTasks.add(t3);
                sampleTasks.add(t4);
                sampleTasks.add(t5);

                CriticalPathCalculator.ScheduleResult sr = schedulingService.calculateSchedule(sampleTasks, 2);
                return sr.algorithmResult;
            }

            case PRIMALITY_TESTING: {
                long n = request.getNumberParam();
                if ("Fermat".equalsIgnoreCase(request.getAlgorithm())) {
                    return primalityService.testFermat(n, 10).algorithmResult;
                } else {
                    return primalityService.testMillerRabin(n, 12).algorithmResult;
                }
            }

            default:
                throw new UnsupportedOperationException("Unknown query type: " + request.getQueryType());
        }
    }

    public PatternSearchService getPatternService() {
        return patternService;
    }

    public FuzzyMatchingService getFuzzyService() {
        return fuzzyService;
    }

    public DocumentSimilarityService getSimilarityService() {
        return similarityService;
    }

    public CitationFlowService getCitationService() {
        return citationService;
    }

    public ProjectSchedulingService getSchedulingService() {
        return schedulingService;
    }

    public PrimalityTestingService getPrimalityService() {
        return primalityService;
    }
}
