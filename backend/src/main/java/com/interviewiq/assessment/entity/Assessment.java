package com.interviewiq.assessment.entity;

import com.interviewiq.application.entity.Application;
import com.interviewiq.assessment.enums.AssessmentStatus;
import com.interviewiq.candidate.entity.CandidateProfile;
import com.interviewiq.job.entity.Job;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "assessments")
@Getter
@Setter
public class Assessment {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private CandidateProfile candidate;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "difficulty", nullable = false)
    private String difficulty = "MEDIUM";

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes = 60;

    @Column(name = "total_marks", nullable = false)
    private int totalMarks = 100;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AssessmentStatus status = AssessmentStatus.NOT_STARTED;

    @Column(name = "score_obtained")
    private Integer scoreObtained;

    @Column(name = "percentage")
    private Integer percentage;

    @Column(name = "plagiarism_flagged")
    private Boolean plagiarismFlagged = false;

    @Column(name = "plagiarism_score")
    private BigDecimal plagiarismScore;

    @Column(name = "deadline_at")
    private Instant deadlineAt;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
