package com.interviewiq.recruiter.entity;

import com.interviewiq.auth.entity.User;
import com.interviewiq.common.audit.BaseEntity;
import com.interviewiq.company.entity.Company;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "recruiter_profiles")
@Getter
@Setter
public class RecruiterProfile extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    private String title;

    private String department;
    
    private String specialization;
    
    @Column(columnDefinition = "TEXT")
    private String bio;
    
    @Column(name = "linkedin_url")
    private String linkedinUrl;
    
    @Column(name = "active_jobs_count")
    private Integer activeJobsCount = 0;
}
