package com.interviewiq.hr.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JobApprovalRequest {
    
    @NotNull(message = "Approval decision is required")
    private Boolean approved;
    
    private String comments;
}
