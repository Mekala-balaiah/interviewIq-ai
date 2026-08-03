package com.interviewiq.candidate.mapper;

import com.interviewiq.candidate.dto.ResumeDto;
import com.interviewiq.candidate.entity.Resume;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResumeMapper {

    ResumeDto toDto(Resume resume);
}
