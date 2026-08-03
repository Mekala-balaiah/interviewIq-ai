package com.interviewiq.hr.dto;

import com.interviewiq.company.dto.CompanyDto;
import lombok.Data;

import java.util.UUID;

@Data
public class HrProfileDto {
    private UUID id;
    private UUID userId;
    private CompanyDto company;
    private String title;
    private String department;
    private String linkedinUrl;
}
