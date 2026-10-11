package com.texthack.service;

import com.texthack.config.AppConfig;
import com.texthack.datastructures.ManualArrayList;
import com.texthack.engine.module2.KMPMatcher;
import com.texthack.model.Document;
import com.texthack.model.SearchResult;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Service for loading and querying multilingual local text corpuses (English, Hindi, Telugu).
 */
public class CorpusService {

    public static class CorpusSearchResult {
        public final Document document;
        public final SearchResult searchResult;

        public CorpusSearchResult(Document document, SearchResult searchResult) {
            this.document = document;
            this.searchResult = searchResult;
        }
    }

    private final ManualArrayList<Document> documents = new ManualArrayList<>();
    private final KMPMatcher kmpMatcher = new KMPMatcher();

    public CorpusService() {
        loadDefaultDocuments();
    }

    private void loadDefaultDocuments() {
        documents.add(loadResourceDocument("doc_en_1", "English DSA Overview", AppConfig.CORPUS_ENGLISH_PATH, "English"));
        documents.add(loadResourceDocument("doc_hi_1", "Hindi Text Analytics", AppConfig.CORPUS_HINDI_PATH, "Hindi"));
        documents.add(loadResourceDocument("doc_te_1", "Telugu Algorithm Bank", AppConfig.CORPUS_TELUGU_PATH, "Telugu"));
    }

    private Document loadResourceDocument(String id, String title, String path, String language) {
        StringBuilder sb = new StringBuilder();
        try (InputStream in = getClass().getResourceAsStream(path)) {
            if (in != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                }
            } else {
                sb.append("Sample text unavailable for ").append(title);
            }
        } catch (Exception e) {
            sb.append("Error loading resource: ").append(e.getMessage());
        }
        return new Document(id, title, sb.toString().trim(), language);
    }

    public ManualArrayList<Document> getDocuments() {
        return documents;
    }

    public ManualArrayList<CorpusSearchResult> searchAll(String pattern) {
        ManualArrayList<CorpusSearchResult> results = new ManualArrayList<>();
        if (pattern == null || pattern.trim().isEmpty()) {
            return results;
        }

        for (int i = 0; i < documents.size(); i++) {
            Document doc = documents.get(i);
            SearchResult sr = kmpMatcher.search(pattern, doc.getContent());
            if (sr.getMatchCount() > 0) {
                results.add(new CorpusSearchResult(doc, sr));
            }
        }
        return results;
    }
}
