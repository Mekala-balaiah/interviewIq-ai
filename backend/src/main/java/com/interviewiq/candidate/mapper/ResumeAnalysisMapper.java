package com.interviewiq.candidate.mapper;

import com.interviewiq.candidate.dto.ResumeAnalysisDto;
import com.interviewiq.candidate.entity.ResumeAnalysis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResumeAnalysisMapper {

    @Mapping(source = "resume.id", target = "resumeId")
    @Mapping(source = "job.id", target = "jobId")
    ResumeAnalysisDto toDto(ResumeAnalysis entity);
}
