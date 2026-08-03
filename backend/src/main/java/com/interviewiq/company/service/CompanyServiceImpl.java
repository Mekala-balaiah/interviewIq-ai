package com.interviewiq.company.service;

import com.interviewiq.common.exception.ResourceNotFoundException;
import com.interviewiq.company.dto.CompanyDto;
import com.interviewiq.company.dto.UpdateCompanyRequest;
import com.interviewiq.company.entity.Company;
import com.interviewiq.company.mapper.CompanyMapper;
import com.interviewiq.company.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    @Override
    @Transactional(readOnly = true)
    public CompanyDto getCompanyById(UUID id) {
        Company company = companyRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));
        return companyMapper.toDto(company);
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyDto getCompanyBySlug(String slug) {
        Company company = companyRepository.findBySlugAndDeletedAtIsNull(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "slug", slug));
        return companyMapper.toDto(company);
    }

    @Override
    @Transactional
    public CompanyDto updateCompany(UUID id, UpdateCompanyRequest request) {
        Company company = companyRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));
        
        companyMapper.updateEntityFromRequest(request, company);
        company = companyRepository.save(company);
        
        return companyMapper.toDto(company);
    }
}
