package com.interviewiq.assessment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateAssessmentRequest {
    @NotNull
    private UUID applicationId;
    
    private String difficulty; // E.g. "EASY", "MEDIUM", "HARD"
}
