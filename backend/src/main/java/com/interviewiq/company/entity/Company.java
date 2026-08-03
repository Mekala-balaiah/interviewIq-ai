package com.interviewiq.company.entity;

import com.interviewiq.common.audit.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "companies")
@Getter
@Setter
public class Company extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(name = "logo_url")
    private String logoUrl;

    private String website;
    
    private String industry;
    
    @Column(name = "size_range")
    private String sizeRange;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String headquarters;
    
    @Column(name = "linkedin_url")
    private String linkedinUrl;

    @Column(name = "status", nullable = false)
    private String status = "ACTIVE";
}
