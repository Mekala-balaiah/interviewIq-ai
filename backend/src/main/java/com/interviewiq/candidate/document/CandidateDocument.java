package com.interviewiq.candidate.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;
import java.util.UUID;

/**
 * Elasticsearch document for candidate search.
 * Synced from the PostgreSQL {@code candidate_profiles} table whenever
 * a profile or skill is created / updated.
 */
@Document(indexName = "candidates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateDocument {

    @Id
    private UUID id;

    /** Candidate's full name from the linked {@code users} row. */
    @Field(type = FieldType.Text, analyzer = "standard")
    private String fullName;

    /** Professional headline (e.g. "Senior Java Engineer"). */
    @Field(type = FieldType.Text, analyzer = "standard")
    private String headline;

    /** Current job title. */
    @Field(type = FieldType.Text, analyzer = "standard")
    private String currentTitle;

    /** Current company name. */
    @Field(type = FieldType.Text, analyzer = "standard")
    private String currentCompany;

    /** Candidate's preferred location (city, country, or "Remote"). */
    @Field(type = FieldType.Keyword)
    private String location;

    /** Total years of professional experience. */
    @Field(type = FieldType.Integer)
    private Integer yearsOfExperience;

    /** Whether the candidate is open to remote work. */
    @Field(type = FieldType.Boolean)
    private Boolean openToRemote;

    /**
     * Flat list of skill names (e.g. ["Java", "Spring Boot", "React"]).
     * Uses keyword type for exact / term queries.
     */
    @Field(type = FieldType.Keyword)
    private List<String> skills;

    /** Employment status (EMPLOYED, OPEN_TO_WORK, etc.). */
    @Field(type = FieldType.Keyword)
    private String employmentStatus;
}
