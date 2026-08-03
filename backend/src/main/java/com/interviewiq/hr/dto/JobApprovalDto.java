package com.interviewiq.hr.dto;

import com.interviewiq.hr.enums.JobApprovalStatus;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class JobApprovalDto {
    private UUID id;
    private UUID jobId;
    private UUID hrId;
    private String hrName;
    private JobApprovalStatus status;
    private String comments;
    private Instant createdAt;
}
