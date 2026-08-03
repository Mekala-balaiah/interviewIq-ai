package com.interviewiq.candidate.mapper;

import com.interviewiq.candidate.dto.CandidateProfileDto;
import com.interviewiq.candidate.entity.CandidateProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CandidateMapper {

    @Mapping(target = "userId", source = "user.id")
    CandidateProfileDto toDto(CandidateProfile profile);
}
