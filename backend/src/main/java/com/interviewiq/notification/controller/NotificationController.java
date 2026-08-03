package com.interviewiq.notification.controller;

import com.interviewiq.auth.security.UserPrincipal;
import com.interviewiq.common.response.ApiResponse;
import com.interviewiq.common.response.PagedResponse;
import com.interviewiq.notification.dto.NotificationDto;
import com.interviewiq.notification.service.NotificationService;
import com.interviewiq.notification.service.SseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Notification center endpoints")
@SecurityRequirement(name = "BearerAuth")
public class NotificationController {

    private final NotificationService notificationService;
    private final SseService sseService;

    @GetMapping
    @Operation(summary = "Get user's notifications")
    public ApiResponse<PagedResponse<NotificationDto>> getNotifications(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Pageable pageable) {
        return ApiResponse.success(notificationService.getNotifications(userPrincipal.getId(), pageable), "Notifications retrieved");
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get user's unread notification count")
    public ApiResponse<Long> getUnreadCount(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(notificationService.getUnreadCount(userPrincipal.getId()), "Unread count retrieved");
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark a notification as read")
    public ApiResponse<NotificationDto> markAsRead(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(notificationService.markAsRead(id, userPrincipal.getId()), "Notification marked as read");
    }

    @PutMapping("/read-all")
    @Operation(summary = "Mark all notifications as read")
    public ApiResponse<Void> markAllAsRead(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        notificationService.markAllAsRead(userPrincipal.getId());
        return ApiResponse.success(null, "All notifications marked as read");
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Subscribe to real-time notifications via SSE")
    public SseEmitter streamNotifications(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return sseService.subscribe(userPrincipal.getId());
    }
}
