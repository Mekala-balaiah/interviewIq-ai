package com.interviewiq.assessment.dto;

import com.interviewiq.assessment.enums.AssessmentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
public class AssessmentDto {
    private UUID id;
    private UUID applicationId;
    private UUID jobId;
    private UUID candidateId;
    
    private String title;
    private String difficulty;
    private int durationMinutes;
    private int totalMarks;
    
    private AssessmentStatus status;
    private Integer scoreObtained;
    private Integer percentage;
    
    private Boolean plagiarismFlagged;
    private BigDecimal plagiarismScore;
    
    private Instant deadlineAt;
    private Instant startedAt;
    private Instant submittedAt;
    private Instant createdAt;
}
