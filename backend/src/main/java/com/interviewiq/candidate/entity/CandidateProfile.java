package com.interviewiq.candidate.entity;

import com.interviewiq.auth.entity.User;
import com.interviewiq.common.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "candidate_profiles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateProfile extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String headline;
    
    @Column(columnDefinition = "TEXT")
    private String bio;
    
    private String location;
    
    @Column(name = "linkedin_url")
    private String linkedinUrl;
    
    @Column(name = "github_url")
    private String githubUrl;
    
    @Column(name = "portfolio_url")
    private String portfolioUrl;
    
    @Column(name = "years_of_experience", nullable = false)
    private Integer yearsOfExperience = 0;
    
    @Column(name = "current_title")
    private String currentTitle;
    
    @Column(name = "current_company")
    private String currentCompany;
    
    @Column(name = "employment_status")
    private String employmentStatus;
    
    @Column(name = "notice_period")
    private String noticePeriod;
    
    @Column(name = "expected_salary_min")
    private BigDecimal expectedSalaryMin;
    
    @Column(name = "expected_salary_max")
    private BigDecimal expectedSalaryMax;
    
    @Column(name = "salary_currency")
    private String salaryCurrency = "USD";
    
    private String availability;
    
    @Column(name = "open_to_remote")
    private Boolean openToRemote = true;
    
    @Column(name = "profile_complete")
    private Boolean profileComplete = false;
    
    @Column(name = "profile_completion_pct")
    private Integer profileCompletionPct = 0;
}
