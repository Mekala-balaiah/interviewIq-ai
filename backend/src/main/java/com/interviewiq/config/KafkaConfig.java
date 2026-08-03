package com.interviewiq.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;

/**
 * Kafka topic and configuration setup.
 *
 * <p>All topics use:
 * <ul>
 *   <li>3 partitions — enables parallel consumer processing
 *   <li>1 replica — suitable for single-broker dev; increase to 3 in production
 * </ul>
 *
 * <p>Topics follow naming convention: {@code interview-iq.<domain>.<event>}
 */
@Configuration
public class KafkaConfig {

    @Value("${interviewiq.kafka.topics.interview-created}")
    private String interviewCreatedTopic;

    @Value("${interviewiq.kafka.topics.resume-uploaded}")
    private String resumeUploadedTopic;

    @Value("${interviewiq.kafka.topics.candidate-applied}")
    private String candidateAppliedTopic;

    @Value("${interviewiq.kafka.topics.assessment-completed}")
    private String assessmentCompletedTopic;

    @Value("${interviewiq.kafka.topics.auth-events}")
    private String authEventsTopic;

    @Value("${interviewiq.kafka.topics.email-events}")
    private String emailEventsTopic;

    @Value("${interviewiq.kafka.topics.notification-events}")
    private String notificationEventsTopic;

    // ─── Topic Beans ─────────────────────────────────────────────────────────

    @Bean
    public NewTopic interviewCreatedTopic() {
        return TopicBuilder.name(interviewCreatedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic resumeUploadedTopic() {
        return TopicBuilder.name(resumeUploadedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic candidateAppliedTopic() {
        return TopicBuilder.name(candidateAppliedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic assessmentCompletedTopic() {
        return TopicBuilder.name(assessmentCompletedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic authEventsTopic() {
        return TopicBuilder.name(authEventsTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic emailEventsTopic() {
        return TopicBuilder.name(emailEventsTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic notificationEventsTopic() {
        return TopicBuilder.name(notificationEventsTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public RecordMessageConverter jsonMessageConverter() {
        return new JsonMessageConverter();
    }
}
