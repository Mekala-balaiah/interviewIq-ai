package com.interviewiq.hr.dto;

import lombok.Data;

@Data
public class UpdateHrProfileRequest {
    private String title;
    private String department;
    private String linkedinUrl;
}
