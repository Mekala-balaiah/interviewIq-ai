package com.interviewiq.messaging.mapper;

import com.interviewiq.messaging.dto.MessageDto;
import com.interviewiq.messaging.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MessageMapper {

    @Mapping(source = "sender.id", target = "senderId")
    @Mapping(source = "sender.firstName", target = "senderName")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "receiver.firstName", target = "receiverName")
    @Mapping(source = "application.id", target = "applicationId")
    MessageDto toDto(Message entity);
}
