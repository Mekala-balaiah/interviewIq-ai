package com.interviewiq.notification.kafka;

import com.interviewiq.notification.dto.KafkaNotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${interviewiq.kafka.topics.notification-events}")
    private String notificationTopic;

    public void sendNotificationEvent(KafkaNotificationEvent event) {
        log.info("Sending notification event for user: {}", event.getUserId());
        kafkaTemplate.send(notificationTopic, event.getUserId().toString(), event);
    }
}
