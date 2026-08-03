package com.interviewiq.interview.dto;

import com.interviewiq.interview.enums.InterviewStatus;
import com.interviewiq.interview.enums.InterviewType;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class InterviewDto {
    private UUID id;
    private UUID applicationId;
    private UUID candidateId;
    private UUID recruiterId;
    private UUID jobId;
    
    private InterviewType type;
    private InterviewStatus status;
    private String round;
    
    private Integer overallScore;
    private String aiFeedback;
    private String aiSummary;
    private String recruiterNotes;
    
    private boolean aiConducted;
    private Integer durationMinutes;
    
    private Instant scheduledAt;
    private Instant startedAt;
    private Instant completedAt;
    private Instant createdAt;
}
