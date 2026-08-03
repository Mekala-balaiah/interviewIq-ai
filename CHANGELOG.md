# CHANGELOG — InterviewIQ AI

All notable changes to this project will be documented in this file.
Format follows [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

---

## [0.3.0] — 2026-08-03 | Sprint 3 — Module 3

### Added
- `backend/pom.xml` — Production Maven POM with 30+ dependencies (Spring Boot 3.3, AI, Security, Redis, Kafka, ES, JWT, MapStruct, Lombok, OpenAPI, Testcontainers)
- `InterviewIqApplication.java` — Main entry point with all enable annotations
- `application.yml` — Full base config (DataSource, JPA, Flyway, Redis, Kafka, ES, Spring AI, Mail, Actuator, custom properties)
- `application-dev.yml` — Dev profile overrides
- `application-prod.yml` — Production profile overrides
- `config/OpenApiConfig.java` — OpenAPI 3.0 + Swagger UI with JWT Bearer scheme
- `config/JacksonConfig.java` — ObjectMapper (ISO-8601 dates, null exclusion)
- `config/RedisConfig.java` — RedisTemplate + CacheManager with 8 named cache regions
- `config/KafkaConfig.java` — 7 Kafka topic beans with partition config
- `config/InterviewIqProperties.java` — Type-safe config properties binding
- `common/response/ApiResponse.java` — Standardized API response wrapper
- `common/response/ApiError.java` — Structured error payload with field errors
- `common/response/PagedResponse.java` — Paginated response wrapper
- `common/exception/GlobalExceptionHandler.java` — 15+ exception handlers
- `common/exception/*.java` — Full exception hierarchy (5 domain exception types)
- `common/audit/BaseEntity.java` — JPA base entity with audit fields + soft-delete
- `common/audit/AuditorAwareImpl.java` — SecurityContext-based auditor
- `docker-compose.yml` — Full dev stack (PostgreSQL, Redis, Zookeeper, Kafka, Kafka UI, ES, Kibana, MailHog)
- `.env.example` — All environment variable documentation
- `.gitignore` — Project-wide gitignore

---

## [0.2.0] — 2026-08-03 | Sprint 2 — Module 2

### Added
- `docs/ER_DIAGRAM.md` — Full ER diagram with 20 entities (Mermaid), index strategy, table descriptions
- `docs/API_DESIGN.md` — Complete REST API design (all endpoints, request/response contracts, status codes)
- `docs/SEQUENCE_DIAGRAMS.md` — 6 core sequence diagrams (Auth, Resume AI, Interview AI, Application, Notifications)
- `backend/src/main/resources/db/migration/V1__create_extensions_and_enums.sql` — PostgreSQL extensions + 15 ENUM types
- `backend/src/main/resources/db/migration/V2__create_auth_tables.sql` — users, refresh_tokens, email_verifications, password_reset_tokens, oauth_accounts
- `backend/src/main/resources/db/migration/V3__create_company_and_profile_tables.sql` — companies, candidate_profiles, recruiter_profiles, skills, candidate_skills, resumes
- `backend/src/main/resources/db/migration/V4__create_jobs_and_applications.sql` — jobs, job_skills, applications, resume_analyses
- `backend/src/main/resources/db/migration/V5__create_interview_and_assessment_tables.sql` — interviews, interview_questions, interview_responses, assessments, assessment_questions, assessment_submissions
- `backend/src/main/resources/db/migration/V6__create_platform_tables.sql` — notifications, calendar_events, audit_logs, learning_roadmaps + 50 seed skills

---

## [0.1.0] — 2026-08-03 | Sprint 1 — Module 1

### Added
- Project vision document (`docs/PROJECT_VISION.md`)
- Business requirements document (`docs/BUSINESS_REQUIREMENTS.md`)
- Full feature list (`docs/FEATURES.md`)
- Tech stack justification (`docs/TECH_STACK.md`)
- Architecture overview (`docs/ARCHITECTURE_OVERVIEW.md`)
- CHANGELOG.md
- VERSION file
- Root README.md

---
