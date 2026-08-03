package com.interviewiq.assessment.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AssessmentQuestionDto {
    private UUID id;
    private UUID assessmentId;
    private int sequenceNumber;
    private String questionType;
    private String difficulty;
    
    private String problemStatement;
    private String constraints;
    
    // We can expose starterCode as a String (JSON representation) or mapped to a specific Object.
    // Keeping as String for simplicity in DTO.
    private String starterCode;
    
    private int marks;
    private String topic;
    private Integer timeLimitMs;
    private Integer memoryLimitKb;
}
