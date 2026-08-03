package com.interviewiq.hr.entity;

import com.interviewiq.hr.enums.JobApprovalStatus;
import com.interviewiq.job.entity.Job;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "job_approvals")
@Getter
@Setter
public class JobApproval {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hr_id", nullable = false)
    private HrProfile hr;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobApprovalStatus status;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
