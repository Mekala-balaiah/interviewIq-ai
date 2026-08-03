package com.interviewiq.candidate.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateCandidateProfileRequest {
    
    private String headline;
    
    private String bio;
    
    private String location;
    
    private String linkedinUrl;
    
    private String githubUrl;
    
    private String portfolioUrl;
    
    @Min(value = 0, message = "Years of experience cannot be negative")
    private Integer yearsOfExperience;
    
    private String currentTitle;
    
    private String currentCompany;
    
    private String employmentStatus;
    
    private String noticePeriod;
    
    private BigDecimal expectedSalaryMin;
    
    private BigDecimal expectedSalaryMax;
    
    private String salaryCurrency;
    
    private String availability;
    
    private Boolean openToRemote;
}
