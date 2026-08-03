package com.interviewiq.candidate.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CandidateProfileDto {
    private UUID id;
    private UUID userId;
    private String headline;
    private String bio;
    private String location;
    private String linkedinUrl;
    private String githubUrl;
    private String portfolioUrl;
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
    private Boolean profileComplete;
    private Integer profileCompletionPct;
}
