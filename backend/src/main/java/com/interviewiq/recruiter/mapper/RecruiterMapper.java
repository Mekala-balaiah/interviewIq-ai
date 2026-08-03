package com.interviewiq.recruiter.mapper;

import com.interviewiq.company.mapper.CompanyMapper;
import com.interviewiq.recruiter.dto.RecruiterProfileDto;
import com.interviewiq.recruiter.dto.UpdateRecruiterProfileRequest;
import com.interviewiq.recruiter.entity.RecruiterProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {CompanyMapper.class})
public interface RecruiterMapper {

    @Mapping(source = "user.id", target = "userId")
    RecruiterProfileDto toDto(RecruiterProfile profile);

    void updateEntityFromRequest(UpdateRecruiterProfileRequest request, @MappingTarget RecruiterProfile profile);
}
