package com.interviewiq.notification.service;

import com.interviewiq.common.response.PagedResponse;
import com.interviewiq.notification.dto.NotificationDto;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NotificationService {
    
    PagedResponse<NotificationDto> getNotifications(UUID userId, Pageable pageable);
    
    long getUnreadCount(UUID userId);
    
    NotificationDto markAsRead(UUID notificationId, UUID userId);
    
    void markAllAsRead(UUID userId);
}
