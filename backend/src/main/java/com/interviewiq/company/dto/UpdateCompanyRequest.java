package com.interviewiq.company.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateCompanyRequest {

    @NotBlank(message = "Company name is required")
    private String name;

    private String logoUrl;
    private String website;
    private String industry;
    private String sizeRange;
    private String description;
    private String headquarters;
    private String linkedinUrl;
}
