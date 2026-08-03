package com.interviewiq.messaging.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class SendMessageRequest {
    
    @NotNull(message = "Receiver ID is required")
    private UUID receiverId;
    
    private UUID applicationId;
    
    private String subject;
    
    @NotBlank(message = "Content is required")
    private String content;
}
