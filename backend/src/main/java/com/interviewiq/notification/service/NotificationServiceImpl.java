package com.interviewiq.notification.service;

import com.interviewiq.common.exception.ResourceNotFoundException;
import com.interviewiq.common.exception.UnauthorizedException;
import com.interviewiq.common.response.PagedResponse;
import com.interviewiq.notification.dto.NotificationDto;
import com.interviewiq.notification.entity.Notification;
import com.interviewiq.notification.enums.NotificationStatus;
import com.interviewiq.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<NotificationDto> getNotifications(UUID userId, Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        Page<NotificationDto> dtoPage = notifications.map(this::toDto);

        return new PagedResponse<>(
                dtoPage.getContent(),
                dtoPage.getNumber(),
                dtoPage.getSize(),
                dtoPage.getTotalElements(),
                dtoPage.getTotalPages(),
                dtoPage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(UUID userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Override
    @Transactional
    public NotificationDto markAsRead(UUID notificationId, UUID userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        if (!notification.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You do not have permission to access this notification");
        }

        if (!notification.getIsRead()) {
            notification.setIsRead(true);
            notification.setReadAt(Instant.now());
            if (notification.getStatus() == NotificationStatus.PENDING || notification.getStatus() == NotificationStatus.SENT) {
                notification.setStatus(NotificationStatus.DELIVERED);
            }
            notification = notificationRepository.save(notification);
        }

        return toDto(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(UUID userId) {
        notificationRepository.markAllAsReadForUser(userId);
    }

    private NotificationDto toDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .userId(notification.getUser().getId())
                .type(notification.getType())
                .channel(notification.getChannel())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .metadata(notification.getMetadata())
                .isRead(notification.getIsRead())
                .readAt(notification.getReadAt())
                .status(notification.getStatus())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
