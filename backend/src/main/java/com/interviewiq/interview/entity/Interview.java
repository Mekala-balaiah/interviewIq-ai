package com.interviewiq.interview.entity;

import com.interviewiq.application.entity.Application;
import com.interviewiq.candidate.entity.CandidateProfile;
import com.interviewiq.interview.enums.InterviewStatus;
import com.interviewiq.interview.enums.InterviewType;
import com.interviewiq.job.entity.Job;
import com.interviewiq.recruiter.entity.RecruiterProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "interviews")
@Getter
@Setter
public class Interview {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private CandidateProfile candidate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_id")
    private RecruiterProfile recruiter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private InterviewType type = InterviewType.TECHNICAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InterviewStatus status = InterviewStatus.SCHEDULED;

    @Column(name = "round")
    private String round;

    @Column(name = "overall_score")
    private Integer overallScore;

    @Column(name = "ai_feedback")
    private String aiFeedback;

    @Column(name = "ai_summary")
    private String aiSummary;

    @Column(name = "recruiter_notes")
    private String recruiterNotes;

    @Column(name = "ai_conducted", nullable = false)
    private boolean aiConducted = false;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "scheduled_at")
    private Instant scheduledAt;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
