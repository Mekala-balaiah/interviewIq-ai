package com.interviewiq.candidate.service;

import com.interviewiq.common.response.PagedResponse;
import com.interviewiq.candidate.document.CandidateDocument;

import java.util.List;
import java.util.UUID;

/**
 * Service for Elasticsearch-backed candidate search operations.
 */
public interface CandidateSearchService {

    /**
     * Fetch a candidate's JPA data and upsert it into the Elasticsearch index.
     *
     * @param candidateId the {@code candidate_profiles.id}
     */
    void syncCandidate(UUID candidateId);

    /**
     * Execute a compound Elasticsearch query across multiple fields.
     *
     * @param keyword         free-text search against headline and currentTitle (fuzzy)
     * @param location        exact location keyword filter (nullable)
     * @param minExperience   minimum years of experience (nullable)
     * @param skills          list of skill names the candidate must possess (nullable)
     * @param openToRemote    filter by remote work preference (nullable)
     * @param page            zero-based page index
     * @param size            page size
     * @return paginated list of matching documents
     */
    PagedResponse<CandidateDocument> searchCandidates(
            String keyword,
            String location,
            Integer minExperience,
            List<String> skills,
            Boolean openToRemote,
            int page,
            int size
    );
}
