package com.interviewiq.application.mapper;

import com.interviewiq.application.dto.ApplicationDto;
import com.interviewiq.application.entity.Application;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ApplicationMapper {

    @Mapping(target = "jobId", source = "job.id")
    @Mapping(target = "candidateId", source = "candidate.id")
    @Mapping(target = "resumeId", source = "resume.id")
    ApplicationDto toDto(Application application);
}
