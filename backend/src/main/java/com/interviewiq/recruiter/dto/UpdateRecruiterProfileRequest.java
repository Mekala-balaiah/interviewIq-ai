package com.interviewiq.recruiter.dto;

import lombok.Data;

@Data
public class UpdateRecruiterProfileRequest {
    private String title;
    private String department;
    private String specialization;
    private String bio;
    private String linkedinUrl;
}
