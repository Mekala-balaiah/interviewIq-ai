package com.interviewiq.job.mapper;

import com.interviewiq.job.dto.CreateJobRequest;
import com.interviewiq.job.dto.JobDto;
import com.interviewiq.job.dto.JobSkillDto;
import com.interviewiq.job.dto.UpdateJobRequest;
import com.interviewiq.job.entity.Job;
import com.interviewiq.job.entity.JobSkill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface JobMapper {

    @Mapping(source = "company.id", target = "companyId")
    @Mapping(source = "company.name", target = "companyName")
    @Mapping(source = "company.logoUrl", target = "companyLogoUrl")
    @Mapping(source = "recruiter.id", target = "recruiterId")
    JobDto toDto(Job job);

    Job toEntity(CreateJobRequest request);

    void updateEntityFromRequest(UpdateJobRequest request, @MappingTarget Job job);

    @Mapping(source = "job.id", target = "jobId")
    JobSkillDto toDto(JobSkill jobSkill);
}
