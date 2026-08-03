package com.interviewiq.company.service;

import com.interviewiq.company.dto.CompanyDto;
import com.interviewiq.company.dto.UpdateCompanyRequest;

import java.util.UUID;

public interface CompanyService {
    CompanyDto getCompanyById(UUID id);
    CompanyDto getCompanyBySlug(String slug);
    CompanyDto updateCompany(UUID id, UpdateCompanyRequest request);
}
