package com.interviewiq.notification.service;

import com.interviewiq.auth.entity.User;
import com.interviewiq.common.exception.ResourceNotFoundException;
import com.interviewiq.common.exception.UnauthorizedException;
import com.interviewiq.common.response.PagedResponse;
import com.interviewiq.notification.dto.NotificationDto;
import com.interviewiq.notification.entity.Notification;
import com.interviewiq.notification.enums.NotificationChannel;
import com.interviewiq.notification.enums.NotificationStatus;
import com.interviewiq.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private UUID userId;
    private UUID notifId;
    private User user;
    private Notification notification;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        notifId = UUID.randomUUID();

        user = new User();
        user.setId(userId);

        notification = Notification.builder()
                .id(notifId)
                .user(user)
                .type("JOB_MATCH")
                .channel(NotificationChannel.IN_APP)
                .title("New Job Match!")
                .message("A job matching your profile has been posted.")
                .isRead(false)
                .status(NotificationStatus.SENT)
                .createdAt(Instant.now())
                .build();
    }

    @Test
    void getNotifications_ReturnsPagedResponse() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Notification> page = new PageImpl<>(List.of(notification), pageable, 1);
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)).thenReturn(page);

        PagedResponse<NotificationDto> result = notificationService.getNotifications(userId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("New Job Match!", result.getContent().get(0).getTitle());
    }

    @Test
    void getUnreadCount_ReturnsCorrectCount() {
        when(notificationRepository.countByUserIdAndIsReadFalse(userId)).thenReturn(5L);

        long count = notificationService.getUnreadCount(userId);

        assertEquals(5L, count);
        verify(notificationRepository).countByUserIdAndIsReadFalse(userId);
    }

    @Test
    void markAsRead_WithValidOwner_SetsReadFlag() {
        when(notificationRepository.findById(notifId)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        NotificationDto result = notificationService.markAsRead(notifId, userId);

        assertNotNull(result);
        assertTrue(notification.getIsRead());
        assertNotNull(notification.getReadAt());
        assertEquals(NotificationStatus.DELIVERED, notification.getStatus());
    }

    @Test
    void markAsRead_WithWrongOwner_ThrowsUnauthorized() {
        UUID otherUserId = UUID.randomUUID();
        when(notificationRepository.findById(notifId)).thenReturn(Optional.of(notification));

        assertThrows(UnauthorizedException.class, () ->
                notificationService.markAsRead(notifId, otherUserId));
    }

    @Test
    void markAsRead_WithNotFoundId_ThrowsResourceNotFoundException() {
        when(notificationRepository.findById(notifId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                notificationService.markAsRead(notifId, userId));
    }

    @Test
    void markAllAsRead_CallsRepository() {
        notificationService.markAllAsRead(userId);
        verify(notificationRepository).markAllAsReadForUser(userId);
    }
}
