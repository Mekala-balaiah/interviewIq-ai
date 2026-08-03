package com.interviewiq.auth.mapper;

import com.interviewiq.auth.dto.UserDto;
import com.interviewiq.auth.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserDto toDto(User user);
}
