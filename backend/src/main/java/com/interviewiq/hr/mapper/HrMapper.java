package com.interviewiq.hr.mapper;

import com.interviewiq.hr.dto.HrProfileDto;
import com.interviewiq.hr.entity.HrProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HrMapper {

    @Mapping(source = "user.id", target = "userId")
    HrProfileDto toDto(HrProfile entity);
}
