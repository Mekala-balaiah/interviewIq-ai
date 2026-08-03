package com.interviewiq.messaging.controller;

import com.interviewiq.auth.security.UserPrincipal;
import com.interviewiq.common.response.ApiResponse;
import com.interviewiq.common.response.PagedResponse;
import com.interviewiq.messaging.dto.MessageDto;
import com.interviewiq.messaging.dto.SendMessageRequest;
import com.interviewiq.messaging.service.MessagingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Messaging", description = "Internal messaging between platform users")
public class MessageController {

    private final MessagingService messagingService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Send a new message")
    public ApiResponse<MessageDto> sendMessage(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody SendMessageRequest request) {
        return ApiResponse.success(messagingService.sendMessage(userPrincipal.getId(), request), "Message sent successfully");
    }

    @GetMapping("/conversations/{otherUserId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get conversation with another user")
    public ApiResponse<PagedResponse<MessageDto>> getConversation(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID otherUserId,
            Pageable pageable) {
        return ApiResponse.success(messagingService.getConversation(userPrincipal.getId(), otherUserId, pageable), "Conversation fetched successfully");
    }

    @GetMapping("/applications/{applicationId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get messages for a specific application")
    public ApiResponse<PagedResponse<MessageDto>> getApplicationMessages(
            @PathVariable UUID applicationId,
            Pageable pageable) {
        // Authorization should ideally be added in service to ensure user has access to application
        return ApiResponse.success(messagingService.getMessagesForApplication(applicationId, pageable), "Application messages fetched successfully");
    }

    @PatchMapping("/{messageId}/read")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Mark a message as read")
    public ApiResponse<Void> markAsRead(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID messageId) {
        messagingService.markAsRead(messageId, userPrincipal.getId());
        return ApiResponse.success(null, "Message marked as read");
    }

    @GetMapping("/unread/count")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get count of unread messages")
    public ApiResponse<Long> getUnreadCount(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(messagingService.getUnreadCount(userPrincipal.getId()), "Unread count fetched successfully");
    }
}
