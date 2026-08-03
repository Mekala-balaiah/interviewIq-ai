package com.interviewiq.candidate.entity;

import com.interviewiq.job.entity.Job;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "resume_analyses")
@Getter
@Setter
public class ResumeAnalysis {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    private Job job;

    @Column(name = "ats_score", nullable = false)
    private int atsScore = 0;

    @Column(name = "keyword_match_score", nullable = false)
    private int keywordMatchScore = 0;

    @Column(name = "semantic_match_score", nullable = false)
    private int semanticMatchScore = 0;

    @Column(name = "format_score", nullable = false)
    private int formatScore = 0;

    @Column(name = "experience_score", nullable = false)
    private int experienceScore = 0;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "extracted_skills")
    private List<String> extractedSkills;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "extracted_education")
    private List<String> extractedEducation;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "extracted_experience")
    private List<String> extractedExperience;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "missing_skills")
    private List<String> missingSkills;

    @Column(name = "ai_summary")
    private String aiSummary;

    @Column(name = "improvement_suggestions")
    private String improvementSuggestions;

    @Column(name = "model_version")
    private String modelVersion;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
