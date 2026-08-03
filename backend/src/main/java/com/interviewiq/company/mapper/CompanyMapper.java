package com.interviewiq.company.mapper;

import com.interviewiq.company.dto.CompanyDto;
import com.interviewiq.company.dto.UpdateCompanyRequest;
import com.interviewiq.company.entity.Company;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CompanyMapper {
    CompanyDto toDto(Company company);
    void updateEntityFromRequest(UpdateCompanyRequest request, @MappingTarget Company company);
}
