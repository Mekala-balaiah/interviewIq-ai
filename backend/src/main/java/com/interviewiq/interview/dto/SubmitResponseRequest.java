package com.interviewiq.interview.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SubmitResponseRequest {
    @NotBlank
    private String responseText;
    
    private Integer responseDurationSeconds;
}
