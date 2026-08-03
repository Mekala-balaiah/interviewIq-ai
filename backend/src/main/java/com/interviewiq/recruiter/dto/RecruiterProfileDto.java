package com.interviewiq.recruiter.dto;

import com.interviewiq.company.dto.CompanyDto;
import lombok.Data;

import java.util.UUID;

@Data
public class RecruiterProfileDto {
    private UUID id;
    private UUID userId;
    private CompanyDto company;
    private String title;
    private String department;
    private String specialization;
    private String bio;
    private String linkedinUrl;
    private Integer activeJobsCount;
}
