package com.interviewiq.notification.kafka;

import com.interviewiq.auth.entity.User;
import com.interviewiq.auth.repository.UserRepository;
import com.interviewiq.notification.dto.KafkaNotificationEvent;
import com.interviewiq.notification.dto.NotificationDto;
import com.interviewiq.notification.entity.Notification;
import com.interviewiq.notification.enums.NotificationChannel;
import com.interviewiq.notification.enums.NotificationStatus;
import com.interviewiq.notification.repository.NotificationRepository;
import com.interviewiq.notification.service.EmailNotificationService;
import com.interviewiq.notification.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SseService sseService;
    private final EmailNotificationService emailNotificationService;

    @KafkaListener(topics = "${interviewiq.kafka.topics.notification-events}", groupId = "notification-group")
    @Transactional
    public void consumeNotificationEvent(KafkaNotificationEvent event) {
        log.info("Consumed notification event for user: {}", event.getUserId());

        User user = userRepository.findById(event.getUserId()).orElse(null);
        if (user == null) {
            log.warn("User {} not found, skipping notification.", event.getUserId());
            return;
        }

        Notification notification = Notification.builder()
                .user(user)
                .type(event.getType())
                .channel(event.getChannel() != null ? event.getChannel() : NotificationChannel.IN_APP)
                .title(event.getTitle())
                .message(event.getMessage())
                .metadata(event.getMetadata())
                .status(NotificationStatus.PENDING)
                .build();

        notification = notificationRepository.save(notification);

        // Map to DTO to send to clients/services
        NotificationDto dto = NotificationDto.builder()
                .id(notification.getId())
                .userId(user.getId())
                .type(notification.getType())
                .channel(notification.getChannel())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .metadata(notification.getMetadata())
                .isRead(notification.getIsRead())
                .status(notification.getStatus())
                .createdAt(notification.getCreatedAt())
                .build();

        try {
            if (notification.getChannel() == NotificationChannel.IN_APP) {
                sseService.sendEventToUser(user.getId(), dto);
                notification.setStatus(NotificationStatus.SENT);
            } else if (notification.getChannel() == NotificationChannel.EMAIL) {
                emailNotificationService.sendEmail(user.getEmail(), dto);
                notification.setStatus(NotificationStatus.SENT);
            }
            // For SMS and PUSH, we could add similar handlers here.
        } catch (Exception e) {
            log.error("Failed to process notification delivery: {}", e.getMessage());
            notification.setStatus(NotificationStatus.FAILED);
        }
        
        notificationRepository.save(notification);
    }
}
