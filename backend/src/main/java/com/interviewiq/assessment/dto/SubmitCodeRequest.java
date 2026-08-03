package com.interviewiq.assessment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SubmitCodeRequest {
    @NotBlank
    private String code;
    
    @NotBlank
    private String language; // E.g., "JAVA", "PYTHON", "CPP"
}
