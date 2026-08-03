package com.interviewiq.messaging.service;

import com.interviewiq.common.response.PagedResponse;
import com.interviewiq.messaging.dto.InboundEmailWebhookRequest;
import com.interviewiq.messaging.dto.MessageDto;
import com.interviewiq.messaging.dto.SendMessageRequest;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface MessagingService {
    MessageDto sendMessage(UUID senderId, SendMessageRequest request);
    PagedResponse<MessageDto> getConversation(UUID userId1, UUID userId2, Pageable pageable);
    PagedResponse<MessageDto> getMessagesForApplication(UUID applicationId, Pageable pageable);
    void markAsRead(UUID messageId, UUID currentUserId);
    long getUnreadCount(UUID userId);
    void processInboundEmail(InboundEmailWebhookRequest request);
}
