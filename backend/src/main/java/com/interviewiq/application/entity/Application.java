package com.interviewiq.application.entity;

import com.interviewiq.application.enums.ApplicationStatus;
import com.interviewiq.candidate.entity.CandidateProfile;
import com.interviewiq.candidate.entity.Resume;
import com.interviewiq.common.audit.BaseEntity;
import com.interviewiq.job.entity.Job;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "applications", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"job_id", "candidate_id"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Application extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private CandidateProfile candidate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    private Resume resume;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    @Column(name = "ats_score")
    private Integer atsScore;

    @Column(name = "ai_rank")
    private Integer aiRank;

    @Column(name = "cover_letter", columnDefinition = "TEXT")
    private String coverLetter;

    @Column(name = "recruiter_notes", columnDefinition = "TEXT")
    private String recruiterNotes;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "applied_at", nullable = false, updatable = false)
    private Instant appliedAt = Instant.now();

    @Column(name = "status_updated_at", nullable = false)
    private Instant statusUpdatedAt = Instant.now();
    
    @PreUpdate
    public void preUpdate() {
        super.preUpdate();
        this.statusUpdatedAt = Instant.now();
    }
}
