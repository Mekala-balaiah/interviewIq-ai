package com.interviewiq.job.entity;

import com.interviewiq.common.audit.BaseEntity;
import com.interviewiq.company.entity.Company;
import com.interviewiq.job.enums.EmploymentType;
import com.interviewiq.job.enums.ExperienceLevel;
import com.interviewiq.job.enums.JobStatus;
import com.interviewiq.job.enums.WorkMode;
import com.interviewiq.recruiter.entity.RecruiterProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "jobs")
@Getter
@Setter
public class Job extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_id", nullable = false)
    private RecruiterProfile recruiter;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", nullable = false)
    private EmploymentType employmentType = EmploymentType.FULL_TIME;

    @Enumerated(EnumType.STRING)
    @Column(name = "work_mode", nullable = false)
    private WorkMode workMode = WorkMode.HYBRID;

    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level", nullable = false)
    private ExperienceLevel experienceLevel = ExperienceLevel.MID;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status = JobStatus.DRAFT;
    
    @Column(columnDefinition = "TEXT")
    private String requirements;
    
    @Column(name = "min_experience_years")
    private Integer minExperienceYears = 0;
    
    @Column(name = "max_experience_years")
    private Integer maxExperienceYears;
    
    private String location;
    
    @Column(name = "salary_min")
    private BigDecimal salaryMin;
    
    @Column(name = "salary_max")
    private BigDecimal salaryMax;
    
    @Column(name = "salary_currency")
    private String salaryCurrency = "USD";
    
    @Column(name = "publish_date")
    private Instant publishDate;
    
    @Column(name = "close_date")
    private Instant closeDate;
    
    @Column(name = "application_count")
    private Integer applicationCount = 0;
    
    @Column(name = "view_count")
    private Integer viewCount = 0;
}
