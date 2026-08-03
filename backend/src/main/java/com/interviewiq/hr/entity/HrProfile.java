package com.interviewiq.hr.entity;

import com.interviewiq.auth.entity.User;
import com.interviewiq.common.audit.BaseEntity;
import com.interviewiq.company.entity.Company;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "hr_profiles")
@Getter
@Setter
public class HrProfile extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    private String title;
    
    private String department;
    
    @Column(name = "linkedin_url")
    private String linkedinUrl;
}
