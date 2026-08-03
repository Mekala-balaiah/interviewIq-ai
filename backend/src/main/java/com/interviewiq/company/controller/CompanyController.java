package com.interviewiq.company.controller;

import com.interviewiq.company.dto.CompanyDto;
import com.interviewiq.company.dto.UpdateCompanyRequest;
import com.interviewiq.company.service.CompanyService;
import com.interviewiq.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
@Tag(name = "Companies", description = "Endpoints for company profile management")
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping("/{id}")
    @Operation(summary = "Get company by ID")
    public ApiResponse<CompanyDto> getCompanyById(@PathVariable UUID id) {
        return ApiResponse.success(companyService.getCompanyById(id), "Company retrieved");
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get company by slug")
    public ApiResponse<CompanyDto> getCompanyBySlug(@PathVariable String slug) {
        return ApiResponse.success(companyService.getCompanyBySlug(slug), "Company retrieved");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER') or hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Update company profile")
    public ApiResponse<CompanyDto> updateCompany(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCompanyRequest request) {
        return ApiResponse.success(companyService.updateCompany(id, request), "Company updated successfully");
    }
}
