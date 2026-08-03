package com.interviewiq.interview.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class InterviewResponseDto {
    private UUID id;
    private UUID interviewId;
    private UUID questionId;
    
    private String responseText;
    private Integer aiScore;
    private String aiFeedback;
    private Integer responseDurationSeconds;
    
    private Instant createdAt;
}
