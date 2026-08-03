package com.interviewiq.application.dto;

import com.interviewiq.application.enums.ApplicationStatus;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class ApplicationDto {
    private UUID id;
    private UUID jobId;
    private UUID candidateId;
    private UUID resumeId;
    private ApplicationStatus status;
    private Integer atsScore;
    private Integer aiRank;
    private Instant appliedAt;
    private Instant statusUpdatedAt;
}
