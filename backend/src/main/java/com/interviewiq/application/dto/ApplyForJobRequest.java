package com.interviewiq.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ApplyForJobRequest {
    
    @NotNull(message = "Job ID is required")
    private UUID jobId;
    
    private UUID resumeId;
    
    private String coverLetter;
}
