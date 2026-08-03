package com.interviewiq.job.dto;

import com.interviewiq.job.enums.EmploymentType;
import com.interviewiq.job.enums.ExperienceLevel;
import com.interviewiq.job.enums.JobStatus;
import com.interviewiq.job.enums.WorkMode;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateJobRequest {
    private String title;
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
}
