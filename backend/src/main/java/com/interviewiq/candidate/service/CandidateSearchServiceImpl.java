package com.interviewiq.candidate.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.FuzzyQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.json.JsonData;
import com.interviewiq.candidate.document.CandidateDocument;
import com.interviewiq.candidate.entity.CandidateProfile;
import com.interviewiq.candidate.entity.CandidateSkill;
import com.interviewiq.candidate.repository.CandidateProfileRepository;
import com.interviewiq.candidate.repository.CandidateSearchRepository;
import com.interviewiq.candidate.repository.CandidateSkillRepository;
import com.interviewiq.common.exception.ResourceNotFoundException;
import com.interviewiq.common.response.PagedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CandidateSearchServiceImpl implements CandidateSearchService {

    private final CandidateProfileRepository candidateProfileRepository;
    private final CandidateSkillRepository candidateSkillRepository;
    private final CandidateSearchRepository candidateSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    @Transactional(readOnly = true)
    public void syncCandidate(UUID candidateId) {
        CandidateProfile profile = candidateProfileRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate profile not found"));

        // Flatten skills to a list of names for ES indexing
        List<String> skills = candidateSkillRepository.findByCandidateId(candidateId)
                .stream()
                .map(CandidateSkill::getSkill)
                .map(s -> s.getName())
                .collect(Collectors.toList());

        String fullName = null;
        if (profile.getUser() != null) {
            fullName = profile.getUser().getFirstName() + " " + profile.getUser().getLastName();
        }

        CandidateDocument doc = CandidateDocument.builder()
                .id(profile.getId())
                .fullName(fullName)
                .headline(profile.getHeadline())
                .currentTitle(profile.getCurrentTitle())
                .currentCompany(profile.getCurrentCompany())
                .location(profile.getLocation())
                .yearsOfExperience(profile.getYearsOfExperience())
                .openToRemote(profile.getOpenToRemote())
                .employmentStatus(profile.getEmploymentStatus())
                .skills(skills)
                .build();

        candidateSearchRepository.save(doc);
        log.info("Synced candidate {} to Elasticsearch", candidateId);
    }

    @Override
    public PagedResponse<CandidateDocument> searchCandidates(
            String keyword,
            String location,
            Integer minExperience,
            List<String> skills,
            Boolean openToRemote,
            int page,
            int size) {

        List<Query> mustQueries = new ArrayList<>();
        List<Query> filterQueries = new ArrayList<>();

        // --- Keyword fuzzy search over headline and currentTitle ---
        if (keyword != null && !keyword.isBlank()) {
            Query headlineFuzzy = Query.of(q -> q.fuzzy(
                    FuzzyQuery.of(f -> f.field("headline").value(keyword).fuzziness("AUTO"))
            ));
            Query titleFuzzy = Query.of(q -> q.fuzzy(
                    FuzzyQuery.of(f -> f.field("currentTitle").value(keyword).fuzziness("AUTO"))
            ));
            // At least one of headline or title should match
            Query shouldKeyword = Query.of(q -> q.bool(b -> b
                    .should(headlineFuzzy)
                    .should(titleFuzzy)
                    .minimumShouldMatch("1")
            ));
            mustQueries.add(shouldKeyword);
        }

        // --- Filter: location exact match ---
        if (location != null && !location.isBlank()) {
            filterQueries.add(Query.of(q -> q.term(t -> t.field("location").value(location))));
        }

        // --- Filter: years of experience (range >=) ---
        if (minExperience != null) {
            filterQueries.add(Query.of(q -> q.range(
                    RangeQuery.of(r -> r.field("yearsOfExperience").gte(JsonData.of(minExperience)))
            )));
        }

        // --- Filter: skills term match (all must be present) ---
        if (skills != null && !skills.isEmpty()) {
            for (String skill : skills) {
                filterQueries.add(Query.of(q -> q.term(t -> t.field("skills").value(skill))));
            }
        }

        // --- Filter: open to remote ---
        if (openToRemote != null) {
            filterQueries.add(Query.of(q -> q.term(t -> t.field("openToRemote").value(openToRemote))));
        }

        BoolQuery.Builder boolBuilder = new BoolQuery.Builder()
                .must(mustQueries)
                .filter(filterQueries);

        NativeQuery query = NativeQuery.builder()
                .withQuery(Query.of(q -> q.bool(boolBuilder.build())))
                .withPageable(PageRequest.of(page, size))
                .build();

        SearchHits<CandidateDocument> hits = elasticsearchOperations.search(query, CandidateDocument.class);

        List<CandidateDocument> results = hits.getSearchHits()
                .stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());

        long totalHits = hits.getTotalHits();
        int totalPages = (int) Math.ceil((double) totalHits / size);
        boolean isLast = (page + 1) >= totalPages;

        return new PagedResponse<>(results, page, size, totalHits, totalPages, isLast);
    }
}
