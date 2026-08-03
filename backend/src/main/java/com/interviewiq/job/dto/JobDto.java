package com.interviewiq.job.dto;

import com.interviewiq.job.enums.EmploymentType;
import com.interviewiq.job.enums.ExperienceLevel;
import com.interviewiq.job.enums.JobStatus;
import com.interviewiq.job.enums.WorkMode;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
public class JobDto {
    private UUID id;
    private UUID companyId;
    private String companyName;
    private String companyLogoUrl;
    private UUID recruiterId;
    private String title;
    private String slug;
    private String description;
    private String requirements;
    private EmploymentType employmentType;
    private WorkMode workMode;
    private ExperienceLevel experienceLevel;
    private Integer minExperienceYears;
    private Integer maxExperienceYears;
    private String location;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private String salaryCurrency;
    private JobStatus status;
    private Instant publishDate;
    private Instant closeDate;
    private Integer applicationCount;
    private Integer viewCount;
    private Instant createdAt;
    private Instant updatedAt;
}
