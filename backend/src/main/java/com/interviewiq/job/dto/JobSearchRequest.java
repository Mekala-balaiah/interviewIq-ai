package com.interviewiq.job.dto;

import com.interviewiq.job.enums.JobType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class JobSearchRequest {
    private String keyword;
    private String location;
    private JobType jobType;
    private BigDecimal minSalary;
}
