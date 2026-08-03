package com.interviewiq.messaging.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class MessageDto {
    private UUID id;
    private UUID senderId;
    private String senderName;
    private UUID receiverId;
    private String receiverName;
    private UUID applicationId;
    private String subject;
    private String content;
    private boolean read;
    private Instant readAt;
    private Instant createdAt;
}
