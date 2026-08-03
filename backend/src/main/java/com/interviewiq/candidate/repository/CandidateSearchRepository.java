package com.interviewiq.candidate.repository;

import com.interviewiq.candidate.document.CandidateDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Spring Data Elasticsearch repository for {@link CandidateDocument}.
 * Provides built-in CRUD + search operations against the {@code candidates} index.
 */
@Repository
public interface CandidateSearchRepository extends ElasticsearchRepository<CandidateDocument, UUID> {
}
