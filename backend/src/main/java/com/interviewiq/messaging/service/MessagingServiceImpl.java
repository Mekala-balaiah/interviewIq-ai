package com.interviewiq.messaging.service;

import com.interviewiq.application.entity.Application;
import com.interviewiq.application.repository.ApplicationRepository;
import com.interviewiq.auth.entity.User;
import com.interviewiq.auth.repository.UserRepository;
import com.interviewiq.common.exception.BusinessException;
import com.interviewiq.common.exception.ResourceNotFoundException;
import com.interviewiq.common.response.PagedResponse;
import com.interviewiq.messaging.dto.InboundEmailWebhookRequest;
import com.interviewiq.messaging.dto.MessageDto;
import com.interviewiq.messaging.dto.SendMessageRequest;
import com.interviewiq.messaging.entity.Message;
import com.interviewiq.messaging.mapper.MessageMapper;
import com.interviewiq.messaging.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessagingServiceImpl implements MessagingService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final MessageMapper messageMapper;

    @Override
    @Transactional
    public MessageDto sendMessage(UUID senderId, SendMessageRequest request) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", senderId));
        
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getReceiverId()));

        Application application = null;
        if (request.getApplicationId() != null) {
            application = applicationRepository.findById(request.getApplicationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Application", "id", request.getApplicationId()));
        }

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setApplication(application);
        message.setSubject(request.getSubject());
        message.setContent(request.getContent());

        message = messageRepository.save(message);
        
        // In a real system, you might trigger a Kafka event or Webhook here to notify the receiver.
        return messageMapper.toDto(message);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<MessageDto> getConversation(UUID userId1, UUID userId2, Pageable pageable) {
        Page<Message> messages = messageRepository.findConversation(userId1, userId2, pageable);
        return new PagedResponse<>(messages.map(messageMapper::toDto));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<MessageDto> getMessagesForApplication(UUID applicationId, Pageable pageable) {
        Page<Message> messages = messageRepository.findByApplicationIdOrderByCreatedAtDesc(applicationId, pageable);
        return new PagedResponse<>(messages.map(messageMapper::toDto));
    }

    @Override
    @Transactional
    public void markAsRead(UUID messageId, UUID currentUserId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", messageId));

        if (!message.getReceiver().getId().equals(currentUserId)) {
            throw new BusinessException("You can only mark your own messages as read");
        }

        message.markAsRead();
        messageRepository.save(message);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(UUID userId) {
        return messageRepository.countByReceiverIdAndReadFalse(userId);
    }

    @Override
    @Transactional
    public void processInboundEmail(InboundEmailWebhookRequest request) {
        log.info("Processing inbound email from: {}", request.getFrom());
        
        // 1. Identify the sender by matching email
        String senderEmail = extractEmail(request.getFrom());
        User sender = userRepository.findByEmail(senderEmail).orElse(null);
        if (sender == null) {
            log.warn("Unknown sender email: {}. Dropping message.", senderEmail);
            return; // Or we could save it as an orphaned message if needed
        }

        // 2. Extract Application ID from subject (e.g. "Re: Application #550e8400-e29b-41d4-a716-446655440000")
        UUID applicationId = extractApplicationIdFromSubject(request.getSubject());
        Application application = null;
        User receiver = null;

        if (applicationId != null) {
            application = applicationRepository.findById(applicationId).orElse(null);
            if (application != null) {
                // If sender is candidate, receiver is recruiter (via Job)
                if (application.getCandidate().getUser().getId().equals(sender.getId())) {
                    receiver = application.getJob().getRecruiter().getUser();
                } 
                // If sender is recruiter, receiver is candidate
                else if (application.getJob().getRecruiter().getUser().getId().equals(sender.getId())) {
                    receiver = application.getCandidate().getUser();
                }
            }
        }

        if (receiver == null) {
            log.warn("Could not determine receiver for email from: {}", senderEmail);
            return;
        }

        // 3. Save the parsed email as a Message
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setApplication(application);
        message.setSubject(request.getSubject());
        message.setContent(request.getBodyPlain());

        messageRepository.save(message);
    }

    private String extractEmail(String fromField) {
        if (fromField == null) return null;
        // Extracts "user@example.com" from "John Doe <user@example.com>"
        Pattern pattern = Pattern.compile("<(.*?)>");
        Matcher matcher = pattern.matcher(fromField);
        if (matcher.find()) {
            return matcher.group(1).trim().toLowerCase();
        }
        return fromField.trim().toLowerCase();
    }

    private UUID extractApplicationIdFromSubject(String subject) {
        if (subject == null) return null;
        // Looking for a pattern like "APP-550e8400-e29b-41d4-a716-446655440000"
        Pattern pattern = Pattern.compile("APP-([a-f0-9\\-]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(subject);
        if (matcher.find()) {
            try {
                return UUID.fromString(matcher.group(1));
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }
}
