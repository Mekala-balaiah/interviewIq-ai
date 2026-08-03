package com.interviewiq.notification.dto;

import com.interviewiq.notification.enums.NotificationChannel;
import com.interviewiq.notification.enums.NotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private UUID id;
    private UUID userId;
    private String type;
    private NotificationChannel channel;
    private String title;
    private String message;
    private String metadata;
    private Boolean isRead;
    private Instant readAt;
    private NotificationStatus status;
    private Instant createdAt;
}
