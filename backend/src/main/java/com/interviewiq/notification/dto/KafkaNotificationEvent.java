package com.interviewiq.notification.dto;

import com.interviewiq.notification.enums.NotificationChannel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KafkaNotificationEvent {
    private UUID userId;
    private String type;
    private NotificationChannel channel;
    private String title;
    private String message;
    private String metadata;
}
