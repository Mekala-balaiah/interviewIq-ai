package com.interviewiq.company.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class CompanyDto {
    private UUID id;
    private String name;
    private String slug;
    private String logoUrl;
    private String website;
    private String industry;
    private String sizeRange;
    private String description;
    private String headquarters;
    private String linkedinUrl;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;
}
