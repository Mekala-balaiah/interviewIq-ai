package com.interviewiq.hr.mapper;

import com.interviewiq.hr.dto.JobApprovalDto;
import com.interviewiq.hr.entity.JobApproval;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface JobApprovalMapper {

    @Mapping(source = "job.id", target = "jobId")
    @Mapping(source = "hr.id", target = "hrId")
    @Mapping(source = "hr.user.firstName", target = "hrName") // Adjust if you want full name
    JobApprovalDto toDto(JobApproval entity);
}
