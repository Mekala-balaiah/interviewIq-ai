package com.interviewiq.candidate.entity;

import com.interviewiq.candidate.enums.ResumeParseStatus;
import com.interviewiq.common.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "resumes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Resume extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private CandidateProfile candidate;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_url", nullable = false, length = 1000)
    private String fileUrl;

    @Column(name = "file_type", nullable = false, length = 20)
    private String fileType;

    @Column(name = "file_size_bytes", nullable = false)
    private Long fileSizeBytes;

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "parse_status", nullable = false)
    private ResumeParseStatus parseStatus = ResumeParseStatus.PENDING;

    @Column(name = "parsed_at")
    private Instant parsedAt;
}
