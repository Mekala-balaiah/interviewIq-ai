package com.interviewiq.notification.service;

import com.interviewiq.notification.dto.NotificationDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailNotificationService {

    public void sendEmail(String emailAddress, NotificationDto notification) {
        // In a real application, you would use JavaMailSender or an API like SendGrid/AWS SES.
        // For this prototype, we will just log the email contents.
        log.info("=========================================");
        log.info("📧 SENDING EMAIL NOTIFICATION");
        log.info("To: {}", emailAddress);
        log.info("Subject: {}", notification.getTitle());
        log.info("Body: {}", notification.getMessage());
        log.info("=========================================");
    }
}
