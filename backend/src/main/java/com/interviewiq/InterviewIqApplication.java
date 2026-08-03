package com.interviewiq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * InterviewIQ AI — Enterprise AI Recruitment & Interview Intelligence Platform
 *
 * <p>Entry point for the Spring Boot 3 modular monolith backend.
 * Architecture: Clean Architecture (Ports & Adapters) with feature-sliced modules.
 *
 * <p>Modules:
 * <ul>
 *   <li>auth      — JWT + OAuth2 authentication and authorization
 *   <li>candidate — Candidate profiles, resumes, applications
 *   <li>recruiter — Recruiter portal, job management, pipeline
 *   <li>hr        — HR dashboards, approvals, compliance
 *   <li>admin     — Platform administration
 *   <li>interview — AI interview engine (Spring AI + LangChain4j)
 *   <li>resume    — Resume parsing and ATS scoring
 *   <li>assessment— Coding assessment engine
 *   <li>analytics — Dashboards and reporting
 *   <li>search    — Elasticsearch-powered search
 *   <li>notification — Real-time notifications (Kafka + SSE)
 *   <li>ai        — Shared AI utilities and RAG pipeline
 * </ul>
 *
 * @author InterviewIQ Engineering Team
 * @version 0.3.0
 * @since 2026-08-03
 */
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
@EnableCaching
@EnableAsync
@EnableScheduling
@EnableKafka
@ConfigurationPropertiesScan("com.interviewiq")
public class InterviewIqApplication {

    public static void main(String[] args) {
        SpringApplication.run(InterviewIqApplication.class, args);
    }
}
